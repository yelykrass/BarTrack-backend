/* Roles */
INSERT INTO roles (id_role, name) VALUES (default, 'ROLE_ADMIN');
INSERT INTO roles (id_role, name) VALUES (default, 'ROLE_USER');

/* Users (bcrypt */
INSERT INTO users (id_user, username, password) 
VALUES (default, 'admin@bartrack.com', '$2a$10$Ck5.2ulLbeh7ux1WPOMedOXsRDKIRVp5ddWpQLKQNMJDqx.DWf9zq'); -- пароль: 123456
INSERT INTO users (id_user, username, password) 
VALUES (default, 'bartender@bartrack.com', '$2a$10$Ck5.2ulLbeh7ux1WPOMedOXsRDKIRVp5ddWpQLKQNMJDqx.DWf9zq'); -- пароль: 123456

/* Roles_users */
INSERT INTO roles_users (role_id, user_id) VALUES (1, 1); -- admin -> ROLE_ADMIN
INSERT INTO roles_users (role_id, user_id) VALUES (2, 2); -- bartender -> ROLE_USER

/* Bebidas (productos para control de existencias) */
/*INSERT INTO drinks (id_drink, name, price, stock_quantity) VALUES (default, 'Mojito', 7.50, 20);
INSERT INTO drinks (id_drink, name, price, stock_quantity) VALUES (default, 'Margarita', 8.00, 15);
INSERT INTO drinks (id_drink, name, price, stock_quantity) VALUES (default, 'Cerveza', 4.50, 50);
INSERT INTO drinks (id_drink, name, price, stock_quantity) VALUES (default, 'Vino tinto', 6.00, 30);
*/
/* Items */
INSERT INTO items (id_item, name, category, quantity, price, user_id, expiry_date) VALUES
(default, 'Whiskey 12y', 'Beverages', 20, 50, 1, '2025-12-31'),
(default, 'Red Wine', 'Beverages', 15, 30, 1, '2025-11-30'),
(default, 'Gin', 'Beverages', 10, 45, 2, '2026-01-15'),
(default, 'Vodka', 'Beverages', 8, 40, 2, '2026-02-28'),
(default, 'Rum', 'Beverages', 12, 35, 2, '2025-10-20');
