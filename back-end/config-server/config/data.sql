INSERT INTO role (name) SELECT 'ADMIN' WHERE NOT EXISTS (SELECT 1 FROM role WHERE name = 'ADMIN');
INSERT INTO users (email, password) 
SELECT 'admin@admin.com', '$2a$10$Vn1Fz9SxEYi9jcbYgrcFfu0H0I3YmF8svC.8DA3DbjIfDA1rupeRu'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'admin@admin.com');
