-- Sistema Integrado de Oficina - esquema inicial PostgreSQL 15+
-- Migration V0: baseline schema
begin;

create table cliente (
    id uuid primary key default gen_random_uuid(),
    nome varchar(150) not null,
    cpf_cnpj varchar(14) not null,
    telefone varchar(20),
    email varchar(254),
    ativo boolean not null default true,
    criado_em timestamptz not null default current_timestamp,
    atualizado_em timestamptz not null default current_timestamp
);
alter table cliente
    add constraint uk_cliente_cpf_cnpj unique (cpf_cnpj);

create table veiculo (
    id uuid primary key default gen_random_uuid(),
    cliente_id uuid not null references cliente(id),
    placa varchar(7) not null,
    marca varchar(80) not null,
    modelo varchar(100) not null,
    ano smallint not null,
    cor varchar(40),
    ativo boolean not null default true,
    criado_em timestamptz not null default current_timestamp,
    atualizado_em timestamptz not null default current_timestamp
);
alter table veiculo
    add constraint uk_veiculo_placa unique (placa);

create table servico (
    id uuid primary key default gen_random_uuid(),
    codigo varchar(30) not null,
    descricao varchar(255) not null,
    valor_unitario numeric(19,2) not null,
    tempo_estimado_minutos integer not null,
    ativo boolean not null default true,
    criado_em timestamptz not null default current_timestamp,
    atualizado_em timestamptz not null default current_timestamp
);

create table item_estoque (
    id uuid primary key default gen_random_uuid(),
    codigo varchar(40) not null,
    descricao varchar(255) not null,
    valor_unitario numeric(19,2) not null,
    quantidade_disponivel numeric(12,3) not null default 0,
    ativo boolean not null default true,
    criado_em timestamptz not null default current_timestamp,
    atualizado_em timestamptz not null default current_timestamp
);

create table ordem_servico (
    id uuid primary key default gen_random_uuid(),
    numero bigint generated always as identity unique,
    cliente_id uuid not null references cliente(id),
    veiculo_id uuid not null references veiculo(id),
    status_ordem_servico numeric(3,0) not null default 0,
    anotacoes text,
    data_recebimento timestamptz not null default current_timestamp,
    inicio_execucao_em timestamptz,
    finalizada_em timestamptz,
    entregue_em timestamptz
);

create table diagnostico (
    id uuid primary key default gen_random_uuid(),
    ordem_servico_id uuid not null unique references ordem_servico(id) on delete cascade,
    descricao text not null,
    criado_em timestamptz not null default current_timestamp,
    atualizado_em timestamptz not null default current_timestamp
);

create table orcamento (
    id uuid primary key default gen_random_uuid(),
    ordem_servico_id uuid not null references ordem_servico(id),
    status_orcamento numeric(3,0) not null default 0,
    gerado_em timestamptz not null default current_timestamp,
    aprovado_em timestamptz
);

create table orcamento_item_servico (
    id uuid primary key default gen_random_uuid(),
    orcamento_id uuid not null references orcamento(id) on delete cascade,
    servico_id uuid not null references servico(id),
    quantidade numeric(12,3) not null
);

create table orcamento_item_peca (
    id uuid primary key default gen_random_uuid(),
    orcamento_id uuid not null references orcamento(id) on delete cascade,
    item_estoque_id uuid not null references item_estoque(id),
    quantidade numeric(12,3) not null
);

create table movimentacao_estoque (
    id uuid primary key default gen_random_uuid(),
    item_estoque_id uuid not null references item_estoque(id),
    quantidade numeric(12,3) not null,
    ocorrido_em timestamptz not null default current_timestamp
);


commit;
