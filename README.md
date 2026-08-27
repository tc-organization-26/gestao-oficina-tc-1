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

Execução local é usada apenas para desenvolvimento, testes manuais e validação da infraestrutura. O deploy contínuo do projeto deve acontecer pelo GitHub Actions, usando runner do GitHub. Portanto, não é necessário executar localmente a sequência completa de Continuous Deployment.

## Versões necessárias para execução local

Estas são as versões identificadas no projeto ou no ambiente local usado para validar a execução.

| Aplicação / dependência | Versão | Origem |
| --- | --- | --- |
| Java / JDK | 21 | Definido em `pom.xml` e usado nas imagens `eclipse-temurin:21-jdk-alpine` e `eclipse-temurin:21-jre-alpine` |
| Java instalado nesta máquina | 21.0.11 | Saída local de `java -version` |
| Maven Wrapper | 3.3.4 | Definido em `.mvn/wrapper/maven-wrapper.properties` |
| Apache Maven usado pelo wrapper | 3.9.16 | Definido em `.mvn/wrapper/maven-wrapper.properties` |
| Spring Boot | 4.0.7 | Definido no `parent` do `pom.xml` |
| Springdoc OpenAPI / Swagger UI | 3.0.2 | Definido em `pom.xml` |
| JJWT | 0.12.6 | Definido em `pom.xml` |
| Build Helper Maven Plugin | 3.6.0 | Definido em `pom.xml` |
| PostgreSQL | 16 Alpine | Definido em `docker-compose.yml` e nos manifestos Kubernetes |
| Docker Desktop | 4.77.0 | Saída local de `docker version` |
| Docker Engine | 29.5.3 | Saída local de `docker version` |
| Docker Compose | v5.1.4 | Saída local de `docker compose version` |
| Terraform | >= 1.6.0 | Definido em `infra/providers.tf` |
| Terraform instalado nesta máquina | 1.15.9 | Saída local de `terraform version` |
| Provider Kubernetes do Terraform | ~> 2.31, lockado em 2.38.0 | Definido em `infra/providers.tf` e `infra/.terraform.lock.hcl` |
| kubectl instalado nesta máquina | v1.34.1 | Saída local de `kubectl version --client` |
| Kustomize embutido no kubectl | v5.7.1 | Saída local de `kubectl version --client` |
| Kubernetes local | v1.34.3 | Server Version do Kubernetes habilitado no Docker Desktop |
| Git | 2.54.0.windows.1 | Saída local de `git --version` |
| Insomnia | 13.1.0 | Versão local informada para importar e executar a collection de testes da API |

Para conferir as versões em outra máquina, use:

```bash
git --version
docker version
docker compose version
kubectl version --client
kubectl version
terraform version
```

Se for usar Docker Desktop com Kubernetes, confirme também a versão do Docker Desktop em `Settings > About` e a versão do cluster Kubernetes exibida em `Server Version` pelo comando `kubectl version`.

## Configuração das variáveis da aplicação

Não é necessário editar `src/main/resources/application.properties` para informar senha do banco, usuário, URL do banco ou segredo JWT.

Esse arquivo já está configurado para ler os valores a partir de variáveis de ambiente, como `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`, `JWT_SECRET`, `SECURITY_JWT_SECRET` e `SECURITY_JWT_EXPIRATION_SECONDS`.

Para Docker Compose, preencha o arquivo `.env`.

Para Kubernetes com Terraform, preencha o arquivo `infra/terraform.tfvars` ou use variáveis de ambiente `TF_VAR_*`.

Para execução sem Docker, configure essas variáveis diretamente no ambiente da máquina antes de iniciar a aplicação.

Os arquivos `.env` e `infra/terraform.tfvars` não devem ser versionados, pois podem conter senhas e segredos.

## Continuous Deployment no GitHub Actions

O workflow `.github/workflows/cicd.yml` configura Continuous Deployment. 

O fluxo esperado é:

- Push na branch `fase-2`: executa build da aplicação, testes automatizados e cria o pull request para a branch principal quando ainda não existir.
- Pull request aberto ou sincronizado: não dispara este workflow novamente.
- Merge/push em `main` ou `master`: executa build da aplicação, testes automatizados, build e publicação da imagem Docker, deploy no cluster Kubernetes, deploy do banco de dados com Terraform e aplicação dos manifestos YAML complementares.

O deploy do banco de dados e da aplicação é feito pelo Terraform. A aplicação de manifestos YAML complementares é feita pelo `kubectl apply -f k8s/cd`.

### Runner self-hosted para deploy

O job de deploy usa um runner próprio Windows:

```yaml
runs-on: [self-hosted, Windows]
```

Por isso, depois do merge em `main` ou `master`, é normal o GitHub Actions mostrar uma mensagem parecida com:

```text
Waiting for a runner to pick up this job...
Requested labels: self-hosted, Windows
```

Essa mensagem significa que o GitHub já decidiu executar o deploy, mas está esperando uma máquina Windows com o runner self-hosted online e registrada no repositório ou na organização.

Para configurar esse runner:

1. No GitHub, abra o repositório.
2. Acesse `Settings > Actions > Runners`.
3. Clique em `New self-hosted runner`.
4. Escolha `Windows`.
5. Selecione a arquitetura `x64`.
6. Abra o PowerShell na pasta onde deseja instalar o runner.
7. Crie uma pasta para o runner:

```powershell
mkdir actions-runner
cd actions-runner
```

8. Baixe o pacote do runner. O GitHub mostra o link atualizado na tela de configuração. Exemplo:

```powershell
Invoke-WebRequest -Uri https://github.com/actions/runner/releases/download/v2.336.0/actions-runner-win-x64-2.336.0.zip -OutFile actions-runner-win-x64-2.336.0.zip
```

9. Opcionalmente, valide o hash do arquivo usando o comando exibido pelo GitHub.
10. Extraia o pacote:

```powershell
Add-Type -AssemblyName System.IO.Compression.FileSystem
[System.IO.Compression.ZipFile]::ExtractToDirectory("$PWD/actions-runner-win-x64-2.336.0.zip", "$PWD")
```

11. Configure o runner usando a URL do repositório e o token temporário exibido pelo GitHub:

```powershell
.\config.cmd --url https://github.com/<organizacao-ou-usuario>/<repositorio> --token <TOKEN_GERADO_PELO_GITHUB>
```

No momento da configuração, aceite ou informe os labels do runner. Para este projeto, o runner precisa atender aos labels usados no workflow:

```yaml
runs-on: [self-hosted, Windows]
```

12. Inicie o runner com:

```powershell
.\run.cmd
```

Enquanto esse comando estiver aberto, o GitHub consegue executar jobs que pedem os labels `self-hosted` e `Windows`.

Se quiser deixar o runner sempre disponível no Windows, responda `Y` quando o `config.cmd` perguntar:

```text
Would you like to run the runner as service? (Y/N)
```

Se o serviço for instalado, mas não iniciar automaticamente, abra o PowerShell como Administrador e execute:

```powershell
Start-Service -Name "actions.runner.tc-organization-26-gestao-oficina-tc-1.THAPC26"
```

Também é possível abrir `services.msc`, localizar `GitHub Actions Runner (...)` e iniciar o serviço pela interface do Windows.

Se o deploy ficar parado esperando runner por muito tempo, verifique se:

- O runner self-hosted está ligado.
- O runner aparece como `Online` em `Settings > Actions > Runners`.
- O runner possui os labels `self-hosted` e `Windows`.
- O runner foi registrado no repositório ou organização correta.
- A máquina do runner tem acesso ao cluster Kubernetes usado no deploy.

A máquina Windows usada como runner self-hosted precisa ter as ferramentas do deploy instaladas:

```powershell
kubectl version --client
terraform version
docker version
```

Neste projeto, o workflow chama o Terraform pelo valor da variable `TERRAFORM_EXE` configurada no GitHub Actions. Para esta máquina, o valor usado pode ser:

```text
C:\Program Files\Terraform\terraform.exe
```

Se essa variable não for configurada, o workflow usa `terraform`, esperando que o executável esteja disponível no `PATH` do runner.

Se o Windows mostrar erro `1068` ao iniciar o serviço do runner, como:

```text
Não foi possível iniciar o serviço ou grupo de dependência.
```

verifique com qual conta o serviço foi instalado. Quando ele fica configurado como `AUTORIDADE NT\SERVIÇO DE REDE`, pode faltar permissão para acessar a pasta `C:\Users\<usuario>\actions-runner`, Docker Desktop, kubeconfig ou ferramentas locais.

Para ajustar pela interface do Windows:

1. Abra `services.msc`.
2. Localize `GitHub Actions Runner (...)`.
3. Abra `Propriedades`.
4. Acesse a aba `Logon`.
5. Selecione `Esta conta`.
6. Informe seu usuário Windows, por exemplo `.\thais` ou `NOME_DO_COMPUTADOR\thais`.
7. Informe a senha do Windows.
8. Clique em `Aplicar`.
9. Inicie o serviço novamente.

Também é possível manter o runner rodando manualmente com `.\run.cmd`. Nesse modo, ele fica online apenas enquanto o terminal estiver aberto.

Para o workflow funcionar, configure estes secrets no repositório GitHub:

| Secret | Uso |
| --- | --- |
| `KUBE_CONFIG_BASE64` | Kubeconfig do cluster codificado em Base64, usado pelo runner self-hosted para acessar o Kubernetes |
| `POSTGRES_USER` | Usuário do PostgreSQL criado no cluster |
| `POSTGRES_PASSWORD` | Senha do PostgreSQL criada no cluster |
| `JWT_SECRET` | Segredo usado para assinar os tokens JWT da aplicação |
| `GHCR_USERNAME` | Usuário do GitHub Container Registry, necessário se a imagem estiver privada |
| `GHCR_TOKEN` | Token com permissão de leitura no GitHub Container Registry, necessário se a imagem estiver privada |

Configure também estas variables no repositório quando precisar sobrescrever os padrões:

| Variable | Uso |
| --- | --- |
| `KUBE_CONTEXT` | Contexto Kubernetes existente no kubeconfig. Pode ficar vazio se o kubeconfig já tiver `current-context` correto |
| `POSTGRES_STORAGE_CLASS_NAME` | StorageClass do PVC do PostgreSQL. Se não for informado, o workflow usa `hostpath` |
| `TERRAFORM_EXE` | Caminho do executável Terraform no runner self-hosted. Exemplo: `C:\Program Files\Terraform\terraform.exe`. Se não for informado, o workflow usa `terraform` |

Para gerar o valor de `KUBE_CONFIG_BASE64` no PowerShell:

```powershell
[Convert]::ToBase64String([IO.File]::ReadAllBytes("$env:USERPROFILE\.kube\config"))
```

No GitHub, adicione esse valor em `Settings > Secrets and variables > Actions > Secrets`.

O state do Terraform no Continuous Deployment usa backend Kubernetes, gravado como Secret no namespace `default` do cluster. Isso evita depender de arquivos locais do runner.

Importante: para caracterizar Continuous Deployment, o workflow não possui aprovação manual de ambiente. Depois de um push em `main` ou `master`, passando build e testes, o deploy segue automaticamente.

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
- Opcional para HPA funcional: Metrics Server instalado no cluster local.

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

Use esta sequência a partir da raiz do projeto:

```powershell
cd "<pasta raiz do seu projeto>"
kubectl config use-context docker-desktop
kubectl cluster-info
docker build -t oficina-api:local .
copy infra\terraform.tfvars.example infra\terraform.tfvars
notepad infra\terraform.tfvars
cd .\infra
terraform init -backend-config="config_path=$env:USERPROFILE\.kube\config" -backend-config="namespace=default" -backend-config="secret_suffix=oficina-api-local"
terraform plan
terraform apply
kubectl get all -n oficina
kubectl get pvc -n oficina
kubectl get hpa -n oficina
kubectl port-forward -n oficina service/oficina-api 18081:8081
```

Durante o `terraform apply`, o Terraform mostra o plano e pergunta se deve aplicar as mudanças. Quando aparecer a pergunta abaixo, digite exatamente `yes` e pressione `Enter`:

```text
Do you want to perform these actions?
  Enter a value:
```

Digite `yes` somente nessa confirmação do `terraform apply`. Se aparecer uma pergunta sobre `secret_suffix`, interrompa com `Ctrl+C` e rode `terraform init -reconfigure`, conforme a seção de problemas deste README.

O caminho `<pasta raiz do seu projeto>` representa a raiz do projeto nesta máquina. Em outra máquina, entre na pasta raiz onde o repositório foi clonado antes de executar os comandos.

O que cada etapa faz:

- `kubectl config use-context docker-desktop`: aponta o `kubectl` para o cluster local do Docker Desktop.
- `kubectl cluster-info`: confirma que o cluster Kubernetes está acessível.
- `docker build -t oficina-api:local .`: cria a imagem local usada pelo Deployment da API.
- `copy infra\terraform.tfvars.example infra\terraform.tfvars`: cria o arquivo local de variáveis sensíveis.
- `notepad infra\terraform.tfvars`: abre o arquivo para preencher usuário, senha do banco e segredo JWT.
- `terraform init`: baixa o provider Kubernetes e configura o backend Kubernetes para guardar o state local no cluster.
- `terraform plan`: mostra o que será criado no cluster.
- `terraform apply`: cria os recursos Kubernetes depois da confirmação manual com `yes`.
- `kubectl get all`, `kubectl get pvc` e `kubectl get hpa`: verificam os recursos criados.
- `kubectl port-forward`: libera acesso local à API pelo endereço `http://localhost:18081`.

Antes do `terraform apply`, confira se `infra\terraform.tfvars` foi preenchido com valores válidos.

Com o `port-forward` aberto, acesse:

```text
http://localhost:18081/swagger-ui/index.html
```

O arquivo `infra\terraform.tfvars` deve seguir este formato:

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

Como o projeto usa backend Kubernetes, se você trocar de cluster ou quiser recriar a configuração local do backend, rode:

```powershell
cd "<pasta raiz do seu projeto>\infra"
terraform init -reconfigure -backend-config="config_path=$env:USERPROFILE\.kube\config" -backend-config="namespace=default" -backend-config="secret_suffix=oficina-api-local"
```

Para Docker Desktop, o comando acima usa o kubeconfig padrão em `%USERPROFILE%\.kube\config`. Antes dele, confirme o contexto:

```powershell
kubectl config use-context docker-desktop
```

No Docker Desktop, a API também pode ficar disponível pelo `NodePort`:

```text
http://localhost:30081/swagger-ui/index.html
```

Se o `NodePort` não responder em `localhost`, use o `port-forward`, que é o caminho mais previsível para execução local:

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

O HPA depende do Metrics Server. Se o cluster local não tiver Metrics Server, o recurso será criado, mas as métricas podem aparecer como indisponíveis até instalar esse componente opcional.

### Opcional: instalar Metrics Server para HPA

O HPA da API usa métricas de CPU e memória. Em clusters locais, como Docker Desktop, essas métricas só ficam disponíveis quando o Metrics Server está instalado.

O Metrics Server não faz parte da infraestrutura atual provisionada pelo Terraform. Ele é um componente do cluster Kubernetes local e pode ser instalado separadamente usando o manifesto oficial:

```powershell
kubectl apply -f https://github.com/kubernetes-sigs/metrics-server/releases/latest/download/components.yaml
```

Depois confira se o Deployment ficou disponível:

```powershell
kubectl rollout status deployment/metrics-server -n kube-system
kubectl top nodes
kubectl top pods -n oficina
```

No Docker Desktop, se o Deployment do Metrics Server ficar sem disponibilidade e os logs mostrarem erro de certificado do kubelet, aplique o ajuste local abaixo:

```powershell
kubectl patch deployment metrics-server -n kube-system --type=strategic --patch-file k8s/metrics-server-docker-desktop-patch.json
kubectl rollout status deployment/metrics-server -n kube-system
```

Se `kubectl top` retornar métricas, o HPA consegue calcular o autoscaling:

```powershell
kubectl get hpa -n oficina
```

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
cd "<pasta raiz do seu projeto>\infra"
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

### Terraform pede `secret_suffix` ou informa backend não inicializado

Como o projeto usa backend Kubernetes para armazenar o state do Terraform, uma execução local de `terraform init` sem a configuração correta pode pedir um valor para `secret_suffix` ou falhar com mensagens parecidas com:

```text
secret_suffix
Enter a value:
```

ou:

```text
Error: Backend initialization required, please run "terraform init"
Reason: Initial configuration of the requested backend "kubernetes"
```

Se isso acontecer, confirme primeiro se o `kubectl` está apontando para o cluster local:

```powershell
kubectl config use-context docker-desktop
kubectl cluster-info
```

Depois, dentro da pasta `infra`, reconfigure o backend:

```powershell
cd "<pasta raiz do seu projeto>\infra"
terraform init -reconfigure
terraform plan
terraform apply
```

O `-reconfigure` força o Terraform a descartar a tentativa anterior de inicialização do backend e usar a configuração atual declarada em `infra/providers.tf`.

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
cd "<pasta raiz do seu projeto>\infra"
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
cd "<pasta raiz do seu projeto>"
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
