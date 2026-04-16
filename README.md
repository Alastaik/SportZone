# SportZone - E-commerce Esportivo

> **Status:** Projeto Integrador N1

O **SportZone** é uma plataforma Web Full-Stack para venda de artigos esportivos. O projeto adota uma **Arquitetura Event-Driven** para gerenciar o ciclo de vida dos pedidos, utilizando processamento assíncrono para garantir escalabilidade, resolvendo gargalos comuns de transações síncronas em e-commerces.

---

## Equipe
* **Yuri de Sousa Silva** - Desenvolvedor fullstack / Arquitetura
---

## Stacks
* **Frontend:** React + Vite + TailwindCSS
* **Backend:** Java 21 + Spring Boot 3
* **Mensageria / Streams:** Apache Kafka
* **Banco de Dados:** PostgreSQL
* **Infraestrutura:** Docker & Docker Compose

---

## Artefatos da Etapa N1

Todos os documentos teóricos e de design exigidos na N1 estão centralizados neste repositório.

1. **Relatório Técnico e Design System:** [Acessar PDF na pasta `/docs`](./docs/Relatorio_DesignSystem_Artefatos_SportZone.pdf)
3. **Modelagem de Domínio:** Incluída no relatório técnico.
4. **Protótipo de Alta Fidelidade (UI/UX):** [Acessar no Figma] https://glory-hello-96058735.figma.site/
---

## Como Executar o Projeto (Ambiente Local)

Para a entrega da N1, configuramos o "Hello World" arquitetural. **React ➔ Spring Boot ➔ Kafka Broker**.

### Pré-requisitos
* [Docker](https://www.docker.com/) e Docker Compose instalados.
* [Java 21](https://adoptium.net/) (JDK) instalado.
* [Node.js](https://nodejs.org/) (v18+) instalado.

### Passo 1: Subir a Infraestrutura (Banco e Mensageria)
Na raiz do projeto, suba os containers do PostgreSQL e do Apache Kafka executando o comando `docker-compose up -d` no seu terminal. Aguarde para que o broker do Kafka inicialize corretamente.

### Passo 2: Iniciar o Backend (Spring Boot)
Abra um novo terminal, navegue até a pasta do backend e inicie a aplicação executando `mvn spring-boot:run` (ou `./mvnw spring-boot:run`). A API estará rodando em http://localhost:8080.

### Passo 3: Iniciar o Frontend (React)
Abra outro terminal, instale as dependências com `npm install` e inicie o servidor com `npm run dev`. O Frontend estará acessível em http://localhost:5173.

---

## Prova de Conceito (Validação N1)

Para validar a exigência de comunicação entre Front, Back e Mensageria:

1. Acesse o Frontend no navegador.
2. Clicar no botão **"Testar Conexão SportZone"**.
3. O Frontend fará uma requisição REST (`POST /api/test/send`) para o Backend.
4. O Spring Boot receberá a requisição, retornará um HTTP 202 (Accepted) para o Front e publicará o evento no tópico `sportzone-test-topic` do Kafka.
5. Verifique o **terminal do Backend**. Você verá o log gerado pelo `@KafkaListener`.
docker-compose up -d
