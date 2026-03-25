insert into members(id, email, name, membership_status, joined_at) values
	(UUID '11111111-1111-1111-1111-111111111111', 'librarian@bookvault.test', 'Default Librarian', 'ACTIVE', CURRENT_TIMESTAMP),
	(UUID '22222222-2222-2222-2222-222222222222', 'member@bookvault.test', 'Default Member', 'ACTIVE', CURRENT_TIMESTAMP);

insert into vault_users(id, email, password_hash, role, member_id) values
	(UUID '33333333-3333-3333-3333-333333333333', 'librarian@bookvault.test', 'password123', 'LIBRARIAN', UUID '11111111-1111-1111-1111-111111111111'),
	(UUID '44444444-4444-4444-4444-444444444444', 'member@bookvault.test', 'password123', 'MEMBER', UUID '22222222-2222-2222-2222-222222222222');

