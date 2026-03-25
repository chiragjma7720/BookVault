create table books (
	id uuid not null,
	isbn varchar(32) not null,
	title varchar(200) not null,
	author varchar(200) not null,
	genre varchar(100) not null,
	total_copies integer not null,
	available_copies integer not null,
	primary key (id),
	constraint uk_books_isbn unique (isbn),
	constraint ck_books_copies check (available_copies >= 0 and total_copies >= 1 and available_copies <= total_copies)
);

create table members (
	id uuid not null,
	email varchar(254) not null,
	name varchar(200) not null,
	membership_status varchar(16) not null,
	joined_at timestamp not null,
	primary key (id),
	constraint uk_members_email unique (email)
);

create table loans (
	id uuid not null,
	book_id uuid not null,
	member_id uuid not null,
	borrowed_at timestamp not null,
	due_date timestamp not null,
	returned_at timestamp,
	status varchar(16) not null,
	primary key (id),
	constraint fk_loans_book foreign key (book_id) references books(id),
	constraint fk_loans_member foreign key (member_id) references members(id),
	constraint ck_loans_status check (status in ('ACTIVE','RETURNED','OVERDUE'))
);

create table vault_users (
	id uuid not null,
	email varchar(254) not null,
	password_hash varchar(200) not null,
	role varchar(16) not null,
	member_id uuid not null,
	primary key (id),
	constraint uk_vault_users_email unique (email),
	constraint fk_vault_users_member foreign key (member_id) references members(id),
	constraint ck_vault_users_role check (role in ('LIBRARIAN','MEMBER'))
);

create index ix_loans_member_id on loans(member_id);
create index ix_loans_due_date on loans(due_date);
