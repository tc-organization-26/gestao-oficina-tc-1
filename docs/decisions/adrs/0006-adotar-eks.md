# ADR-0006 — Executar a aplicação no Amazon EKS

- **Data:** 02/10/2026
- **Status:** Aceita

## Contexto

A API precisa de implantação reproduzível, substituição de processos e ajuste de capacidade. A solução usa imagem Docker e infraestrutura AWS em ambiente educacional. A administração do cluster acrescenta trabalho a um projeto desenvolvido individualmente. Este documento registra retrospectivamente a escolha presente no código; a [RFC-0004](../rfcs/0004-execucao-da-aplicacao-no-eks.md) compara alternativas sem atribuir testes históricos.

## Decisão

Executamos a API no EKS com Managed Node Group em sub-redes privadas. Versionamos Deployment, Service, ConfigMap/Secret utilizados pelo deploy e HPA no ciclo de implantação da aplicação. Publicamos a imagem no GHCR e aplicamos os recursos pelo GitHub Actions.

Usamos startup, readiness e liveness probes, Metrics Server e Cluster Autoscaler como apoio à operação. Encaminhamos tráfego pelo Gateway e NLB interno, conforme o [ADR-0007](0007-manter-banco-e-aplicacao-em-rede-privada.md). Mantemos o endpoint administrativo do EKS público na configuração atual para acesso da esteira.

## Consequências

O estado desejado fica descrito em manifests e o Kubernetes pode substituir Pods e executar rollouts. O HPA do [ADR-0005](0005-adotar-hpa.md) permite variar réplicas conforme as métricas.

É necessário manter versões, add-ons, credenciais de imagem, permissões e capacidade dos nós. Há custos de EKS, nós e rede. A inicialização depende do banco e do Flyway; a instalação de observabilidade também participa da esteira. Réplicas e probes não asseguram disponibilidade sem capacidade e dependências saudáveis. Distribuição das réplicas e restrições ao endpoint administrativo devem ser avaliadas na evolução fora do laboratório.

Evidências: [Deployment](../../../k8s/cloud/deployment.yaml), [esteira da API](../../../.github/workflows/cicd.yml) e [plataforma](https://github.com/tc-organization-26/oficina-k8s-infra/blob/main/main.tf).
