# ADR-0005 — Adotar HPA para a Oficina API

- **Data:** 02/10/2026
- **Status:** Aceita

## Contexto

O aumento de atendimentos pode elevar consumo de CPU e memória da API. Um número fixo de réplicas exige intervenção para acompanhar variações. A aplicação é executada em Kubernetes e dispõe de Metrics Server e Cluster Autoscaler. Os valores registrados são os parâmetros atuais do código, sem comprovação de dimensionamento por teste de carga neste documento. A adoção é individual e o registro é retrospectivo.

## Decisão

Aplicamos HPA `autoscaling/v2` ao Deployment `oficina-api`, mantendo entre duas e seis réplicas. Usamos alvos de utilização média de CPU de 70% e memória de 80%, relativos aos respectivos requests. O Deployment solicita `250m` de CPU e `512Mi` de memória por contêiner, com limites de `500m` e `768Mi`.

Dependemos do Metrics Server para fornecer as métricas. Mantemos o Cluster Autoscaler como mecanismo complementar para capacidade de nós, com intervalo padrão de um a quatro nós na plataforma. Aumentar réplicas e aumentar nós são controles distintos; a criação de Pods não assegura capacidade imediata para agendá-los.

## Consequências

O HPA automatiza o ajuste de réplicas a partir do consumo observado. Duas réplicas constituem o piso configurado, mas não garantem distribuição entre zonas ou nós. O máximo de seis limita a expansão automática, sem assegurar atendimento de qualquer volume de tráfego.

Métricas ausentes, requests inadequados ou falta de capacidade prejudicam o funcionamento. CPU e memória não medem diretamente fila ou latência; uso de memória da JVM pode não diminuir proporcionalmente após escalar. Mais réplicas aumentam consumo, custo e conexões ao RDS. Os alvos precisam de validação sob carga e revisão se banco ou rede forem o gargalo.

Evidências: [HPA](../../../k8s/cloud/hpa.yaml), [Deployment](../../../k8s/cloud/deployment.yaml) e [escalabilidade da plataforma](https://github.com/tc-organization-26/oficina-k8s-infra/blob/main/autoscaling.tf).
