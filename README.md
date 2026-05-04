# FinTrack API

![Java](https://img.shields.io/badge/Java-21-orange?style=flat-square&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.0.6-brightgreen?style=flat-square&logo=springboot)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue?style=flat-square&logo=postgresql)
![Apache Kafka](https://img.shields.io/badge/Apache_Kafka-3.x-black?style=flat-square&logo=apachekafka)
![AWS](https://img.shields.io/badge/AWS-S3_·_SES-FF9900?style=flat-square&logo=amazonaws)
![License](https://img.shields.io/badge/License-MIT-lightgrey?style=flat-square)

API de gestão financeira pessoal com motor de orçamento inteligente, recomendações de investimento por perfil, streaming de eventos via Kafka e geração de relatórios PDF enviados por e-mail.

---

## Arquitetura

```
┌──────────────────────────────────────────────────────────────────────┐
│                            FINTRACK API                              │
│                                                                      │
│   ┌──────────┐   ┌──────────────────────────────────────────────┐    │
│   │  Client  │─▶│            Spring Boot 4.x (MVC)              │     │
│   └──────────┘   │                                              │    │
│                  │  /auth  /users  /expenses  /debts  /goals    │    │
│                  │  /budget  /market  /recommendations          │    │
│                  │  /webhooks  /reports                         │    │
│                  └─────────────────────┬────────────────────────┘    │
│                                        │                             │
│                  ┌─────────────────────▼────────────────────────┐    │
│                  │            Apache Kafka                      │    │
│                  │  fintrack.goal.achieved                      │    │
│                  │  fintrack.market.quote.updated               │    │
│                  └──────────┬───────────────────────────────────┘    │
│                             │                                        │
│              ┌──────────────┼──────────────┐                         │
│              ▼              ▼              ▼                         │
│   ┌──────────────┐  ┌────────────┐  ┌──────────────┐                 │
│   │  WebhookSvc  │  │  EmailSvc  │  │  MarketSvc   │                 │
│   │ (HMAC-SHA256)│  │ (AWS SES)  │  │   (BRAPI)    │                 │
│   └──────────────┘  └────────────┘  └──────────────┘                 │
│                                                                      │
│   ┌──────────────┐  ┌────────────┐  ┌──────────────┐                 │
│   │  PostgreSQL  │  │   AWS S3   │  │  Swagger UI  │                 │
│   │  + Flyway    │  │ (Reports)  │  │  /swagger-ui │                 │
│   └──────────────┘  └────────────┘  └──────────────┘                 │
└──────────────────────────────────────────────────────────────────────┘
```

---

## Stack

| Tecnologia | Versão | Justificativa |
|---|---|---|
| Java | 21 | Records, pattern matching, virtual threads |
| Spring Boot | 4.0.6 | Versão mais recente, suporte a WebMVC moderno |
| Spring Security 6 | incluso | JWT stateless, SecurityFilterChain fluente |
| Spring Data JPA | incluso | Derived queries sem boilerplate |
| PostgreSQL | 16 | Banco relacional robusto para dados financeiros |
| Flyway | incluso | Versionamento de schema com rastreabilidade |
| Apache Kafka | 3.x | Event bus desacoplado para notificações e webhooks |
| AWS S3 + SES | SDK v2 | Armazenamento de relatórios e envio de e-mails transacionais |
| iText 7 | 7.2.5 | Geração de PDF com layout programático |
| com.auth0:java-jwt | 4.4.0 | JWT leve sem dependências desnecessárias |
| springdoc-openapi | 2.x | Swagger UI gerado automaticamente |
| Testcontainers | 1.20.4 | Testes de integração com PostgreSQL real |
| JaCoCo | 0.8.12 | Cobertura mínima de 80% nos services |
| Lombok | incluso | Eliminação de boilerplate sem perda de legibilidade |

---

## Setup Local

### Pré-requisitos

- Java 21+
- Docker + Docker Compose
- Maven 3.9+

### 1. Clonar o repositório

```bash
git clone https://github.com/seu-usuario/fintrack.git
cd fintrack
```

### 2. Configurar variáveis de ambiente

```bash
cp .env.example .env
```

Edite o `.env` com seus valores (ver seção [Variáveis de Ambiente](#variáveis-de-ambiente)).

### 3. Subir a infraestrutura local

```bash
docker compose up -d
```

Isso sobe PostgreSQL, Kafka, Zookeeper e Kafka UI.

### 4. Rodar a aplicação

```bash
./mvnw spring-boot:run
```

A API estará disponível em `http://localhost:8080`.

Swagger UI: `http://localhost:8080/swagger-ui.html`

Kafka UI: `http://localhost:8090`

---

## Variáveis de Ambiente

| Variável | Padrão | Descrição |
|---|---|---|
| `JWT_SECRET` | `dev-secret-change-in-production` | Segredo para assinar tokens JWT |
| `BRAPI_TOKEN` | _(vazio)_ | Token da API BRAPI (opcional no plano gratuito) |
| `BRAPI_TICKERS` | `PETR4,VALE3,ITUB4,...` | Tickers monitorados separados por vírgula |
| `AWS_REGION` | `us-east-1` | Região AWS |
| `AWS_S3_BUCKET` | `fintrack-reports` | Bucket S3 para relatórios PDF |
| `AWS_SES_FROM_EMAIL` | `noreply@fintrack.com` | E-mail remetente SES (deve estar verificado) |

As credenciais AWS são obtidas automaticamente via **default credential provider chain** (variáveis de ambiente `AWS_ACCESS_KEY_ID` e `AWS_SECRET_ACCESS_KEY`, IAM role, etc.).

---

## Endpoints

| Módulo | Base | Destaques |
|---|---|---|
| Auth | `/auth` | `POST /login`, `POST /register` |
| Users | `/api/v1/users` | `GET /me`, `PUT /me`, `DELETE /me` |
| Expenses | `/api/v1/expenses` | CRUD + soft delete |
| Debts | `/api/v1/debts` | CRUD + `PATCH /{id}/payment` |
| Goals | `/api/v1/goals` | CRUD + `PATCH /{id}/contribute` + projeções |
| Budget | `/api/v1/budget` | `/summary`, `/history`, `/debt-projection`, `/goals-projection` |
| Market | `/api/v1/market` | `/quotes`, `/quotes/{ticker}` — sempre do banco |
| Recommendations | `/api/v1/recommendations` | `GET /` + `POST /generate` |
| Webhooks | `/api/v1/webhooks` | CRUD + `POST /{id}/receive` (HMAC) |
| Reports | `/api/v1/reports` | `POST /generate` → 202 Accepted |

---

## Design Decisions

### Estrutura por domínio, não por camada
Cada módulo contém sua própria `entity/`, `repository/`, `service/`, `controller/` e `dtos/`. Isso facilita a leitura e evolução isolada de cada funcionalidade sem navegar entre pacotes distantes.

### Java Records para DTOs
Todos os DTOs são Records do Java 21. Imutáveis por padrão, sem boilerplate, com validação inline no construtor canônico.

### Sem mappers — Response DTOs com construtor da entidade
Em vez de usar MapStruct ou conversões manuais espalhadas, cada `ResponseDTO` tem um construtor que recebe a entidade diretamente. Mantém a lógica de conversão encapsulada no próprio DTO.

### BudgetEngine como classe pura
O `BudgetEngine` não tem anotações Spring. É uma classe com método `calculate(income, expenses, debts)` que retorna um `BudgetSummaryDTO`. Isso facilita testes unitários sem necessidade de contexto Spring e deixa claro que é lógica de domínio pura.

### Strategy Pattern no motor de recomendações
`ConservativeStrategy`, `ModerateStrategy` e `AggressiveStrategy` implementam `InvestmentStrategy`. Um `Map<InvestorProfile, InvestmentStrategy>` é registrado como `@Bean` e injetado no `RecommendationService`, que seleciona a estratégia pelo perfil do usuário sem nenhum `if/switch`.

### Snowball vs Avalanche
O `BudgetService` simula a quitação de dívidas mês a mês para os dois algoritmos:
- **Snowball**: quita primeiro a menor dívida — efeito psicológico positivo
- **Avalanche**: quita primeiro a maior taxa de juros — matematicamente ótimo

### HMAC-SHA256 nos webhooks
Cada evento despachado inclui o header `X-FinTrack-Signature: sha256=<hex>` assinado com o segredo exclusivo do webhook. Retry automático 3x com desativação após falhas consecutivas.

### Eventos Kafka desacoplados
Quando uma meta é atingida (`GoalAchievedEvent`), o `GoalService` publica o evento no Kafka. O `GoalAchievedConsumer` consome e dispara webhook + e-mail de forma independente, sem acoplamento direto entre os módulos.

### RestClient (Spring 6.1+)
Usado em `BrapiClient` e `WebhookService` em vez de `RestTemplate` (deprecated) ou `WebClient` (requer WebFlux). É a API HTTP moderna e síncrona do Spring Framework.

---

## Testes

```bash
./mvnw test
```

Relatório JaCoCo gerado em `target/site/jacoco/index.html`.

| Tipo | Cobertura alvo |
|---|---|
| Unit (BudgetEngine, Strategies) | 100% |
| Integration (Auth + TestContainers) | endpoints principais |
| JaCoCo (service packages) | mínimo 80% |
