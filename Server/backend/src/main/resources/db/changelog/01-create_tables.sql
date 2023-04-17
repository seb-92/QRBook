
create table if not exists users
(
    id         bigserial
        primary key,
    address    varchar(255) not null,
    first_name varchar(255) not null,
    last_name  varchar(255) not null,
    login      varchar(255) not null
        constraint uk_ow0gan20590jrb00upg3va2fn
            unique,
    password   varchar(255) not null
);



create table if not exists book
(
    id                   bigserial
        primary key,
    amount_of_pages      integer,
    author               varchar(255) not null,
    available            boolean,
    title                varchar(255) not null,
    publish_year         integer      not null,
    currently_ordered_by bigint
        constraint fkswi83mllnas2d1c2an3np8bc5
            references users
);


create table if not exists book_order_history
(
    id            bigserial
        primary key,
    date_of_order timestamp not null,
    order_type    varchar(255),
    book          bigint    not null
        constraint fkibpjydupg417jc28lhaa7rexs
            references book,
    user_id       bigint    not null
        constraint fki099u5op0re49f3qyrr1gkcy2
            references users
);

