import { useState, useEffect } from "react";
import { AuthContext } from "./AuthContext";
import { login, loginFuncionario } from "../services/authService";

export function AuthProvider({ children }) {
  const STORAGE_KEY = "biblioteca-user";
  const TOKEN_KEY = "biblioteca-token";

  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    function rehydrateSession() {
      const rawUser = localStorage.getItem(STORAGE_KEY);
      if (!rawUser) {
        setLoading(false);
        return;
      }

      try {
        const parsedUser = JSON.parse(rawUser);
        setUser(parsedUser);
      } catch {
        localStorage.removeItem(STORAGE_KEY);
        localStorage.removeItem(TOKEN_KEY);
      }

      setLoading(false);
    }

    rehydrateSession();
  }, []);

  async function handleLogin(email, password, type) {
    let currentUser;
    if (type === "funcionario") {
      const funcionario = await loginFuncionario(email, password);
      currentUser = {
        id: funcionario.idFuncionario,
        name: funcionario.nome,
        email: funcionario.email,
        role: "ADMIN",
        raw: funcionario,
      };
    } else {
      const cliente = await login(email, password);
      currentUser = {
        id: cliente.idCliente,
        name: cliente.nomeCliente,
        email: cliente.email,
        role: "USER",
        raw: cliente,
      };
    }
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