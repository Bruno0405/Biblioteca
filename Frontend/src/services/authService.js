// Simula um backend (mock)
// Depois será substituído pelo backend Java

export function login(email, password) {
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      // Usuário administrador
      if (email === "admin@biblioteca.com" && password === "123") {
        resolve({
          name: "Administrador",
          email,
          role: "ADMIN",
          token: "fake-jwt-admin"
        })
      }

      // Usuário comum
      else if (email === "user@biblioteca.com" && password === "123") {
        resolve({
          name: "Usuário Comum",
          email,
          role: "USER",
          token: "fake-jwt-user"
        })
      }

      else {
        reject("Email ou senha inválidos")
      }
    }, 1000)
  })
}