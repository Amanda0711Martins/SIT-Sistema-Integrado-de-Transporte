INSERT INTO role (name) VALUES ('ADMIN') ON CONFLICT DO NOTHING;
INSERT INTO cliente (email, password) VALUES ('admin@admin.com', '$2a$10$Vn1Fz9SxEYi9jcbYgrcFfu0H0I3YmF8svC.8DA3DbjIfDA1rupeRu') ON CONFLICT DO NOTHING;
-- E associe ao role conforme seu mapeamento ManyToMany
