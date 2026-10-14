# Ampéra - Backend

Projeto: **Ampéra**, monitoramento de consumo de energia residencial.
Grupo: **Ampera**.

Repositórios do sistema:

- Backend (este): https://github.com/Ampera-PI/ampera-backend
- Aplicativo (Android, desktop e web): https://github.com/Ampera-PI/ampera-app

## Integrantes e entidades

| Integrante | RA | Entidades sob responsabilidade |
| --- | --- | --- |
| Arthur Francisco de Lima | 1111392411008 | Usuário e Residência |
| Enzo Fernandes Dantas | 1111392411037 | Cômodo e Dispositivo |
| José Augusto Mamani Rosales | 1111392411012 | Sensor e Medição |
| Luís Gustavo Gomes Gonçalves | 1111392411015 | Tarifa e Alerta |
| Maria Fernanda Silva Mirita | 1111392411035 | Meta de consumo e Relatório |

### Divisão de tarefas

Cada integrante cuida das duas entidades acima em todas as camadas: entidade e migration, repositório com busca `LIKE`, serviço, controller, DTOs, mapper e validação no backend, e também modelo, formulário (Gravar e Limpar Campos) e tela no aplicativo. Além disso:

- **Arthur Francisco de Lima**: autenticação (`/signin` e `/signup`), utilitário JWT, Spring Security, suporte de sessão e posse dos recursos, e a estrutura do projeto Gradle.
- **Enzo Fernandes Dantas**: infraestrutura de execução (`docker-compose.yml` e scripts locais), configuração da aplicação (`application.yml`), tratamento de erros e Swagger.
- **José Augusto Mamani Rosales**: leituras de energia e regras de consumo do período.
- **Luís Gustavo Gomes Gonçalves**: cálculo de custo pela tarifa vigente, avaliação dos alertas, migration dos campos complementares e este README.
- **Maria Fernanda Silva Mirita**: previsão de consumo por regressão linear, geração de relatórios, classe principal da aplicação e dados de demonstração (seed).

## O que a aplicação faz

Quem mora em uma casa ou apartamento costuma só descobrir quanto gastou de energia quando a conta chega. O Ampéra resolve isso: o usuário cadastra suas residências, cômodos, dispositivos e sensores, registra as medições de tensão, corrente e potência, e o backend grava cada medição já com o custo calculado pela tarifa vigente da residência.

Com isso o usuário:

- acompanha o consumo e o custo por residência, cômodo e dispositivo;
- recebe alertas quando tensão, corrente ou potência passam de um limite;
- define metas de consumo e vê o quanto já usou delas;
- gera relatórios por período, com a previsão do consumo do dia seguinte por regressão linear.

Cada residência, e tudo o que depende dela, só pode ser acessado pelo seu dono.

## Tecnologias

- Kotlin 2 e Java 17
- Spring Boot 3.4: Spring Web, Spring Data JPA (Hibernate), Bean Validation e Spring Security
- JWT com jjwt
- PostgreSQL com migrations Flyway
- springdoc OpenAPI (Swagger)
- JUnit 5 e H2 nos testes
- Aplicativo: Kotlin Multiplatform com Compose Multiplatform e Ktor Client

## Camadas e pastas

Backend, em `src/main/kotlin/com/ampera`:

| Camada | Pasta | O que tem |
| --- | --- | --- |
| Controladores REST | `api` | Um controller por entidade, tratamento de erros e configuração do Swagger |
| DTOs | `dto` | Request (com Bean Validation) e Response de cada entidade |
| Mapper | `mapper` | Uma classe por entidade que converte entidade e DTO |
| Serviços | `application` | Um serviço por entidade, com CRUD, posse por usuário e uso dos repositórios |
| Domínio | `domain` | Cálculo de custo, alertas, previsão, consumo do período e exceções |
| Persistência | `infrastructure/persistence` | Entidades JPA e repositórios (com consultas JPQL `LIKE`) |
| Segurança | `infrastructure/security` | `JwtUtil`, filtro JWT, `PasswordEncoder` e `SecurityFilterChain` |
| Banco | `src/main/resources/db` | `migration` (esquema) e `seed` (dados de demonstração) |
| Testes | `src/test/kotlin` | Testes de regras, validação, JWT e busca `LIKE` |

Aplicativo (`ampera-app`), em `composeApp/src`:

| Camada | Pasta |
| --- | --- |
| Dados (modelos, cliente HTTP, repositório) | `commonMain/kotlin/com/ampera/dados` |
| Interface (telas, ViewModels, tema) | `commonMain/kotlin/com/ampera/ui` |
| Código de Android, desktop e web | `androidMain`, `desktopMain`, `wasmJsMain` |

## Instalação, execução e acesso

Requisitos: JDK 17, e Docker (ou o modo local para Windows abaixo).

### 1. Subir o banco

Com Docker:

```
docker compose up -d
```

Sem Docker (Windows), com PostgreSQL portátil:

```
powershell -ExecutionPolicy Bypass -File scripts\instalar-local.ps1   # uma vez
powershell -ExecutionPolicy Bypass -File scripts\subir-local.ps1
```

### 2. Rodar a API

```
./gradlew bootRun
```

- Swagger: http://localhost:8080/swagger-ui.html
- Login de demonstração: `demo@ampera.local` / `demo123`. O seed fica em `db/seed` e só roda no perfil `local`, que é o padrão.
- Testes: `./gradlew test`

### 3. Autenticação

- `POST /api/auth/signup` cria o usuário.
- `POST /api/auth/signin` valida as credenciais com o Spring Security e devolve um token JWT.
- Todas as outras rotas em `/api/**` exigem `Authorization: Bearer <token>`.

### 4. Rotas de cada entidade

Todas seguem o mesmo padrão, com `GET` (lista), `GET /{id}`, `POST`, `PUT /{id}` e `DELETE /{id}`: `/api/usuarios`, `/api/residencias`, `/api/comodos`, `/api/dispositivos`, `/api/sensores`, `/api/medicoes`, `/api/tarifas`, `/api/alertas`, `/api/metas-consumo` e `/api/relatorios`.

A listagem aceita `?busca=texto`, que faz uma busca parcial (JPQL com `LIKE`, sem diferenciar maiúsculas) em um campo de texto da entidade: nome (usuários, residências, cômodos e dispositivos), modelo (sensores), observação (medições), distribuidora (tarifas), mensagem (alertas) e título (metas e relatórios). Relatórios também têm `POST /api/relatorios/gerar`.

### 5. Aplicativo

Veja o README de `ampera-app` (desktop, web e Android).
