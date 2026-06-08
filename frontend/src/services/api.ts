import axios from 'axios';

const api = axios.create({
    baseURL: 'http://localhost:8080/api'
});

// Interceptor de REQUEST — adiciona o token
api.interceptors.request.use((config) => {
    const token = localStorage.getItem('token');
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
});

// Interceptor de RESPONSE — trata erros de autenticação
api.interceptors.response.use(
    (response) => response, // se deu certo, retorna normal
    (error) => {
        if (error.response?.status === 401 || error.response?.status === 403) {
            localStorage.removeItem('token'); // limpa o token inválido
            window.location.href = '/login'; // redireciona pro login
        }
        return Promise.reject(error);
    }
);

export default api;