import React from 'react';
import { ShoppingCart } from 'lucide-react';

interface LayoutProps {
  children: React.ReactNode;
  cartItemCount?: number;
  onCartClick?: () => void;
  showCart?: boolean;
}

export const Layout: React.FC<LayoutProps> = ({ children, cartItemCount = 0, onCartClick, showCart = true }) => {
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
      </header>

      {/* Main Content */}
      <main className="flex-1 max-w-7xl w-full mx-auto p-4 sm:p-6 lg:p-8">
        {children}
      </main>
    </div>
  );
};
