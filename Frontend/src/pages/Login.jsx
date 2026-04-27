import { useState, useContext } from "react";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faUser, faUserTie } from "@fortawesome/free-solid-svg-icons";
import { AuthContext } from "../context/AuthContext";
import { useNavigate } from "react-router-dom";

function Login() {
  const { handleLogin } = useContext(AuthContext);
  const navigate = useNavigate();

  const [loginType, setLoginType] = useState("cliente");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [submitting, setSubmitting] = useState(false);

  async function submit(e) {
    e.preventDefault();
    setError("");
    setSubmitting(true);
    try {
      await handleLogin(email, password, loginType);
      navigate(loginType === "funcionario" ? "/admin" : "/dashboard");
    } catch (err) {
      setError(err.message);
    } finally {
      setSubmitting(false);
    }
  }

  function switchType(type) {
    setLoginType(type);
    setError("");
    setEmail("");
    setPassword("");
  }

  const isAdmin = loginType === "funcionario";

  return (
    <div className="login-shell">
      <div className="login-card">
        <p className="kicker">Sistema Biblioteca</p>
        <h1>Bem-vindo</h1>
        <p className="muted" style={{ marginBottom: 20 }}>
          Selecione o tipo de acesso para continuar.
        </p>

        <div className="login-tabs">
          <button
            type="button"
            className={"login-tab" + (!isAdmin ? " active" : "")}
            onClick={() => switchType("cliente")}
          >
            <FontAwesomeIcon icon={faUser} />
            <span>Sou Cliente</span>
          </button>
          <button
            type="button"
            className={"login-tab" + (isAdmin ? " active" : "")}
            onClick={() => switchType("funcionario")}
          >
            <FontAwesomeIcon icon={faUserTie} />
            <span>Sou Funcionário</span>
          </button>
        </div>

        <form onSubmit={submit} className="form-grid">
          <label htmlFor="email">Email</label>
          <input
            id="email"
            type="email"
            placeholder={isAdmin ? "funcionario@biblioteca.com" : "cliente@biblioteca.com"}
            value={email}
            onChange={(event) => setEmail(event.target.value)}
            required
          />

          <label htmlFor="password">Senha</label>
          <input
            id="password"
            type="password"
            placeholder="••••••••"
            value={password}
            onChange={(event) => setPassword(event.target.value)}
            required
          />

          {error && <p className="form-error">{error}</p>}

          <button
            type="submit"
            disabled={submitting}
            className={isAdmin ? "admin-login-btn" : ""}
          >
            {submitting
              ? "Entrando..."
              : "Entrar como " + (isAdmin ? "Funcionário" : "Cliente")}
          </button>
        </form>

        <p className="form-help">
          {isAdmin
            ? "Funcionários: POST /funcionarios/login. Teste: admin@biblioteca.com / 123."
            : "Clientes: POST /clientes/login. Testes: cliente@biblioteca.com / 123 ou maria@biblioteca.com / 123."}
        </p>
      </div>
    </div>
  );
}

export default Login;