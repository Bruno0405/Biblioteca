import apiClient from "./apiClient";

export async function login(email, senha, tipo) {
  try {
    const response = await apiClient.post("/auth/login", { email, senha, tipo });
    return response.data;
  } catch (error) {
    const backendMessage = error.response?.data;
    if (typeof backendMessage === "string" && backendMessage.trim()) {
      throw new Error(backendMessage);
    }

    if (error.response?.status === 0 || error.code === "ERR_NETWORK") {
      throw new Error(
        "Nao foi possivel conectar ao backend em https://biblioteca-0k9o.onrender.com."
      );
    }

    throw new Error("Falha ao autenticar. Verifique suas credenciais.");
  }
}
