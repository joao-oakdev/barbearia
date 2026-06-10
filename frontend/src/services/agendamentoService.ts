import api from './api';
import type { Agendamento } from '../types';

export const listarHorariosDisponiveis = async (barbeiroId: number, data: string): Promise<string[]> => {
    const resposta = await api.get('/agendamentos/disponiveis', {
        params: { barbeiroId, data }
    });
    return resposta.data;
};

export const criarAgendamento = async (barbeiroId: number, dataHora: string): Promise<Agendamento> => {
    const resposta = await api.post('/agendamentos', { barbeiroId, dataHora });
    return resposta.data;
};

export const cancelarAgendamento = async (id: number): Promise<void> => {
    await api.delete(`/agendamentos/${id}`);
};

export const listarMeusAgendamentos = async (): Promise<Agendamento[]> => {
    const resposta = await api.get('/agendamentos/meus-agendamentos');
    return resposta.data;
};

export const logout = async (): Promise<void> => {
    await api.post('/auth/logout');
};