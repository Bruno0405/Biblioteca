import { BrowserRouter, Routes, Route } from "react-router-dom"
import ProtectedRoute from "./routes/ProtectedRoute"
import Login from "./pages/Login"
import Dashboard from "./pages/Dashboard"

function App() {
  return ( 
    <BrowserRouter>
      <Routes>
        {/* Página pública */}
        <Route path="/" element={<Login />} />

         {/* Página protegida */}
        <Route path="/dashboard" element={<ProtectedRoute><Dashboard /></ProtectedRoute>} />
      </Routes>
    </BrowserRouter>
  ) }

export default App
