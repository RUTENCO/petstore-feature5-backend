# 📊 Auditoría Completa de Endpoints API

## 🔍 **Resumen Ejecutivo**

- **Total REST**: 25 endpoints
- **Total GraphQL**: 21 operaciones + 3 schema mappings
- **Fecha de auditoría**: 2025-01-02

---

## 🌐 **REST API Endpoints (25 total)**

### 📱 **AuthController (5 endpoints)**
1. `GET /api/auth/status` - Health check de autenticación
2. `POST /api/auth/login` - Login con JWT
3. `GET /api/auth/verify` - Verificar token JWT
4. `GET /api/auth/me` - Obtener usuario actual
5. `POST /api/auth/logout` - Logout

### 🎯 **PromotionController (9 endpoints)**
1. `GET /api/promotions` - Obtener promociones públicas
2. `GET /api/promotions/all` - Obtener todas las promociones
3. `GET /api/promotions/category/{categoryId}` - Promociones por categoría
4. `GET /api/promotions/valid` - Promociones válidas/activas
5. `GET /api/promotions/status` - Promociones por estado
6. `DELETE /api/promotions/{id}` - **Eliminación con papelera temporal**
7. `GET /api/promotions/trash` - **Ver papelera temporal (30 días)**
8. `GET /api/promotions/trash/user/{userId}` - **Papelera por usuario**
9. `POST /api/promotions/{id}/restore` - **Restaurar desde papelera**

### 🛍️ **ProductController (5 endpoints)**
1. `GET /api/products` - Obtener todos los productos
2. `GET /api/products/category/{categoryId}` - Productos por categoría
3. `GET /api/products/{id}` - Producto específico
4. `GET /api/products/search` - Búsqueda de productos
5. `GET /api/products/price-range` - Productos por rango de precio

### 📂 **CategoryController (6 endpoints)**
1. `GET /api/categories` - Obtener todas las categorías
2. `GET /api/categories/{id}` - Categoría específica
3. `POST /api/categories` - Crear categoría (requiere JWT)
4. `PUT /api/categories/{id}` - Actualizar categoría (requiere JWT)
5. `DELETE /api/categories/{id}` - Eliminar categoría (requiere JWT)
6. `GET /api/categories/info` - Información de categorías

---

## 🔗 **GraphQL API (21 operaciones + 3 schema mappings)**

### 🔍 **Queries (16 queries)**
1. `health` - Health check
2. `currentUser` - Usuario autenticado (JWT)
3. `promotions` - Todas las promociones
4. `promotionsActive` - Promociones activas
5. `promotionsExpired` - Promociones expiradas
6. `promotionsScheduled` - Promociones programadas
7. `promotionsByStatus(statusName)` - Por estado específico
8. `promotionsByCategory(categoryId)` - Por categoría
9. `promotion(id)` - Promoción específica
10. `deletedPromotions` - **Papelera temporal (JWT)**
11. `deletedPromotionsByUser(userId)` - **Papelera por usuario (JWT)**
12. `categories` - Todas las categorías
13. `category(id)` - Categoría específica
14. `products` - Todos los productos
15. `productsByCategory(categoryId)` - Productos por categoría
16. `product(id)` - Producto específico

### ⚡ **Mutations (5 mutations)**
1. `login(email, password)` - Login con JWT real
2. `createPromotion(input)` - Crear promoción (JWT)
3. `updatePromotion(id, input)` - Actualizar promoción (JWT)
4. `deletePromotion(id, userId)` - **Eliminar a papelera (JWT)**
5. `restorePromotion(id, userId)` - **Restaurar desde papelera (JWT)**

### 🔗 **Schema Mappings (3 resolvers)**
1. `Promotion.products` - Resolver productos de promoción
2. `Category.promotions` - Resolver promociones de categoría
3. `Category.products` - Resolver productos de categoría

---

## 🔐 **Análisis de Seguridad**

### **REST API**
- **Públicos**: 19 endpoints
- **Protegidos (JWT)**: 6 endpoints
- **Papelera temporal**: 3 endpoints específicos

### **GraphQL API**
- **Públicos**: 15 operaciones (14 queries + 1 mutation)
- **Protegidos (JWT)**: 6 operaciones (2 queries + 4 mutations)
- **Papelera temporal**: 4 operaciones específicas

---

## 🗑️ **Sistema de Papelera Temporal**

### **Funcionalidad Implementada**
- ✅ Eliminación suave (soft delete) con triggers de BD
- ✅ Retención de 30 días automática
- ✅ Cambio de estado ACTIVE → INACTIVE
- ✅ Función de restauración con triggers
- ✅ API REST + GraphQL para gestión completa
- ✅ Auditoría de usuario en cada operación

### **Endpoints de Papelera**
**REST:**
- `DELETE /api/promotions/{id}` - Eliminar
- `GET /api/promotions/trash` - Ver papelera
- `GET /api/promotions/trash/user/{userId}` - Por usuario
- `POST /api/promotions/{id}/restore` - Restaurar

**GraphQL:**
- `mutation deletePromotion(id, userId)` - Eliminar
- `query deletedPromotions` - Ver papelera
- `query deletedPromotionsByUser(userId)` - Por usuario
- `mutation restorePromotion(id, userId)` - Restaurar

---

## ✅ **Verificación de Implementación**

Todos los endpoints han sido **auditados directamente desde el código fuente**:

1. **REST Controllers**: Revisión de `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping`
2. **GraphQL Resolver**: Revisión de `@QueryMapping` y `@MutationMapping`
3. **Schema Mappings**: Identificación de `@SchemaMapping`

**Estado**: ✅ **COMPLETO Y VERIFICADO**
