# Oficina API

API REST para gestão integrada de atendimento e execução de serviços em uma oficina mecânica, desenvolvida como MVP do Tech Challenge FIAP - Fase 1.

O sistema resolve um problema comum em oficinas de médio porte: processos espalhados em anotações manuais e planilhas, com perda de histórico, falhas no controle de estoque, dificuldade de acompanhar status das ordens de serviço e pouca rastreabilidade de orçamentos e aprovações.

## Link do vídeo de apresentação
https://youtu.be/JMrTZXY2hYE

## Link do Miro
https://miro.com/app/board/uXjVHFKgYIc=/?share_link_id=451822825670

## Contexto do desafio

A oficina precisa de um sistema back-end para organizar o fluxo de atendimento, diagnóstico, orçamento, execução e entrega dos veículos. O objetivo desta primeira versão é oferecer uma base funcional, segura e documentada para que clientes, atendentes, mecânicos e gestores acompanhem o ciclo completo de uma ordem de serviço.

O MVP contempla:

- Gestão de clientes.
- Gestão de veículos.
- Gestão de serviços.
- Gestão de peças e insumos com controle de estoque.
- Criação e acompanhamento de ordens de serviço.
- Orçamentos com serviços e peças.
- Aprovação, recusa e ajustes de orçamento.
- Acompanhamento do status da OS.
- Autenticação com JWT para APIs administrativas.
- Documentação da API com Swagger.
- Execução local com Docker e Docker Compose.

## Storytelling do fluxo principal

O fluxo do sistema começa quando o cliente entra em contato com a oficina solicitando atendimento.

O atendente localiza o cliente no sistema usando CPF ou CNPJ. Se o cliente já existir, o sistema exibe seus dados, os veículos vinculados e o histórico de atendimentos. Se o cliente ainda não existir, o atendente realiza o cadastro.

Em seguida, o atendente verifica se o veículo já está registrado no cadastro do cliente. Se estiver, ele seleciona o veículo. Caso contrário, cadastra o veículo e o associa ao cliente.

Com cliente e veículo identificados, o atendente registra os serviços solicitados. O sistema cria uma ordem de serviço com código único, cliente, veículo, serviços solicitados, data de abertura, observações técnicas e status inicial `Recebida`.

O mecânico acessa o sistema, visualiza as ordens de serviço e inicia a avaliação do veículo. Nesse momento, o sistema altera o status da OS para `Em diagnóstico`.

Durante a avaliação, o mecânico registra o diagnóstico, os serviços necessários, observações técnicas e as peças que serão utilizadas. O sistema consulta o estoque para verificar a disponibilidade dessas peças.

Se as peças estiverem disponíveis, elas passam a compor o orçamento. Se alguma peça não estiver disponível, o sistema registra a necessidade para que o gestor decida se a peça será comprada ou substituída por outra.

Quando as peças e serviços necessários estão definidos, o orçamento da OS é fechado. A OS passa para o status `Aguardando aprovação`, e o orçamento fica disponível para envio e avaliação do cliente.

Se o cliente concordar com o orçamento, ele aprova. Se não concordar, pode recusar ou solicitar ajustes. Caso sejam necessários ajustes, o orçamento é atualizado e volta para aprovação.

Com o orçamento aprovado, o mecânico inicia a execução dos serviços. A OS passa para o status `Em execução`. Conforme peças são retiradas do estoque, o sistema registra a baixa para manter o controle atualizado.

Durante a execução, o cliente pode consultar o status da OS sem precisar entrar em contato com a oficina. Atendente e gestor também conseguem acompanhar o andamento em tempo real.

Se durante a execução o mecânico identificar necessidade de alterar o orçamento, ele registra a alteração no sistema. O orçamento é atualizado e a OS retorna para o status `Aguardando aprovação`.

Quando os serviços são concluídos, a OS passa para `Finalizada`. O sistema informa que o veículo está pronto para retirada e permite notificar o cliente.

No momento da retirada, o cliente realiza o pagamento, retira o veículo e a OS é alterada para `Entregue`, finalizando o atendimento.

## Status da ordem de serviço

A ordem de serviço percorre os seguintes status:

- `Recebida`: OS criada após o atendimento inicial.
- `Em diagnóstico`: mecânico iniciou a avaliação do veículo.
- `Aguardando aprovação`: orçamento fechado ou ajustado, aguardando decisão do cliente.
- `Em execução`: orçamento aprovado e serviços em andamento.
- `Finalizada`: serviços concluídos e veículo pronto para retirada.
- `Entregue`: pagamento e retirada realizados, encerrando o atendimento.

## Valor gerado pelo sistema para a oficina

- Histórico de atendimentos completo e centralizado.
- Menor risco de perda de dados de cliente, veículo, serviços, peças, datas e status.
- Melhor controle do fluxo de orçamentos e aprovações.
- Acompanhamento do status da OS por cliente, atendente e gestor.
- Controle de estoque mais confiável.
- Registro das peças utilizadas em cada atendimento.
- Monitoramento do tempo médio de execução dos serviços.
- Mais previsibilidade para a gestão da oficina.

## Principais recursos da API

- `POST /auth/login`: autenticação e geração de token JWT.
- `/clientes`: CRUD de clientes e consulta por documento.
- `/clientes/{id}/veiculos`: consulta de veículos vinculados ao cliente.
- `/veiculos`: CRUD de veículos.
- `/servicos`: CRUD de serviços oferecidos pela oficina.
- `/estoque`: CRUD de peças e insumos, inclusões, baixas e consultas de disponibilidade.
- `/ordens-servico`: criação, listagem, detalhamento e acompanhamento de ordens de serviço.
- `/ordens-servico/{id}/diagnostico`: registro de diagnóstico.
- `/ordens-servico/{id}/orcamento`: fluxo de composição, fechamento, aprovação, recusa e ajustes de orçamento.
- `/ordens-servico/{id}/execucao`: início e finalização da execução.
- `/ordens-servico/{id}/pagamento`: registro de pagamento.
- `/ordens-servico/{id}/entrega`: entrega do veículo e encerramento da OS.
- `/swagger-ui/index.html`: documentação interativa da API.

## Tecnologias utilizadas

- Java 21
- Spring Boot 4
- Spring Web MVC
- Spring Data JPA
- Spring Security
- JWT
- PostgreSQL
- Flyway
- Maven
- Docker e Docker Compose
- Springdoc OpenAPI / Swagger

## Arquitetura da aplicação

![alt text](hex.jpg)

O projeto foi implementado como um monolito modular com arquitetura hexagonal, também conhecida como ports and adapters.

Embora o desafio permita arquitetura em camadas para o MVP, a arquitetura hexagonal foi escolhida para deixar o domínio mais protegido e independente dos detalhes de infraestrutura. Assim, as regras centrais da oficina não ficam acopladas diretamente a controllers REST, JPA, banco de dados ou configurações do Spring.

Essa decisão traz benefícios importantes:

- O domínio fica mais claro e próximo da linguagem do negócio.
- Os casos de uso ficam isolados dos detalhes de entrada e saída.
- Controllers REST atuam como adaptadores de entrada.
- Repositórios JPA atuam como adaptadores de saída.
- Fica mais fácil testar regras de negócio sem depender de HTTP ou banco real.
- O sistema fica mais preparado para evoluir sem grandes reescritas.

A organização dos pacotes segue essa separação:

- `domain`: modelos, eventos e regras de negócio.
- `application`: comandos, portas de entrada, portas de saída e serviços de aplicação.
- `adapter/in/rest`: controllers, requests e responses da API REST.
- `adapter/out/persistence`: entidades JPA, repositories e adapters de persistência.
- `adapter/out/context`: integrações entre contextos internos da aplicação.
- `shared`: recursos compartilhados, como exceções e tratamento global de erros.

## Por que PostgreSQL

O PostgreSQL foi escolhido por ser um banco relacional robusto, maduro e adequado para sistemas transacionais como o de uma oficina mecânica.

A escolha faz sentido para este projeto porque:

- O domínio possui entidades fortemente relacionadas, como cliente, veículo, OS, serviços, peças e orçamentos.
- O banco oferece integridade referencial para proteger esses relacionamentos.
- Transações ajudam a manter consistência em operações como aprovar orçamento, baixar estoque e atualizar status da OS.
- Possui excelente suporte no ecossistema Spring Data JPA e Hibernate.
- Funciona bem com Flyway para versionamento e evolução controlada do schema.
- É fácil de executar com Docker, sem exigir instalação local do banco.
- É uma opção sólida para evoluir do MVP para ambientes mais próximos de produção.


# Como executar o projeto

## Pré-requisitos

- Docker Desktop instalado e em execução.
- Git instalado.

Não é necessário instalar Java, Maven ou PostgreSQL localmente para executar via Docker.

## Passo a passo

Clone o repositório:

No Bash:

```bash
git clone https://github.com/tc-organization-26/gestao-oficina-tc-1.git
cd gestao-oficina-tc-1/
```

No Windows PowerShell:

```powershell
git clone https://github.com/tc-organization-26/gestao-oficina-tc-1.git
cd .\gestao-oficina-tc-1\
```

Crie o arquivo `.env`:

No Bash:

```bash
cp .env.example .env
```

No Windows PowerShell:

```powershell
copy .env.example .env
```

Edite o `.env` a partir desse exemplo e altere as senhas.

```env
POSTGRES_DB=oficina_db_2
POSTGRES_USER=postgres
POSTGRES_PASSWORD=troque_aqui

SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/oficina_db_2
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=troque_aqui

JWT_SECRET=troque_por_uma_chave_segura_com_32_bytes_ou_mais
JWT_EXPIRATION_SECONDS=3600
```

A senha definida em `POSTGRES_PASSWORD` deve ser a mesma definida em `SPRING_DATASOURCE_PASSWORD`, pois uma configura o usuário do banco e a outra é usada pela aplicação para conectar nesse banco.

Depois execute o container:

```bash
docker compose up --build
```

Você pode executar as chamadas para os endpoints pelo Swagger, no endereço http://localhost:8081/swagger-ui/index.html.

Ou, caso prefira, pode utilizar o Insomnia e importar a collection no projeto em https://github.com/tc-organization-26/gestao-oficina-tc-1/tree/master/src/main/resources/oficina-api-insomnia.har 

## Possíveis problemas

### Docker Desktop não está rodando

Se aparecer erro parecido com:

```text
failed to connect to the docker API
```

Abra o Docker Desktop e aguarde ele inicializar. Depois teste:

```powershell
docker version
```

O comando deve mostrar informações de `Client` e `Server`.

### Docker Compose não reconhecido

Se aparecer erro parecido com:

```text
docker: 'compose' is not a docker command
```

Verifique se o Docker Desktop está atualizado. Em instalações mais antigas, o comando pode ser:

```bash
docker-compose up --build
```

Se esse comando funcionar, você pode usá-lo no lugar de `docker compose up --build`.

### Arquivo `.env` não encontrado

Se a aplicação ou o Docker indicar que variáveis como `POSTGRES_DB`, `POSTGRES_USER`, `POSTGRES_PASSWORD` ou `JWT_SECRET` não foram encontradas, confirme se o arquivo `.env` foi criado na raiz do projeto.

No Windows PowerShell:

```powershell
copy .env.example .env
```

No Bash:

```bash
cp .env.example .env
```

Depois edite o arquivo e preencha as senhas antes de executar o Docker Compose novamente.

### Senha do banco diferente da senha da aplicação

Se aparecer erro de autenticação no PostgreSQL, como:

```text
password authentication failed for user "postgres"
```

Confira se os valores abaixo no `.env` estão iguais:

```env
POSTGRES_PASSWORD=troque_aqui
SPRING_DATASOURCE_PASSWORD=troque_aqui
```

Depois reinicie os containers:

```bash
docker compose down
docker compose up --build
```

### Banco inicializado com credenciais antigas

Se você alterou `POSTGRES_DB`, `POSTGRES_USER` ou `POSTGRES_PASSWORD` depois da primeira execução, o volume do PostgreSQL pode continuar usando as credenciais antigas.

Para recriar o banco local do zero, execute:

```bash
docker compose down -v
docker compose up --build
```

Atenção: esse comando remove os dados locais gravados no volume do banco.

### Porta 5432 em uso

Se já existir um PostgreSQL local usando a porta `5432`, altere no `docker-compose.yml`:

```yaml
ports:
  - "5433:5432"
```

A aplicação continuará acessando o banco internamente por `postgres:5432`. A mudança afeta apenas o acesso ao banco pela máquina host.

Se preferir, também é possível parar o PostgreSQL local antes de subir o projeto.

### Porta 8081 em uso

Se a porta da API já estiver ocupada, altere o mapeamento no `docker-compose.yml`:

```yaml
ports:
  - "8082:8081"
```

Nesse caso, acesse a API por:

```text
http://localhost:8082
```

O Swagger ficará disponível em:

```text
http://localhost:8082/swagger-ui/index.html
```

### Aplicação inicia antes do banco estar pronto

Em algumas máquinas, o container da aplicação pode tentar conectar no PostgreSQL antes do banco terminar a inicialização. Se aparecer erro de conexão recusada ou indisponível, aguarde alguns segundos e rode novamente:

```bash
docker compose up --build
```

Para acompanhar os logs, use:

```bash
docker compose logs -f
```

### Erro nas migrations do Flyway

Se aparecer erro relacionado ao Flyway, como falha ao aplicar uma migration, pode ser que o banco local esteja com uma versão antiga do schema.

Em ambiente local de desenvolvimento, a forma mais simples de resolver é recriar o volume:

```bash
docker compose down -v
docker compose up --build
```

Atenção: isso apaga os dados locais do banco.

### JWT secret muito curto

Se a aplicação falhar ao gerar ou validar token, confira o valor de `JWT_SECRET` no `.env`. Use uma chave longa, com pelo menos 32 caracteres.

Exemplo:

```env
JWT_SECRET=minha_chave_super_secreta_com_mais_de_32_caracteres
```

### Endpoints retornando 401 Unauthorized

Alguns endpoints exigem autenticação. Primeiro faça login em:

```text
POST /auth/login
```

Depois envie o token retornado no header:

```text
Authorization: Bearer seu_token_aqui
```

No Swagger, clique em `Authorize` e informe o token antes de testar os endpoints protegidos.

### Build falha ao baixar dependências Maven

Se o build falhar com erro de download de dependências, verifique a conexão com a internet e tente novamente:

```bash
docker compose build --no-cache
docker compose up
```

Se estiver usando rede corporativa, proxy ou VPN, pode ser necessário configurar o acesso do Docker/Maven à internet.

### Permissão negada no Maven Wrapper

Em Linux/macOS, se aparecer erro de permissão ao rodar o Maven Wrapper:

```text
permission denied: ./mvnw
```

Execute:

```bash
chmod +x mvnw
./mvnw clean package
```

### Java em versão incorreta no build sem Docker

Ao executar sem Docker, o projeto exige Java 21. Se aparecer erro como `release version 21 not supported`, verifique a versão ativa:

```bash
java -version
```

Instale ou selecione o Java 21 antes de executar:

```bash
./mvnw clean package
```

No Windows, também confira se a variável `JAVA_HOME` aponta para uma instalação do JDK 21.

### Swagger não abre

Se `http://localhost:8081/swagger-ui/index.html` não abrir, confirme se os containers estão rodando:

```bash
docker compose ps
```

Depois verifique os logs da aplicação:

```bash
docker compose logs -f app
```

Se você alterou a porta da API no `docker-compose.yml`, use a nova porta no navegador.

## Build sem Docker

Também é possível compilar com Maven Wrapper:

```powershell
.\mvnw.cmd clean package
```

Ou em Linux/macOS:

```bash
./mvnw clean package
```

Para executar fora do Docker, será necessário ter Java 21, PostgreSQL disponível e as variáveis de ambiente configuradas na máquina.
