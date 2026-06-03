# SportZone - E-commerce Esportivo

> **Status:** Projeto Integrador N2

O **SportZone** é uma plataforma Web Full-Stack para venda de artigos esportivos. O projeto adota uma **Arquitetura Event-Driven** para gerenciar o ciclo de vida dos pedidos, utilizando processamento assíncrono para garantir escalabilidade, resolvendo gargalos comuns de transações síncronas em e-commerces.

---

## Equipe
* **Yuri de Sousa Silva** - Desenvolvedor fullstack / Arquitetura

---

## Stacks
* **Frontend:** React + Vite + TailwindCSS
* **Backend:** Java 21 + Spring Boot 3
* **Mensageria / Streams:** Apache Kafka + WebSockets (STOMP)
* **Banco de Dados:** PostgreSQL
* **Infraestrutura:** Docker & Docker Compose

---

## Artefatos da Etapa N2

Todos os documentos teóricos e de design exigidos estão centralizados neste repositório.

1. **Todos os Documentos estão na pasta Docs

---

## Funcionalidades de UI/UX (Frontend)
- **Design System Premium:** Tipografia e paleta de cores escuras/vibrantes estabelecidas.
- **Header Dinâmico:** Logo customizada via CSS puro e um Hero Banner imersivo com *fade-out* (fundo cinematográfico).
- **Busca em Tempo Real:** Filtro imediato de produtos por nome e categoria.
- **Feedback Interativo:** Uso de *toast notifications* para interações como adição ao carrinho.
- **Catálogo Animado:** Marquee de produtos com efeito de rolagem infinita.
- **Compra Rápida:** Botão para pular o carrinho e ir direto ao checkout.

---

## Padrões de Projeto (Design Patterns)

Para garantir um código limpo, testável e aderente aos princípios SOLID (especialmente Open/Closed Principle e Single Responsibility), aplicamos Padrões de Projeto bem definidos e de fácil localização no código-fonte:

### 1. Strategy Pattern (Processamento de Pagamentos)
Isola os algoritmos de pagamento, permitindo plugar novas formas de pagamento (ex: Boleto, Criptomoedas) sem alterar as regras de negócio de Pedidos ou o código base de pagamentos existentes.
* **Interface Base:** [`PagamentoStrategy`](./backend/src/main/java/com/sportzone/strategy/PagamentoStrategy.java)
* **Implementações (Estratégias):** 
  * [`PagamentoPixStrategy`](./backend/src/main/java/com/sportzone/strategy/PagamentoPixStrategy.java)
  * [`PagamentoCartaoStrategy`](./backend/src/main/java/com/sportzone/strategy/PagamentoCartaoStrategy.java)
* **Gerenciamento (Factory):** A classe [`PagamentoStrategyFactory`](./backend/src/main/java/com/sportzone/strategy/PagamentoStrategyFactory.java) injeta dinamicamente e resolve qual estratégia utilizar em tempo de execução, com base no método de pagamento selecionado pelo usuário.

### 2. State Pattern (Máquina de Estados de Pedidos - Arquitetural)
O fluxo do Pedido obedece a uma máquina de estados rigorosa: `PROCESSANDO_PAGAMENTO` -> `SEPARANDO_ESTOQUE` -> `ENVIADO` -> `ENTREGUE`. Toda transição de estados é controlada por Eventos no Apache Kafka, processados por Workers em background.

---

## Como Executar o Projeto (Ambiente Local)

### Pré-requisitos
* [Docker](https://www.docker.com/) e Docker Compose instalados.
* [Java 21](https://adoptium.net/) (JDK) instalado.
* [Node.js](https://nodejs.org/) (v18+) instalado.

### Passo 1: Subir a Infraestrutura (Banco e Mensageria)
suba os containers do PostgreSQL e do Apache Kafka executando o comando `docker-compose up -d` no seu terminal. Aguarde para que o broker do Kafka inicialize corretamente.

### Passo 2: Iniciar o Backend (Spring Boot)
Abra um novo terminal, navegue até a pasta do backend (`cd backend`) e inicie a aplicação com `mvn spring-boot:run` (ou `./mvnw spring-boot:run`). A API estará rodando em `http://localhost:8081`.

### Passo 3: Iniciar o Frontend (React)
Abra outro terminal, navegue até a pasta do frontend (`cd frontend`), instale as dependências com `npm install` e inicie o servidor com `npm run dev`. O Frontend estará acessível em `http://localhost:5173`.

---

## Validação de Fluxo E2E (WebSockets & Mensageria)

1. Acesse o Frontend em `http://localhost:5173`.
2. O sistema inicializa a Dashboard de Acompanhamento e automaticamente estabelece uma conexão WebSocket.
3. Ao clicar em **"Testar Checkout (Assíncrono)"**:
   * O Frontend realiza uma requisição HTTP POST para o Backend.
   * O Backend delega o processamento pesado para o Apache Kafka e devolve instantaneamente um HTTP 202 (Accepted).
   * Em background, os Workers (Kafka Consumers) simulam o tempo de API de terceiros (usando os Strategies de Pagamento) e avançam os estados da máquina.
   * O STOMP WebSocket trafega os eventos de mudança de status em tempo real de volta para a tela, onde o React reage e avança os "steps" da Timeline visual.
