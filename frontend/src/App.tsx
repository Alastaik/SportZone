import { useState } from 'react';
import { ProductCatalog } from './components/ProductCatalog';
import { Cart, CartItem } from './components/Cart';
import { OrderTrackingScreen } from './components/OrderTrackingScreen';
import { Layout } from './components/Layout';
import { Auth } from './components/Auth';
import { Produto } from './services/api';
import { Toaster, toast } from 'react-hot-toast';

type View = 'catalog' | 'cart' | 'tracking' | 'auth';

function App() {
  const [view, setView] = useState<View>('catalog');
  const [cartItems, setCartItems] = useState<CartItem[]>([]);
  const [pedidoId, setPedidoId] = useState<string>('');
  const [userName, setUserName] = useState<string | null>(null);

  const handleAdicionarCarrinho = (item: { produto: Produto; quantidade: number }) => {
    setCartItems((prev) => {
      const existing = prev.find((i) => i.produto.id === item.produto.id);
      if (existing) {
        return prev.map((i) =>
          i.produto.id === item.produto.id
            ? { ...i, quantidade: i.quantidade + item.quantidade }
            : i
        );
      }
      return [...prev, item];
    });
    
    toast.success(`${item.produto.nome} adicionado!`, {
      style: {
        background: '#18181B',
        color: '#FFFFFF',
        border: '1px solid #27272A',
      },
      iconTheme: {
        primary: '#10B981',
        secondary: '#0F1115',
      },
    });
  };

  const handleUpdateQuantidade = (produtoId: string, delta: number) => {
    setCartItems((prev) =>
      prev
        .map((item) =>
          item.produto.id === produtoId
            ? { ...item, quantidade: Math.max(0, item.quantidade + delta) }
            : item
        )
        .filter((item) => item.quantidade > 0)
    );
  };

  const handleRemover = (produtoId: string) => {
    setCartItems((prev) => prev.filter((item) => item.produto.id !== produtoId));
  };

  const handleCheckoutSuccess = (id: string) => {
    setPedidoId(id);
    setCartItems([]);
    setView('tracking');
  };

  const handleComprar = (item: { produto: Produto; quantidade: number }) => {
    handleAdicionarCarrinho(item);
    setView('cart');
  };

  const totalCartItems = cartItems.reduce((acc, item) => acc + item.quantidade, 0);

  if (view === 'tracking') {
    return <OrderTrackingScreen pedidoId={pedidoId} onVoltar={() => setView('catalog')} />;
  }

  return (
    <Layout 
      cartItemCount={totalCartItems} 
      onCartClick={() => setView('cart')}
      onAuthClick={() => setView('auth')}
      userName={userName}
      showCart={view !== 'auth'}
    >
      <Toaster position="bottom-right" />
      {view === 'catalog' && (
        <ProductCatalog 
          onAdicionarCarrinho={handleAdicionarCarrinho} 
          onComprar={handleComprar}
        />
      )}
      {view === 'cart' && (
        <Cart 
          itens={cartItems} 
          onUpdateQuantidade={handleUpdateQuantidade}
          onRemover={handleRemover}
          onVoltar={() => setView('catalog')}
          onCheckoutSuccess={handleCheckoutSuccess}
        />
      )}
      {view === 'tracking' && (
        <OrderTrackingScreen 
          pedidoId={pedidoId} 
          onVoltar={() => setView('catalog')}
        />
      )}
      {view === 'auth' && (
        <Auth 
          onLoginSuccess={(name) => {
            setUserName(name);
            setView('catalog');
            toast.success(`Bem-vindo, ${name}!`, {
              style: {
                background: '#18181B',
                color: '#FFFFFF',
                border: '1px solid #27272A',
              },
              iconTheme: {
                primary: '#10B981',
                secondary: '#0F1115',
              },
            });
          }}
        />
      )}
    </Layout>
  );
}

export default App;
