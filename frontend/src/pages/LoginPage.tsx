import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { login } from '../services/authService';

function LoginPage() {
    const [email, setEmail] = useState('');
    const [senha, setSenha] = useState('');
    const [erro, setErro] = useState('');
    const navigate = useNavigate();

    const handleLogin = async () => {
        try {
            const usuario = await login(email, senha);
            localStorage.setItem('token', usuario.token);
            navigate('/agendamentos');
        } catch (error) {
            setErro('Email ou senha inválidos');
        }
    };

    return (
        <div className="min-h-screen bg-preto flex items-center justify-center">
            <div className="w-full max-w-md px-8">

                {/* Logo / Header */}
                <div className="text-center mb-10">
                    <h1 className="text-6xl text-laranja">OAK'S BARBEARIA</h1>
                    <p className="text-cinza text-sm tracking-widest uppercase mt-1">Sistema de Agendamentos</p>
                </div>

                {/* Card */}
                <div className="bg-preto-card border border-preto-borda rounded-xl p-8 shadow-2xl">
                    <h2 className="text-3xl text-branco mb-6">ENTRAR</h2>

                    {erro && (
                        <div className="bg-red-500/10 border border-red-500/30 text-red-400 px-4 py-3 rounded-lg mb-6 text-sm">
                            {erro}
                        </div>
                    )}

                    <div className="flex flex-col gap-4">
                        <div>
                            <label className="text-cinza text-xs uppercase tracking-widest mb-2 block">Email</label>
                            <input
                                type="email"
                                placeholder="seu@email.com"
                                value={email}
                                onChange={(e) => setEmail(e.target.value)}
                                className="w-full bg-preto border border-preto-borda text-branco px-4 py-3 rounded-lg focus:outline-none focus:border-laranja transition-colors"
                            />
                        </div>

                        <div>
                            <label className="text-cinza text-xs uppercase tracking-widest mb-2 block">Senha</label>
                            <input
                                type="password"
                                placeholder="••••••••"
                                value={senha}
                                onChange={(e) => setSenha(e.target.value)}
                                onKeyDown={(e) => e.key === 'Enter' && handleLogin()}
                                className="w-full bg-preto border border-preto-borda text-branco px-4 py-3 rounded-lg focus:outline-none focus:border-laranja transition-colors"
                            />
                        </div>

                        <button
    onClick={handleLogin}
    className="w-full bg-orange-500 hover:bg-orange-600 text-black font-bold py-3 rounded-lg mt-2 tracking-widest uppercase transition-colors cursor-pointer"
>
    Entrar
</button>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default LoginPage;