import { useState, useEffect } from "react"
import { AuthContext } from "./AuthContext"

export function AuthProvider({ children }) {

  const [user, setUser] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {

    function rehydrateSession() {

      const token = localStorage.getItem("token")

      if (!token) {
        setLoading(false)
        return
      }

      // Simulação de validação de token
      let restoredUser = null

      if (token === "token-admin-123") {
        restoredUser = {
          name: "Administrador",
          role: "ADMIN"
        }
      }

      if (token === "token-user-123") {
        restoredUser = {
          name: "Usuário",
          role: "USER"
        }
      }

      setUser(restoredUser)
      setLoading(false)
    }

    rehydrateSession()

  }, [])

  async function handleLogin(email, password) {

    if (email === "admin@email.com" && password === "123") {

      const token = "token-admin-123"
      localStorage.setItem("token", token)

      setUser({
        name: "Administrador",
        role: "ADMIN"
      })

    } else if (email === "user@email.com" && password === "123") {

      const token = "token-user-123"
      localStorage.setItem("token", token)

      setUser({
        name: "Usuário",
        role: "USER"
      })

    } else {
      throw new Error("Email ou senha inválidos")
    }
  }

  function logout() {
    localStorage.removeItem("token")
    setUser(null)
  }

  return (
    <AuthContext.Provider value={{ user, handleLogin, logout, loading }}>
      {children}
    </AuthContext.Provider>
  )
}