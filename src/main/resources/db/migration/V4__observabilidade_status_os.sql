-- No invented timestamps for pre-existing orders.
create table ordem_servico_status_historico (
    id bigint generated always as identity primary key,
    ordem_id uuid not null references ordem_servico(id) on delete cascade,
    status_anterior integer,
    status_atual integer not null,
    entrou_em timestamptz not null,
    duracao_anterior_ms bigint
);
create index idx_os_status_historico_ordem on ordem_servico_status_historico(ordem_id, id desc);

create function registrar_historico_status_os() returns trigger language plpgsql as $$
declare
    inicio timestamptz;
    agora timestamptz := clock_timestamp();
begin
    if TG_OP = 'INSERT' then
        insert into ordem_servico_status_historico(ordem_id, status_atual, entrou_em)
        values (NEW.id, NEW.status_ordem_servico, agora);
    elsif NEW.status_ordem_servico is distinct from OLD.status_ordem_servico then
        select entrou_em into inicio from ordem_servico_status_historico
        where ordem_id = NEW.id order by id desc limit 1;
        insert into ordem_servico_status_historico
            (ordem_id, status_anterior, status_atual, entrou_em, duracao_anterior_ms)
        values (NEW.id, OLD.status_ordem_servico, NEW.status_ordem_servico, agora,
            case when inicio is null then null
                 else greatest(0, floor(extract(epoch from (agora - inicio)) * 1000)::bigint) end);
    end if;
    return NEW;
end;
$$;
create trigger trg_os_status_historico after insert or update on ordem_servico
for each row execute function registrar_historico_status_os();
