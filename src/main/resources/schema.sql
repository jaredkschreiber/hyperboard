CREATE TABLE if not exists users (
	id bigserial PRIMARY KEY,
	user_name varchar(30) unique not null,
	user_password text NOT NULL,
    global_admin boolean not null default false,
    global_mod BOOLEAN not null default false,
    create_dt date not null default CURRENT_DATE
);

CREATE TABLE if not exists entries (
	id bigserial PRIMARY KEY,
    ipaddr_txt varchar(50) not null,
	name_txt varchar(75),
    subject_txt varchar(75) not null,
    comment_txt text not null,
    create_dt TIMESTAMP not null,
    prune_dt TIMESTAMP not null,
    attachment_type_txt varchar(5),
    content_warning boolean not null,
    archive boolean not null
);

CREATE TABLE if not exists replies(
	id bigserial PRIMARY KEY,
    ipaddr_txt varchar(50) not null,
    fk_entry_id bigint not null references entries(id) ON DELETE CASCADE,
	name_txt varchar(75),
    comment_txt text not null,
    create_dt TIMESTAMP not null
);

CREATE TABLE if not exists tags(
	id bigserial PRIMARY KEY,
    fk_entry_id bigint not null references entries(id) ON DELETE CASCADE,
	tag_txt varchar(15) not null,
	unique(fk_entry_id,tag_txt)
);

CREATE TABLE if not exists replylinks(
	id bigserial PRIMARY KEY,
    fk_reply_id bigint not null references replies(id) ON DELETE CASCADE,
    fk_reply_id2 bigint not null references replies(id) ON DELETE CASCADE
);

CREATE TABLE if not exists bans(
	id bigserial PRIMARY KEY,
    ipaddr_txt varchar(50) not null,
    reason_txt varchar(250) not null,
    expiration_dt TIMESTAMP not null
);

CREATE TABLE if not exists reports(
	id bigserial PRIMARY KEY,
    ipaddr_txt varchar(50) not null,
    fk_reply_id bigint references replies(id) ON DELETE CASCADE,
    fk_entry_id bigint not null references entries(id) ON DELETE CASCADE,
    reason_txt varchar(250) not null
);