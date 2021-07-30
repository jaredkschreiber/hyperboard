-- user: admin
-- password: 12345

--initial admin user, feel free to demote it to a regular account afterwards

INSERT INTO users (
user_name
,user_password
,global_admin
,global_mod) VALUES (
'admin'
,'$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG'
,true
,true
) ON CONFLICT DO NOTHING;