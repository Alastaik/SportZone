import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080/api'
});

export const testKafkaConnection = async (msg: string) => {
  return await api.post('/test/send', msg);
};
