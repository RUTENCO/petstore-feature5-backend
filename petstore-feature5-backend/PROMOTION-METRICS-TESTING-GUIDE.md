# Guía de Pruebas: APIs de Métricas de Promociones

## 📋 Introducción

Esta guía explica cómo probar las nuevas APIs de métricas de promociones que permiten a los administradores de marketing ver el rendimiento de sus promociones, incluyendo variación de inventario, ventas e ingresos.

## 🎯 Funcionalidades Implementadas

✅ **Métricas de Rendimiento**: Visualización completa del impacto de promociones  
✅ **Variación de Inventario**: Cantidad inicial vs final por producto  
✅ **APIs REST**: Endpoints completos con documentación Swagger  
✅ **APIs GraphQL**: Queries y mutaciones para tiempo real  
✅ **Simulación de Datos**: Para testing y demos  

---

## 🚀 Iniciar la Aplicación

```bash
cd petstore-feature5-backend
mvn spring-boot:run
```

Verificar que esté funcionando:
- **Swagger UI**: http://localhost:8080/swagger-ui/index.html
- **GraphQL Playground**: http://localhost:8080/graphiql

---

## 🛠️ Testing con REST APIs

### 1. **Verificar si una promoción tiene métricas**

```bash
curl -X GET "http://localhost:8080/api/promotions/metrics/1/exists" \
     -H "Content-Type: application/json"
```

**Respuesta esperada:**
```json
{
  "promotionId": 1,
  "hasMetrics": false,
  "message": "No hay datos disponibles para esta promoción"
}
```

### 2. **Inicializar métricas para una promoción (requiere auth)**

Primero, obtener token de autenticación:
```bash
curl -X POST "http://localhost:8080/api/auth/login" \
     -H "Content-Type: application/json" \
     -d '{"username":"admin","password":"admin123"}'
```

Luego inicializar métricas:
```bash
curl -X POST "http://localhost:8080/api/promotions/metrics/1/initialize" \
     -H "Content-Type: application/json" \
     -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Respuesta esperada:**
```json
{
  "success": true,
  "promotionId": 1,
  "message": "Métricas inicializadas exitosamente"
}
```

### 3. **Simular ventas para generar datos (requiere auth)**

```bash
curl -X POST "http://localhost:8080/api/promotions/metrics/1/simulate" \
     -H "Content-Type: application/json" \
     -H "Authorization: Bearer YOUR_JWT_TOKEN" \
     -d '{
       "productId": 1,
       "unitsSold": 15
     }'
```

**Respuesta esperada:**
```json
{
  "success": true,
  "promotionId": 1,
  "productId": 1,
  "unitsSoldAdded": 15,
  "message": "Métricas actualizadas exitosamente"
}
```

### 4. **Obtener métricas completas de rendimiento**

```bash
curl -X GET "http://localhost:8080/api/promotions/metrics/1" \
     -H "Content-Type: application/json"
```

**Respuesta esperada:**
```json
{
  "promotionId": 1,
  "promotionName": "Black Friday 2024",
  "promotionDescription": "Descuentos especiales",
  "discountPercentage": 25.0,
  "startDate": "2024-11-17T00:00:00",
  "endDate": "2024-11-30T23:59:59",
  "isActive": true,
  "totalProducts": 1,
  "totalUnitsSold": 15,
  "totalRevenue": 292.31,
  "totalInitialInventory": 100,
  "totalCurrentInventory": 85,
  "totalInventoryDifference": 15,
  "inventoryReductionPercentage": 15.0,
  "lastUpdated": "2024-11-18T16:10:00",
  "productMetrics": [
    {
      "productId": 1,
      "productName": "Collar Premium para Perros",
      "productSku": "12345",
      "basePrice": 25.99,
      "discountedPrice": 19.49,
      "initialInventory": 100,
      "currentInventory": 85,
      "inventoryDifference": 15,
      "inventoryReductionPercentage": 15.0,
      "unitsSold": 15,
      "revenueGenerated": 292.31,
      "category": {
        "categoryId": 1,
        "categoryName": "Accesorios",
        "description": "Accesorios para mascotas"
      },
      "lastUpdated": "2024-11-18T16:10:00"
    }
  ]
}
```

### 5. **Obtener métricas detalladas por producto**

```bash
curl -X GET "http://localhost:8080/api/promotions/metrics/1/products" \
     -H "Content-Type: application/json"
```

---

## 🎯 Testing con GraphQL

### Acceder al GraphQL Playground

1. Ir a: http://localhost:8080/graphiql
2. Si necesitas autenticación, usar el header:
   ```json
   {
     "Authorization": "Bearer YOUR_JWT_TOKEN"
   }
   ```

### 1. **Verificar si una promoción tiene métricas**

```graphql
query CheckMetrics {
  promotionHasMetrics(promotionId: "1")
}
```

### 2. **Obtener métricas completas de rendimiento**

```graphql
query GetPromotionPerformance {
  promotionPerformance(promotionId: "1") {
    promotionId
    promotionName
    promotionDescription
    discountPercentage
    startDate
    endDate
    isActive
    totalProducts
    totalUnitsSold
    totalRevenue
    totalInitialInventory
    totalCurrentInventory
    totalInventoryDifference
    inventoryReductionPercentage
    lastUpdated
    productMetrics {
      productId
      productName
      productSku
      basePrice
      discountedPrice
      initialInventory
      currentInventory
      inventoryDifference
      inventoryReductionPercentage
      unitsSold
      revenueGenerated
      category {
        categoryId
        categoryName
        description
      }
      lastUpdated
    }
  }
}
```

### 3. **Obtener solo métricas de productos**

```graphql
query GetProductMetrics {
  promotionProductMetrics(promotionId: "1") {
    productId
    productName
    productSku
    basePrice
    discountedPrice
    initialInventory
    currentInventory
    inventoryDifference
    inventoryReductionPercentage
    unitsSold
    revenueGenerated
    category {
      categoryId
      categoryName
      description
    }
  }
}
```

### 4. **Inicializar métricas (requiere auth)**

```graphql
mutation InitializeMetrics {
  initializePromotionMetrics(promotionId: "1")
}
```

### 5. **Simular actualización de ventas (requiere auth)**

```graphql
mutation SimulateUpdate {
  simulateMetricsUpdate(
    promotionId: "1"
    productId: "3"
    unitsSold: 10
  )
}
```

---

## 📊 Swagger UI Testing

### Acceso a Swagger UI

1. **URL Local**: http://localhost:8080/swagger-ui/index.html
2. **URL Producción**: https://petstore-feature5-backend.onrender.com/swagger-ui/index.html

### Pasos para probar:

1. **Buscar "Métricas de Promociones"** en la lista de controladores
2. **Expandir los endpoints** disponibles:
   - `GET /api/promotions/metrics/{promotionId}/exists`
   - `GET /api/promotions/metrics/{promotionId}`
   - `GET /api/promotions/metrics/{promotionId}/products`
   - `POST /api/promotions/metrics/{promotionId}/initialize`
   - `POST /api/promotions/metrics/{promotionId}/simulate`

3. **Para endpoints que requieren auth**:
   - Hacer clic en el botón "Authorize" en la parte superior
   - Ingresar el token JWT: `Bearer YOUR_JWT_TOKEN`
   - Hacer clic en "Authorize"

4. **Probar endpoints step by step**:
   - Verificar existencia de métricas
   - Inicializar métricas
   - Simular ventas
   - Obtener métricas completas

---

## 🎮 Flujo de Prueba Completo

### Escenario: Administrador revisa rendimiento de promoción Black Friday

1. **Verificar si hay métricas** (REST o GraphQL)
2. **Si no hay métricas, inicializarlas** (requiere auth)
3. **Simular algunas ventas** para generar datos (requiere auth)
4. **Obtener métricas completas** para revisar rendimiento
5. **Simular más ventas** para ver actualización en tiempo real
6. **Obtener métricas actualizadas**

### Datos esperados después de simulaciones:

- **Inventario inicial**: 100 unidades
- **Unidades vendidas**: 15-25 (según simulaciones)
- **Inventario actual**: 75-85 unidades
- **Ingresos generados**: $292-$487 (dependiendo de ventas)
- **Reducción de inventario**: 15-25%

---

## ⚡ Actualización en Tiempo Real

Para simular la actualización dinámica que requiere el frontend:

### Con REST:
```bash
# Simular venta
curl -X POST "http://localhost:8080/api/promotions/metrics/1/simulate" \
     -H "Authorization: Bearer YOUR_JWT_TOKEN" \
     -d '{"productId": 1, "unitsSold": 5}'

# Inmediatamente obtener métricas actualizadas
curl -X GET "http://localhost:8080/api/promotions/metrics/1"
```

### Con GraphQL:
```graphql
# Simular venta
mutation { 
  simulateMetricsUpdate(promotionId: "1", productId: "1", unitsSold: 5) 
}

# Inmediatamente consultar métricas
query { 
  promotionPerformance(promotionId: "1") { 
    totalUnitsSold 
    totalCurrentInventory 
    totalRevenue 
  } 
}
```

---

## 🚨 Casos de Error a Probar

### 1. **Promoción no encontrada**
```bash
curl -X GET "http://localhost:8080/api/promotions/metrics/999"
# Respuesta: 404 Not Found
```

### 2. **Sin métricas disponibles**
```bash
curl -X GET "http://localhost:8080/api/promotions/metrics/1"
# Respuesta: 204 No Content (si no hay métricas)
```

### 3. **Sin autenticación para endpoints protegidos**
```bash
curl -X POST "http://localhost:8080/api/promotions/metrics/1/initialize"
# Respuesta: 401 Unauthorized
```

### 4. **Datos inválidos en simulación**
```bash
curl -X POST "http://localhost:8080/api/promotions/metrics/1/simulate" \
     -H "Authorization: Bearer YOUR_JWT_TOKEN" \
     -d '{"productId": null, "unitsSold": -5}'
# Respuesta: 400 Bad Request
```

---

## 📋 Checklist de Validación

### ✅ Funcionalidad Básica
- [ ] Verificar existencia de métricas funciona
- [ ] Inicialización de métricas funciona
- [ ] Simulación de ventas funciona  
- [ ] Obtención de métricas completas funciona

### ✅ Cálculos Correctos
- [ ] Inventario inicial = 100
- [ ] Inventario actual = inicial - unidades vendidas
- [ ] Diferencia de inventario calculada correctamente
- [ ] Porcentaje de reducción calculado correctamente
- [ ] Ingresos = unidades vendidas × precio con descuento

### ✅ Autenticación y Seguridad
- [ ] Endpoints públicos (GET) funcionan sin auth
- [ ] Endpoints administrativos (POST) requieren auth
- [ ] JWT válido permite acceso
- [ ] JWT inválido/ausente bloquea acceso

### ✅ Actualización Dinámica
- [ ] Métricas se actualizan inmediatamente después de simulaciones
- [ ] Consultas posteriores muestran datos actualizados
- [ ] Sin necesidad de recargar o esperar

### ✅ Casos de Error
- [ ] 404 para promociones inexistentes
- [ ] 204 para promociones sin métricas
- [ ] 401 para endpoints sin auth
- [ ] 400 para datos inválidos

---

## 🎯 Métricas Clave para el Frontend

El frontend puede usar estas APIs para mostrar:

### 📊 **Dashboard Principal**
- Total de unidades vendidas
- Ingresos generados
- Porcentaje de reducción de inventario
- Estado de la promoción (activa/inactiva)

### 📈 **Tabla de Productos**
- Nombre y SKU del producto
- Inventario inicial vs actual
- Unidades vendidas por producto
- Ingresos por producto
- Porcentaje de reducción individual

### 🔄 **Indicadores de Actualización**
- Timestamp de última actualización
- Spinner/indicador durante fetch
- Notificación de datos actualizados

---

¡Las APIs están listas para soportar completamente los requisitos del administrador de marketing para evaluar el impacto de promociones en ventas e inventario con actualización en tiempo real!
