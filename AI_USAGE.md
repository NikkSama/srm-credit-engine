# Uso de IA (AI as a Co-Pilot)

Este documento registra como ferramentas de IA (LLMs) foram usadas na construção do SRM Credit Engine, conforme a política do desafio. A IA foi usada como acelerador, não como substituto de decisão técnica.

## 1. Prompts estratégicos utilizados

| Objetivo                 | Prompt (resumo)                                                                                                                    | O que aproveitei                                       |
|--------------------------|------------------------------------------------------------------------------------------------------------------------------------|--------------------------------------------------------|
| Scaffolding              | "Gerar estrutura de projeto Spring Boot 3 em 3 camadas com Flyway e Postgres para precificação de recebíveis e conversão de moeda" | Estrutura de pacotes e pom.xml base                    |
| Strategy Pattern         | "Modelar Strategy Pattern para spreads de risco por tipo de recebível"                                                             | Interface PricingStrategy                              |
| Cálculo financeiro       | "Implementar VP = FV / (1+taxa+spread)^prazo com BigDecimal e arredondamento bancário"                                             | Uso de MathContext + HALF_EVEN                         |
| Relatório                | "Query nativa com filtros dinâmicos e paginação"                                                                                   | Montagem do WHERE dinâmico + count                     |
| Correção de escrita      | "Revisar e corrigir código com problemas de sintaxe e lógica"                                                                      | Ajustes em métodos com erros de digitação e estrutura  |
| Testes unitários         | "Gerar estrutura base para testes com JUnit 5 e Mockito"                                                                           | Rascunho de casos de teste que completei manualmente   |
| Postman Collection       | "Gerar collection com endpoints e payloads para testes manuais"                                                                    | Template dos endpoints que validei e adapti ao projeto |
| Diagnóstico de erros     | "Me ajude a entender esse erro"                                                                                                    | Identificação de causas raiz de falhas durante build   |
| Validação das exceptions | "Quais validações ainda posso adicionar nessas classes?"                                                                           | Sugestões de validações adicionais que implementei     |
| Integração Frontend      | "Criar painel administrativo em Angular com abas para operações, configurações e relatórios com tratamento RFC 7807"               | Esqueleto do componente, rotas HTTP e bindings básicos  |

## 2. Onde a IA alucinou / gerou código inseguro (e como corrigi)

| Problema                                                                  | Correção aplicada                                                                        |
|---------------------------------------------------------------------------|------------------------------------------------------------------------------------------|
| Sugestão de usar double para valores monetários                           | Substituí por BigDecimal                                                                 |
| Testes incompletos com mocks faltando setup                               | Completei a estrutura                                                                    |
| Alguns endpoints da collection em formato incorreto                       | Padronizei headers e payloads                                                            |
| Quando pedi ajuda com o strategy pattern ela criou classes com hardcoded  | Apaguei as classes criadas, ajustei manualmente e escrevi um novo prompt para validação  | 
| Gerou classes de validação                                                | Apaguei essas classes e adicionei os métodos na GlobalExceptionHandler                   |

## 3. Análise crítica

**Onde economizou tempo:**
- Boilerplate repetitivo (DTOs, mapeamentos, configuração de build).
- Rascunho inicial de queries e da estrutura de testes.
- Geração de templates para Postman Collection com endpoints base.
- Diagnóstico rápido de erros de compilação e stack traces.
- Correção dos erros de ortografia.
- Complemento do código com comentários explicativos.

**Onde atrapalhou / exigiu revisão cuidadosa:**
- Alucinou nomes de métodos e propriedades que não existiam.
- Gerou código com problemas de sintaxe que precisava correção manual.
- Testes incompletos: precisei adicionar @Mock, @BeforeEach e asserções finais.
- Collection com endpoints mal formatados: tive que validar e ajustar headers/payloads.
- Sugestões de configuração insegura (SQL injection, valores monetários com double). 
- Quando pedi ajuda com o strategy pattern, ela criou classes com hardcoded que não faziam sentido no contexto do projeto.
- Quando pedi ajuda com as validações ela também gerou classes a mais que na minha visão poderia ser apenas um método de validação.

## 4. Conclusão

A IA acelerou algumas partes da codifivação, mas as decisões de arquitetura, e testes manuais foram validadas e ajustadas manualmente.
