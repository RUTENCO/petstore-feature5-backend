# GraphQL Queries & Mutations - Pet Store API

Este archivo contiene todas las consultas (queries) y mutaciones (mutations) disponibles en la API GraphQL del Pet Store.

## 🔧 Configuración Inicial

**Endpoint:** `POST http://localhost:8080/graphql`

**Headers necesarios para mutaciones autenticadas:**
```
Content-Type: application/json
Authorization: Bearer <tu-jwt-token>
```

---

## 🔍 QUERIES (Consultas)

### Health Check
```graphql
query {
  health
}
```

### 🔐 Autenticación

#### Obtener usuario actual (requiere autenticación)
```graphql
query {
  currentUser {
    userId
    userName
    email
    role {
      roleId
      roleName
    }
  }
}
```

### 🏷️ Promociones

#### Obtener todas las promociones
```graphql
query {
  promotions {
    promotionId
    promotionName
    description
    startDate
    endDate
    discountPercentage
    status {
      statusId
      statusName
    }
    user {
      userId
      userName
      email
    }
    category {
      categoryId
      categoryName
      description
    }
    products {
      productId
      productName
      basePrice
      sku
    }
  }
}
```

#### Obtener promociones activas
```graphql
query {
  promotionsActive {
    promotionId
    promotionName
    description
    startDate
    endDate
    discountPercentage
    status {
      statusId
      statusName
    }
    category {
      categoryId
      categoryName
    }
  }
}
```

#### Obtener promociones expiradas
```graphql
query {
  promotionsExpired {
    promotionId
    promotionName
    description
    startDate
    endDate
    discountPercentage
    status {
      statusId
      statusName
    }
  }
}
```

#### Obtener promociones programadas
```graphql
query {
  promotionsScheduled {
    promotionId
    promotionName
    description
    startDate
    endDate
    discountPercentage
    status {
      statusId
      statusName
    }
  }
}
```

#### Obtener promociones por estado
```graphql
query GetPromotionsByStatus($statusName: String!) {
  promotionsByStatus(statusName: $statusName) {
    promotionId
    promotionName
    description
    startDate
    endDate
    discountPercentage
    status {
      statusId
      statusName
    }
  }
}
```

**Variables:**
```json
{
  "statusName": "Active"
}
```

#### Obtener promociones por categoría
```graphql
query GetPromotionsByCategory($categoryId: ID!) {
  promotionsByCategory(categoryId: $categoryId) {
    promotionId
    promotionName
    description
    startDate
    endDate
    discountPercentage
    category {
      categoryId
      categoryName
    }
  }
}
```

**Variables:**
```json
{
  "categoryId": "1"
}
```

#### Obtener una promoción específica
```graphql
query GetPromotion($id: ID!) {
  promotion(id: $id) {
    promotionId
    promotionName
    description
    startDate
    endDate
    discountPercentage
    status {
      statusId
      statusName
    }
    user {
      userId
      userName
      email
    }
    category {
      categoryId
      categoryName
      description
    }
    products {
      productId
      productName
      basePrice
      sku
    }
  }
}
```

**Variables:**
```json
{
  "id": "1"
}
```

### 🗑️ Papelera de Promociones

#### Obtener promociones eliminadas
```graphql
query {
  deletedPromotions {
    promotionId
    promotionName
    description
    startDate
    endDate
    discountPercentage
    status {
      statusId
      statusName
    }
    user {
      userId
      userName
    }
    category {
      categoryId
      categoryName
    }
    deletedAt
    deletedBy {
      userId
      userName
    }
    daysUntilPurge
  }
}
```

#### Obtener promociones eliminadas por usuario
```graphql
query GetDeletedPromotionsByUser($userId: ID!) {
  deletedPromotionsByUser(userId: $userId) {
    promotionId
    promotionName
    description
    deletedAt
    deletedBy {
      userId
      userName
    }
    daysUntilPurge
  }
}
```

**Variables:**
```json
{
  "userId": "1"
}
```

### 📂 Categorías

#### Obtener todas las categorías
```graphql
query {
  categories {
    categoryId
    categoryName
    description
    promotions {
      promotionId
      promotionName
      discountPercentage
    }
    products {
      productId
      productName
      basePrice
    }
  }
}
```

#### Obtener una categoría específica
```graphql
query GetCategory($id: ID!) {
  category(id: $id) {
    categoryId
    categoryName
    description
    promotions {
      promotionId
      promotionName
      description
      discountPercentage
    }
    products {
      productId
      productName
      basePrice
      sku
    }
  }
}
```

**Variables:**
```json
{
  "id": "1"
}
```

### 📦 Productos

#### Obtener todos los productos
```graphql
query {
  products {
    productId
    productName
    basePrice
    sku
    category {
      categoryId
      categoryName
      description
    }
    promotion {
      promotionId
      promotionName
      discountPercentage
    }
  }
}
```

#### Obtener productos por categoría
```graphql
query GetProductsByCategory($categoryId: ID!) {
  productsByCategory(categoryId: $categoryId) {
    productId
    productName
    basePrice
    sku
    category {
      categoryId
      categoryName
    }
    promotion {
      promotionId
      promotionName
      discountPercentage
    }
  }
}
```

**Variables:**
```json
{
  "categoryId": "1"
}
```

#### Obtener un producto específico
```graphql
query GetProduct($id: ID!) {
  product(id: $id) {
    productId
    productName
    basePrice
    sku
    category {
      categoryId
      categoryName
      description
    }
    promotion {
      promotionId
      promotionName
      description
      discountPercentage
      startDate
      endDate
    }
  }
}
```

**Variables:**
```json
{
  "id": "1"
}
```

---

## ⚡ MUTATIONS (Mutaciones)

### 🔐 Autenticación

#### Login
```graphql
mutation Login($email: String!, $password: String!) {
  login(email: $email, password: $password) {
    token
    user {
      userId
      userName
      email
      role {
        roleId
        roleName
      }
    }
    success
  }
}
```

**Variables:**
```json
{
  "email": "admin@petstore.com",
  "password": "admin123"
}
```

### 🏷️ Gestión de Promociones (requiere autenticación de admin)

#### Crear promoción
```graphql
mutation CreatePromotion($input: PromotionInput!) {
  createPromotion(input: $input) {
    promotionId
    promotionName
    description
    startDate
    endDate
    discountPercentage
    status {
      statusId
      statusName
    }
    user {
      userId
      userName
    }
    category {
      categoryId
      categoryName
    }
  }
}
```

**Variables:**
```json
{
  "input": {
    "promotionName": "Summer Sale 2025",
    "description": "Gran descuento de verano",
    "startDate": "2025-06-01",
    "endDate": "2025-08-31",
    "discountPercentage": 25.0,
    "statusId": "1",
    "userId": "1",
    "categoryId": "1"
  }
}
```

#### Actualizar promoción
```graphql
mutation UpdatePromotion($id: ID!, $input: PromotionInput!) {
  updatePromotion(id: $id, input: $input) {
    promotionId
    promotionName
    description
    startDate
    endDate
    discountPercentage
    status {
      statusId
      statusName
    }
    category {
      categoryId
      categoryName
    }
  }
}
```

**Variables:**
```json
{
  "id": "1",
  "input": {
    "promotionName": "Summer Sale 2025 - Updated",
    "description": "Gran descuento de verano actualizado",
    "startDate": "2025-06-01",
    "endDate": "2025-09-15",
    "discountPercentage": 30.0,
    "statusId": "1",
    "userId": "1",
    "categoryId": "1"
  }
}
```

#### Eliminar promoción (soft delete)
```graphql
mutation DeletePromotion($id: ID!, $userId: ID) {
  deletePromotion(id: $id, userId: $userId)
}
```

**Variables:**
```json
{
  "id": "1",
  "userId": "1"
}
```

### 🔗 Asociación de Productos con Promociones

#### Asociar productos a una promoción

**Opción 1: Mutación directa**
```graphql
mutation {
  associateProductsToPromotion(promotionId: "20", productIds: ["1", "2"]) {
    promotionId
    promotionName
    category {
      categoryId
      categoryName
    }
    products {
      productId
      productName
      basePrice
    }
  }
}
```

**Opción 2: Con variables (recomendada)**
```graphql
mutation AssociateProductsToPromotion($promotionId: ID!, $productIds: [ID!]!) {
  associateProductsToPromotion(promotionId: $promotionId, productIds: $productIds) {
    promotionId
    promotionName
    category {
      categoryId
      categoryName
    }
    products {
      productId
      productName
      basePrice
    }
  }
}
```

**Variables:**
```json
{
  "promotionId": "20",
  "productIds": ["1", "2"]
}
```

#### Remover productos de una promoción
```graphql
mutation RemoveProductsFromPromotion($promotionId: ID!, $productIds: [ID!]!) {
  removeProductsFromPromotion(promotionId: $promotionId, productIds: $productIds) {
    promotionId
    promotionName
    products {
      productId
      productName
      basePrice
    }
  }
}
```

**Variables:**
```json
{
  "promotionId": "20",
  "productIds": ["2", "3"]
}
```

### 🗑️ Gestión de Papelera

#### Restaurar promoción desde papelera
```graphql
mutation RestorePromotion($id: ID!, $userId: ID!) {
  restorePromotion(id: $id, userId: $userId)
}
```

**Variables:**
```json
{
  "id": "1",
  "userId": "1"
}
```

#### Eliminar promoción permanentemente
```graphql
mutation PermanentDeletePromotion($id: ID!, $userId: ID!) {
  permanentDeletePromotion(id: $id, userId: $userId)
}
```

**Variables:**
```json
{
  "id": "1",
  "userId": "1"
}
```

---

## 🧪 Ejemplos de Pruebas Completas

### Flujo de trabajo completo: Crear y gestionar promoción

#### 1. Login
```graphql
mutation {
  login(email: "admin@petstore.com", password: "admin123") {
    token
    success
    user {
      userId
      userName
      role {
        roleName
      }
    }
  }
}
```

#### 2. Crear promoción
```graphql
mutation {
  createPromotion(input: {
    promotionName: "Black Friday 2025"
    description: "Descuentos especiales de Black Friday"
    startDate: "2025-11-29"
    endDate: "2025-11-30"
    discountPercentage: 50.0
    statusId: "1"
    userId: "1"
    categoryId: "1"
  }) {
    promotionId
    promotionName
    discountPercentage
  }
}
```

#### 3. Asociar productos
```graphql
mutation {
  associateProductsToPromotion(promotionId: "1", productIds: ["1", "2"]) {
    promotionId
    promotionName
    products {
      productId
      productName
    }
  }
}
```

#### 4. Verificar promoción creada
```graphql
query {
  promotion(id: "1") {
    promotionId
    promotionName
    description
    discountPercentage
    products {
      productId
      productName
      basePrice
    }
  }
}
```

---

## 🚨 Notas Importantes

1. **Autenticación**: Las mutaciones requieren token JWT válido
2. **Permisos**: Solo usuarios con rol "ADMIN" pueden crear/modificar promociones
3. **Fechas**: Usar formato ISO (YYYY-MM-DD) para fechas
4. **IDs**: Todos los IDs son strings, aunque representen números
5. **Eliminación**: `deletePromotion` hace soft delete, `permanentDeletePromotion` es irreversible

---

## 🔧 Testing con herramientas

### Usando curl:
```bash
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{"query": "query { health }"}'
```

### Usando GraphQL Playground/Apollo Studio:
1. Conectar a: `http://localhost:8080/graphql`
2. Configurar headers de autorización
3. Copiar y pegar las queries/mutations de este archivo

¡Listo para hacer pruebas! 🚀
