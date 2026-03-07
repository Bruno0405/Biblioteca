import { useContext } from "react"
import { Navigate } from "react-router-dom"
import { AuthContext } from "../context/AuthContext"

function ProtectedRoute({ children }) {

  const { user, loading } = useContext(AuthContext)

  if (loading) {
    return <p>Carregando...</p>
  }

  if (!user) {
    return <Navigate to="/" />
  }

  return children
}

export default ProtectedRoute