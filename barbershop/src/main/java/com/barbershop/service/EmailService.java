package com.barbershop.service;

import com.barbershop.model.Appointment;
import com.barbershop.model.Barber;
import com.barbershop.model.User;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Service
public class EmailService {

    private static final ZoneId   CHILE_ZONE = ZoneId.of("America/Santiago");
    private static final Locale   CL_LOCALE  = new Locale("es", "CL");
    private static final DateTimeFormatter HORA  = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("EEEE d 'de' MMMM", CL_LOCALE);

    // ── Design tokens Flow Futurama ──────────────────────────────────────────
    private static final String NEON    = "#b7ff00";
    private static final String BLACK   = "#030403";
    private static final String SURFACE = "#0a0d09";
    private static final String BODY_BG = "#0d110c";
    private static final String CARD    = "#111510";
    private static final String LINE    = "#1e2a1a";
    private static final String TEXT    = "#f0f4e8";
    private static final String MUTED   = "#8d9484";
    private static final String DIM     = "#5a6055";
    private static final String GHOST   = "#3a3f35";

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.business.name:ElPipeBarber}")
    private String businessName;

    @Value("${app.business.address:La Galaxia 2292, Maipu}")
    private String businessAddress;

    @Value("${app.business.phone:+56 9 9809 8449}")
    private String businessPhone;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    // ── Emails públicos ──────────────────────────────────────────────────────

    public void enviarVerificacionCliente(User user, String verificationUrl) {
        String body =
                hero("VERIFICA TU<br>EMAIL", "Activa tu cuenta para poder reservar.") +
                        parrafo("Hola <strong style='color:" + TEXT + ";'>" + safe(user.getFullName()) + "</strong>, confirma que este correo es tuyo. Asi protegemos tu agenda y evitamos reservas falsas.") +
                        btnNeon("VERIFICAR MI EMAIL", verificationUrl) +
                        nota("Este link vence en 24 horas. Si no creaste una cuenta puedes ignorar este mensaje.");

        enviar(user.getEmail(),
                "Verifica tu email — " + businessName,
                wrapper("VERIFICACION", "Cuenta de cliente", body));
    }

    public void enviarConfirmacionCliente(Appointment cita) {
        String fecha   = FECHA.format(cita.getStartTime().toLocalDate());
        String horaStr = HORA.format(cita.getStartTime()) + " — " + HORA.format(cita.getEndTime());

        String body =
                hero("RESERVA<br>CONFIRMADA", "Te esperamos en " + safe(businessName) + ".") +
                        parrafo("Hola <strong style='color:" + TEXT + ";'>" + safe(cita.getClient().getFullName()) + "</strong>, tu hora quedo registrada. Nos vemos pronto.") +
                        ticket(
                                filaTicket("Fecha",    safe(fecha),                                     false) +
                                        filaTicket("Hora",     safe(horaStr),                                   true)  +
                                        filaTicket("Servicio", safe(cita.getService().getName()),               false) +
                                        filaTicket("Barbero",  safe(cita.getBarber().getName()),                false) +
                                        filaTicket("Duracion", cita.getService().getDurationMinutes() + " min", false) +
                                        filaPrecio(money(cita.getService().getPrice()))
                        ) +
                        nota("Direccion: " + safe(businessAddress) +
                                "<br>Telefono: " + safe(businessPhone) +
                                "<br>Si necesitas cancelar, hazlo con anticipacion desde la plataforma.");

        enviar(cita.getClient().getEmail(),
                "Reserva confirmada — " + cita.getService().getName() + " — " + HORA.format(cita.getStartTime()),
                wrapper("CONFIRMACION", safe(fecha) + " &nbsp;·&nbsp; " + safe(horaStr), body));
    }

    public void enviarNuevaCitaHoy(Appointment cita) {
        Barber barber = cita.getBarber();
        if (barber.getUser() == null || barber.getUser().getEmail() == null) return;

        String fecha   = FECHA.format(cita.getStartTime().toLocalDate());
        String horaStr = HORA.format(cita.getStartTime()) + " — " + HORA.format(cita.getEndTime());

        String body =
                hero("NUEVA CITA<br>EN TU AGENDA", "Se acaba de registrar una reserva.") +
                        parrafo("Hola <strong style='color:" + TEXT + ";'>" + safe(barber.getName()) + "</strong>, revisa el detalle y prepara el servicio.") +
                        ticket(
                                filaTicket("Fecha",    safe(fecha),                                              false) +
                                        filaTicket("Hora",     safe(horaStr),                                            true)  +
                                        filaTicket("Cliente",  safe(cita.getClient().getFullName()),                     false) +
                                        filaTicket("Telefono", safe(valueOrDash(cita.getClient().getPhone())),           false) +
                                        filaTicket("Servicio", safe(cita.getService().getName()),                        false) +
                                        filaNota(cita) +
                                        filaPrecio(money(cita.getService().getPrice()))
                        ) +
                        nota("Enviado por " + safe(businessName) + ". Si el cliente cancela, recibiras otra notificacion.");

        enviar(barber.getUser().getEmail(),
                "Nueva cita — " + cita.getClient().getFullName() + " — " + fecha + " " + HORA.format(cita.getStartTime()),
                wrapper("NUEVA RESERVA", safe(fecha) + " &nbsp;·&nbsp; " + safe(horaStr), body));
    }

    public void enviarAgendaDiaria(Barber barber, List<Appointment> citas) {
        if (barber.getUser() == null || barber.getUser().getEmail() == null) return;

        String fecha   = FECHA.format(LocalDate.now(CHILE_ZONE));
        int    totalMin = citas.stream().mapToInt(a -> a.getService().getDurationMinutes()).sum();

        StringBuilder body = new StringBuilder();

        if (citas.isEmpty()) {
            body.append(hero("DIA SIN<br>CITAS", "No hay reservas activas para hoy."));
            body.append(estadoVacio());
        } else {
            body.append(hero("AGENDA<br>DE HOY", "Tienes " + citas.size() + " cita(s) programadas."));
            body.append(statsBlock(citas.size(), totalMin));
            body.append(tablaAgenda(citas));
        }

        body.append(nota("Horario: lunes a viernes 10:45 — 21:00 &nbsp;(almuerzo 16:00 — 17:00)&nbsp; · &nbsp;Sabado 10:00 — 21:00."));

        enviar(barber.getUser().getEmail(),
                "Agenda de hoy — " + fecha + " — " + (citas.isEmpty() ? "sin citas" : citas.size() + " cita(s)"),
                wrapper("AGENDA DIARIA", safe(fecha), body.toString()));
    }

    // ── Estructura base ──────────────────────────────────────────────────────

    private String wrapper(String tag, String sub, String body) {
        String logoBox =
                "<td style='padding:20px 28px 16px;background:" + SURFACE + ";border-left:1px solid " + LINE + ";border-right:1px solid " + LINE + ";'>" +
                        "<table width='100%' cellpadding='0' cellspacing='0'><tr>" +
                        "<td>" +
                        "<table cellpadding='0' cellspacing='0'><tr>" +
                        "<td style='width:38px;height:38px;border:1.5px solid " + NEON + ";text-align:center;vertical-align:middle;font-size:16px;font-weight:900;color:" + NEON + ";padding:0 6px;'>EP</td>" +
                        "<td style='padding-left:10px;'>" +
                        "<div style='font-size:18px;font-weight:900;color:" + TEXT + ";letter-spacing:2px;text-transform:uppercase;line-height:1;'>ELPIPEBARBER</div>" +
                        "<div style='font-size:8px;color:" + NEON + ";font-weight:700;letter-spacing:5px;text-transform:uppercase;margin-top:3px;'>FLOW FUTURAMA</div>" +
                        "</td></tr></table>" +
                        "<div style='margin-top:10px;font-size:11px;color:" + DIM + ";'>" + safe(businessAddress) + " &nbsp;&middot;&nbsp; " + safe(businessPhone) + "</div>" +
                        "</td>" +
                        "<td align='right' valign='top'>" +
                        "<span style='display:inline-block;padding:4px 10px;border:1px solid " + NEON + ";font-size:8px;color:" + NEON + ";font-weight:700;letter-spacing:3px;text-transform:uppercase;'>" + safe(tag) + "</span>" +
                        "<div style='margin-top:8px;font-size:10px;color:" + DIM + ";text-align:right;'>" + sub + "</div>" +
                        "</td>" +
                        "</tr></table></td>";

        return "<!DOCTYPE html><html lang='es'><head><meta charset='UTF-8'>" +
                "<meta name='viewport' content='width=device-width,initial-scale=1'></head>" +
                "<body style='margin:0;padding:0;background:" + BLACK + ";font-family:Arial,Helvetica,sans-serif;'>" +
                "<table width='100%' cellpadding='0' cellspacing='0' style='background:" + BLACK + ";padding:28px 12px;'>" +
                "<tr><td align='center'>" +
                "<table width='600' cellpadding='0' cellspacing='0' style='max-width:600px;width:100%;'>" +

                // Tab tipo arriba derecha
                "<tr><td align='right' style='padding-bottom:6px;'>" +
                "</td></tr>" +

                // Barra neon top
                "<tr><td style='height:3px;background:" + NEON + ";font-size:0;'>&nbsp;</td></tr>" +

                // Header logo
                "<tr>" + logoBox + "</tr>" +

                // Linea degradada neon
                "<tr><td style='height:1px;font-size:0;background:linear-gradient(90deg," + NEON + " 0%," + NEON + " 35%,transparent 100%);'>&nbsp;</td></tr>" +

                // Body con watermark FLOW
                "<tr><td style='background:" + BODY_BG + ";padding:28px 28px 24px;border-left:1px solid " + LINE + ";border-right:1px solid " + LINE + ";position:relative;'>" +
                "<div style='position:relative;'>" +
                body +
                "</div>" +
                "</td></tr>" +

                // Footer
                "<tr><td style='background:" + SURFACE + ";padding:14px 28px;border-left:1px solid " + LINE + ";border-right:1px solid " + LINE + ";border-top:1px solid " + LINE + ";font-size:11px;color:" + GHOST + ";'>" +
                "Mensaje automatico de <strong style='color:" + DIM + ";'>" + safe(businessName) + "</strong>. No responder este correo." +
                "</td></tr>" +

                // Barra neon bottom
                "<tr><td style='height:3px;background:" + NEON + ";font-size:0;'>&nbsp;</td></tr>" +

                "</table></td></tr></table></body></html>";
    }

    // ── Componentes ──────────────────────────────────────────────────────────

    private String hero(String headline, String sub) {
        return "<p style='margin:0 0 6px;font-size:8px;color:" + NEON + ";letter-spacing:4px;text-transform:uppercase;font-weight:700;'>" + safe(businessName) + "</p>" +
                "<h1 style='margin:0 0 6px;font-size:30px;font-weight:900;color:" + TEXT + ";text-transform:uppercase;letter-spacing:1px;line-height:1.05;'>" + headline + "</h1>" +
                "<p style='margin:0 0 20px;font-size:13px;color:" + DIM + ";padding-bottom:18px;border-bottom:1px solid " + LINE + ";'>" + safe(sub) + "</p>";
    }

    private String parrafo(String html) {
        return "<p style='margin:0 0 22px;font-size:15px;line-height:1.7;color:" + MUTED + ";'>" + html + "</p>";
    }

    private String ticket(String filas) {
        return "<div style='border:1px solid " + LINE + ";margin:0 0 24px;'>" +
                "<div style='background:" + CARD + ";padding:9px 16px;border-bottom:1px solid " + LINE + ";display:block;'>" +
                "<span style='font-size:8px;color:" + NEON + ";letter-spacing:3px;font-weight:700;text-transform:uppercase;'>Tu reserva</span>" +
                "<span style='font-size:8px;color:" + GHOST + ";letter-spacing:2px;float:right;'>&mdash; FLOW FUTURAMA &mdash;</span>" +
                "</div>" +
                "<table width='100%' cellpadding='0' cellspacing='0'>" + filas + "</table>" +
                "</div>";
    }

    private String filaTicket(String label, String value, boolean highlight) {
        String bg = highlight ? CARD : BODY_BG;
        return "<tr style='background:" + bg + ";'>" +
                "<td style='padding:11px 16px;font-size:9px;color:" + DIM + ";text-transform:uppercase;letter-spacing:2px;font-weight:700;border-bottom:1px solid " + LINE + ";width:36%;'>" + label + "</td>" +
                "<td style='padding:11px 16px;font-size:13px;color:" + (highlight ? NEON : TEXT) + ";font-weight:" + (highlight ? "900" : "600") + ";text-align:right;border-bottom:1px solid " + LINE + ";'>" + value + "</td>" +
                "</tr>";
    }

    private String filaPrecio(String precio) {
        return "<tr style='background:" + CARD + ";'>" +
                "<td style='padding:14px 16px;font-size:9px;color:" + DIM + ";text-transform:uppercase;letter-spacing:2px;font-weight:700;'>Total</td>" +
                "<td style='padding:14px 16px;text-align:right;'>" +
                "<span style='font-size:26px;font-weight:900;color:" + NEON + ";letter-spacing:1px;'>" + precio + "</span>" +
                "</td></tr>";
    }

    private String filaNota(Appointment cita) {
        if (cita.getNotes() == null || cita.getNotes().isBlank()) return "";
        return filaTicket("Nota", safe(cita.getNotes()), false);
    }

    private String btnNeon(String text, String url) {
        return "<table cellpadding='0' cellspacing='0' style='margin:0 0 24px;'><tr>" +
                "<td style='background:" + NEON + ";'>" +
                "<a href='" + safe(url) + "' style='display:inline-block;padding:13px 28px;color:" + BLACK + ";text-decoration:none;font-weight:900;font-size:12px;letter-spacing:3px;text-transform:uppercase;'>" + text + " &rarr;</a>" +
                "</td></tr></table>";
    }

    private String nota(String html) {
        return "<div style='border-left:3px solid " + NEON + ";padding:12px 16px;background:" + CARD + ";font-size:12px;color:" + DIM + ";line-height:1.65;margin-top:4px;'>" + html + "</div>";
    }

    private String estadoVacio() {
        return "<div style='border:1px solid " + LINE + ";padding:32px;text-align:center;margin:0 0 24px;background:" + CARD + ";'>" +
                "<div style='font-size:36px;color:" + LINE + ";font-weight:900;margin-bottom:10px;'>&#9988;</div>" +
                "<div style='font-size:14px;color:" + DIM + ";'>Hoy no tienes citas agendadas.</div>" +
                "</div>";
    }

    private String statsBlock(int citas, int totalMin) {
        return "<table width='100%' cellpadding='0' cellspacing='0' style='margin:0 0 20px;border-collapse:separate;border-spacing:8px;'><tr>" +
                "<td style='background:" + CARD + ";border:1px solid " + LINE + ";padding:16px 18px;width:50%;'>" +
                "<div style='font-size:8px;color:" + DIM + ";text-transform:uppercase;letter-spacing:3px;font-weight:700;'>Citas hoy</div>" +
                "<div style='font-size:38px;color:" + NEON + ";font-weight:900;margin-top:4px;line-height:1;'>" + citas + "</div>" +
                "</td>" +
                "<td style='background:" + CARD + ";border:1px solid " + LINE + ";padding:16px 18px;width:50%;'>" +
                "<div style='font-size:8px;color:" + DIM + ";text-transform:uppercase;letter-spacing:3px;font-weight:700;'>Tiempo total</div>" +
                "<div style='font-size:38px;color:" + NEON + ";font-weight:900;margin-top:4px;line-height:1;'>" + totalMin + "<span style='font-size:13px;color:" + DIM + ";'> min</span></div>" +
                "</td>" +
                "</tr></table>";
    }

    private String tablaAgenda(List<Appointment> citas) {
        StringBuilder t = new StringBuilder();
        t.append("<div style='border:1px solid ").append(LINE).append(";margin:0 0 24px;'>")
                .append("<div style='background:").append(CARD).append(";padding:9px 16px;border-bottom:1px solid ").append(LINE).append(";'>")
                .append("<span style='font-size:8px;color:").append(NEON).append(";letter-spacing:3px;font-weight:700;text-transform:uppercase;'>Citas del dia</span>")
                .append("<span style='font-size:8px;color:").append(GHOST).append(";letter-spacing:2px;float:right;'>&mdash; FLOW FUTURAMA &mdash;</span>")
                .append("</div>")
                .append("<table width='100%' cellpadding='0' cellspacing='0'>")
                .append("<tr style='background:").append(BODY_BG).append(";'>")
                .append("<th style='").append(thStyle()).append("'>Hora</th>")
                .append("<th style='").append(thStyle()).append("'>Cliente</th>")
                .append("<th style='").append(thStyle()).append("'>Servicio</th>")
                .append("<th style='").append(thStyle()).append("text-align:right;'>Total</th>")
                .append("</tr>");

        for (int i = 0; i < citas.size(); i++) {
            Appointment a  = citas.get(i);
            String      bg = i % 2 == 0 ? BODY_BG : CARD;
            t.append("<tr style='background:").append(bg).append(";'>")
                    .append("<td style='").append(tdStyle(NEON)).append("font-weight:900;white-space:nowrap;'>")
                    .append(safe(HORA.format(a.getStartTime()))).append(" &mdash; ").append(safe(HORA.format(a.getEndTime())))
                    .append("</td>")
                    .append("<td style='").append(tdStyle(TEXT)).append("'>")
                    .append(safe(a.getClient().getFullName()))
                    .append("<br><span style='font-size:10px;color:").append(DIM).append(";'>").append(safe(valueOrDash(a.getClient().getPhone()))).append("</span>")
                    .append("</td>")
                    .append("<td style='").append(tdStyle(MUTED)).append("'>").append(safe(a.getService().getName())).append("</td>")
                    .append("<td style='").append(tdStyle(NEON)).append("font-weight:900;text-align:right;'>").append(money(a.getService().getPrice())).append("</td>")
                    .append("</tr>");
        }

        t.append("</table></div>");
        return t.toString();
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private String thStyle() {
        return "padding:9px 14px;text-align:left;font-size:8px;color:" + DIM + ";letter-spacing:2px;text-transform:uppercase;font-weight:700;border-bottom:1px solid " + LINE + ";";
    }

    private String tdStyle(String color) {
        return "padding:12px 14px;font-size:12px;color:" + color + ";border-bottom:1px solid " + LINE + ";";
    }

    private String money(BigDecimal value) {
        NumberFormat fmt = NumberFormat.getCurrencyInstance(CL_LOCALE);
        fmt.setMaximumFractionDigits(0);
        return fmt.format(value);
    }

    private String valueOrDash(String value) {
        return (value == null || value.isBlank()) ? "-" : value;
    }

    private String safe(String value) {
        return HtmlUtils.htmlEscape(value == null ? "" : value);
    }

    private void enviar(String destino, String asunto, String htmlCuerpo) {
        try {
            MimeMessage     mensaje = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(destino);
            helper.setSubject(asunto);
            helper.setText(htmlCuerpo, true);
            mailSender.send(mensaje);
            System.out.println("[EmailService] Email enviado a " + destino);
        } catch (Exception e) {
            System.err.println("[EmailService] Error enviando a " + destino + ": " + e.getMessage());
        }
    }
}