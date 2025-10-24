-- =========================
-- PETSTORE DATA SEED FOR NEON
-- =========================
-- Script para insertar datos de prueba en Neon Database
-- Ejecutar este archivo con psql o desde tu cliente SQL favorito

-- =========================
-- ROLES
-- =========================
INSERT INTO public.roles (role_name) VALUES
('Marketing Admin')
ON CONFLICT (role_name) DO NOTHING;

-- =========================
-- STATUSES
-- =========================
INSERT INTO public.statuses (status_name) VALUES
('ACTIVE'),
('EXPIRED'),
('SCHEDULE')
ON CONFLICT (status_name) DO NOTHING;

-- =========================
-- CATEGORIES
-- =========================
INSERT INTO public.categories (category_name, description) VALUES
('Electronics', 'Devices and gadgets'),
('Clothing', 'Apparel and fashion items'),
('Home', 'Household goods'),
('Sports', 'Sports equipment and gear'),
('Books', 'Books and literature'),
('Toys', 'Kids toys and games'),
('Beauty', 'Cosmetics and skincare'),
('Automotive', 'Car accessories'),
('Food', 'Groceries and snacks'),
('Health', 'Healthcare products')
ON CONFLICT (category_name) DO NOTHING;

-- =========================
-- USERS
-- =========================
-- NOTA: Las contraseñas están hasheadas con BCrypt (password123)
INSERT INTO public.users (user_name, email, password, role_id) VALUES
('Alice Johnson', 'alice@example.com', '$2a$10$K8k7yX9kF3WzE3Z5A1B2H.Xy5Z8fN9mF5P3Q7J2N5M8K3F7G2H4L6', 1),
('Bob Smith', 'bob@example.com', '$2a$10$K8k7yX9kF3WzE3Z5A1B2H.Xy5Z8fN9mF5P3Q7J2N5M8K3F7G2H4L6', 1),
('Charlie Brown', 'charlie@example.com', '$2a$10$K8k7yX9kF3WzE3Z5A1B2H.Xy5Z8fN9mF5P3Q7J2N5M8K3F7G2H4L6', 1),
('Diana Prince', 'diana@example.com', '$2a$10$K8k7yX9kF3WzE3Z5A1B2H.Xy5Z8fN9mF5P3Q7J2N5M8K3F7G2H4L6', 1),
('Ethan Hunt', 'ethan@example.com', '$2a$10$K8k7yX9kF3WzE3Z5A1B2H.Xy5Z8fN9mF5P3Q7J2N5M8K3F7G2H4L6', 1),
('Fiona Adams', 'fiona@example.com', '$2a$10$K8k7yX9kF3WzE3Z5A1B2H.Xy5Z8fN9mF5P3Q7J2N5M8K3F7G2H4L6', 1),
('George Clark', 'george@example.com', '$2a$10$K8k7yX9kF3WzE3Z5A1B2H.Xy5Z8fN9mF5P3Q7J2N5M8K3F7G2H4L6', 1),
('Hannah Lee', 'hannah@example.com', '$2a$10$K8k7yX9kF3WzE3Z5A1B2H.Xy5Z8fN9mF5P3Q7J2N5M8K3F7G2H4L6', 1),
('Ian Wright', 'ian@example.com', '$2a$10$K8k7yX9kF3WzE3Z5A1B2H.Xy5Z8fN9mF5P3Q7J2N5M8K3F7G2H4L6', 1),
('Julia Roberts', 'julia@example.com', '$2a$10$K8k7yX9kF3WzE3Z5A1B2H.Xy5Z8fN9mF5P3Q7J2N5M8K3F7G2H4L6', 1),
-- Admin user for testing
('Admin User', 'admin@petstore.com', '$2a$10$K8k7yX9kF3WzE3Z5A1B2H.Xy5Z8fN9mF5P3Q7J2N5M8K3F7G2H4L6', 1)
ON CONFLICT (email) DO NOTHING;

-- =========================
-- PROMOTIONS
-- =========================
INSERT INTO public.promotions (promotion_name, description, start_date, end_date, discount_value, status_id, user_id, category_id) VALUES
('Summer Sale', 'Discount on summer collection', '2025-06-01', '2025-07-30', 15.0, 1, 1, 2),
('Electronics Blast', 'Up to 20% off gadgets', '2025-07-01', '2025-07-15', 20.0, 2, 2, 1),
('Book Fair', 'Special prices on books', '2025-08-01', '2025-12-31', 10.0, 1, 3, 5),
('Toy Carnival', 'Discount on kids toys', '2025-09-01', '2026-01-15', 25.0, 1, 4, 6),
('Fitness Promo', 'Sports gear sale', '2025-10-01', '2025-10-20', 18.0, 3, 5, 4),
('Beauty Week', 'Skincare discounts', '2025-11-01', '2025-11-07', 12.0, 3, 6, 7),
('Food Fiesta', 'Snacks at low prices', '2025-09-21', '2025-12-10', 8.0, 1, 7, 9),
('Car Month', 'Automotive discounts', '2025-09-23', '2025-12-25', 22.0, 1, 8, 8),
('Health Check', 'Healthcare products', '2025-09-22', '2026-01-15', 14.0, 1, 9, 10),
('Clothing Rush', 'Fashion discounts', '2025-09-22', '2026-02-14', 30.0, 1, 10, 2)
ON CONFLICT (promotion_name) DO NOTHING;

-- =========================
-- PRODUCTS
-- =========================
INSERT INTO public.products (product_name, base_price, sku, category_id, promotion_id) VALUES
('Smartphone X', 699.99, 10001, 1, 2),
('Laptop Pro', 1299.99, 10002, 1, NULL),
('T-Shirt Cotton', 19.99, 10003, 2, 1),
('Running Shoes', 89.99, 10004, 4, NULL),
('Novel Book', 14.99, 10005, 5, 3),
('Toy Car', 24.99, 10006, 6, NULL),
('Face Cream', 29.99, 10007, 7, NULL),
('Engine Oil', 49.99, 10008, 8, NULL),
('Chips Pack', 2.99, 10009, 9, 7),
('Vitamins Bottle', 12.99, 10010, 10, NULL)
ON CONFLICT (sku) DO NOTHING;

-- =========================
-- VERIFICATION QUERIES
-- =========================
-- Verificar los datos insertados
SELECT 'ROLES' as table_name, COUNT(*) as count FROM public.roles
UNION ALL
SELECT 'STATUSES', COUNT(*) FROM public.statuses
UNION ALL  
SELECT 'CATEGORIES', COUNT(*) FROM public.categories
UNION ALL
SELECT 'USERS', COUNT(*) FROM public.users
UNION ALL
SELECT 'PRODUCTS', COUNT(*) FROM public.products
UNION ALL
SELECT 'PROMOTIONS', COUNT(*) FROM public.promotions;
