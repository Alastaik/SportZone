import React, { useEffect, useState } from 'react';
import { Client } from '@stomp/stompjs';
// @ts-ignore
import SockJS from 'sockjs-client/dist/sockjs';
import { ArrowLeft, CheckCircle2, CircleDashed, Check } from 'lucide-react';

interface OrderTrackingScreenProps {
  pedidoId: string;
  onVoltar: () => void;
}

type OrderStatus = 'PROCESSANDO_PAGAMENTO' | 'SEPARANDO_ESTOQUE' | 'ENVIADO' | 'ENTREGUE';

const STATUS_ORDER: OrderStatus[] = [
  'PROCESSANDO_PAGAMENTO',
  'SEPARANDO_ESTOQUE',
  'ENVIADO',
  'ENTREGUE',
];

const STATUS_LABELS: Record<OrderStatus, string> = {
  PROCESSANDO_PAGAMENTO: 'Processando Pagamento',
  SEPARANDO_ESTOQUE: 'Separando Estoque',
  ENVIADO: 'Enviado',
  ENTREGUE: 'Entregue',
};

export const OrderTrackingScreen: React.FC<OrderTrackingScreenProps> = ({
  pedidoId,
  onVoltar,
}) => {
  const [currentStatus, setCurrentStatus] = useState<OrderStatus>('PROCESSANDO_PAGAMENTO');
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const socket = new SockJS('http://localhost:8081/ws');
    const stompClient = new Client({
      webSocketFactory: () => socket,
      debug: (str) => console.log(str),
      onConnect: () => {
        console.log('Conectado ao WebSocket');
        stompClient.subscribe(`/topic/pedidos/${pedidoId}`, (message) => {
          try {
            const data = JSON.parse(message.body);
            if (data.status) {
              setCurrentStatus(data.status as OrderStatus);
            }
          } catch (e) {
            console.error('Erro ao fazer parse da mensagem WebSocket', e);
          }
        });
      },
      onStompError: (frame) => {
        console.error('Erro no STOMP: ' + frame.headers['message']);
        setError('Falha de conexão com o rastreamento em tempo real.');
      },
    });

    stompClient.activate();

    return () => {
      stompClient.deactivate();
    };
  }, [pedidoId]);

  const getTimelineColors = (_status: OrderStatus, index: number) => {
    const currentIndex = STATUS_ORDER.indexOf(currentStatus);

    if (index < currentIndex || (currentStatus === 'ENTREGUE' && index === currentIndex)) {
      return 'text-[#10B981] border-[#10B981]';
    }
    if (index === currentIndex) {
      return 'text-[#F97316] border-[#F97316]';
    }
    return 'text-[#3F3F46] border-[#27272A]';
  };

  return (
    <div className="animate-in zoom-in-95 duration-500 flex flex-col items-center justify-center py-10">
      <div className="w-full max-w-md bg-[#0F1115] border border-[#27272A] rounded-2xl shadow-2xl p-8">
        
        {/* Cabeçalho */}
        <div className="mb-10 text-center border-b border-[#27272A]/50 pb-8">
          <h1 className="text-[#FFFFFF] text-2xl font-black uppercase tracking-tighter">
            Status do <span className="text-[#F97316]">Pedido</span>
          </h1>
          <p className="text-[#A1A1AA] bg-[#18181B] inline-block px-3 py-1 rounded-md font-mono mt-3 text-xs uppercase tracking-wider border border-[#27272A]">
            {pedidoId.split('-')[0]}
          </p>
        </div>

        {error && (
          <div className="mb-6 p-4 rounded-lg bg-red-500/10 border border-red-500/20 text-red-400 text-sm font-medium text-center">
            {error}
          </div>
        )}

        {/* Timeline */}
        <div className="relative border-l-2 border-[#27272A] ml-5 mb-12 space-y-12">
          {STATUS_ORDER.map((status, index) => {
            const isCompleted = index < STATUS_ORDER.indexOf(currentStatus) || (currentStatus === 'ENTREGUE' && index === STATUS_ORDER.indexOf(currentStatus));
            const isCurrent = index === STATUS_ORDER.indexOf(currentStatus) && currentStatus !== 'ENTREGUE';

            return (
              <div key={status} className="relative pl-10">
                {/* Node indicator */}
                <div
                  className={`absolute -left-[17px] top-0 flex h-8 w-8 items-center justify-center rounded-full bg-[#0F1115] border-2 transition-all duration-700 ${getTimelineColors(status, index)} ${isCurrent ? 'shadow-[0_0_15px_rgba(249,115,22,0.4)]' : ''}`}
                >
                  {isCompleted ? (
                    <Check className="h-4 w-4" strokeWidth={3} />
                  ) : isCurrent ? (
                    <div className="h-3 w-3 rounded-full bg-[#F97316] animate-pulse"></div>
                  ) : null}
                </div>

                {/* Text content */}
                <div className={`transition-all duration-500 ${isCompleted ? 'opacity-100' : isCurrent ? 'opacity-100' : 'opacity-40'}`}>
                  <h3 className={`font-black uppercase tracking-widest text-sm ${getTimelineColors(status, index).split(' ')[0]}`}>
                    {STATUS_LABELS[status]}
                  </h3>
                  
                  {isCurrent && (
                    <div className="flex items-center gap-2 mt-2 text-[#A1A1AA] text-xs font-medium">
                      <CircleDashed className="h-3.5 w-3.5 animate-spin text-[#F97316]" />
                      Atualizando em tempo real...
                    </div>
                  )}
                </div>
              </div>
            );
          })}
        </div>

        {/* Status Finalizado */}
        <div className={`transition-all duration-1000 ${currentStatus === 'ENTREGUE' ? 'opacity-100 translate-y-0' : 'opacity-0 translate-y-4 pointer-events-none'}`}>
          <div className="w-full flex items-center justify-center gap-2 py-4 rounded-xl font-bold uppercase tracking-wider text-sm bg-[#10B981]/10 text-[#10B981] border border-[#10B981]/20 mb-4">
            <CheckCircle2 className="h-5 w-5" />
            Pedido Entregue!
          </div>
        </div>

        {/* Botão Voltar */}
        <button
          onClick={onVoltar}
          className="w-full flex items-center justify-center py-4 rounded-xl font-bold uppercase tracking-wider text-sm bg-[#18181B] text-[#FFFFFF] border border-[#27272A] hover:bg-[#27272A] hover:border-[#3F3F46] transition-all"
        >
          <ArrowLeft className="h-5 w-5 mr-2" />
          Voltar ao Catálogo
        </button>

      </div>
    </div>
  );
};
