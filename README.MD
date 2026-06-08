#  Barbearia - Sistema de Agendamentos

Esse projeto nasceu de uma vontade simples: entender de verdade como o frontend conversa com o backend.
Já tinha experiência com Java e Spring Boot em projetos de microsserviços, mas nunca tinha construído
um sistema com frontend próprio — do login até a interface do usuário.

Levei 2 semanas para construir do zero até o deploy. No caminho aprofundei conhecimentos que já tinha
e aprendi coisas novas: entendi na prática como funcionam testes unitários com Mockito, fixei melhor
o uso de Optional e lambdas em situações reais, e encarei o desafio de entender os relacionamentos
entre entidades na hora de montar as queries com JPA.

##  Tecnologias

### Backend
- Java 17 + Spring Boot 3
- Spring Security + JWT
- Spring Data JPA + Hibernate
- PostgreSQL
- JUnit 5 + Mockito (~86% de cobertura)

### Frontend
- React + TypeScript
- Tailwind CSS
- Axios
- React Router DOM

##  Funcionalidades

- Autenticação com JWT (login e registro)
- Cadastro de clientes
- Listagem de horários disponíveis por barbeiro
- Criação e cancelamento de agendamentos
- Logs de observabilidade com SLF4J

##  O que aprendi nesse projeto

- Como o frontend se comunica com o backend via requisições HTTP
- Testes unitários com JUnit 5 e Mockito — mocks, assertions e cobertura de código
- Relacionamentos JPA na prática — OneToOne, ManyToOne e queries derivadas
- Optional e lambdas aplicados em situações reais de negócio
- Como usar o debugger para acompanhar uma requisição do frontend até o banco

##  Como rodar localmente

### Pré-requisitos
- Java 17
- Node.js 18+
- PostgreSQL

### Backend
```bash
cd backend
# Configure as variáveis de ambiente:
# db_username, db_password, jwt_secret
./mvnw spring-boot:run
```

### Frontend
```bash
cd frontend
npm install
npm run dev
```

##  Testes
```bash
cd backend
./mvnw test
```

## 🔗 Outros projetos
- [Agendador de Tarefas — Microsserviços](https://github.com/joao-oakdev/agendador-tarefas)
- [BFF Agendador de Tarefas](https://github.com/joao-oakdev/bff-agendador-tarefas)

##  Autor
Desenvolvido por **Joao Carvalho** — [GitHub](https://github.com/joao-oakdev)