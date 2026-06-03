import React from 'react';
import { ShoppingCart } from 'lucide-react';

interface LayoutProps {
  children: React.ReactNode;
  cartItemCount?: number;
  onCartClick?: () => void;
  onAuthClick?: () => void;
  userName?: string | null;
  showCart?: boolean;
}

export const Layout: React.FC<LayoutProps> = ({ children, cartItemCount = 0, onCartClick, onAuthClick, userName, showCart = true }) => {
  return (
    <div className="min-h-screen bg-[#0A0A0A] font-sans text-white flex flex-col">
      {/* Navbar Persistente */}
      <header className="sticky top-0 z-50 bg-[#0F1115]/90 backdrop-blur-md border-b border-[#27272A] shadow-sm">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center h-16">
          {/* Logo */}
          <div className="flex items-center gap-4 cursor-pointer" onClick={() => window.scrollTo(0,0)}>
            {/* Ícone Geométrico baseado na imagem */}
            <div className="flex -space-x-2 mt-1">
              <div className="w-4 h-7 bg-white -skew-x-12 rounded-sm"></div>
              <div className="w-4 h-7 bg-[#F97316] -skew-x-12 rounded-sm translate-y-1.5"></div>
            </div>
            <span className="text-[#FFFFFF] text-3xl font-black italic tracking-tighter uppercase">
              SPORT<span className="text-[#F97316]">ZONE</span>
            </span>
          </div>  
            
            <div className="flex items-center gap-4">
              {userName ? (
                <div className="flex items-center gap-2 px-3 py-1.5 rounded-full bg-[#18181B] border border-[#27272A] text-[#FFFFFF]">
                  <span className="text-sm font-medium hidden sm:block">Olá, {userName.split(' ')[0]}</span>
                  <div className="w-6 h-6 rounded-full bg-[#10B981] flex items-center justify-center text-[#0A0A0A]">
                    <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
                  </div>
                </div>
              ) : (
                onAuthClick && (
                  <button
                    onClick={onAuthClick}
                    className="flex items-center gap-2 px-3 py-1.5 rounded-full bg-[#18181B] border border-[#27272A] text-[#A1A1AA] hover:text-[#FFFFFF] hover:border-[#F97316] transition-all"
                  >
                    <span className="text-sm font-medium hidden sm:block">Entrar</span>
                    <div className="w-6 h-6 rounded-full bg-[#27272A] flex items-center justify-center">
                      <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
                    </div>
                  </button>
                )
              )}
              {showCart && onCartClick && (
                <button 
                  onClick={onCartClick}
                  className="relative p-2 text-[#A1A1AA] hover:text-[#FFFFFF] transition-colors"
                >
                  <ShoppingCart className="h-6 w-6" />
                  {cartItemCount > 0 && (
                    <span className="absolute top-0 right-0 inline-flex items-center justify-center px-1.5 py-0.5 text-xs font-bold leading-none text-white transform translate-x-1/4 -translate-y-1/4 bg-[#F97316] rounded-full">
                      {cartItemCount}
                    </span>
                  )}
                </button>
              )}
            </div>
          </div>
        </div>
      </header>

      {/* Main Content */}
      <main className="flex-1 max-w-7xl w-full mx-auto p-4 sm:p-6 lg:p-8">
        {children}
      </main>
    </div>
  );
};
