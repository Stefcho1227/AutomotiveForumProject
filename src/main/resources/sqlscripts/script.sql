create table roles
(
    id        int         not null
        primary key,
    role_name varchar(50) not null,
    constraint role_name
        unique (role_name)
);

create table tags
(
    id       int auto_increment
        primary key,
    tag_name varchar(50) not null,
    constraint tag_name
        unique (tag_name)
);

create table users
(
    id          int auto_increment
        primary key,
    first_name  varchar(32)          null,
    last_name   varchar(32)          not null,
    email       varchar(255)         not null,
    username    varchar(50)          not null,
    password    varchar(255)         not null,
    role_id     int                  not null,
    is_blocked  tinyint(1) default 0 null,
    profile_url varchar(320)         null,
    constraint email
        unique (email),
    constraint username
        unique (username),
    constraint users_ibfk_1
        foreign key (role_id) references roles (id)
);

create table posts
(
    id         int auto_increment
        primary key,
    user_id    int                                   not null,
    title      varchar(64)                           not null,
    content    text                                  not null,
    likes      int       default 0                   null,
    created_at timestamp default current_timestamp() null,
    constraint posts_ibfk_1
        foreign key (user_id) references users (id)
            on delete cascade
);

create table comments
(
    id         int auto_increment
        primary key,
    post_id    int                                   not null,
    user_id    int                                   not null,
    content    text                                  not null,
    created_at timestamp default current_timestamp() null,
    constraint comments_ibfk_1
        foreign key (post_id) references posts (id)
            on delete cascade,
    constraint comments_ibfk_2
        foreign key (user_id) references users (id)
            on delete cascade
);

create index post_id
    on comments (post_id);

create index user_id
    on comments (user_id);

create table likes
(
    user_id int not null,
    post_id int not null,
    primary key (user_id, post_id),
    constraint likes_ibfk_1
        foreign key (user_id) references users (id)
            on delete cascade,
    constraint likes_ibfk_2
        foreign key (post_id) references posts (id)
            on delete cascade
);

create index post_id
    on likes (post_id);

create table post_tags
(
    post_id int not null,
    tag_id  int not null,
    primary key (post_id, tag_id),
    constraint post_tags_ibfk_1
        foreign key (post_id) references posts (id)
            on delete cascade,
    constraint post_tags_ibfk_2
        foreign key (tag_id) references tags (id)
            on delete cascade
);

create index tag_id
    on post_tags (tag_id);

create index user_id
    on posts (user_id);

create table user_phone_numbers
(
    user_id      int         not null,
    phone_number varchar(20) not null,
    primary key (user_id, phone_number),
    constraint user_phone_numbers_ibfk_1
        foreign key (user_id) references users (id)
            on delete cascade
);

create index role_id
    on users (role_id);

