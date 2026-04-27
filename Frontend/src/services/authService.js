import apiClient from "./apiClient";

export async function login(email, senha) {
  try {
    const response = await apiClient.post("/clientes/login", { email, senha });
    return response.data;
  } catch (error) {
    const backendMessage = error.response?.data;
    const status = error.response?.status;

    if (typeof backendMessage === "string" && backendMessage.trim()) {
      throw new Error(backendMessage);
    }

    if (status === 404) {
      throw new Error(
        "Esse login autentica apenas clientes. Use cliente@biblioteca.com / 123 ou maria@biblioteca.com / 123."
      );
    }

    const message =
      error.code === "ERR_NETWORK"
        ? "Nao foi possivel conectar ao backend em http://localhost:8080."
        : "Falha ao autenticar com o backend.";

    throw new Error(message);
  }
}

export async function loginFuncionario(email, senha) {
  try {
    const response = await apiClient.post("/funcionarios/login", { email, senha });
    return response.data;
  } catch (error) {
    const backendMessage = error.response?.data;
    if (typeof backendMessage === "string" && backendMessage.trim()) {
      throw new Error(backendMessage);
    }
    const message =
      error.code === "ERR_NETWORK"
        ? "Nao foi possivel conectar ao backend em http://localhost:8080."
        : "Falha ao autenticar com o backend.";
    throw new Error(message);
  }
}