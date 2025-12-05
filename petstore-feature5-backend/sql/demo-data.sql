-- ================================================
-- SCRIPT PARA LLENAR LA BASE DE DATOS CON DATOS DE PRUEBA
-- PetStore Demo Data
-- ================================================

-- Limpiar datos existentes (opcional)
-- TRUNCATE TABLE products, promotions, users, categories, roles, statuses CASCADE;

-- 1. INSERTAR ROLES
INSERT INTO public.roles (role_name) VALUES 
('ADMIN'),
('USER'),
('MODERATOR'),
('MANAGER')
ON CONFLICT (role_name) DO NOTHING;

-- 2. INSERTAR STATUSES
INSERT INTO public.statuses (status_name) VALUES 
('ACTIVE'),
('INACTIVE'),
('PENDING'),
('EXPIRED'),
('DRAFT')
ON CONFLICT (status_name) DO NOTHING;

-- 3. INSERTAR CATEGORÍAS
INSERT INTO public.categories (category_name, description) VALUES 
('Perros', 'Productos y accesorios para perros'),
('Gatos', 'Productos y accesorios para gatos'),
('Aves', 'Productos para aves domésticas'),
('Peces', 'Acuarios y productos para peces'),
('Reptiles', 'Terrarios y productos para reptiles'),
('Roedores', 'Productos para hamsters, conejos y otros roedores'),
('Alimentación', 'Comida y snacks para mascotas'),
('Juguetes', 'Juguetes y entretenimiento'),
('Salud', 'Productos veterinarios y de salud'),
('Accesorios', 'Collares, correas y accesorios generales')
ON CONFLICT (category_name) DO NOTHING;

-- 4. INSERTAR USUARIOS
INSERT INTO public.users (user_name, email, password, role_id) VALUES 
('admin', 'admin@petstore.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 1),
('juan.perez', 'juan.perez@email.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 2),
('maria.garcia', 'maria.garcia@email.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 2),
('carlos.rodriguez', 'carlos.rodriguez@email.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 3),
('ana.martinez', 'ana.martinez@email.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 2),
('luis.fernandez', 'luis.fernandez@email.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 4),
('sofia.lopez', 'sofia.lopez@email.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 2),
('diego.sanchez', 'diego.sanchez@email.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 2),
('carmen.ruiz', 'carmen.ruiz@email.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 2),
('manager', 'manager@petstore.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 4)
ON CONFLICT (email) DO NOTHING;

-- 5. INSERTAR PROMOCIONES
INSERT INTO public.promotions (promotion_name, description, start_date, end_date, discount_value, status_id, user_id, category_id) VALUES 
('Black Friday Mascotas', 'Descuento especial de Black Friday en todos los productos', '2025-11-29', '2025-12-02', 25.0, 1, 1, 1),
('Cyber Monday Gatos', 'Promoción especial para productos de gatos', '2025-12-02', '2025-12-09', 20.0, 1, 1, 2),
('Descuento Alimentación', 'Promoción en comida para mascotas', '2025-12-01', '2025-12-31', 15.0, 1, 6, 7),
('Juguetes de Navidad', 'Descuento en juguetes para la temporada navideña', '2025-12-15', '2025-01-15', 30.0, 1, 6, 8),
('Promoción Acuarios', 'Descuento especial en productos para peces', '2025-12-01', '2025-12-20', 18.0, 1, 4, 4),
('Salud y Bienestar', 'Promoción en productos de salud', '2025-11-15', '2025-12-15', 12.0, 1, 4, 9),
('Verano Aves', 'Promoción de verano para productos de aves', '2025-06-01', '2025-08-31', 22.0, 4, 4, 3),
('Descuento Reptiles', 'Promoción especial para reptiles', '2025-12-10', '2025-12-25', 28.0, 3, 6, 5),
('Mega Sale Accesorios', 'Gran venta en accesorios', '2025-12-05', '2025-12-12', 35.0, 1, 1, 10),
('Promoción Roedores', 'Descuento en productos para roedores', '2025-12-01', '2025-12-20', 16.0, 1, 6, 6)
ON CONFLICT DO NOTHING;

-- 6. INSERTAR PRODUCTOS
INSERT INTO public.products (product_name, base_price, sku, category_id, promotion_id) VALUES 
-- Productos para Perros
('Collar de Cuero Premium', 29.99, 1001, 1, 1),
('Correa Extensible 5m', 24.99, 1002, 1, 1),
('Cama Ortopédica Grande', 89.99, 1003, 1, NULL),
('Juguete Kong Classic', 19.99, 1004, 1, NULL),
('Comida Premium Adulto 15kg', 79.99, 1005, 1, NULL),

-- Productos para Gatos
('Rascador Torre 120cm', 159.99, 2001, 2, 2),
('Arena Aglomerante 10kg', 24.99, 2002, 2, 2),
('Comedero Automático', 89.99, 2003, 2, 2),
('Juguete Ratón con Hierba Gatera', 12.99, 2004, 2, NULL),
('Transportín de Viaje', 69.99, 2005, 2, NULL),

-- Productos para Aves
('Jaula Grande con Accesorios', 199.99, 3001, 3, NULL),
('Semillas Premium Mix 5kg', 34.99, 3002, 3, NULL),
('Columpio de Madera', 16.99, 3003, 3, NULL),
('Bebedero Automático', 22.99, 3004, 3, NULL),

-- Productos para Peces
('Acuario 100L con Filtro', 299.99, 4001, 4, 5),
('Comida Tropical Escamas 250g', 18.99, 4002, 4, 5),
('Plantas Artificiales Set', 29.99, 4003, 4, 5),
('Calentador 100W', 45.99, 4004, 4, NULL),

-- Productos para Reptiles
('Terrario 80x40x40cm', 249.99, 5001, 5, 8),
('Lámpara UV 13W', 39.99, 5002, 5, 8),
('Sustrato Fibra de Coco 10L', 19.99, 5003, 5, NULL),
('Termostato Digital', 59.99, 5004, 5, NULL),

-- Productos para Roedores
('Jaula Multi-nivel', 119.99, 6001, 6, 10),
('Viruta de Madera 15L', 14.99, 6002, 6, 10),
('Rueda de Ejercicio', 24.99, 6003, 6, NULL),
('Casa de Madera', 18.99, 6004, 6, NULL),

-- Alimentación General
('Snacks Naturales Mix 500g', 16.99, 7001, 7, 3),
('Vitaminas Multipeques 100ml', 28.99, 7002, 7, 3),
('Huesos Dentales Pack 10', 22.99, 7003, 7, NULL),
('Comida Orgánica Premium 5kg', 64.99, 7004, 7, NULL),

-- Juguetes
('Pelota Interactiva LED', 34.99, 8001, 8, 4),
('Set de Peluches 5 piezas', 29.99, 8002, 8, 4),
('Cuerda Dental 3 Nudos', 11.99, 8003, 8, 4),
('Puzzle Dispensador de Premios', 42.99, 8004, 8, NULL),

-- Productos de Salud
('Kit de Primeros Auxilios', 49.99, 9001, 9, 6),
('Champú Medicinal 500ml', 26.99, 9002, 9, 6),
('Termómetro Digital', 19.99, 9003, 9, NULL),
('Antiparasitario Natural 250ml', 33.99, 9004, 9, NULL),

-- Accesorios Generales
('Placa de Identificación Personalizada', 12.99, 10001, 10, 9),
('Bebedero Portátil Viaje', 18.99, 10002, 10, 9),
('Mochila Transportín', 89.99, 10003, 10, 9),
('Manta Impermeable', 32.99, 10004, 10, NULL),
('Kit de Aseo Completo', 45.99, 10005, 10, NULL)

ON CONFLICT (sku) DO NOTHING;

-- Consulta para verificar los datos insertados
SELECT 
  'Roles' as tabla, COUNT(*) as registros FROM roles
UNION ALL SELECT 
  'Statuses' as tabla, COUNT(*) as registros FROM statuses  
UNION ALL SELECT 
  'Categorias' as tabla, COUNT(*) as registros FROM categories
UNION ALL SELECT 
  'Usuarios' as tabla, COUNT(*) as registros FROM users
UNION ALL SELECT 
  'Promociones' as tabla, COUNT(*) as registros FROM promotions
UNION ALL SELECT 
  'Productos' as tabla, COUNT(*) as registros FROM products;

-- Consulta para ver productos con promociones activas
SELECT 
  p.product_name,
  p.base_price,
  pr.promotion_name,
  pr.discount_value,
  c.category_name,
  s.status_name
FROM products p
LEFT JOIN promotions pr ON p.promotion_id = pr.promotion_id
JOIN categories c ON p.category_id = c.category_id
LEFT JOIN statuses s ON pr.status_id = s.status_id
ORDER BY c.category_name, p.product_name;
