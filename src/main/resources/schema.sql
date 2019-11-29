create table users (
    user_id uuid primary key,
    user_name varchar(255) not null,
    user_login varchar(255) unique not null,
    user_password varchar(255) not null,
    status varchar(50) not null,
    user_role varchar(50) not null,
    created timestamp not null default now(),
    updated timestamp not null default now()
);

create table chats (
    chat_id uuid primary key,
    chat_name varchar(255) not null,
    status varchar(50) not null,
    creator_id uuid references users(user_id),
    created timestamp not null default now(),
    updated timestamp not null default now()
);

create table users_chats (
    user_id uuid references users(user_id),
    chat_id uuid references chats(chat_id),
    primary key (user_id, chat_id)
);

create table messages (
    message_id uuid primary key,
    chat_id uuid references chats(chat_id),
    user_id uuid references users(user_id),
    message text not null,
    status varchar(50) not null,
    created timestamp not null default now(),
    updated timestamp not null default now()
);

create table messages_chats (
    message_id uuid references messages(message_id),
    chat_id uuid  references chats(chat_id),
    primary key (message_id, chat_id)
)
