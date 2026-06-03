import React, { useState } from 'react';
import { Mail, Lock, User, ArrowRight } from 'lucide-react';

interface AuthProps {
  onLoginSuccess: (name: string) => void;
}

export const Auth: React.FC<AuthProps> = ({ onLoginSuccess }) => {
  const [isLogin, setIsLogin] = useState(true);
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [name, setName] = useState('');

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    // Simulate login/register process
    // In a real app this would call an API
    setTimeout(() => {
      onLoginSuccess(isLogin ? email.split('@')[0] : name || 'Usuário');
    }, 500);
  };

  return (
    <div className="flex flex-col items-center justify-center min-h-[70vh] px-4">
      <div className="w-full max-w-md relative">
        {/* Background glow effects */}
        <div className="absolute -inset-0.5 bg-gradient-to-r from-[#F97316] to-[#10B981] rounded-2xl blur opacity-20 animate-pulse"></div>
        
        <div className="bg-[#0F1115]/80 backdrop-blur-xl border border-[#27272A] rounded-2xl p-8 shadow-2xl relative z-10">
          <div className="text-center mb-8">
            <h2 className="text-3xl font-black text-white tracking-tight">
              {isLogin ? 'BEM-VINDO' : 'CRIAR CONTA'}
            </h2>
            <p className="text-[#A1A1AA] mt-2">
              {isLogin ? 'Faça login para continuar na SportZone' : 'Junte-se a nós para a melhor experiência'}
            </p>
          </div>

          <form onSubmit={handleSubmit} className="space-y-5">
            {!isLogin && (
              <div className="relative">
                <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                  <User className="h-5 w-5 text-[#A1A1AA]" />
                </div>
                <input
                  type="text"
                  required
                  value={name}
                  onChange={(e) => setName(e.target.value)}
                  className="bg-[#18181B] border border-[#27272A] text-white text-sm rounded-lg focus:ring-[#F97316] focus:border-[#F97316] block w-full pl-10 p-3 outline-none transition-colors"
                  placeholder="Seu nome completo"
                />
              </div>
            )}

            <div className="relative">
              <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                <Mail className="h-5 w-5 text-[#A1A1AA]" />
              </div>
              <input
                type="email"
                required
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                className="bg-[#18181B] border border-[#27272A] text-white text-sm rounded-lg focus:ring-[#F97316] focus:border-[#F97316] block w-full pl-10 p-3 outline-none transition-colors"
                placeholder="seu@email.com"
              />
            </div>

            <div className="relative">
              <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                <Lock className="h-5 w-5 text-[#A1A1AA]" />
              </div>
              <input
                type="password"
                required
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                className="bg-[#18181B] border border-[#27272A] text-white text-sm rounded-lg focus:ring-[#F97316] focus:border-[#F97316] block w-full pl-10 p-3 outline-none transition-colors"
                placeholder="Sua senha secreta"
              />
            </div>

            {isLogin && (
              <div className="flex justify-end">
                <a href="#" className="text-sm text-[#F97316] hover:text-[#fb923c] hover:underline transition-colors">
                  Esqueceu a senha?
                </a>
              </div>
            )}

            <button
              type="submit"
              className="w-full text-white bg-gradient-to-r from-[#F97316] to-[#ea580c] hover:from-[#ea580c] hover:to-[#c2410c] focus:ring-4 focus:outline-none focus:ring-[#F97316]/50 font-bold rounded-lg text-sm px-5 py-3.5 text-center flex items-center justify-center gap-2 transition-all shadow-[0_0_15px_rgba(249,115,22,0.3)] hover:shadow-[0_0_25px_rgba(249,115,22,0.5)] transform hover:-translate-y-0.5"
            >
              {isLogin ? 'ENTRAR' : 'CADASTRAR'}
              <ArrowRight className="w-5 h-5" />
            </button>
          </form>

          <div className="mt-6 text-center text-sm text-[#A1A1AA]">
            {isLogin ? 'Ainda não tem conta? ' : 'Já possui uma conta? '}
            <button
              onClick={() => setIsLogin(!isLogin)}
              className="text-[#F97316] font-bold hover:underline hover:text-[#fb923c] transition-colors"
            >
              {isLogin ? 'Crie uma agora' : 'Faça login'}
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};
