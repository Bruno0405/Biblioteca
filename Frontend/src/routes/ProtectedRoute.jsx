import { useContext } from "react"
import { Navigate } from "react-router-dom"
import { AuthContext } from "../context/AuthContext"

function ProtectedRoute({ children, requiredRole }) {
  const { user, loading } = useContext(AuthContext);

  if (loading) {
    return <p>Carregando...</p>;
  }

  if (!user) {
    return <Navigate to="/" />;
  }

  if (requiredRole && user.role !== requiredRole) {
    return <Navigate to={user.role === "ADMIN" ? "/admin" : "/dashboard"} />;
  }

  return children;
}

export default ProtectedRoute