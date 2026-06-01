import React, { useEffect, useState } from 'react';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { checkoutPedido } from '../services/api';

type OrderStatus = 'PROCESSANDO_PAGAMENTO' | 'SEPARANDO_ESTOQUE' | 'ENVIADO' | 'ENTREGUE';

const STATUS_ORDER: OrderStatus[] = [
  'PROCESSANDO_PAGAMENTO',
  'SEPARANDO_ESTOQUE',
  'ENVIADO',
  'ENTREGUE'
];

interface OrderTrackingScreenProps {
  orderId?: string;
}

export const OrderTrackingScreen: React.FC<OrderTrackingScreenProps> = ({ orderId = 'PEDIDO-999' }) => {
  const [currentStatus, setCurrentStatus] = useState<OrderStatus>('PROCESSANDO_PAGAMENTO');
  const [isProcessingCheckout, setIsProcessingCheckout] = useState(false);

  useEffect(() => {
    // 1. Configuração do SockJS apontando para o endpoint do Spring Boot
    const socket = new SockJS('http://localhost:8080/ws');
    
    // 2. Configuração do Cliente STOMP
    const stompClient = new Client({
      webSocketFactory: () => socket,
      reconnectDelay: 5000,
      onConnect: () => {
        console.log('✅ Conectado ao WebSocket via STOMP');
        
        // 3. Inscrição no Tópico do Pedido Específico
        stompClient.subscribe(`/topic/pedidos/${orderId}`, (message) => {
          if (message.body) {
            try {
              const data = JSON.parse(message.body);
              if (data.status && STATUS_ORDER.includes(data.status)) {
                // Atualiza o estado local reativamente quando a mensagem do worker/kafka chega
                setCurrentStatus(data.status as OrderStatus);
              }
            } catch (error) {
              console.error('Erro ao fazer parse da mensagem WebSocket', error);
            }
          }
        });
      },
      onStompError: (frame) => {
        console.error('Broker reportou erro: ' + frame.headers['message']);
        console.error('Detalhes: ' + frame.body);
      },
    });

    stompClient.activate();

    // Cleanup: Desconecta ao desmontar o componente
    return () => {
      stompClient.deactivate();
    };
  }, [orderId]);

  const handleSimulateCheckout = async () => {
    setIsProcessingCheckout(true);
    try {
      // Dispara o HTTP POST (Retorna 202 Accepted)
      await checkoutPedido({ orderId, items: [] });
      // A UI não é atualizada aqui! 
      // Ela reage apenas às mensagens do WebSocket garantindo fidelidade ao processamento real.
    } catch (error) {
      console.error('Erro no checkout', error);
    } finally {
      setIsProcessingCheckout(false);
    }
  };

  // Helper para definir as cores baseadas na posição atual do pedido
  const getTimelineColors = (status: OrderStatus, index: number) => {
    const currentIndex = STATUS_ORDER.indexOf(currentStatus);
    
    if (index < currentIndex) {
      return 'text-[#10B981] border-[#10B981]'; // Passado: Verde (Entregue/Sucesso)
    }
    if (index === currentIndex) {
      return 'text-[#F97316] border-[#F97316] animate-pulse'; // Atual: Electric Orange + Pulsante
    }
    return 'text-[#A1A1AA] border-[#27272A]'; // Futuro: Cinza (Muted)
  };

  return (
    <div className="min-h-screen bg-[#0A0A0A] p-4 flex flex-col items-center justify-center font-sans">
      {/* Container Principal (Painel) */}
      <div className="w-full max-w-md bg-[#0F1115] border border-[#27272A] rounded-xl shadow-2xl p-6 sm:p-8">
        
        {/* Cabeçalho */}
        <div className="mb-8 border-b border-[#27272A] pb-6">
          <h1 className="text-[#FFFFFF] text-3xl font-black uppercase tracking-tighter skew-x-[-10deg]">
            Status do <span className="text-[#F97316]">Pedido</span>
          </h1>
          <p className="text-[#A1A1AA] font-medium mt-2 text-sm uppercase">
            ID: {orderId}
          </p>
        </div>

        {/* Timeline Vertical */}
        <div className="relative border-l-2 border-[#27272A] ml-4 mb-10 space-y-10">
          {STATUS_ORDER.map((status, index) => {
            const isCompleted = index < STATUS_ORDER.indexOf(currentStatus);
            const isCurrent = index === STATUS_ORDER.indexOf(currentStatus);
            
            return (
              <div key={status} className="relative pl-8">
                {/* Indicador / Bolinha da Timeline */}
                <span 
                  className={`absolute -left-[11px] top-0 flex h-5 w-5 items-center justify-center rounded-full bg-[#0F1115] border-2 transition-colors duration-500 ${getTimelineColors(status, index)}`}
                >
                  {isCompleted ? (
                    <svg className="w-3 h-3 text-[#10B981]" fill="currentColor" viewBox="0 0 20 20">
                      <path fillRule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clipRule="evenodd" />
                    </svg>
                  ) : isCurrent ? (
                    <span className="h-2 w-2 rounded-full bg-[#F97316]"></span>
                  ) : null}
                </span>

                {/* Textos da Timeline */}
                <h3 className={`font-bold uppercase tracking-wide text-sm transition-colors duration-500 ${getTimelineColors(status, index).split(' ')[0]}`}>
                  {status.replace('_', ' ')}
                </h3>
                {isCurrent && (
                  <p className="text-[#A1A1AA] text-xs mt-1.5 font-medium animate-pulse">
                    Atualizando status em tempo real...
                  </p>
                )}
              </div>
            );
          })}
        </div>

        {/* Botão de Ação Primário (CTA) */}
        <div className="mt-8 flex justify-center">
          <button 
            onClick={handleSimulateCheckout}
            disabled={isProcessingCheckout}
            className="group relative w-full bg-[#F97316] text-black font-black uppercase tracking-tighter skew-x-[-10deg] px-6 py-4 transition-all duration-200 hover:bg-[#EA580C] hover:-translate-y-1 shadow-[4px_4px_0px_0px_#27272A] hover:shadow-[6px_6px_0px_0px_#27272A] active:translate-y-0 active:shadow-[2px_2px_0px_0px_#27272A] disabled:opacity-50 disabled:cursor-not-allowed"
          >
            {/* O texto interno sofre um skew invertido para ficar reto (legível) enquanto o botão fica inclinado */}
            <span className="block skew-x-[10deg] tracking-wide">
              {isProcessingCheckout ? 'Aguardando 202...' : 'Testar Checkout (Assíncrono)'}
            </span>
          </button>
        </div>

      </div>
    </div>
  );
};
