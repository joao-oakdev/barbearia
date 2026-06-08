import { useState, useEffect } from 'react';
import { listarMeusAgendamentos, listarHorariosDisponiveis, criarAgendamento, cancelarAgendamento } from '../services/agendamentoService';
import type { Agendamento } from '../types';
import { logout } from '../services/authService';

function AgendamentosPage() {
    const [agendamentos, setAgendamentos] = useState<Agendamento[]>([]);
    const [horarios, setHorarios] = useState<string[]>([]);
    const [barbeiroId, setBarbeiroId] = useState('');
    const [data, setData] = useState('');
    const [erro, setErro] = useState('');
    const [sucesso, setSucesso] = useState('');

    useEffect(() => {
        carregarAgendamentos();
    }, []);

    const carregarAgendamentos = async () => {
        try {
            const lista = await listarMeusAgendamentos();
            setAgendamentos(lista);
        } catch (error) {
            setErro('Erro ao carregar agendamentos');
        }
    };

    const buscarHorarios = async () => {
        try {
            const lista = await listarHorariosDisponiveis(Number(barbeiroId), data);
            setHorarios(lista);
        } catch (error) {
            setErro('Erro ao buscar horários');
        }
    };

    const handleAgendar = async (dataHora: string) => {
        try {
            await criarAgendamento(Number(barbeiroId), dataHora);
            setSucesso('Agendamento criado com sucesso!');
            setErro('');
            carregarAgendamentos();
            setHorarios([]);
        } catch (error) {
            setErro('Erro ao criar agendamento');
        }
    };

    const handleCancelar = async (id: number) => {
        try {
            await cancelarAgendamento(id);
            setSucesso('Agendamento cancelado!');
            setErro('');
            carregarAgendamentos();
        } catch (error) {
            setErro('Erro ao cancelar agendamento');
        }
    };

    const formatarData = (dataHora: string) => {
        return new Date(dataHora).toLocaleString('pt-BR', {
            day: '2-digit', month: '2-digit', year: 'numeric',
            hour: '2-digit', minute: '2-digit'
        });
    };

    return (
        <div className="min-h-screen bg-[#0a0a0a] text-white">

            {/* Header */}
            <header className="border-b border-[#222] px-8 py-5 flex items-center justify-between">
                <h1 className="text-4xl text-orange-500" style={{ fontFamily: 'Bebas Neue, sans-serif', letterSpacing: '3px' }}>
                    BARBEARIA
                </h1>

<button
    onClick={async () => {
        await logout();
        localStorage.removeItem('token');
        window.location.href = '/login';
    }}
    className="text-[#888] hover:text-white text-sm uppercase tracking-widest transition-colors cursor-pointer"
>
    
    Sair
</button>
            </header>

            <div className="max-w-4xl mx-auto px-8 py-10 flex flex-col gap-10">

                {/* Mensagens */}
                {erro && (
                    <div className="bg-red-500/10 border border-red-500/30 text-red-400 px-4 py-3 rounded-lg text-sm">
                        {erro}
                    </div>
                )}
                {sucesso && (
                    <div className="bg-green-500/10 border border-green-500/30 text-green-400 px-4 py-3 rounded-lg text-sm">
                        {sucesso}
                    </div>
                )}

                {/* Novo Agendamento */}
                <div className="bg-[#141414] border border-[#222] rounded-xl p-8">
                    <h2 className="text-3xl text-white mb-6" style={{ fontFamily: 'Bebas Neue, sans-serif', letterSpacing: '2px' }}>
                        NOVO AGENDAMENTO
                    </h2>

                    <div className="flex gap-4 flex-wrap">
                        <div className="flex flex-col gap-2 flex-1 min-w-[150px]">
                            <label className="text-[#888] text-xs uppercase tracking-widest">ID do Barbeiro</label>
                            <input
                                type="number"
                                placeholder="Ex: 1"
                                value={barbeiroId}
                                onChange={(e) => setBarbeiroId(e.target.value)}
                                className="bg-[#0a0a0a] border border-[#222] text-white px-4 py-3 rounded-lg focus:outline-none focus:border-orange-500 transition-colors"
                            />
                        </div>

                        <div className="flex flex-col gap-2 flex-1 min-w-[150px]">
                            <label className="text-[#888] text-xs uppercase tracking-widest">Data</label>
                            <input
                                type="date"
                                value={data}
                                onChange={(e) => setData(e.target.value)}
                                className="bg-[#0a0a0a] border border-[#222] text-white px-4 py-3 rounded-lg focus:outline-none focus:border-orange-500 transition-colors"
                            />
                        </div>

                        <div className="flex items-end">
                            <button
                                onClick={buscarHorarios}
                                className="bg-orange-500 hover:bg-orange-600 text-black font-bold px-6 py-3 rounded-lg uppercase tracking-widest transition-colors cursor-pointer"
                            >
                                Buscar
                            </button>
                        </div>
                    </div>

                    {/* Horários */}
                    {horarios.length > 0 && (
                        <div className="mt-6">
                            <p className="text-[#888] text-xs uppercase tracking-widest mb-3">Horários Disponíveis</p>
                            <div className="flex flex-wrap gap-3">
                                {horarios.map((horario) => (
                                    <button
                                        key={horario}
                                        onClick={() => handleAgendar(horario)}
                                        className="border border-orange-500 text-orange-500 hover:bg-orange-500 hover:text-black font-bold px-4 py-2 rounded-lg transition-colors cursor-pointer text-sm"
                                    >
                                        {formatarData(horario)}
                                    </button>
                                ))}
                            </div>
                        </div>
                    )}
                </div>

                {/* Meus Agendamentos */}
                <div>
                    <h2 className="text-3xl text-white mb-6" style={{ fontFamily: 'Bebas Neue, sans-serif', letterSpacing: '2px' }}>
                        MEUS AGENDAMENTOS
                    </h2>

                    {agendamentos.length === 0 ? (
                        <p className="text-[#888]">Nenhum agendamento encontrado.</p>
                    ) : (
                        <div className="flex flex-col gap-4">
                            {agendamentos.map((agendamento) => (
                                <div key={agendamento.id} className="bg-[#141414] border border-[#222] rounded-xl p-6 flex items-center justify-between">
                                    <div className="flex flex-col gap-1">
                                        <p className="text-white font-semibold">{agendamento.nomeBarbeiro}</p>
                                        <p className="text-[#888] text-sm">{formatarData(agendamento.dataHora)}</p>
                                    </div>

                                    <div className="flex items-center gap-4">
                                        <span className={`text-xs font-bold uppercase tracking-widest px-3 py-1 rounded-full ${
                                            agendamento.status === 'AGENDADO'
                                                ? 'bg-orange-500/10 text-orange-500 border border-orange-500/30'
                                                : 'bg-[#222] text-[#888] border border-[#333]'
                                        }`}>
                                            {agendamento.status}
                                        </span>

                                        {agendamento.status === 'AGENDADO' && (
                                            <button
                                                onClick={() => handleCancelar(agendamento.id)}
                                                className="text-red-400 hover:text-red-300 text-sm uppercase tracking-widest transition-colors cursor-pointer"
                                            >
                                                Cancelar
                                            </button>
                                        )}
                                    </div>
                                </div>
                            ))}
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
}

export default AgendamentosPage;