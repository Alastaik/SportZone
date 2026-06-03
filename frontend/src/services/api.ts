import axios from 'axios';

// Instância Axios configurada para o backend
export const api = axios.create({
  baseURL: 'http://localhost:8081/api',
});

// Tipos
export interface Produto {
  id: string;
  nome: string;
  descricao: string;
  preco: number;
  categoria: string;
  marca: string;
  quantidadeEstoque: number;
}

export interface ItemPedidoDTO {
  produtoId: string;
  quantidade: number;
}

export interface PedidoDTO {
  itens: ItemPedidoDTO[];
  metodoPagamento: 'CARTAO_CREDITO' | 'PIX';
}

export interface CheckoutResponse {
  mensagem: string;
  pedidoId: string;
  status: string;
}

// Busca todos os produtos do catálogo
export const fetchProdutos = async (): Promise<Produto[]> => {
  const response = await api.get('/produtos');
  return response.data;
};

// Envia o checkout — retorna 202 Accepted com o pedidoId
export const checkoutPedido = async (dto: PedidoDTO): Promise<CheckoutResponse> => {
  const response = await api.post('/pedidos', dto);
  return response.data;
};
