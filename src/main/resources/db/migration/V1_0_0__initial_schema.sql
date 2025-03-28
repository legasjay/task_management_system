--CREATE TABLE users (
--    id BIGSERIAL PRIMARY KEY,
--    email VARCHAR(255) NOT NULL UNIQUE,
--    username VARCHAR(255) NOT NULL UNIQUE,
--    password VARCHAR(255) NOT NULL,
--    role VARCHAR(20) NOT NULL CHECK (role IN ('ADMIN', 'USER'))
--);
--
--CREATE TABLE tasks (
--    id BIGSERIAL PRIMARY KEY,
--    title VARCHAR(255) NOT NULL,
--    description TEXT,
--    status VARCHAR(20) NOT NULL,
--    author_id BIGINT,
--    assignee_id BIGINT,
--    priority VARCHAR(20) NOT NULL,
--    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
--);
--
--CREATE TABLE comments (
--    id BIGSERIAL PRIMARY KEY,
--    content TEXT,
--    task_id BIGINT REFERENCES tasks(id),
--    user_id BIGINT REFERENCES users(id),
--    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
--);
--
--
--ALTER TABLE tasks ADD CONSTRAINT fk_author FOREIGN KEY (author_id) REFERENCES users(id);
--ALTER TABLE tasks ADD CONSTRAINT fk_assignee FOREIGN KEY (assignee_id) REFERENCES users(id);
--ALTER TABLE comments ADD CONSTRAINT fk_task FOREIGN KEY (task_id) REFERENCES tasks(id);
--ALTER TABLE comments ADD CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id);
--
--
--INSERT INTO users (id, email, username, password, role)
--VALUES (1, 'admin@example.com', 'admin', 'admin1', 'ADMIN');
create TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('ADMIN', 'USER'))
);

create TABLE tasks (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(20) NOT NULL CHECK (priority IN ('PENDING', 'IN_PROGRESS', 'COMPLETED')),
    priority VARCHAR(20) NOT NULL CHECK (priority IN ('HIGH', 'MEDIUM', 'LOW')),
    author_id BIGINT,
    assignee_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

create TABLE comments (
    id BIGSERIAL PRIMARY KEY,
    content TEXT,
    task_id BIGINT REFERENCES tasks(id),
    user_id BIGINT REFERENCES users(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

alter table tasks add CONSTRAINT fk_author FOREIGN KEY (author_id) REFERENCES users(id);
alter table tasks add CONSTRAINT fk_assignee FOREIGN KEY (assignee_id) REFERENCES users(id);
alter table comments add CONSTRAINT fk_task FOREIGN KEY (task_id) REFERENCES tasks(id);
alter table comments add CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id);

insert into users (id, email, username, password, role)
values (1, 'admin@example.com', 'admin', 'admin', 'ADMIN');
insert into users (id, email, username, password, role)
values (2, 'user1@example.com', 'user1', 'admin', 'USER');
insert into users (id, email, username, password, role)
values (3, 'user2@example.com', 'user2', 'admin', 'USER');

--INSERT INTO tasks (id, title, description, status, priority, author_id, assignee_id) VALUES (1, 'Задача 1', 'Описание первой задачи', 'PENDING', 'HIGH', 1, 2);
--INSERT INTO tasks (id, title, description, status, priority, author_id, assignee_id) VALUES (2, 'Задача 2', 'Описание второй задачи', 'COMPLETED', 'LOW', 2, 3);
--INSERT INTO tasks (id, title, description, status, priority, author_id, assignee_id) VALUES (3, 'Задача 3', 'Описание третьей задачи', 'IN_PROGRESS', 'MEDIUM', 3, 1);
--INSERT INTO tasks (id, title, description, status, priority, author_id, assignee_id) VALUES (4, 'Задача 4', 'Описание четвертой задачи', 'PENDING', 'HIGH', 1, 2);
--INSERT INTO tasks (id, title, description, status, priority, author_id, assignee_id) VALUES (5, 'Задача 5', 'Описание пятой задачи', 'COMPLETED', 'LOW', 3, 1);
--
--INSERT INTO comments (id, content, task_id, user_id) VALUES (1, 'Первый комментарий к задаче 1', 1, 1);
--INSERT INTO comments (id, content, task_id, user_id) VALUES (2, 'Второй комментарий к задаче 1', 1, 2);
--INSERT INTO comments (id, content, task_id, user_id) VALUES (3, 'Третий комментарий к задаче 1', 1, 3);
--INSERT INTO comments (id, content, task_id, user_id) VALUES (4, 'Четвертый комментарий к задаче 2', 2, 1);
--INSERT INTO comments (id, content, task_id, user_id) VALUES (5, 'Пятый комментарий к задаче 2', 2, 2);
