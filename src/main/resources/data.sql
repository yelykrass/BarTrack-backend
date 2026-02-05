/* Roles */
INSERT INTO roles (id_role, name) VALUES (default, 'ROLE_ADMIN');
INSERT INTO roles (id_role, name) VALUES (default, 'ROLE_USER');

/* Users (bcrypt */
INSERT INTO users (id_user, username, password, active) 
VALUES (default, 'admin@bartrack.com', '$2a$10$Ck5.2ulLbeh7ux1WPOMedOXsRDKIRVp5ddWpQLKQNMJDqx.DWf9zq', TRUE); -- : 123456
INSERT INTO users (id_user, username, password, active) 
VALUES (default, 'bartender@bartrack.com', '$2a$10$Ck5.2ulLbeh7ux1WPOMedOXsRDKIRVp5ddWpQLKQNMJDqx.DWf9zq', TRUE); -- : 123456

/* Roles_users */
INSERT INTO roles_users (role_id, user_id) VALUES (1, 1); -- admin -> ROLE_ADMIN
INSERT INTO roles_users (role_id, user_id) VALUES (2, 2); -- bartender -> ROLE_USER

INSERT INTO items (id_item, name, category, quantity, price, active) VALUES
(DEFAULT, 'Whiskey 12y', 'Beverages', 20, 25.00, true),
(DEFAULT, 'Red Wine', 'Beverages', 15, 12.50, true),
(DEFAULT, 'Gin', 'Beverages', 10, 18.00, true),
(DEFAULT, 'Vodka', 'Beverages', 8, 14.00, true),
(DEFAULT, 'Rum', 'Beverages', 12, 16.00, true);