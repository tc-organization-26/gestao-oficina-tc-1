# RFC-0003 — Autenticação com Lambda e JWT

- **Data:** 02/10/2026
- **Status:** Encerrada – Aprovada

## Resumo

Identificar clientes no ambiente AWS por login em Lambda, emitir JWT e validar o token no API Gateway e na API Java, registrando os limites da identificação apenas por CPF.

## Problema

A API expõe operações sobre clientes, veículos, estoque e ordens de serviço. A entrada na nuvem precisa rejeitar chamadas sem token válido e transportar a identidade do cliente para a aplicação. O mecanismo também precisa se integrar ao cadastro ativo existente e ao ambiente educacional.

## Proposta técnica

Disponibilizar `POST /auth/login` público no API Gateway, integrado à Lambda Python. A função normaliza e valida o CPF, consulta o cliente ativo no PostgreSQL e emite JWT HS256 com `sub` igual ao UUID do cliente, `papel=CLIENTE`, `iat` e `exp`, com validade padrão de 3600 segundos.

Proteger as rotas de negócio com Lambda Authorizer e validar o JWT novamente no filtro Spring. Compartilhar a chave de assinatura pelo Secrets Manager; o workflow da API fornece a chave à aplicação via Secret Kubernetes. A Lambda de login acessa o RDS na VPC; o Authorizer não precisa consultar o banco e fica fora da VPC.

Manter as rotas de saúde previstas como públicas. No ambiente local, o endpoint Java recebe `login` e `senha`; esse contrato difere do login por CPF integrado ao Gateway na AWS.

## Impacto esperado

A validação na entrada evita encaminhar requisições sem token aceito à API, e a validação Spring mantém a exigência dentro da aplicação. A emissão e a validação dependem de configuração consistente da chave; HS256 exige proteger o segredo em todos os componentes que o conhecem.

CPF válido e cadastro ativo não comprovam a identidade de quem envia o documento. O mecanismo atual é demonstrativo e não equivale a autenticação forte. A API ainda não aplica regras de permissão por papel nem vincula automaticamente `sub` ao `clienteId` solicitado. A aceitação de um JWT não assegura isolamento de dados por cliente. Alterar o cadastro para inativo após emitir um token também não implica revogação imediata no Authorizer.

## Alternativas consideradas

Nesta comparação retrospectiva, concentrar o login na API reduziria componentes e aproximaria os contratos local e AWS, mas acoplaria a emissão de tokens ao ciclo da aplicação. Um provedor de identidade, como Cognito ou outro serviço OIDC, permitiria estruturar credenciais e fluxos de identidade, exigindo integração e migração do login atual. Assinatura assimétrica permitiria separar a chave de emissão das chaves de verificação, mas exigiria gestão e rotação correspondentes. Não há registro de protótipos dessas alternativas.

## Pontos em aberto

- Evoluir a comprovação de identidade e definir autorização por papel e por recurso.
- Definir rotação da chave, revogação de tokens e tratamento de clientes desativados.
- Avaliar alinhamento dos contratos local e AWS e proteção contra abuso no login.

Registro retrospectivo individual, conforme a [convenção dos documentos](../README.md). Decisão no [ADR-0003](../adrs/0003-adotar-autenticacao-com-jwt-e-lambda.md). Evidências: [fluxo integrado](../../ARQUITETURA-SOLUCAO.md) e [handler de autenticação](https://github.com/tc-organization-26/oficina-auth-lambda/blob/main/src/handler.py).
