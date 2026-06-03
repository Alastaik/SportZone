import React, { useState } from 'react';
import { checkoutPedido, Produto } from '../services/api';
import { Trash2, ArrowLeft, CreditCard, QrCode, ShieldCheck, Cpu } from 'lucide-react';

export interface CartItem {
  produto: Produto;
  quantidade: number;
}

interface CartProps {
  itens: CartItem[];
  onUpdateQuantidade: (produtoId: string, delta: number) => void;
  onRemover: (produtoId: string) => void;
  onVoltar: () => void;
  onCheckoutSuccess: (pedidoId: string) => void;
}

const PRODUCT_IMAGES: Record<string, string> = {
  'Tênis esportivo': '/images/Tenis_Esportivo.jpg',
  'Camisa Seleção': '/images/Camisa_Seleção.jpg',
  'Mochila esportiva': '/images/Mochila_esportiva.jpg',
  'Bola de Basquete': '/images/Bola_basquete.jpg',
  'Short esportivo': '/images/Short_Esportivo.jpg',
  'Meias esportivas': '/images/Meia_esportiva.jpg',
};

const DEFAULT_IMAGE = 'https://images.unsplash.com/photo-1515955656352-a1fa3ffcd111?auto=format&fit=crop&q=80&w=800';

export const Cart: React.FC<CartProps> = ({
  itens,
  onUpdateQuantidade,
  onRemover,
  onVoltar,
  onCheckoutSuccess,
}) => {
  const [metodoPagamento, setMetodoPagamento] = useState<'PIX' | 'CARTAO_CREDITO'>('PIX');
  const [loading, setLoading] = useState(false);

  const valorTotal = itens.reduce((acc, item) => acc + item.produto.preco * item.quantidade, 0);

  const handleFinalizar = async () => {
    if (itens.length === 0) return;
    setLoading(true);
    try {
      const response = await checkoutPedido({
        itens: itens.map((i) => ({ produtoId: i.produto.id, quantidade: i.quantidade })),
        metodoPagamento
      });
      onCheckoutSuccess(response.pedidoId);
    } catch (err) {
      console.error(err);
      alert('Erro ao processar pedido.');
      setLoading(false);
    }
  };

  return (
    <div className="animate-in slide-in-from-right-8 duration-500">
      {/* Header */}
      <div className="flex items-center gap-4 mb-8">
        <button
          onClick={onVoltar}
          className="p-2 rounded-lg bg-[#18181B] hover:bg-[#27272A] text-[#A1A1AA] hover:text-white transition-colors"
        >
          <ArrowLeft className="h-5 w-5" />
        </button>
        <h1 className="text-[#FFFFFF] text-3xl font-black uppercase tracking-tighter">
          Seu <span className="text-[#F97316]">Carrinho</span>
        </h1>
      </div>

      <div className="flex flex-col lg:flex-row gap-8">
        {/* Itens do Carrinho */}
        <div className="flex-1 space-y-4">
          {itens.length === 0 ? (
            <div className="bg-[#0F1115] border border-[#27272A] border-dashed rounded-2xl p-12 text-center">
              <p className="text-[#71717A] text-lg font-medium">Seu carrinho está vazio.</p>
              <button
                onClick={onVoltar}
                className="mt-6 text-[#F97316] font-bold uppercase tracking-wider text-sm hover:underline"
              >
                Voltar ao catálogo
              </button>
            </div>
          ) : (
            itens.map((item) => (
              <div
                key={item.produto.id}
                className="bg-[#0F1115] border border-[#27272A] rounded-2xl p-4 flex flex-col sm:flex-row sm:items-center justify-between gap-4 transition-colors hover:border-[#3F3F46]"
              >
                {/* Thumb + Title */}
                <div className="flex items-center gap-4 flex-1">
                  <div className="h-16 w-16 sm:h-20 sm:w-20 rounded-xl overflow-hidden bg-white/5 shrink-0 border border-[#27272A] p-2 flex items-center justify-center">
                    <img 
                      src={PRODUCT_IMAGES[item.produto.nome] || DEFAULT_IMAGE} 
                      alt={item.produto.nome} 
                      className="max-w-full max-h-full object-contain"
                    />
                  </div>
                  <div>
                    <h3 className="text-[#FFFFFF] font-bold text-lg line-clamp-1">{item.produto.nome}</h3>
                    <p className="text-[#A1A1AA] text-xs uppercase tracking-widest mt-1">{item.produto.categoria}</p>
                  </div>
                </div>
                
                {/* Controls */}
                <div className="flex items-center gap-6 justify-between sm:justify-end pl-20 sm:pl-0">
                  <div className="flex items-center bg-[#18181B] rounded-lg border border-[#27272A]">
                    <button
                      onClick={() => onUpdateQuantidade(item.produto.id, -1)}
                      className="px-3 py-1 text-[#A1A1AA] hover:text-white transition-colors text-lg font-medium"
                    >
                      -
                    </button>
                    <span className="w-8 text-center font-bold text-white">{item.quantidade}</span>
                    <button
                      onClick={() => onUpdateQuantidade(item.produto.id, 1)}
                      className="px-3 py-1 text-[#A1A1AA] hover:text-white transition-colors text-lg font-medium"
                    >
                      +
                    </button>
                  </div>
                  
                  <div className="text-right w-24">
                    <p className="text-[#F97316] font-black text-lg">
                      R$ {(item.produto.preco * item.quantidade).toFixed(2).replace('.', ',')}
                    </p>
                  </div>

                  <button
                    onClick={() => onRemover(item.produto.id)}
                    className="p-2 text-[#71717A] hover:text-red-500 hover:bg-red-500/10 rounded-lg transition-colors"
                    title="Remover item"
                  >
                    <Trash2 className="h-5 w-5" />
                  </button>
                </div>
              </div>
            ))
          )}
        </div>

        {/* Resumo e Pagamento */}
        {itens.length > 0 && (
          <div className="w-full lg:w-96 shrink-0">
            <div className="bg-[#0F1115] border border-[#27272A] rounded-2xl p-6 sticky top-24">
              <h2 className="text-xl font-black uppercase tracking-tight mb-6">Resumo</h2>
              
              <div className="flex justify-between items-center mb-6 text-lg border-b border-[#27272A] pb-6">
                <span className="text-[#A1A1AA] font-medium">Total:</span>
                <span className="text-[#FFFFFF] font-black text-2xl">
                  R$ {valorTotal.toFixed(2).replace('.', ',')}
                </span>
              </div>

              {/* Pagamento */}
              <div className="mb-8">
                <h3 className="text-[#A1A1AA] text-sm uppercase tracking-widest font-bold mb-4">
                  Método de Pagamento
                </h3>
                <div className="space-y-3">
                  <label
                    className={`flex items-center justify-between p-4 rounded-xl border cursor-pointer transition-all ${
                      metodoPagamento === 'PIX'
                        ? 'bg-[#F97316]/10 border-[#F97316] text-[#F97316]'
                        : 'bg-[#18181B] border-[#27272A] text-[#A1A1AA] hover:border-[#3F3F46]'
                    }`}
                  >
                    <div className="flex items-center gap-3">
                      <QrCode className="h-5 w-5" />
                      <span className="font-bold">PIX</span>
                    </div>
                    <input
                      type="radio"
                      name="pagamento"
                      value="PIX"
                      checked={metodoPagamento === 'PIX'}
                      onChange={() => setMetodoPagamento('PIX')}
                      className="hidden"
                    />
                  </label>

                  <label
                    className={`flex items-center justify-between p-4 rounded-xl border cursor-pointer transition-all ${
                      metodoPagamento === 'CARTAO_CREDITO'
                        ? 'bg-[#F97316]/10 border-[#F97316] text-[#F97316]'
                        : 'bg-[#18181B] border-[#27272A] text-[#A1A1AA] hover:border-[#3F3F46]'
                    }`}
                  >
                    <div className="flex items-center gap-3">
                      <CreditCard className="h-5 w-5" />
                      <span className="font-bold">Cartão de Crédito</span>
                    </div>
                    <input
                      type="radio"
                      name="pagamento"
                      value="CARTAO_CREDITO"
                      checked={metodoPagamento === 'CARTAO_CREDITO'}
                      onChange={() => setMetodoPagamento('CARTAO_CREDITO')}
                      className="hidden"
                    />
                  </label>
                </div>

                {/* GoF Strategy Badge Showcase */}
                <div className="mt-4 p-4 rounded-lg bg-[#18181B] border border-[#27272A] flex gap-3">
                  <Cpu className="h-5 w-5 text-[#3B82F6] shrink-0 mt-0.5" />
                  <div>
                    <h4 className="text-[#3B82F6] text-xs font-bold uppercase tracking-wider mb-1">
                      Arquitetura: Strategy Pattern
                    </h4>
                    <p className="text-[#71717A] text-xs font-mono leading-relaxed">
                      Sua escolha determina a instância em runtime: <br />
                      <span className="text-[#A1A1AA] font-bold">
                        {metodoPagamento === 'PIX' ? 'new PixStrategy()' : 'new CartaoCreditoStrategy()'}
                      </span>
                    </p>
                  </div>
                </div>
              </div>

              <button
                onClick={handleFinalizar}
                disabled={loading}
                className="w-full flex items-center justify-center py-4 rounded-xl font-bold uppercase tracking-wider text-sm bg-[#F97316] text-[#0A0A0A] hover:bg-[#EA580C] transition-all hover:shadow-lg hover:shadow-[#F97316]/20 disabled:opacity-50 disabled:cursor-not-allowed"
              >
                {loading ? (
                  <span className="animate-pulse">Processando...</span>
                ) : (
                  <>
                    <ShieldCheck className="h-5 w-5 mr-2" />
                    Finalizar Pedido
                  </>
                )}
              </button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};
