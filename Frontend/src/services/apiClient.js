import axios from "axios";

const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_URL || "http://localhost:8080",
  headers: {
    "Content-Type": "application/json",
  },
});

apiClient.interceptors.request.use((config) => {
  if (config.url === "/auth/login") return config;
  const token = localStorage.getItem("biblioteca-token");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem("biblioteca-token");
      localStorage.removeItem("biblioteca-user");
      window.location.href = "/";
    }
    return Promise.reject(error);
  }
);

export default apiClient;
