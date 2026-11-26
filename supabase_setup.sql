-- ПАРТНЁР CRM: общая облачная база Supabase
-- Выполнить целиком в Supabase Dashboard -> SQL Editor -> New query.

create table if not exists public.partner_crm_data (
    id text primary key check (id = 'shared'),
    payload jsonb not null default '{}'::jsonb,
    updated_at timestamptz not null default now(),
    updated_by uuid references auth.users(id)
);

create or replace function public.partner_crm_data_set_updated_at()
returns trigger language plpgsql set search_path = public as $$
begin new.updated_at = now(); return new; end;
$$;

drop trigger if exists partner_crm_data_updated_at on public.partner_crm_data;
create trigger partner_crm_data_updated_at before update on public.partner_crm_data
for each row execute function public.partner_crm_data_set_updated_at();

alter table public.partner_crm_data enable row level security;
grant select, insert, update on public.partner_crm_data to authenticated;

drop policy if exists "CRM users can read shared data" on public.partner_crm_data;
create policy "CRM users can read shared data" on public.partner_crm_data
for select to authenticated using (id = 'shared');

drop policy if exists "CRM users can create shared data" on public.partner_crm_data;
create policy "CRM users can create shared data" on public.partner_crm_data
for insert to authenticated with check (id = 'shared' and auth.uid() is not null);

drop policy if exists "CRM users can update shared data" on public.partner_crm_data;
create policy "CRM users can update shared data" on public.partner_crm_data
for update to authenticated using (id = 'shared' and auth.uid() is not null)
with check (id = 'shared' and auth.uid() is not null);
