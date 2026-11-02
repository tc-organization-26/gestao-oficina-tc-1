# ADR-0003 — Adotar autenticação com JWT e Lambda

- **Data:** 02/10/2026
- **Status:** Aceita

## Contexto

A entrada AWS precisa validar tokens antes de encaminhar chamadas às operações da Oficina e fornecer a identidade do cliente à API. O mecanismo implementado integra o cadastro ativo do PostgreSQL ao login por CPF. Este é um registro retrospectivo da adoção individual, sem comentários ou aprovações externas.

A [RFC-0003](../rfcs/0003-estrategia-de-autenticacao.md) detalha a proposta, suas limitações e alternativas em comparação retrospectiva.

## Decisão

Disponibilizamos login público por CPF em Lambda Python. Validamos o formato do documento e a existência de cliente ativo no RDS antes de emitir JWT HS256 com UUID em `sub`, `papel=CLIENTE`, `iat` e `exp`, com validade padrão de uma hora.

Validamos os tokens no Lambda Authorizer do Gateway e novamente no filtro Spring. Compartilhamos a chave pelo Secrets Manager e fornecemos sua configuração à API via Secret Kubernetes. Mantemos a Lambda de login na VPC para consultar o RDS e o Authorizer fora da VPC. Preservamos as rotas públicas de saúde previstas no Gateway. O login Java local continua com contrato `login` e `senha`.

## Consequências

O Gateway rejeita chamadas sem token aceito antes da integração, e a aplicação mantém validação própria. A chave precisa ser protegida e sincronizada entre os componentes; com HS256, quem conhece o segredo de verificação também pode assinar tokens.

Conhecer CPF não comprova identidade: o mecanismo demonstra o fluxo, sem senha ou segundo fator na Lambda. A autorização atual não diferencia papéis nem garante que o cliente autenticado seja o proprietário do recurso solicitado. A validação do token não consulta continuamente o estado do cadastro. Rotação, revogação, controle por recurso e evolução para autenticação forte permanecem pontos de evolução. Os contratos local e AWS diferentes exigem cenários de validação próprios.

Evidências: [fluxo de autenticação](../../ARQUITETURA-SOLUCAO.md) e [implementação Lambda](https://github.com/tc-organization-26/oficina-auth-lambda/blob/main/src/handler.py).
