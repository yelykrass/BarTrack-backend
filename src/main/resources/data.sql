/* Roles */
INSERT INTO roles (id_role, name) VALUES (default, 'ROLE_ADMIN');
INSERT INTO roles (id_role, name) VALUES (default, 'ROLE_USER');

/* Users (bcrypt */
INSERT INTO users (id_user, username, password) 
VALUES (default, 'admin@bartrack.com', '$2a$12$8LegtLQWe717tIPvZeivjuqKnaAs5.bm0Q05.5GrAmcKzXw2NjoUO'); -- пароль: 123456
INSERT INTO users (id_user, username, password) 
VALUES (default, 'bartender@bartrack.com', '$2a$12$8LegtLQWe717tIPvZeivjuqKnaAs5.bm0Q05.5GrAmcKzXw2NjoUO'); -- пароль: 123456

/* Roles_users */
INSERT INTO roles_users (role_id, user_id) VALUES (1, 1); -- admin -> ROLE_ADMIN
INSERT INTO roles_users (role_id, user_id) VALUES (2, 2); -- bartender -> ROLE_USER

/* Bebidas (productos para control de existencias) */
/*INSERT INTO drinks (id_drink, name, price, stock_quantity) VALUES (default, 'Mojito', 7.50, 20);
INSERT INTO drinks (id_drink, name, price, stock_quantity) VALUES (default, 'Margarita', 8.00, 15);
INSERT INTO drinks (id_drink, name, price, stock_quantity) VALUES (default, 'Cerveza', 4.50, 50);
INSERT INTO drinks (id_drink, name, price, stock_quantity) VALUES (default, 'Vino tinto', 6.00, 30);
*/
