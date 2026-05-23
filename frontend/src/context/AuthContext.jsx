import { createContext, useContext, useEffect, useState } from 'react';
import { authApi } from '../services/endpoints';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {

  const [user, setUser] = useState(() => {
    const raw = localStorage.getItem('blade_user');
    return raw ? JSON.parse(raw) : null;
  });

  useEffect(() => {
    if (user) localStorage.setItem('blade_user', JSON.stringify(user));
    else localStorage.removeItem('blade_user');
  }, [user]);

  const login = async (email, password) => {
    const data = await authApi.login(email, password);
    localStorage.setItem('blade_token', data.token);
    setUser({ id: data.userId, email: data.email, fullName: data.fullName, role: data.role });
    return data;
  };

  const register = async (payload) => {
    const data = await authApi.register(payload);
    localStorage.setItem('blade_token', data.token);
    setUser({ id: data.userId, email: data.email, fullName: data.fullName, role: data.role });
    return data;
  };

  const logout = () => {
    localStorage.removeItem('blade_token');
    setUser(null);
  };

  return (
    <AuthContext.Provider value={{ user, login, register, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export const useAuth = () => useContext(AuthContext);