create table if not exists users (
    user_id uuid primary key,
    user_pic uuid,
    user_name varchar(255) not null,
    user_login varchar(255) unique not null,
    user_password varchar(255) not null,
    status varchar(50) not null,
    user_role varchar(50) not null,
    created timestamp not null default now(),
    updated timestamp not null default now()
);

create table if not exists chats (
    chat_id uuid primary key,
    chat_name varchar(255) not null,
    status varchar(50) not null,
    creator_id uuid references users(user_id),
    created timestamp not null default now(),
    updated timestamp not null default now()
);

create table if not exists users_chats (
    user_id uuid references users(user_id),
    chat_id uuid references chats(chat_id),
    primary key (user_id, chat_id)
);

create table if not exists messages (
    message_id uuid primary key,
    chat_id uuid references chats(chat_id),
    user_id uuid references users(user_id),
    message text not null,
    status varchar(50) not null,
    created timestamp not null default now(),
    updated timestamp not null default now()
);

create table if not exists user_files (
    file_id uuid primary key,
    user_id uuid references users(user_id),
    file_mime_type varchar(150) not null
);

alter table users add CONSTRAINT IF NOT EXISTS fk_user_pic_file_id FOREIGN KEY (user_pic) REFERENCES user_files(file_id);