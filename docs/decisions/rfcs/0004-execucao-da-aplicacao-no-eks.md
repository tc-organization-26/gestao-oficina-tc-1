# RFC-0004 — Execução da Oficina API no Amazon EKS

- **Data:** 02/10/2026
- **Status:** Encerrada – Aprovada

## Resumo

Executar a imagem da Oficina API em Kubernetes gerenciado pelo EKS, usando Deployment, probes e escalabilidade de Pods e nós para acompanhar a demanda.

## Problema

A API precisa sobreviver à substituição de processos e permitir alteração de capacidade sem configuração manual de cada instância. A expansão do atendimento motiva mecanismos de escala e deploy reproduzíveis. Ao mesmo tempo, o projeto individual e o ambiente educacional impõem limites de operação, permissões e custo.

## Proposta técnica

Provisionar EKS e Managed Node Group privados pelo repositório de plataforma. Publicar a imagem Docker no GHCR e aplicar os manifests da API pelo GitHub Actions. Usar ConfigMap e Secret para configuração, startup/readiness/liveness probes para acompanhar a inicialização e a saúde, e Service NodePort como destino do NLB interno.

Expor o tráfego da aplicação pelo caminho API Gateway → VPC Link → NLB → Service → Pods. Aplicar HPA de 2 a 6 réplicas com Metrics Server e usar Cluster Autoscaler para ajustar a capacidade dos nós. O endpoint administrativo EKS permanece público na configuração atual para o deploy pelo Actions; isso é distinto de expor a API de negócio diretamente.

## Impacto esperado

Kubernetes oferece declaração do estado desejado, substituição de Pods e mecanismos de rollout e escala. Esses mecanismos não garantem disponibilidade por si: dependem de capacidade, probes corretas, rede, banco e distribuição efetiva das réplicas.

EKS exige administrar manifests, versões, add-ons, permissões e diagnóstico do cluster. Há custos do serviço, nós, rede e coleta de telemetria, inclusive em períodos ociosos. O uso de GHCR requer conectividade e credenciais quando aplicável. Mais Pods podem aumentar o número de conexões ao RDS sem resolver gargalos de banco.

## Alternativas consideradas

Na comparação retrospectiva, ECS/Fargate reduziria a administração de nós em uma abordagem de contêineres sem Kubernetes, exigindo mudar o modelo de implantação. EC2 com Docker teria menos objetos de orquestração, mas transferiria mais tarefas de disponibilidade e escala à operação. Kubernetes autogerenciado acrescentaria administração do plano de controle. Não há benchmark de custo ou desempenho dessas alternativas registrado.

## Pontos em aberto

- Medir custo e capacidade do cluster em cenários de uso representativos.
- Validar comportamento sob carga, falha de nós e atualização da aplicação.
- Avaliar distribuição de réplicas, orçamento de interrupção e acesso administrativo ao migrar do laboratório.

Registro retrospectivo individual, conforme a [convenção dos documentos](../README.md). Decisão no [ADR-0006](../adrs/0006-adotar-eks.md). Evidências: [Deployment](../../../k8s/cloud/deployment.yaml), [HPA](../../../k8s/cloud/hpa.yaml) e [plataforma](https://github.com/tc-organization-26/oficina-k8s-infra/blob/main/main.tf).
