import axios from 'axios';

// Instância Axios
export const api = axios.create({
  baseURL: 'http://localhost:8080/api',
});

// Envia checkout
export const checkoutPedido = async (pedidoData: any) => {
  const response = await api.post('/pedidos/checkout', pedidoData);
  return response.data;
};
