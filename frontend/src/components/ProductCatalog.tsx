import React, { useEffect, useState } from 'react';
import { fetchProdutos, Produto } from '../services/api';
import { Check, Plus, ShoppingBag, Footprints, Shirt, Backpack, Tags, Search } from 'lucide-react';

interface CartItem {
  produto: Produto;
  quantidade: number;
}

interface ProductCatalogProps {
  onAdicionarCarrinho: (item: CartItem) => void;
  onComprar: (item: CartItem) => void;
}

const CATEGORIA_ICONS: Record<string, React.ReactNode> = {
  'Calçados': <Footprints className="h-4 w-4" />,
  'Camisas': <Shirt className="h-4 w-4" />,
  'Acessórios': <Backpack className="h-4 w-4" />,
  'Vestuário': <Tags className="h-4 w-4" />,
};

// Mapa genérico de imagens baseado no nome do produto
const PRODUCT_IMAGES: Record<string, string> = {
  'Tênis esportivo': '/images/Tenis_Esportivo.jpg',
  'Camisa Seleção': '/images/Camisa_Seleção.jpg',
  'Mochila esportiva': '/images/Mochila_esportiva.jpg',
  'Bola de Basquete': '/images/Bola_basquete.jpg',
  'Short esportivo': '/images/Short_Esportivo.jpg',
  'Meias esportivas': '/images/Meia_esportiva.jpg',
};

const DEFAULT_IMAGE = 'https://images.unsplash.com/photo-1515955656352-a1fa3ffcd111?auto=format&fit=crop&q=80&w=800';

export const ProductCatalog: React.FC<ProductCatalogProps> = ({ onAdicionarCarrinho, onComprar }) => {
  const [produtos, setProdutos] = useState<Produto[]>([]);
  const [loading, setLoading] = useState(true);
  const [addedId, setAddedId] = useState<string | null>(null);
  const [searchTerm, setSearchTerm] = useState('');

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

  const filteredProdutos = produtos.filter((p) =>
    p.nome.toLowerCase().includes(searchTerm.toLowerCase()) ||
    p.categoria.toLowerCase().includes(searchTerm.toLowerCase())
  );

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
    <div className="animate-in fade-in duration-500 pb-20">
      {/* Hero Banner Header */}
      <div className="relative w-full h-[350px] sm:h-[500px] mb-12 flex items-center justify-center">
        <div className="absolute inset-0 z-0 overflow-hidden rounded-3xl bg-[#0A0A0A]">
          <img 
            src="/images/cabecalho.jpg" 
            alt="Supere seus limites" 
            className="w-full h-full object-cover object-[center_30%] grayscale blur-[2px] opacity-60" 
          />
          {/* Fades intensos nas bordas */}
          <div className="absolute inset-0 bg-[radial-gradient(ellipse_at_center,_var(--tw-gradient-stops))] from-transparent via-[#0A0A0A]/40 to-[#0A0A0A]/90"></div>
          <div className="absolute inset-0 bg-gradient-to-b from-[#0A0A0A] via-transparent to-[#0A0A0A]"></div>
          <div className="absolute inset-0 bg-gradient-to-r from-[#0A0A0A] via-transparent to-[#0A0A0A]"></div>
        </div>
        
        <div className="relative z-10 text-center px-4">
          <h1 className="text-5xl sm:text-7xl font-black italic tracking-tighter uppercase text-white drop-shadow-2xl">
            Supere Seus <span className="text-[#F97316]">Limites</span>
          </h1>
          <p className="mt-4 text-[#A1A1AA] text-sm sm:text-lg uppercase tracking-widest font-bold drop-shadow-md">
            O equipamento certo para a sua performance
          </p>
        </div>
      </div>

      {/* Title & Search */}
      <div className="mb-10 flex flex-col sm:flex-row sm:items-end justify-between gap-6">
        <div>
          <h1 className="text-[#FFFFFF] text-4xl sm:text-5xl font-black uppercase tracking-tighter">
            <span className="text-[#F97316]">Catálogo</span>
          </h1>
          <p className="text-[#A1A1AA] text-sm uppercase tracking-widest mt-2">
            Equipamentos e vestuário de alta performance
          </p>
        </div>

        <div className="relative w-full sm:w-72">
          <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
            <Search className="h-5 w-5 text-[#71717A]" />
          </div>
          <input
            type="text"
            placeholder="Buscar produtos..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="w-full bg-[#0F1115] border border-[#27272A] text-white rounded-xl pl-10 pr-4 py-3 focus:outline-none focus:border-[#F97316] focus:ring-1 focus:ring-[#F97316] transition-colors"
          />
        </div>
      </div>

      {/* Product Grid */}
      {filteredProdutos.length === 0 ? (
        <div className="text-center py-20 text-[#71717A] text-lg font-medium">
          Nenhum produto encontrado.
        </div>
      ) : (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
          {filteredProdutos.map((produto) => (
            <div
              key={produto.id}
              className="group bg-[#0F1115] border border-[#27272A] rounded-2xl overflow-hidden flex flex-col justify-between transition-all duration-300 hover:border-[#4F4F56] hover:-translate-y-1.5 hover:shadow-2xl hover:shadow-[#F97316]/10"
            >
              {/* Product Image */}
              <div className="relative h-64 w-full overflow-hidden bg-[#18181B] p-4 flex items-center justify-center">
                <img
                  src={PRODUCT_IMAGES[produto.nome] || DEFAULT_IMAGE}
                  alt={produto.nome}
                  className="w-full h-full object-contain transition-transform duration-700 group-hover:scale-110 opacity-90 group-hover:opacity-100"
                />
                <div className="absolute top-3 left-3 flex items-center gap-2">
                  <div className="p-1.5 rounded-lg bg-[#0F1115]/80 backdrop-blur-md text-[#F97316] border border-[#27272A]">
                    {CATEGORIA_ICONS[produto.categoria] || <Tags className="h-4 w-4" />}
                  </div>
                </div>
              </div>

              <div className="p-6 flex flex-col flex-1">
                {/* Categoria + Nome */}
                <div className="mb-4">
                  <span className="text-[#A1A1AA] text-xs uppercase tracking-widest font-bold block mb-1">
                    {produto.categoria}
                  </span>
                  <h2 className="text-[#FFFFFF] text-xl font-black leading-tight mb-2 tracking-tight group-hover:text-[#F97316] transition-colors line-clamp-1">
                    {produto.nome}
                  </h2>
                  <p className="text-[#71717A] text-sm leading-relaxed font-medium line-clamp-2">
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
                  <div className="flex gap-2">
                    <button
                      onClick={() => handleAdicionar(produto)}
                      className={`flex items-center justify-center h-10 w-10 rounded-xl font-bold uppercase text-xs tracking-wider transition-all duration-300 ${
                        addedId === produto.id
                          ? 'bg-[#10B981] text-white shadow-lg shadow-[#10B981]/20'
                          : 'bg-[#18181B] text-[#FFFFFF] hover:bg-[#27272A] border border-[#27272A]'
                      }`}
                      title="Adicionar ao carrinho"
                    >
                      {addedId === produto.id ? <Check className="h-5 w-5" /> : <Plus className="h-5 w-5" />}
                    </button>
                    <button
                      onClick={() => onComprar({ produto, quantidade: 1 })}
                      className="flex items-center justify-center h-10 px-4 rounded-xl font-bold uppercase text-xs tracking-wider transition-all duration-300 bg-[#F97316] text-[#0A0A0A] hover:bg-[#EA580C] shadow-lg shadow-[#F97316]/20"
                    >
                      Comprar
                    </button>
                  </div>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Marquee Footer */}
      <div className="mt-20 border-t border-[#27272A] pt-10 overflow-hidden relative">
        <div className="absolute inset-y-0 left-0 w-24 bg-gradient-to-r from-[#0A0A0A] to-transparent z-10 pointer-events-none"></div>
        <div className="absolute inset-y-0 right-0 w-24 bg-gradient-to-l from-[#0A0A0A] to-transparent z-10 pointer-events-none"></div>
        
        <div className="flex w-max animate-[marquee_30s_linear_infinite] hover:[animation-play-state:paused]">
          {/* Duplicated for infinite scroll effect */}
          {[...produtos, ...produtos, ...produtos].map((p, i) => (
            <div key={`${p.id}-${i}`} className="relative h-32 w-48 mx-4 rounded-xl overflow-hidden grayscale opacity-40 hover:grayscale-0 hover:opacity-100 transition-all duration-500 cursor-pointer bg-[#18181B] p-2 flex items-center justify-center">
              <img src={PRODUCT_IMAGES[p.nome] || DEFAULT_IMAGE} alt={p.nome} className="max-w-full max-h-full object-contain" />
              <div className="absolute inset-0 bg-gradient-to-t from-black/80 via-transparent to-transparent flex items-end p-3">
                <span className="text-white text-xs font-bold truncate">{p.nome}</span>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};
