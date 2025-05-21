---
title: Documentação SIT - ESOF II

---

![ifmg-completa](https://hackmd.io/_uploads/SJfg3c9Jex.png =50%x)

#### Bacharelado em Sistemas de Informação
Autores: Amanda Nelva Almeida Martins, Caique Augusto de Aquino Braga, Daniel Gonçalves da Silva, Glauber Vinícius Campos Batista, Monique Evelin Miranda Domingos.

R.A.: 0101525, 	0101526, 0040252, 0078859, 0076892

### 1. Introdução

Este documento apresenta a definição de domínio, escopo, requisitos e plano de execução do projeto ERP para gestão de transportadoras, baseado em arquitetura de microserviços.
### **2. Definição do Domínio e Escopo do Projeto**
#### 2.1 Domínio do Projeto
O projeto consiste no desenvolvimento de um Sistema ERP (Enterprise Resource Planning) voltado para a gestão de transportadoras, utilizando uma arquitetura baseada em microserviços.
O domínio do sistema abrange as seguintes áreas principais:

##### Gestão de Recursos Humanos (RH)
##### Operações Logísticas
##### Gestão de Clientes
##### Gestão Financeira

O sistema será entregue como uma aplicação web, com infraestrutura moderna e escalável, visando atender às necessidades operacionais, administrativas e financeiras das transportadoras, integrando dados de diferentes áreas da empresa de maneira confiável e eficiente.

#### 2.2 Escopo do Projeto
O escopo do projeto contempla:

#### Desenvolvimento de Microserviços
* Microserviço de RH: Gerenciamento de funcionários, folha de pagamento e controle de ponto.
* Microserviço Operacional: Gestão de cotações, pedidos de coleta e frota de veículos.
* Microserviço de Clientes: Cadastro, manutenção e consulta de informações de clientes.
* Microserviço Financeiro: Controle de contas a pagar, emissão de cobranças e gestão de notas fiscais.

#### Tecnologias principais:
* Spring Boot (Java) — desenvolvimento dos microserviços.
* Docker — containerização de ambientes.
* PostgreSQL — banco de dados relacional.
* API Gateway — roteamento e centralização de serviços.

### 3. Especificação de Requisitos
#### 3.1 Requisitos Funcionais
A seguir, a divisão conforme as sprints planejadas:

#### Sprint 1 - Microserviço de Cliente
Objetivo: Entregar funcionalidades de cadastro e gestão de clientes.
#### Histórias de Usuário
1. Cadastro e Pesquisa de Clientes

Como atendente, quero cadastrar clientes com dados básicos (nome, CNPJ, endereço, contato) e após o cadastro quero poder realizar pesquisas com os dados fornecidos.

#### Critérios de Aceitação:

* Formulário com validação de campos obrigatórios.
* Persistência no banco de dados.
* Busca de clientes por nome/CNPJ.

2. Edição e Exclusão de Clientes

Como gestor editar dados ou desativar clientes.

#### Critérios de Aceitação:

* Opção de editar e desativar (soft delete).
* Histórico de alterações.

3. Integração Básica com API Gateway

Como desenvolvedor, quero que o serviço seja acessível via API Gateway.

#### Critérios de Aceitação:

* Rotas /api/clientes configuradas.
* Autenticação básica.

#### Tarefas Técnicas
✔ Configurar banco de dados (PostgreSQL).
✔ Desenvolver API REST com Spring Boot.
✔ Dockerizar o microserviço.
✔ Testes unitários e de integração.

#### Sprint 2 - Microserviço Operacional
Objetivo: Entregar funcionalidades de cotações de frete, pedidos de coleta e gestão de frota.

#### Histórias de Usuário
1. Cotações de Frete

Como atendente, quero calcular valores de frete com base em origem, destino e peso.

#### Critérios de Aceitação:

* Cálculo automático com regras pré-definidas.
* Geração de PDF com os dados de cotação.

2. Pedidos de Coleta

Como cliente, quero solicitar coleta/entrega de mercadorias.

#### Critérios de Aceitação:

* Associação a um cliente cadastrado.
* Status (pendente, agendado, concluído).

3. Cadastro de Veículos

Como gestor de frota, quero registrar veículos (placa, modelo, capacidade).

#### Critérios de Aceitação:

* Validação de campos obrigatórios.
* Filtros por tipo de veículo/disponibilidade.

#### Tarefas Técnicas
✔ Integração com Microserviço de Clientes (consulta de clientes).
✔ Testes de carga para cotações.
✔ Dockerizar o microserviço.
✔ Testes unitários e de integração.

#### Sprint 3 - Microserviço Financeiro
Objetivo: Entregar funcionalidades de contas a pagar, cobranças e emissão de notas fiscais.

#### Histórias de Usuário
1. Contas a Pagar

Como assistente financeiro, quero registrar despesas operacionais (combustível, manutenção, salários).

#### Critérios de Aceitação:

* Categorização por tipo de gasto.
* Relatório mensal.

2. Emissão de Notas Fiscais

Como assistente financeiro, quero emitir NFe vinculada a um pedido de coleta.

#### Critérios de Aceitação:

* Integração com mock da API SEFAZ.
* Geração de DANFE em PDF.

3. Cobranças

Como cobrador, quero gerar boletos/faturas para clientes.

#### Critérios de Aceitação:

* Link com Microserviço de Clientes.
* Registro de pagamentos.

#### Tarefas Técnicas
✔ Integração com mock de bancos (boletos via API).
✔ Testes de integração com sistemas legados (SEFAZ).
✔ Dockerizar o microserviço.
✔ Testes unitários e de integração.

#### Sprint 4 - Microserviço de RH
Objetivo: Entrega de funcionalidades de gestão de funcionários, folha de pagamento e ponto.

#### Histórias de Usuário
1. Cadastro de Funcionários

Como gestor de RH, quero registrar colaboradores (dados pessoais, cargo, salário).

#### Critérios de Aceitação:

* Campos obrigatórios validados.
* Criptografia de dados sensíveis (CPF, salário).

2. Folha de Pagamento

Como assistente financeiro, quero calcular salários líquidos.

#### Critérios de Aceitação:

* Descontos (INSS, IRRF) calculados automaticamente.
* Exportação para arquivo de pagamento.

#### Tarefas Técnicas
✔ Autenticação robusta.
✔ Relatórios em PDF/Excel.
✔ Auditoria de alterações (Logs).
✔ Dockerizar o microserviço.
✔ Testes unitários e de integração.

#### Sprint 5 - Consolidação e Melhorias
Objetivo: Realizar integrações finais, melhorias de desempenho e criação de dashboards.

#### Tarefas Técnicas
✔ Dashboard unificado com KPIs logísticos, financeiros e operacionais.
✔ Testes E2E (Postman).
✔ Otimização de desempenho.
✔ Refatorações técnicas com base em feedback das sprints anteriores.

#### 3.2 Requisitos Não-Funcionais
* Desempenho: O sistema deve responder em menos de 3 segundos para 95% das requisições
* Disponibilidade: 99,9% de uptime (exceto para manutenções agendadas)
* Escalabilidade: Cada microserviço deverá ser escalável horizontalmente de forma independente.
* Compatibilidade: Suporte aos principais navegadores (Chrome, Firefox, Edge)

### 4. Modelos de Processos
#### 4.1 Metodologia de Desenvolvimento
O projeto será conduzido utilizando práticas ágeis:

* Frameworks: Scrum e XP (Extreme Programming)
* Sprints: Ciclos de 2 semanas
* Entregas Incrementais: MVP inicial + melhorias contínuas

O projeto será dividido em 4 sprints iniciais para entrega do MVP (Produto Mínimo Viável).

### 5. Arquitetura e Infraestrutura
#### 5.1 Diagrama de Microserviços
[Client Frontend] → [API Gateway]  → [Serviço RH]
                                   → [Serviço Operacional]
                                   → [Serviço Clientes]
                                   → [Serviço Financeiro]
#### 5.2 Infraestrutura Técnica
* Cada microserviço estará isolado em containers Docker.
* Cada serviço possuirá uma instância separada de banco PostgreSQL para isolamento e performance..
* Balanceamento de carga será aplicado no servidor de aplicação.

### 6. Critérios de Aceitação
* Todos os microserviços devem se comunicar via API REST com documentação Swagger.
* O sistema deve suportar pelo menos 50 usuários concorrentes.
* Deve haver logs detalhados para auditoria.
* O deploy deve ser realizado via pipeline CI/CD.
* Testes automatizados devem cobrir pelo menos 70% do código.

### 7. Roadmap

| Fase | Duração | Entregas |
| -------- | -------- | -------- |
| Fase 1 | 2 meses|MVP com funcionalidades básicas |
| Fase 2 | 1 mês | Integrações e melhorias de desempenho |
| Fase 3 | 1 mês | Dashboards e relatórios |

### 8. Vantagens da estrutura utilizada
* Entrega incremental: Cada microserviço é independente e utilizável desde o início.
* Feedback antecipado: Clientes podem testar módulos prontos enquanto outros são desenvolvidos.
* Redução de riscos: Se um microsserviço falhar, os outros continuam operando.
* Escalabilidade: Time pode trabalhar em paralelo após a estrutura inicial (API Gateway, Docker, etc.).
* Isolamento técnico: Bugs e falhas são mais fáceis de localizar e corrigir.
* Facilidade de manutenção e evolução: Atualizações em um microserviço não impactam o restante.

### 9. Mapa de Escopo do Projeto ERP (Fluxograma) - Transportadoras
Projeto ERP - Transportadoras (Microserviços)
│
├── 1. Microserviço de Clientes (Sprint 1)
│   ├─ Cadastro de Clientes (nome, CNPJ, endereço, contato)
│   ├─ Pesquisa de Clientes (nome/CNPJ)
│   ├─ Edição e Desativação de Clientes (soft delete)
│   └─ Integração com API Gateway
│
├── 2. Microserviço Operacional (Sprint 2)
│   ├─ Cotações de Frete (origem, destino, peso, PDF)
│   ├─ Pedidos de Coleta (vincular cliente, status)
│   ├─ Cadastro de Veículos (placa, modelo, capacidade)
│   └─ Integração com Microserviço de Clientes
│
├── 3. Microserviço Financeiro (Sprint 3)
│   ├─ Contas a Pagar (combustível, manutenção, salários)
│   ├─ Emissão de Notas Fiscais (mock SEFAZ, DANFE em PDF)
│   ├─ Geração de Cobranças (boletos, faturas)
│   └─ Integração com Bancos e SEFAZ
│
├── 4. Microserviço de RH (Sprint 4)
│   ├─ Cadastro de Funcionários (dados pessoais, cargo, salário)
│   ├─ Registro de Ponto
│   ├─ Folha de Pagamento (descontos automáticos)
│   ├─ Relatórios em PDF/Excel
│   └─ Logs de Auditoria
│
└── 5. Consolidação e Melhorias (Sprint 5)
    ├─ Dashboard Unificado (dados dos microserviços)
    ├─ Testes End-to-End (Postman)
    ├─ Otimização de Desempenho
    └─ Refatorações Técnicas

Tecnologias:
- Spring Boot (Java)
- Docker (Containers)
- PostgreSQL (Banco de Dados)
- API Gateway (Roteamento)
- CI/CD (Deploy Automatizado)

