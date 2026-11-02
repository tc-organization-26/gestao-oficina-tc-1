# Tecnologias e versões da Oficina API

[README](../README.md) · [Referência da API](API.md) · [Execução local](EXECUCAO-LOCAL.md) · [Execução na AWS](EXECUCAO-AWS.md)

| Tecnologia | Versão / referência | Uso |
| --- | --- | --- |
| Java | 21 | Build e runtime |
| Spring Boot | 4.0.7 | Web MVC, Data JPA, Security e Actuator |
| PostgreSQL / Flyway | 16 (`16-alpine`) / 11.14.1 | Banco local e migrations |
| Maven / Maven Wrapper | 3.9.16 / 3.3.4 | Build e testes |
| Springdoc OpenAPI / JJWT | 3.0.2 / 0.12.6 | Swagger e JWT |
| Docker Desktop / Engine / Compose | 4.77.0 / 29.5.3 / 5.1.4 | Ambiente local de referência |
| Kubernetes/EKS / Helm CLI | 1.34 / 3.17.3 no deploy | Execução e instalação da observabilidade |
| Terraform / provider Datadog | 1.15.9 no CI; mínimo 1.10.0 / 4.27.0 no lock | Dashboards e monitores; constraint Datadog `~> 4.0` |
| Datadog Operator | Chart 2.31.0 | Métricas, logs e traces no EKS |
| GitHub Actions / GHCR | Serviços gerenciados | CI/CD e registry; versões das actions no workflow |

As versões deste documento são as referências adotadas pelo projeto. Versões fixadas no código, valores padrão e resoluções de lockfile estão identificados.

## Fontes das versões

- [pom.xml](../pom.xml): Java, Spring Boot, Springdoc OpenAPI e JJWT; demais bibliotecas Spring/Flyway são gerenciadas pelo Spring Boot.
- [Maven Wrapper](../.mvn/wrapper/maven-wrapper.properties): Maven e versão do wrapper.
- [Dockerfile](../Dockerfile) e [Compose](../docker-compose.yml): imagens Temurin 21 e PostgreSQL 16 Alpine.
- [Workflow](../.github/workflows/cicd.yml): Helm CLI, Terraform e chart Datadog Operator.
- [Terraform Datadog](../observability/datadog/versions.tf) e [lockfile](../observability/datadog/.terraform.lock.hcl): constraint e resolução do provider.

## Bibliotecas gerenciadas pelo Spring Boot

- Spring Web MVC `7.0.8`;
- Spring Data JPA `4.0.6`;
- Spring Security Core `7.0.6`;
- Hibernate Validator `9.0.1.Final`;
- driver JDBC PostgreSQL `42.7.11`;
- Lombok `1.18.46`.

## Build e testes

| Tecnologia | Versão | Uso |
| --- | --- | --- |
| ArchUnit | 1.4.1 | Verificação das dependências entre camadas |
| Build Helper Maven Plugin | 3.6.0 | Inclusão dos testes de integração no build |
| JaCoCo Maven Plugin | 0.8.12 | Relatório e verificação de cobertura |

## Outras ferramentas de ambiente

Docker Desktop, Engine e Compose estão na tabela principal. Para operação e validação, o ambiente de referência utiliza também:

- AWS CLI `2.36.34`;
- kubectl `1.34.1`;
- Git `2.54.0.windows.1`;
- Insomnia `13.1.0`.

Serviços como GitHub Actions, GHCR, S3 e Secrets Manager não possuem uma versão de produto fixada pelo projeto.
