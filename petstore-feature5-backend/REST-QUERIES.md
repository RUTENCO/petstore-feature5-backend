# REST API Queries - Pet Store Backend

Este documento contiene ejemplos prácticos de todas las consultas REST disponibles en la API del Pet Store Backend.

## 🔗 URLs Base

- **Desarrollo**: `http://localhost:8080`
- **Producción**: `https://petstore-feature5-backend.onrender.com`
- **Frontend**: `https://fluffy-deals-hub.vercel.app`

## 🔐 Autenticación

### 1. Login de Usuario
```bash
POST /api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
```

**Respuesta:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "user": {
    "userId": 1,
    "username": "admin",
    "email": "admin@petstore.com",
    "roles": ["ADMIN"]
  }
}
```

### 2. Verificar Token
```bash
GET /api/auth/verify
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

### 3. Obtener Usuario Actual
```bash
GET /api/auth/current-user
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

### 4. Logout
```bash
POST /api/auth/logout
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

## 🛍️ Gestión de Productos

### 1. Obtener Todos los Productos
```bash
GET /api/products
```

**Respuesta:**
```json
[
  {
    "productId": 1,
    "productName": "Collar Premium para Perros",
    "description": "Collar Premium para Perros",
    "price": 25.99,
    "sku": "SKU12345",
    "stock": null,
    "imageUrl": null,
    "category": {
      "categoryId": 1,
      "categoryName": "Accesorios",
      "description": "Accesorios para mascotas"
    },
    "status": "ACTIVE",
    "createdAt": "2024-11-01T21:00:00",
    "updatedAt": "2024-11-01T21:00:00"
  }
]
```

### 2. Obtener Producto por ID
```bash
GET /api/products/1
```

### 3. Buscar Productos por Nombre
```bash
GET /api/products/search?name=collar
```

### 4. Obtener Productos por Categoría
```bash
GET /api/products/category/1
```

### 5. **[NUEVO]** Obtener Productos por Promoción
```bash
GET /api/products/promotion/1
```

### 6. Obtener Productos por Rango de Precios
```bash
GET /api/products/price-range?minPrice=10.0&maxPrice=50.0
```

## 📂 Gestión de Categorías

### 1. Obtener Todas las Categorías
```bash
GET /api/categories
```

**Respuesta:**
```json
[
  {
    "categoryId": 1,
    "categoryName": "Accesorios",
    "description": "Accesorios para mascotas de alta calidad"
  },
  {
    "categoryId": 2,
    "categoryName": "Alimentos",
    "description": "Alimentos nutritivos para mascotas"
  }
]
```

### 2. Obtener Categoría por ID
```bash
GET /api/categories/1
```

### 3. Crear Nueva Categoría (Requiere Autenticación)
```bash
POST /api/categories
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
Content-Type: application/json

{
  "categoryName": "Juguetes",
  "description": "Juguetes divertidos para mascotas"
}
```

### 4. Actualizar Categoría (Requiere Autenticación)
```bash
PUT /api/categories/1
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
Content-Type: application/json

{
  "categoryName": "Accesorios Premium",
  "description": "Accesorios de lujo para mascotas exigentes"
}
```

### 5. Eliminar Categoría (Requiere Autenticación)
```bash
DELETE /api/categories/1
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

## 🎉 Gestión de Promociones

### 1. Obtener Promociones Activas (Público)
```bash
GET /api/promotions
```

**Respuesta:**
```json
[
  {
    "promotionId": 1,
    "promotionName": "Black Friday 2024",
    "description": "Descuentos especiales para Black Friday",
    "discountPercentage": 25.5,
    "startDate": "2024-11-25T00:00:00",
    "endDate": "2024-11-30T23:59:59",
    "isActive": true,
    "createdAt": "2024-11-01T10:00:00",
    "updatedAt": "2024-11-01T10:00:00"
  }
]
```

### 2. Obtener Promoción por ID (Público)
```bash
GET /api/promotions/1
```

### 3. Crear Nueva Promoción (Requiere Autenticación)
```bash
POST /api/promotions
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
Content-Type: application/json

{
  "promotionName": "Cyber Monday 2024",
  "description": "Ofertas especiales para mascotas",
  "discountPercentage": 30.0,
  "startDate": "2024-12-01T00:00:00",
  "endDate": "2024-12-01T23:59:59",
  "isActive": true
}
```

### 4. Actualizar Promoción (Requiere Autenticación)
```bash
PUT /api/promotions/1
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
Content-Type: application/json

{
  "promotionName": "Black Friday Extended",
  "description": "Extensión de ofertas Black Friday",
  "discountPercentage": 20.0,
  "startDate": "2024-11-25T00:00:00",
  "endDate": "2024-12-05T23:59:59",
  "isActive": true
}
```

### 5. Eliminar Promoción Temporalmente (Sistema de Papelera)
```bash
DELETE /api/promotions/1
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

### 6. Ver Papelera de Promociones Eliminadas
```bash
GET /api/promotions/trash
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

### 7. Restaurar Promoción desde Papelera
```bash
POST /api/promotions/1/restore
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

### 8. Eliminar Promoción Permanentemente
```bash
DELETE /api/promotions/1/permanent
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

## 🌐 Headers Requeridos

### Para todas las peticiones:
```
Content-Type: application/json
Accept: application/json
```

### Para endpoints autenticados:
```
Authorization: Bearer {token}
```

### CORS - Dominios Permitidos:
- `http://localhost:3000` (desarrollo frontend)
- `http://localhost:8080` (desarrollo backend)
- `https://petstore-feature5-backend.onrender.com` (backend producción)
- `https://fluffy-deals-hub.vercel.app` (frontend producción)

## 📊 Códigos de Respuesta HTTP

| Código | Descripción |
|--------|-------------|
| 200 | OK - Solicitud exitosa |
| 201 | Created - Recurso creado exitosamente |
| 400 | Bad Request - Datos inválidos |
| 401 | Unauthorized - Token inválido o faltante |
| 403 | Forbidden - Sin permisos suficientes |
| 404 | Not Found - Recurso no encontrado |
| 500 | Internal Server Error - Error del servidor |

## 🧪 Ejemplos con cURL

### Ejemplo completo de flujo de autenticación:

```bash
# 1. Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# 2. Usar el token recibido para crear una categoría
curl -X POST http://localhost:8080/api/categories \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..." \
  -d '{"categoryName":"Nuevos Juguetes","description":"Juguetes innovadores"}'

# 3. Obtener todas las categorías (sin autenticación)
curl -X GET http://localhost:8080/api/categories
```

### Ejemplo de búsqueda avanzada:

```bash
# Buscar productos por nombre
curl -X GET "http://localhost:8080/api/products/search?name=collar"

# Filtrar por rango de precios
curl -X GET "http://localhost:8080/api/products/price-range?minPrice=20&maxPrice=100"

# Obtener productos de una categoría específica
curl -X GET http://localhost:8080/api/products/category/1
```

## 📝 Notas Importantes

1. **Autenticación JWT**: Los tokens tienen expiración. Renovar cuando sea necesario.
2. **CORS**: Configurado para permitir solo dominios específicos por seguridad.
3. **SKU**: Ahora incluido en todas las respuestas de productos.
4. **Descripciones de Categoría**: Incluidas en el mapping de productos.
5. **Sistema de Papelera**: Las promociones eliminadas van a una papelera temporal antes de eliminación permanente.
6. **Documentación Swagger**: Disponible en `/swagger-ui/index.html` para pruebas interactivas.

## 🔗 Enlaces Útiles

- **Swagger UI Local**: http://localhost:8080/swagger-ui/index.html
- **Swagger UI Producción**: https://petstore-feature5-backend.onrender.com/swagger-ui/index.html
- **Frontend**: https://fluffy-deals-hub.vercel.app
