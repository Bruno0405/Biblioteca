import { useState, useEffect } from "react";
import { AuthContext } from "./AuthContext";
import { login as loginService } from "../services/authService";

export function AuthProvider({ children }) {
  const STORAGE_KEY = "biblioteca-user";
  const TOKEN_KEY = "biblioteca-token";

  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const rawUser = localStorage.getItem(STORAGE_KEY);
    const token = localStorage.getItem(TOKEN_KEY);
    if (rawUser && token) {
      try {
        setUser(JSON.parse(rawUser));
      } catch {
        localStorage.removeItem(STORAGE_KEY);
        localStorage.removeItem(TOKEN_KEY);
      }
    }
    setLoading(false);
  }, []);

  async function handleLogin(email, password, tipo) {
    const data = await loginService(email, password, tipo);

    const role = tipo === "funcionario" ? "ADMIN" : "USER";

    const currentUser = {
      id: data.userId,
      name: data.name,
      email: data.email,
      role,
      tipo: data.tipo,
      perfil: data.perfil,
      raw: data,
    };

    localStorage.setItem(TOKEN_KEY, data.token);
    localStorage.setItem(STORAGE_KEY, JSON.stringify(currentUser));
    setUser(currentUser);
  }

  function logout() {
    localStorage.removeItem(STORAGE_KEY);
    localStorage.removeItem(TOKEN_KEY);
    setUser(null);
  }

  return (
    <AuthContext.Provider value={{ user, handleLogin, logout, loading }}>
      {children}
    </AuthContext.Provider>
  );
}
