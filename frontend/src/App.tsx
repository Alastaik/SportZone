import { useState } from 'react';
import { ProductCatalog } from './components/ProductCatalog';
import { Cart, CartItem } from './components/Cart';
import { OrderTrackingScreen } from './components/OrderTrackingScreen';
import { Layout } from './components/Layout';
import { Produto } from './services/api';

type View = 'catalog' | 'cart' | 'tracking';

function App() {
  const [view, setView] = useState<View>('catalog');
  const [cartItems, setCartItems] = useState<CartItem[]>([]);
  const [pedidoId, setPedidoId] = useState<string>('');

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

  const totalCartItems = cartItems.reduce((acc, item) => acc + item.quantidade, 0);

  return (
    <Layout 
      cartItemCount={totalCartItems} 
      onCartClick={() => setView('cart')}
      showCart={view === 'catalog'}
    >
      {view === 'catalog' && (
        <ProductCatalog 
          onAdicionarCarrinho={handleAdicionarCarrinho} 
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
    </Layout>
  );
}

export default App;
