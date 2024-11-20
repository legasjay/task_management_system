ALTER TABLE tasks ADD CONSTRAINT fk_author FOREIGN KEY (author_id) REFERENCES users(id);
ALTER TABLE tasks ADD CONSTRAINT fk_assignee FOREIGN KEY (assignee_id) REFERENCES users(id);
ALTER TABLE comments ADD CONSTRAINT fk_task FOREIGN KEY (task_id) REFERENCES tasks(id);
ALTER TABLE comments ADD CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id);