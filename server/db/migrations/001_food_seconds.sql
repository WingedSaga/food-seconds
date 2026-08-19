create table if not exists public.food_member_roles (user_id uuid primary key references public.users(id) on delete cascade, role text not null check(role in ('member','manager')), created_at timestamptz not null default now(), updated_at timestamptz not null default now());
create table if not exists public.food_categories (id uuid primary key default gen_random_uuid(), name text not null, image_url text, sort_order integer not null default 0, is_visible boolean not null default true, created_at timestamptz not null default now());
create table if not exists public.food_items (id uuid primary key default gen_random_uuid(), category_id uuid references public.food_categories(id) on delete set null, name text not null, description text not null default '', image_url text, price_cents integer not null check(price_cents >= 0), sort_order integer not null default 0, is_visible boolean not null default true, is_available boolean not null default true, created_at timestamptz not null default now());
create table if not exists public.food_settings (key text primary key, value jsonb not null, updated_at timestamptz not null default now());
insert into public.food_settings(key,value) values ('site_name','"Еда секунды"'),('transfer_text','""'),('payment_cash_enabled','true'),('payment_transfer_enabled','true'),('sound_enabled','true'),('tips_enabled','true'),('tip_options','[{"id":"fixed50","label":"0,50 €","type":"fixed","value":0.5},{"id":"fixed100","label":"1 €","type":"fixed","value":1}]') on conflict(key) do nothing;
create sequence if not exists public.food_order_number_seq;
create table if not exists public.food_orders (id uuid primary key default gen_random_uuid(), number integer not null unique default nextval('public.food_order_number_seq'), user_id uuid not null references public.users(id), status text not null default 'new' check(status in ('new','cooking','ready','issued','cancelled')), payment_method text not null check(payment_method in ('cash','transfer')), payment_claimed boolean not null default false, comment text not null default '', food_total integer not null, tips_total integer not null default 0, grand_total integer not null, created_at timestamptz not null default now(), updated_at timestamptz not null default now());
create table if not exists public.food_order_items (id uuid primary key default gen_random_uuid(), order_id uuid not null references public.food_orders(id) on delete cascade, food_item_id uuid references public.food_items(id) on delete set null, item_name text not null, quantity integer not null check(quantity>0), unit_price integer not null check(unit_price>=0));
create index if not exists food_orders_status_idx on public.food_orders(status,created_at desc); create index if not exists food_items_category_idx on public.food_items(category_id,sort_order);
alter table public.food_member_roles enable row level security; alter table public.food_categories enable row level security; alter table public.food_items enable row level security; alter table public.food_settings enable row level security; alter table public.food_orders enable row level security; alter table public.food_order_items enable row level security;
grant all privileges on public.food_member_roles,public.food_categories,public.food_items,public.food_settings,public.food_orders,public.food_order_items to service_role; grant usage,select on sequence public.food_order_number_seq to service_role;
insert into public.food_categories(name,sort_order)
select seed.name, seed.sort_order
from (values ('Бургеры',1),('Напитки',2),('Десерты',3)) as seed(name,sort_order)
where not exists (select 1 from public.food_categories c where c.name = seed.name);

insert into public.food_items(category_id,name,description,price_cents,sort_order,image_url)
select c.id, seed.name, seed.description, seed.price_cents, seed.sort_order, seed.image_url
from (values
  ('Бургеры','Классический бургер','Сочная котлета, сыр и соус',650,1,'https://images.unsplash.com/photo-1568901346375-23c9450c58cd?auto=format&fit=crop&w=800'),
  ('Напитки','Лимонад','Домашний лимонад со льдом',250,1,'https://images.unsplash.com/photo-1523371054106-bbf80586c38c?auto=format&fit=crop&w=800'),
  ('Десерты','Шоколадный брауни','Тёплый брауни к чаю',390,1,'https://images.unsplash.com/photo-1606313564200-e75d5e30476c?auto=format&fit=crop&w=800')
) as seed(category_name,name,description,price_cents,sort_order,image_url)
join public.food_categories c on c.name = seed.category_name
where not exists (select 1 from public.food_items i where i.name = seed.name and i.category_id = c.id);
