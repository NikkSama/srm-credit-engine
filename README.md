# SRM Credit Engine

Plataforma de Cessão de Crédito Multimoedas (BRL/USD) para precificar e liquidar recebíveis (duplicatas, cheques) com precisão decimal e transações auditáveis.

Desafio técnico — Nível Pleno

## Stack

| Camada | Tecnologia |
| --- | --- |
| Frontend | Angular 17+ (Standalone Components), Bootstrap/CSS, HttpClient |
| Backend | Java 21, Spring Boot 4, Spring Data JPA, JdbcTemplate |
| Banco | PostgreSQL 16 + Flyway |
| Docs API | springdoc-openapi (Swagger UI) |
| Infra | Docker + Docker Compose |

## Arquitetura em camadas

```
frontend/    → Camada de Apresentação (Abas operacionais, relatórios e captura de erros RFC 7807)
controller/  → Camada de Aplicação (REST, DTOs, validação)
service/     → Camada de Negócio (Strategy, orquestração @Transactional)
repository/  → Camada de Persistência (JPA)
report/      → Atalho analítico de 2 camadas (controller → repository, SQL nativo)
```

O motor usa Strategy Pattern para desacoplar a regra de risco (spread) de cada produto do cálculo:

```
Valor Presente = Valor de Face / (1 + Taxa Base + Spread)^Prazo
Valor Líquido   = cross-currency ? Valor Presente * Taxa de Câmbio : Valor Presente
```

- **Duplicata Mercantil**: spread 1.5% a.m.
- **Cheque Pré-datado**: spread 2.5% a.m.
Cadastrados na base

Todos os cálculos usam `BigDecimal`. Nunca `double`.

## Como rodar

Na pasta **srm-credit-engine**:

### Docker Compose

```bash
docker compose up --build
```

- API: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html
- Interface do Operador: http://localhost:4200

## Endpoints principais

| Método | Rota | Descrição |
| --- | --- | --- |
| POST | `/api/v1/exchange-rates` | Cria/atualiza taxa de câmbio (mock/manual) |
| GET | `/api/v1/exchange-rates` | Lista taxas |
| POST | `/api/v1/settlements/simulate` | Simula o cálculo (sem persistir) |
| POST | `/api/v1/settlements` | Liquida (persiste em transação ACID) |
| GET | `/api/v1/reports/settlement-statement` | Extrato de liquidação (filtros + paginação server-side) |

### Exemplo — simular liquidação

```bash
curl -X POST http://localhost:8080/api/v1/settlements/simulate \
  -H "Content-Type: application/json" \
  -d '{
        "assignor": "Empresa Cedente LTDA",
        "receivableType": "DUPLICATA_MERCANTIL",
        "faceValue": 100000.00,
        "termMonths": 12,
        "baseRate": 0.010000,
        "originalCurrency": "USD",
        "paymentCurrency": "BRL"
      }'
```

A collection `SRM Credit Engine API.postman_collection.json` está na pasta .postman na raiz do projeto e pode ser importada no Postman


## Testes

```bash
cd backend
mvn test
```

Cobre as regras de precificação: spread por produto, múltiplos meses, combinação taxa base + spread e conversão cross-currency.

## Modelagem de dados

- Diagrama ER: [`docs/ER-diagram.md`](docs/ER-diagram.md)
- Scripts DDL: [`docs/schema.sql`](docs/schema.sql) (espelham as migrations Flyway)

## Decisões de projeto

- **`NUMERIC`/`BigDecimal`** em todo valor monetário para precisão exata.
- **`@Transactional`** na criação da liquidação — nada fica "pela metade".
- **Relatório com SQL nativo** (`JdbcTemplate` + filtros dinâmicos) em vez de ORM puro, priorizando performance analítica.
- **RFC 7807 (`ProblemDetail`)** para erros padronizados via `@RestControllerAdvice`.
- **Precificação híbrida (`Strategy + data-driven`)** — o monthly_spread por tipo de recebível continua no banco (adicionar produto comum = só um INSERT, sem deploy). O Strategy Pattern (PricingStrategy + PricingStrategyResolver) entra apenas quando o produto tem regra própria (ex.: CHEQUE_PRE_DATADO, com prêmio de risco por prazo); tipos sem estratégia caem no DefaultDataDrivenStrategy.
- **Interface Reativa em Angular**: O painel do operador foi desacoplado em abas reutilizáveis usando arquitetura de *Standalone Components*. O consumo das rotas assíncronas do backend gerencia nativamente as estruturas da RFC 7807 (`HttpErrorResponse`), exibindo os alertas de validação e bloqueio de concorrência de forma tratada para o usuário.

## Uso de IA

Ver [`AI_USAGE.md`](AI_USAGE.md).
