-- Partner CRM: shared cloud database for desktop and Android
-- Run this script in Supabase Dashboard -> SQL Editor -> New query.

create table if not exists public.crm_state (
  id integer primary key check (id = 1),
  revision bigint not null default 1,
  data jsonb not null default '{}'::jsonb,
  updated_at timestamptz not null default now(),
  updated_by text
);

alter table public.crm_state enable row level security;

grant select, insert, update on public.crm_state to authenticated;

drop policy if exists "crm_authenticated_read" on public.crm_state;
create policy "crm_authenticated_read"
  on public.crm_state for select to authenticated
  using (true);

drop policy if exists "crm_authenticated_insert" on public.crm_state;
create policy "crm_authenticated_insert"
  on public.crm_state for insert to authenticated
  with check (true);

drop policy if exists "crm_authenticated_update" on public.crm_state;
create policy "crm_authenticated_update"
  on public.crm_state for update to authenticated
  using (true)
  with check (true);

-- No anonymous access and no delete policy.
