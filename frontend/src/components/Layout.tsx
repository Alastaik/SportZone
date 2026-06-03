import React from 'react';
import { ShoppingCart, Activity } from 'lucide-react';

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
            <div className="flex items-center cursor-pointer" onClick={() => window.location.href = '/'}>
              <Activity className="h-8 w-8 text-[#F97316] mr-2" />
              <span className="text-xl font-black uppercase tracking-tighter italic">SportZone</span>
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
