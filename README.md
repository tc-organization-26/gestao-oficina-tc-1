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
- `/ordens-servico/{id}/status`: consulta e atualização de status da OS, disparando as regras de negócio da transição.
- `/ordens-servico/{id}/orcamento`: fluxo de composição, aprovação e recusa de orçamento.
- `/ordens-servico/{id}/pagamento`: registro de pagamento.
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
- Kubernetes
- Terraform
- Springdoc OpenAPI / Swagger

## Arquitetura da aplicação

O projeto foi implementado como um monolito modular utilizando Clean Architecture. Isso significa que a aplicação foi organizada em camadas concêntricas, onde as regras mais importantes ficam no centro e os detalhes técnicos ficam nas bordas.

A ideia principal da Clean Architecture é proteger o negócio contra mudanças externas. No contexto desta API, as regras da oficina, como criação de ordem de serviço, aprovação de orçamento, baixa de estoque, cadastro de cliente e controle de veículos, não devem depender diretamente de HTTP, banco de dados, Spring, JWT ou JPA. Esses recursos são importantes para a aplicação funcionar, mas são detalhes de infraestrutura.

### Objetivo da Clean Architecture

O objetivo da Clean Architecture é organizar o sistema para que a regra de negócio seja a parte mais importante, mais independente e mais protegida da aplicação. Em vez de começar o desenho do software pelo banco de dados, pelas rotas REST ou pelo framework, essa arquitetura começa pelo domínio: quais problemas a oficina precisa resolver e quais regras precisam ser respeitadas.

Neste projeto, isso significa que o código que representa o funcionamento da oficina deve continuar compreensível e testável mesmo que algum detalhe técnico mude. Por exemplo, a regra para aprovar um orçamento, registrar um diagnóstico ou baixar uma peça do estoque não deve ser reescrita apenas porque a API mudou um endpoint, porque o banco passou a ter outra tabela ou porque uma configuração do Spring foi alterada.

De forma prática, a arquitetura foi usada para alcançar estes objetivos:

- Separar regra de negócio de tecnologia.
- Deixar claro onde cada responsabilidade deve ficar.
- Facilitar testes automatizados das regras principais.
- Reduzir o impacto de mudanças em banco de dados, controllers, segurança ou infraestrutura.
- Tornar os casos de uso mais fáceis de localizar, ler e evoluir.
- Permitir que o projeto cresça mantendo um padrão de organização entre os módulos.

Assim, a Clean Architecture não foi usada apenas para organizar pastas. Ela orienta a forma como o sistema foi pensado: o domínio define as regras, os casos de uso coordenam as ações, os adaptadores traduzem dados entre camadas e os frameworks ficam como mecanismos externos que dão suporte à aplicação.

Por isso, o código foi separado para que as dependências sempre apontem para dentro:

```text
Frameworks e Drivers
        ↓
Interface Adapters
        ↓
Application / Use Cases
        ↓
Domain
```

Essa direção é importante porque o domínio não conhece as tecnologias externas. Uma entidade como `OrdemServico`, por exemplo, representa conceitos e comportamentos da oficina. Ela não precisa saber se os dados vieram de uma requisição REST, de um banco PostgreSQL, de um teste automatizado ou de outro mecanismo.

### Camada de domínio

A camada `domain` é o núcleo da aplicação. Ela concentra os conceitos mais estáveis do sistema e representa a linguagem do negócio.

Nesta camada ficam:

- Entidades de negócio, como `Cliente`, `Veiculo`, `Servico`, `ItemEstoque`, `OrdemServico` e `Orcamento`.
- Value Objects, como identificadores, placa de veículo, CPF/CNPJ e códigos de serviço.
- Enums que representam estados ou classificações do domínio.
- Eventos de domínio, como eventos relacionados a orçamento, falta de peça e finalização de ordem de serviço.
- Exceções de domínio usadas para sinalizar violações de regra de negócio.

Essa camada não depende de Spring, JPA, controllers ou banco de dados. Isso deixa as regras mais fáceis de entender, testar e evoluir.

### Camada de aplicação e casos de uso

A camada `application` representa o comportamento da aplicação. Ela coordena os fluxos de negócio e define o que o sistema é capaz de fazer.

Nesta camada ficam:

- Interfaces de casos de uso, como `CadastrarClienteUseCase`, `CriarOrdemServicoUseCase`, `AprovarOrcamentoUseCase`, `BaixarItemEstoqueUseCase` e `AutenticarUsuarioUseCase`.
- Interactors, implementados como services de aplicação, que executam os casos de uso.
- DTOs de entrada da aplicação, como commands usados para transportar os dados necessários para uma ação.
- Gateways, que são contratos que a aplicação precisa para acessar recursos externos, como persistência, autenticação, estoque ou publicação de eventos.

Os casos de uso recebem dados já organizados, aplicam regras, chamam entidades de domínio e solicitam operações através de interfaces. Eles não sabem qual controller chamou a operação e também não sabem qual tecnologia será usada para salvar ou buscar dados.

Por exemplo, um service de aplicação pode depender de `ClienteGateway`, `VeiculoGateway` ou `OrdemServicoGateway`, mas não depende diretamente de `SpringDataClienteRepository` ou de entidades JPA. Isso preserva a independência da regra de negócio.

### Camada de interface adapters

A camada `interfaceadapters` faz a tradução entre o mundo externo e o formato esperado pela aplicação.

Nesta camada ficam:

- Controllers REST, responsáveis por receber requisições HTTP.
- Requests e responses da API.
- Presenters, usados para devolver dados no formato adequado ao cliente da API.
- Gateways concretos, que implementam os contratos definidos na camada de aplicação.
- Mappers, que convertem objetos de domínio para entidades de persistência e vice-versa.
- Listeners de eventos, quando a entrada do fluxo acontece por evento interno da aplicação.

Um controller REST, por exemplo, recebe JSON, valida os dados básicos da requisição, monta um command e chama um caso de uso. Ele não concentra regra de negócio. A regra fica nos casos de uso e no domínio.

Da mesma forma, um gateway de persistência implementa uma interface da aplicação e traduz entre o modelo de domínio e o modelo usado pelo banco. Assim, a aplicação continua falando com contratos próprios, enquanto os detalhes de JPA ficam isolados.

### Camada de frameworks e drivers

A camada `frameworks` contém os detalhes técnicos necessários para a aplicação rodar.

Nesta camada ficam:

- Entidades JPA.
- Repositórios Spring Data.
- Configurações de beans.
- Configurações de segurança.
- Filtros JWT.
- Integrações com recursos do Spring e infraestrutura.

Essa camada fica na parte mais externa porque muda com mais facilidade. Caso o projeto precise trocar uma tecnologia de persistência, alterar configurações de segurança ou reorganizar beans do Spring, a tendência é que a mudança fique concentrada nas bordas, sem contaminar as regras centrais.

### Organização dos pacotes no projeto

Cada módulo funcional segue a mesma lógica de separação. Exemplos de módulos são `cliente`, `veiculo`, `servico`, `estoque`, `ordemservico` e `autenticacao`.

A organização usada no projeto é:

- `domain`: entidades, value objects, enums, eventos e exceções ligadas ao negócio.
- `application/usecases`: contratos dos casos de uso que representam as ações disponíveis no sistema.
- `application/usecases/interactors`: implementações dos casos de uso, responsáveis por coordenar os fluxos da aplicação.
- `application/dtos`: commands e objetos de entrada usados pelos casos de uso.
- `application/gateways`: interfaces que descrevem dependências externas necessárias para os casos de uso.
- `interfaceadapters/controllers`: controllers REST e listeners que recebem chamadas externas ou eventos.
- `interfaceadapters/presenters`: responses e objetos de saída voltados para a API.
- `interfaceadapters/gateways`: implementações concretas dos gateways definidos pela aplicação.
- `frameworks/persistence`: entidades JPA e repositories Spring Data.
- `frameworks/config`: configurações do Spring para injeção das dependências.
- `frameworks/security`: configuração e filtros de segurança.
- `shared`: recursos compartilhados, como entidade base, eventos, exceções e tratamento global de erros.

### Fluxo de uma requisição

Um fluxo típico da API segue este caminho:

1. O cliente da API envia uma requisição HTTP para um controller em `interfaceadapters/controllers/rest`.
2. O controller converte o request em um command da camada `application`.
3. O controller chama uma interface de caso de uso.
4. O interactor executa o fluxo, valida regras, usa entidades de domínio e acessa gateways quando precisa buscar ou salvar dados.
5. O gateway concreto, em `interfaceadapters/gateways`, usa recursos da camada `frameworks`, como repositories Spring Data e entidades JPA.
6. O resultado volta para o controller, que monta uma response adequada para a API.

Esse fluxo mantém cada responsabilidade no seu lugar. Controllers cuidam de HTTP, casos de uso cuidam da intenção da aplicação, entidades cuidam das regras essenciais e frameworks cuidam dos detalhes técnicos.

### Benefícios para este projeto

A Clean Architecture foi usada neste projeto porque traz vantagens práticas para um MVP que pode evoluir:

- Regras de negócio ficam mais claras e próximas da linguagem da oficina.
- Casos de uso ficam fáceis de localizar e entender.
- Testes de domínio e aplicação podem ser escritos sem depender de servidor HTTP ou banco real.
- Mudanças em controllers, banco, segurança ou configuração causam menos impacto no núcleo da aplicação.
- Novos recursos podem seguir um padrão previsível de pacotes e responsabilidades.
- O projeto continua sendo um monolito simples de executar, mas com separação interna suficiente para crescer com organização.

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


# Como executar localmente

O projeto possui duas formas de execução local:

- Docker Compose: caminho mais simples para desenvolvimento e validação rápida da API.
- Kubernetes com Terraform: caminho exigido para demonstrar orquestração, IaC, banco no cluster, ConfigMaps, Secrets, Services, Deployments, PVC e HPA.

Nos dois casos, a aplicação usa PostgreSQL e executa as migrations do Flyway automaticamente ao iniciar.

## Opção 1 - Docker Compose

Use esta opção quando quiser subir rapidamente a API e o banco localmente.

### Pré-requisitos

- Git instalado.
- Docker Desktop instalado e em execução.

Não é necessário instalar Java, Maven ou PostgreSQL localmente para executar via Docker Compose.

### Passo a passo

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

Depois execute os containers da API e do PostgreSQL:

```bash
docker compose up --build
```

O Compose cria:

- Container `oficina-postgres`, com PostgreSQL.
- Container `oficina-api`, com a aplicação Spring Boot.
- Volume `oficina-postgres-data`, para persistir os dados locais do banco.
- Rede interna para a API acessar o banco pelo host `postgres`.

A API fica disponível em:

```text
http://localhost:8081
```

O Swagger fica disponível em:

```text
http://localhost:8081/swagger-ui/index.html
```

Para acompanhar os logs:

```bash
docker compose logs -f
```

Para parar os containers sem apagar o banco:

```bash
docker compose down
```

Para parar e apagar o volume local do banco:

```bash
docker compose down -v
```

Atenção: `docker compose down -v` remove os dados locais do PostgreSQL.

O projeto foi testado com o Insomnia, por isso a forma recomendada para validar os endpoints é importar e executar a collection disponível no projeto em https://github.com/tc-organization-26/gestao-oficina-tc-1/tree/master/src/main/resources/oficina-api-insomnia.har 

Ao usar a collection do Insomnia, ajuste a base URL conforme o modo de execução:

```text
Docker Compose: http://localhost:8081
Kubernetes com port-forward: http://localhost:18081
```

Essa forma de execução da collection vale para qualquer modo de execução da API, seja Docker Compose ou Kubernetes. Para executar o fluxo principal, use a opção `Run folder` na pasta `FLUXO COMPLETO`.

## Opção 2 - Kubernetes local com Terraform

Além do Docker Compose, a Fase 2 possui execução local em Kubernetes, com banco PostgreSQL também dentro do cluster.

Essa opção mostra a parte de infraestrutura do desafio:

- A aplicação roda em um Deployment Kubernetes.
- O banco PostgreSQL roda em outro Deployment Kubernetes.
- A API acessa o banco pelo Service interno `postgres`.
- As configurações abertas ficam em ConfigMaps.
- Usuários, senhas e token JWT ficam em Secrets.
- Os dados do PostgreSQL ficam em um PVC.
- A API possui HPA para escalar por CPU e memória.
- O Terraform cria os recursos usando vários `resource "kubernetes_*"`, sem `local-exec`.

Os manifestos Kubernetes ficam em `k8s/` e estão separados por responsabilidade:

- `namespace.yaml`
- `app-configmap.yaml`
- `app-secret.yaml`
- `app-deployment.yaml`
- `app-service.yaml`
- `app-hpa.yaml`
- `postgres-configmap.yaml`
- `postgres-secret.yaml`
- `postgres-pvc.yaml`
- `postgres-deployment.yaml`
- `postgres-service.yaml`

Os scripts Terraform ficam em `infra/`. A documentação específica da infraestrutura está em `infra/INFRA-README.md`.

O Terraform provisiona:

- Namespace da aplicação.
- ConfigMaps com variáveis não sensíveis.
- Secrets com usuário, senha e segredo JWT.
- Deployment e Service da API.
- HPA da API por CPU e memória.
- Deployment, Service e PVC do PostgreSQL.

### Pré-requisitos para Kubernetes local

- Docker.
- Kubernetes local, usando uma destas opções:
  - Docker Desktop com Kubernetes habilitado.
  - Minikube.
  - Kind.
- `kubectl`.
- Terraform.
- Imagem local da aplicação criada com o nome `oficina-api:local`.

### Preparar Docker Desktop com Kubernetes

No Docker Desktop:

1. Abra o Docker Desktop.
2. Entre em `Settings`.
3. Acesse `Kubernetes`.
4. Marque `Enable Kubernetes`.
5. Clique em `Apply & Restart`.
6. Aguarde o Kubernetes ficar com status `Running`.

Depois, no PowerShell, confira o contexto:

```powershell
kubectl config get-contexts
kubectl config use-context docker-desktop
kubectl cluster-info
```

Se `kubectl cluster-info` responder com informações do cluster, o Kubernetes local está pronto.

### Instalar Terraform no Windows

Uma forma simples é usar Winget:

```powershell
winget install HashiCorp.Terraform
```

Depois feche e abra o terminal novamente e confira:

```powershell
terraform version
```

Se preferir instalar manualmente:

1. Baixe o Terraform no site da HashiCorp.
2. Extraia o executável `terraform.exe`.
3. Coloque a pasta do executável no `PATH` do Windows.
4. Abra um novo PowerShell.
5. Rode `terraform version`.

### Passo a passo com Terraform

Sequência rápida:

```powershell
cd "C:\Users\thais\OneDrive\Área de Trabalho\proj-tc-1-fiap"
kubectl config use-context docker-desktop
kubectl cluster-info
docker build -t oficina-api:local .
cd infra
copy terraform.tfvars.example terraform.tfvars
terraform init
terraform apply
kubectl get all -n oficina
kubectl get pvc -n oficina
kubectl get hpa -n oficina
kubectl port-forward -n oficina service/oficina-api 18081:8081
```

O caminho `C:\Users\thais\OneDrive\Área de Trabalho\proj-tc-1-fiap` representa a raiz do projeto nesta máquina. Em outra máquina, entre na pasta raiz onde o repositório foi clonado antes de executar os comandos.

Com o `port-forward` aberto, acesse:

```text
http://localhost:18081/swagger-ui/index.html
```

Na raiz do projeto, gere a imagem local da API:

```powershell
docker build -t oficina-api:local .
```

Essa imagem é usada pelo Deployment da API no Kubernetes.

Crie o arquivo local de variáveis sensíveis do Terraform:

```powershell
copy infra\terraform.tfvars.example infra\terraform.tfvars
```

Edite `infra\terraform.tfvars` e ajuste as senhas e o segredo JWT.

Exemplo:

```hcl
kubeconfig_context = "docker-desktop"
postgres_storage_class_name = "hostpath"

postgres_user     = "postgres"
postgres_password = "troque_aqui"
jwt_secret        = "troque_por_uma_chave_segura_com_32_bytes_ou_mais"
```

Não use a sintaxe de variáveis do Spring dentro do Terraform. Isto é inválido em `terraform.tfvars`:

```hcl
postgres_user = ${SPRING_DATASOURCE_USERNAME:postgres}
```

Se preferir não usar `terraform.tfvars`, também é possível passar valores por variáveis de ambiente do Terraform. No PowerShell:

```powershell
$env:TF_VAR_kubeconfig_context = "docker-desktop"
$env:TF_VAR_postgres_storage_class_name = "hostpath"
$env:TF_VAR_postgres_user = "postgres"
$env:TF_VAR_postgres_password = "troque_aqui"
$env:TF_VAR_jwt_secret = "troque_por_uma_chave_segura_com_32_bytes_ou_mais"
```

O Terraform só reconhece variáveis de ambiente quando elas começam com `TF_VAR_`.

Depois aplique a infraestrutura:

```powershell
cd infra
terraform init
terraform plan
terraform apply
```

Verifique os recursos criados:

```powershell
kubectl get all -n oficina
kubectl get pvc -n oficina
kubectl get hpa -n oficina
```

No Docker Desktop, a API deve ficar disponível em:

```text
http://localhost:30081/swagger-ui/index.html
```

Se o `NodePort` não responder em `localhost`, use `port-forward`:

```powershell
kubectl port-forward -n oficina service/oficina-api 18081:8081
```

Enquanto esse comando estiver aberto, acesse:

```text
http://localhost:18081/swagger-ui/index.html
```

Nesse comando, `18081` é a porta da máquina local e `8081` é a porta do Service no Kubernetes. Se a porta `18081` já estiver ocupada, troque a porta local, por exemplo:

```powershell
kubectl port-forward -n oficina service/oficina-api 18082:8081
```

Nesse caso, a URL passa a ser:

```text
http://localhost:18082/swagger-ui/index.html
```

Se estiver usando a collection do Insomnia, ajuste a base URL para a mesma porta local usada no `port-forward`.

Para remover os recursos criados pelo Terraform:

```powershell
cd infra
terraform destroy
```

O HPA depende do metrics-server. Se o cluster local não tiver metrics-server, o recurso será criado, mas as métricas podem aparecer como indisponíveis até instalar esse componente.

## Possíveis problemas

### Terraform não reconhecido no PowerShell

Se aparecer erro parecido com:

```text
terraform : O termo 'terraform' não é reconhecido como nome de cmdlet, função, arquivo de script ou programa operável.
```

O Terraform não está instalado ou a pasta do `terraform.exe` não está no `PATH`.

Se instalou manualmente em `C:\terraform`, teste:

```powershell
C:\terraform\terraform.exe version
```

Se funcionar, adicione a pasta ao `PATH` do Windows:

```text
C:\terraform
```

Depois feche e abra o PowerShell novamente e teste:

```powershell
terraform version
```

### Erro `Invalid character` no Terraform

Se aparecer erro parecido com:

```text
Error: Invalid character
on terraform.tfvars line 3:
postgres_user = ${SPRING_DATASOURCE_USERNAME:postgres}
```

O arquivo `terraform.tfvars` está usando sintaxe do Spring, não do Terraform.

Corrija para valores diretos:

```hcl
kubeconfig_context = "docker-desktop"

postgres_user     = "postgres"
postgres_password = "troque_aqui"
jwt_secret        = "troque_por_uma_chave_segura_com_32_bytes_ou_mais"
```

Ou remova esses valores do `terraform.tfvars` e use variáveis de ambiente `TF_VAR_*`:

```powershell
$env:TF_VAR_postgres_user = "postgres"
$env:TF_VAR_postgres_password = "troque_aqui"
$env:TF_VAR_jwt_secret = "troque_por_uma_chave_segura_com_32_bytes_ou_mais"
```

### Terraform executado na pasta errada

Se `terraform init`, `terraform plan` ou `terraform apply` não encontrar arquivos `.tf`, confirme se você está na pasta `infra`:

```powershell
cd "C:\Users\thais\OneDrive\Área de Trabalho\proj-tc-1-fiap\infra"
dir
```

A pasta deve conter:

```text
main.tf
providers.tf
variables.tf
outputs.tf
terraform.tfvars
```

### Terraform não baixa o provider Kubernetes

Se `terraform init` falhar com erro de download do provider, como:

```text
Failed to query available provider packages
```

Verifique a conexão com a internet, VPN ou proxy. Depois tente:

```powershell
terraform init -upgrade
```

### Kubernetes sem contexto configurado

Se aparecer erro parecido com:

```text
error: current-context is not set
```

O `kubectl` ainda não está apontando para um cluster local.

No Docker Desktop, habilite Kubernetes e rode:

```powershell
kubectl config get-contexts
kubectl config use-context docker-desktop
kubectl cluster-info
```

Se `docker-desktop` não aparecer na lista, o Kubernetes do Docker Desktop ainda não foi habilitado ou terminou de iniciar.

### PVC do Postgres travado no Terraform

Se o Terraform ficar vários minutos em:

```text
kubernetes_persistent_volume_claim_v1.postgres: Still creating...
```

e depois falhar com:

```text
client rate limiter Wait returned an error: context deadline exceeded
```

o Kubernetes local provavelmente não conseguiu criar o volume persistente do PostgreSQL. Confira os StorageClasses disponíveis:

```powershell
kubectl get storageclass
```

No Docker Desktop, o StorageClass normalmente é:

```text
hostpath
```

No Minikube, normalmente é:

```text
standard
```

Se o StorageClass aparecer com `VOLUMEBINDINGMODE` igual a `WaitForFirstConsumer`, isso é esperado em alguns clusters locais. Nesse modo, o volume só é vinculado quando existir um pod consumidor, como o pod do PostgreSQL. Por isso o Terraform foi configurado com `wait_until_bound = false` no PVC, evitando que ele espere o bind do volume antes de criar o Deployment do banco.

O valor usado pelo Terraform fica em `infra\terraform.tfvars`:

```hcl
postgres_storage_class_name = "hostpath"
```

Se estiver usando Minikube, troque para:

```hcl
postgres_storage_class_name = "standard"
```

Depois tente aplicar novamente:

```powershell
cd "C:\Users\thais\OneDrive\Área de Trabalho\proj-tc-1-fiap\infra"
terraform apply
```

Para investigar o PVC:

```powershell
kubectl get pvc -n oficina
kubectl describe pvc postgres-data -n oficina
```

### Imagem local não encontrada no Kubernetes

Se o pod da API ficar com erro como `ImagePullBackOff` ou `ErrImageNeverPull`, gere a imagem local antes do `terraform apply`:

```powershell
cd "C:\Users\thais\OneDrive\Área de Trabalho\proj-tc-1-fiap"
docker build -t oficina-api:local .
```

Depois recrie ou atualize a infraestrutura:

```powershell
cd infra
terraform apply
```

### NodePort não abre no localhost

Se os pods estiverem `Running`, o Service existir, mas `http://localhost:30081` não abrir, use `port-forward`:

```powershell
kubectl port-forward -n oficina service/oficina-api 18081:8081
```

Depois acesse:

```text
http://localhost:18081/swagger-ui/index.html
```

Para confirmar que o Service aponta para o pod da API:

```powershell
kubectl get endpoints -n oficina
kubectl describe service oficina-api -n oficina
```

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
