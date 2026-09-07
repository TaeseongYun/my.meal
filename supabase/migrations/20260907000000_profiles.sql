-- 유저 정보 등록 대상 테이블. auth.users(이메일 가입/카카오 가입 모두)에서 트리거로 동기화된다.
create table if not exists public.profiles (
  id uuid primary key references auth.users (id) on delete cascade,
  email text,
  nickname text,
  avatar_url text,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

alter table public.profiles enable row level security;

drop policy if exists profiles_select_own on public.profiles;
create policy profiles_select_own on public.profiles
  for select to authenticated using ((select auth.uid()) = id);

drop policy if exists profiles_update_own on public.profiles;
create policy profiles_update_own on public.profiles
  for update to authenticated using ((select auth.uid()) = id) with check ((select auth.uid()) = id);

-- 가입(insert)과 이메일/메타데이터 변경(update, 카카오 계정에 이메일을 붙일 때)을 한 함수로 처리.
create or replace function public.sync_profile()
returns trigger
language plpgsql
security definer
set search_path = ''
as $$
begin
  insert into public.profiles (id, email, nickname, avatar_url)
  values (
    new.id,
    new.email,
    coalesce(
      new.raw_user_meta_data ->> 'nickname',
      new.raw_user_meta_data ->> 'name',
      new.raw_user_meta_data ->> 'full_name'
    ),
    coalesce(
      new.raw_user_meta_data ->> 'avatar_url',
      new.raw_user_meta_data ->> 'picture'
    )
  )
  on conflict (id) do update set
    email = coalesce(excluded.email, public.profiles.email),
    nickname = coalesce(excluded.nickname, public.profiles.nickname),
    avatar_url = coalesce(excluded.avatar_url, public.profiles.avatar_url),
    updated_at = now();
  return new;
end;
$$;

drop trigger if exists on_auth_user_changed on auth.users;
create trigger on_auth_user_changed
  after insert or update of email, raw_user_meta_data on auth.users
  for each row execute function public.sync_profile();

-- 트리거 전용 함수라 REST rpc(/rest/v1/rpc/sync_profile)로 노출될 이유가 없다
-- (Supabase security advisor 0028/0029)
revoke execute on function public.sync_profile() from public, anon, authenticated;
