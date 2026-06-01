import axios from 'axios';

// Instância base do Axios apontando para o backend Spring Boot
export const api = axios.create({
  baseURL: 'http://localhost:8080/api',
});

/**
 * Função para enviar o checkout.
 * O Backend deve retornar um HTTP 202 (Accepted) imediatamente,
 * delegando o processamento pesado para o Kafka e workers.
 */
export const checkoutPedido = async (pedidoData: any) => {
  const response = await api.post('/pedidos/checkout', pedidoData);
  return response.data;
};
