import api from './api';
import type{ Usuario } from '../types';

export const login = async (email: string, senha: string): Promise<Usuario> => {
    const resposta = await api.post('/auth/login', { email, senha });
    return resposta.data;
};

export const registro = async (nome: string, email: string, senha: string, telefone: string): Promise<void> => {
    await api.post('/auth/registro', { nome, email, senha, telefone });
};

export const logout = async (): Promise<void> => {
    await api.post('/auth/logout');
};