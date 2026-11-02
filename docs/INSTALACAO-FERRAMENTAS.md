# Instalação das ferramentas

[README](../README.md) · [Versões de referência](VERSOES.md) · [Execução local](EXECUCAO-LOCAL.md)

Este guia prepara o computador para desenvolver e operar os quatro repositórios. Os exemplos usam Windows e PowerShell. Instale somente as ferramentas necessárias ao fluxo escolhido; os runners do GitHub Actions têm setup próprio nos workflows.

## O que instalar

| Objetivo | Ferramentas |
| --- | --- |
| API e PostgreSQL com Compose | Git e Docker Desktop com Docker Compose |
| Java e Maven fora do contêiner | JDK 21; o Maven Wrapper já está no repositório |
| Testes e pacote da autenticação | Python 3.13 com pip |
| Validar Terraform | Terraform; PowerShell 7 para o patch atual do módulo EKS |
| Executar Terraform na AWS | Terraform, AWS CLI v2 e autenticação na conta; PowerShell 7 nos scripts |
| Administrar EKS e diagnosticar Pods | AWS CLI v2 e kubectl compatível com o cluster |
| Operar observabilidade manualmente | Helm 3 compatível com o workflow |
| Operar GitHub pelo terminal | GitHub CLI; necessário nos scripts que cadastram variables pelo comando `gh` |
| Demonstrar chamadas | Insomnia ou Swagger no navegador |

As [versões do projeto](VERSOES.md) e os arquivos `versions.tf`/workflows de cada componente são a referência. Um instalador que oferece a versão mais recente pode instalar uma major diferente da utilizada pelo projeto.

## Git e PowerShell 7 no Windows

Instale o Git pelo [distribuidor oficial](https://git-scm.com/install/windows). No instalador, habilite o uso do Git no terminal. Depois, abra um terminal novo e execute `git --version`.

Instale o PowerShell 7 conforme a [documentação Microsoft](https://learn.microsoft.com/en-us/powershell/scripting/install/installing-powershell-on-windows). Uma opção com WinGet é:

```powershell
winget install --id Microsoft.PowerShell --exact
pwsh --version
```

Abra **PowerShell 7** ou execute `pwsh` antes dos scripts do projeto. O Windows PowerShell 5.1, aberto por `powershell.exe`, é um produto diferente; o patch atual usa `utf8NoBOM`, disponível no PowerShell moderno.

## Docker Desktop e Compose

Siga a [instalação oficial para Windows](https://docs.docker.com/desktop/setup/install/windows-install/), incluindo os requisitos de virtualização e WSL 2 para o backend escolhido. Inicie o Docker Desktop e utilize contêineres Linux, como as imagens Java e PostgreSQL do projeto.

```powershell
docker version
docker compose version
docker info
```

`docker version` deve mostrar Client e Server. Se mostrar somente Client ou erro de conexão, aguarde o daemon iniciar. O Compose é executado como `docker compose`; não é necessário instalar o comando legado `docker-compose`. Não é necessário habilitar Kubernetes no Docker Desktop para executar o Compose.

## JDK 21 e Maven Wrapper

Instale o **JDK 21** do Eclipse Temurin pela [documentação Adoptium](https://adoptium.net/installation/), escolhendo a arquitetura do computador. Habilite a configuração de `JAVA_HOME` e a inclusão no `PATH`, quando oferecidas pelo instalador.

Em um terminal novo, na raiz da API:

```powershell
java -version
javac -version
./mvnw.cmd --version
```

Confira que Java e Maven Wrapper usam Java 21. O Wrapper baixa o Maven configurado em `.mvn/wrapper/maven-wrapper.properties`; não exige Maven global. Para executar os testes, prepare também o PostgreSQL conforme o [guia local](EXECUCAO-LOCAL.md#build-e-testes).

## Python 3.13

Instale Python **3.13** para corresponder ao runtime da Lambda e ao CI. Use a [documentação oficial para Windows](https://docs.python.org/3/using/windows.html), habilitando o interpretador no terminal. Se houver várias versões, selecione explicitamente a 3.13 pelo launcher disponível no computador:

```powershell
py -3.13 --version
py -3.13 -m pip --version
```

Se o launcher `py` não estiver disponível, use `python --version` e confirme a versão antes de seguir. Execute os testes e o empacotamento no repositório `oficina-auth-lambda`, conforme seu guia de validação manual; pip deve instalar as dependências no interpretador/pasta usados nesse fluxo.

## Terraform no Windows

O código exige Terraform **>= 1.10.0** e os workflows adotam **1.15.9**. Para reproduzir o CI, escolha essa versão; não confunda a versão da CLI com as versões dos providers nos lockfiles.

1. Abra a [página oficial de instalação](https://developer.hashicorp.com/terraform/install) e selecione Windows e a versão desejada no catálogo de releases.
2. Baixe o ZIP para a arquitetura do computador, como AMD64 em Windows x64, e confira o checksum publicado para esse arquivo.
3. Extraia `terraform.exe` para uma pasta estável, por exemplo `C:\Tools\Terraform\1.15.9`.
4. Abra **Editar as variáveis de ambiente da sua conta → Path → Editar → Novo** e acrescente essa pasta. Não substitua o conteúdo existente de `Path`.
5. Abra um terminal novo e confira qual executável foi encontrado:

```powershell
Get-Command terraform -All
terraform version
```

Se houver várias instalações, ajuste a prioridade no `Path`. O diretório `.terraform` de cada repositório contém providers e módulos baixados pelo `terraform init`; ele não é a instalação da CLI.

Para validar, siga o guia de cada componente. Na plataforma atual, a sequência inclui `terraform init -backend=false`, o patch educacional via `pwsh` e `terraform validate`. Na adaptação para conta comum, retire o patch conforme o [guia de preparação AWS](https://github.com/tc-organization-26/oficina-k8s-infra/blob/main/docs/CONTA-AWS-COMUM.md).

## AWS CLI v2

Instale a CLI v2 usando o MSI indicado na [documentação AWS para Windows](https://docs.aws.amazon.com/cli/latest/userguide/getting-started-install.html). Abra um terminal novo e execute:

```powershell
aws --version
```

O resultado deve começar com `aws-cli/2`. Instalar a ferramenta não configura a identidade. Use as credenciais temporárias do Academy conforme os guias atuais ou configure um perfil federado conforme o [roteiro de conta comum](https://github.com/tc-organization-26/oficina-k8s-infra/blob/main/docs/CONTA-AWS-COMUM.md#identidade-humana-e-perfil-local).

## kubectl e Helm

Instale kubectl conforme a [documentação Kubernetes para Windows](https://kubernetes.io/docs/tasks/tools/install-kubectl-windows/), selecionando uma versão compatível com o Kubernetes 1.34 adotado no projeto. Não instale automaticamente a versão mais recente sem conferir a compatibilidade; a política suporta diferença de uma minor entre cliente e servidor.

```powershell
kubectl version --client
Get-Command kubectl -All
```

Docker Desktop pode fornecer outro `kubectl`; confira qual está no `Path`. A instalação não cria contexto EKS. Depois que o cluster existir e a identidade tiver acesso, use `aws eks update-kubeconfig` conforme o guia AWS.

Para operar charts manualmente, siga a [instalação Helm](https://helm.sh/docs/intro/install/) e selecione **Helm 3**, compatível com o fluxo que fixa 3.17.3. Confira com `helm version`. O provider Helm do Terraform instala os charts da plataforma sem exigir o binário Helm no computador; a CLI é necessária para operações manuais que usem `helm`.

## GitHub CLI e Insomnia

Instale GitHub CLI pela [página oficial](https://cli.github.com/), abra um terminal novo e execute:

```powershell
gh --version
gh auth login
gh auth status
```

A identidade precisa ter acesso aos repositórios da organização. Acesso de leitura não concede administração de Actions Variables; confira as permissões do fluxo que será executado.

Instale Insomnia pelo distribuidor oficial e importe a collection indicada no README da API. Prepare o ambiente conforme o [roteiro do fluxo completo](VALIDACAO-FLUXO.md), em vez de reutilizar URLs, tokens ou IDs exportados.

## Linux e macOS

Use os links oficiais acima para os pacotes do seu sistema, mantendo as versões principais do projeto. Na API, substitua `./mvnw.cmd` por `./mvnw`; scripts `.ps1` continuam exigindo PowerShell 7 (`pwsh`). Ajuste os comandos de variáveis de ambiente para o shell utilizado. Os exemplos PowerShell dos guias não devem ser colados diretamente no Bash.

## Conferência antes de executar

Para Compose, confirme o daemon Docker. Para Java direto, confirme JDK 21 e banco disponível. Para Terraform, confirme CLI e downloads de providers. Para operações AWS, execute `aws sts get-caller-identity` e confira conta/região. Uma instalação correta das ferramentas não substitui permissões IAM ou acesso ao Kubernetes.
