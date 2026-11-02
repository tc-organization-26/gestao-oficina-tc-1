insert into cliente (id, nome, cpf_cnpj, telefone, email, ativo)
values (
    '00000000-0000-0000-0000-000000000001',
    'Cliente Oficina',
    '00361937040',
    '11999999999',
    'cliente@oficina.local',
    true
)
on conflict (cpf_cnpj) do update
set nome = excluded.nome,
    telefone = excluded.telefone,
    email = excluded.email,
    ativo = true,
    atualizado_em = current_timestamp;
