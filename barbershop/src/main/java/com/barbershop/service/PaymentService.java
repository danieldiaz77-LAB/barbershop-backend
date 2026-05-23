package com.barbershop.service;

import com.barbershop.dto.request.CheckoutRequest;
import com.barbershop.dto.response.CheckoutResponse;
import com.barbershop.dto.response.PaymentResponse;
import com.barbershop.exception.BadRequestException;
import com.barbershop.exception.NotFoundException;
import com.barbershop.model.Appointment;
import com.barbershop.model.Payment;
import com.barbershop.model.enums.AppointmentStatus;
import com.barbershop.model.enums.PaymentMethod;
import com.barbershop.model.enums.PaymentStatus;
import com.barbershop.repository.AppointmentRepository;
import com.barbershop.repository.PaymentRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    private final AppointmentRepository appointmentRepository;
    private final PaymentRepository paymentRepository;
    private final AppointmentService appointmentService;

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    public PaymentService(AppointmentRepository appointmentRepository,
                          PaymentRepository paymentRepository,
                          AppointmentService appointmentService) {
        this.appointmentRepository = appointmentRepository;
        this.paymentRepository = paymentRepository;
        this.appointmentService = appointmentService;
    }

    // inicializa Stripe con la clave al arrancar el servicio
    @PostConstruct
    void init() {
        Stripe.apiKey = stripeSecretKey;
    }

    // verifica si la clave de Stripe es real (no placeholder)
    private boolean isStripeConfigured() {
        return stripeSecretKey != null
                && (stripeSecretKey.startsWith("sk_test_") ||
                stripeSecretKey.startsWith("sk_live_"))
                && !stripeSecretKey.contains("reemplazame");
    }

    @Transactional
    public CheckoutResponse createCheckout(CheckoutRequest req, String userEmail) {

        Appointment appointment = appointmentRepository.findById(req.appointmentId())
                .orElseThrow(() -> new NotFoundException("Cita no encontrada"));

        // solo el dueño de la cita puede pagar
        if (!appointment.getClient().getEmail().equalsIgnoreCase(userEmail)) {
            throw new BadRequestException("No tienes permiso para pagar esta cita");
        }

        if (appointment.getStatus() == AppointmentStatus.PAID) {
            throw new BadRequestException("Esta cita ya fue pagada");
        }

        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new BadRequestException("No se puede pagar una cita cancelada");
        }

        // monto en centavos (Stripe requiere entero)
        long amountCents = appointment.getService().getPrice()
                .multiply(BigDecimal.valueOf(100))
                .longValueExact();

        String origin     = req.originUrl().replaceAll("/+$", "");
        String successUrl = origin + "/payment/success?session_id={CHECKOUT_SESSION_ID}";
        String cancelUrl  = origin + "/payment/cancel?appointment_id=" + appointment.getId();

        String sessionId;
        String checkoutUrl;

        if (isStripeConfigured()) {
            // flujo real con Stripe Checkout
            try {
                SessionCreateParams params = SessionCreateParams.builder()
                        .setMode(SessionCreateParams.Mode.PAYMENT)
                        .setSuccessUrl(successUrl)
                        .setCancelUrl(cancelUrl)
                        .putMetadata("appointmentId", appointment.getId().toString())
                        .putMetadata("clientEmail", userEmail)
                        .addLineItem(SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                        .setCurrency("usd")
                                        .setUnitAmount(amountCents)
                                        .setProductData(
                                                SessionCreateParams.LineItem.PriceData
                                                        .ProductData.builder()
                                                        .setName(appointment.getService().getName()
                                                                + " con "
                                                                + appointment.getBarber().getName())
                                                        .build())
                                        .build())
                                .build())
                        .build();

                Session session = Session.create(params);
                sessionId   = session.getId();
                checkoutUrl = session.getUrl();

            } catch (StripeException e) {
                log.error("Error al crear sesión de Stripe", e);
                throw new BadRequestException("Error con Stripe: " + e.getMessage());
            }
        } else {
            // modo MOCK para desarrollo sin clave de Stripe
            sessionId   = "mock_sess_" + UUID.randomUUID();
            checkoutUrl = origin + "/payment/mock?session_id=" + sessionId
                    + "&appointment_id=" + appointment.getId();
            log.warn("Stripe no configurado. Usando modo MOCK. " +
                    "Agrega tu sk_test_xxx en application.properties");
        }

        // guarda o actualiza el registro de pago
        Payment payment = paymentRepository
                .findByAppointmentId(appointment.getId())
                .orElseGet(Payment::new);

        payment.setAppointment(appointment);
        payment.setAmount(appointment.getService().getPrice());
        payment.setCurrency("USD");
        payment.setMethod(PaymentMethod.STRIPE_CARD);
        payment.setStatus(PaymentStatus.INITIATED);
        payment.setStripeSessionId(sessionId);
        paymentRepository.save(payment);

        return new CheckoutResponse(sessionId, checkoutUrl);
    }

    @Transactional
    public PaymentResponse checkStatus(String sessionId) {

        Payment payment = paymentRepository.findByStripeSessionId(sessionId)
                .orElseThrow(() -> new NotFoundException("Sesión de pago no encontrada"));

        // si ya está pagado no hace falta consultar Stripe de nuevo
        if (payment.getStatus() == PaymentStatus.PAID) {
            return PaymentResponse.from(payment);
        }

        if (isStripeConfigured() && !sessionId.startsWith("mock_")) {
            // consulta el estado real en Stripe
            try {
                Session session = Session.retrieve(sessionId);
                if ("paid".equalsIgnoreCase(session.getPaymentStatus())) {
                    payment.setStatus(PaymentStatus.PAID);
                    payment.setPaidAt(LocalDateTime.now());
                    payment.setStripePaymentIntentId(session.getPaymentIntent());
                    appointmentService.markPaid(payment.getAppointment().getId());
                }
            } catch (StripeException e) {
                log.error("Error al verificar estado de Stripe", e);
                throw new BadRequestException("Error con Stripe: " + e.getMessage());
            }
        } else {
            // modo MOCK: auto-aprueba el pago
            payment.setStatus(PaymentStatus.PAID);
            payment.setPaidAt(LocalDateTime.now());
            appointmentService.markPaid(payment.getAppointment().getId());
        }

        return PaymentResponse.from(paymentRepository.save(payment));
    }
}