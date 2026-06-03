import React, { useEffect, useState } from 'react';
import { fetchProdutos, Produto } from '../services/api';
import { Check, Plus, ShoppingBag, Footprints, Shirt, Backpack, Tags } from 'lucide-react';

interface CartItem {
  produto: Produto;
  quantidade: number;
}

interface ProductCatalogProps {
  onAdicionarCarrinho: (item: CartItem) => void;
}

const CATEGORIA_ICONS: Record<string, React.ReactNode> = {
  'Calçados': <Footprints className="h-4 w-4" />,
  'Camisas': <Shirt className="h-4 w-4" />,
  'Acessórios': <Backpack className="h-4 w-4" />,
  'Vestuário': <Tags className="h-4 w-4" />,
};

export const ProductCatalog: React.FC<ProductCatalogProps> = ({ onAdicionarCarrinho }) => {
  const [produtos, setProdutos] = useState<Produto[]>([]);
  const [loading, setLoading] = useState(true);
  const [addedId, setAddedId] = useState<string | null>(null);

  useEffect(() => {
    fetchProdutos()
      .then(setProdutos)
      .catch((err) => console.error('Erro ao carregar produtos:', err))
      .finally(() => setLoading(false));
  }, []);

  const handleAdicionar = (produto: Produto) => {
    onAdicionarCarrinho({ produto, quantidade: 1 });
    setAddedId(produto.id);
    setTimeout(() => setAddedId(null), 800);
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center py-20">
        <div className="text-[#F97316] text-xl font-bold uppercase tracking-wider animate-pulse flex items-center">
          <ShoppingBag className="h-6 w-6 mr-3 animate-bounce" />
          Carregando catálogo...
        </div>
      </div>
    );
  }

  return (
    <div className="animate-in fade-in duration-500">
      {/* Hero Section */}
      <div className="mb-10 text-center sm:text-left">
        <h1 className="text-[#FFFFFF] text-3xl sm:text-5xl font-black uppercase tracking-tighter">
          Nosso <span className="text-[#F97316]">Catálogo</span>
        </h1>
        <p className="text-[#A1A1AA] text-sm uppercase tracking-widest mt-2">
          Equipamentos e vestuário de alta performance
        </p>
      </div>

      {/* Product Grid */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
        {produtos.map((produto) => (
          <div
            key={produto.id}
            className="group bg-[#0F1115] border border-[#27272A] rounded-2xl p-6 flex flex-col justify-between transition-all duration-300 hover:border-[#4F4F56] hover:-translate-y-1.5 hover:shadow-2xl hover:shadow-[#F97316]/10"
          >
            {/* Categoria + Nome */}
            <div>
              <div className="flex items-center gap-2 mb-4">
                <div className="p-2 rounded-lg bg-[#18181B] text-[#F97316] group-hover:bg-[#F97316] group-hover:text-[#0F1115] transition-colors">
                  {CATEGORIA_ICONS[produto.categoria] || <Tags className="h-4 w-4" />}
                </div>
                <span className="text-[#A1A1AA] text-xs uppercase tracking-widest font-bold">
                  {produto.categoria}
                </span>
              </div>
              <h2 className="text-[#FFFFFF] text-xl font-black leading-tight mb-2 tracking-tight group-hover:text-[#F97316] transition-colors">
                {produto.nome}
              </h2>
              <p className="text-[#71717A] text-sm leading-relaxed mb-6 font-medium">
                {produto.descricao}
              </p>
            </div>

            {/* Preço + Botão */}
            <div className="flex items-center justify-between mt-auto pt-5 border-t border-[#27272A]/50">
              <div>
                <span className="text-[#FFFFFF] text-2xl font-black tracking-tight flex items-baseline">
                  <span className="text-sm text-[#F97316] mr-1">R$</span>
                  {produto.preco.toFixed(2).replace('.', ',')}
                </span>
                <p className="text-[#52525B] text-xs mt-1 font-mono">
                  {produto.quantidadeEstoque} UNIDADES
                </p>
              </div>
              <button
                onClick={() => handleAdicionar(produto)}
                className={`flex items-center justify-center h-10 w-10 sm:w-auto sm:px-4 rounded-xl font-bold uppercase text-xs tracking-wider transition-all duration-300 ${
                  addedId === produto.id
                    ? 'bg-[#10B981] text-white shadow-lg shadow-[#10B981]/20'
                    : 'bg-[#18181B] text-[#FFFFFF] hover:bg-[#F97316] hover:text-[#0A0A0A] border border-[#27272A] hover:border-[#F97316]'
                }`}
                title="Adicionar ao carrinho"
              >
                {addedId === produto.id ? (
                  <>
                    <Check className="h-5 w-5 sm:mr-1.5" />
                    <span className="hidden sm:inline">Adicionado</span>
                  </>
                ) : (
                  <>
                    <Plus className="h-5 w-5 sm:mr-1.5" />
                    <span className="hidden sm:inline">Adicionar</span>
                  </>
                )}
              </button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};
