# 🛍️ Petstore Feature 5 Backend

Sistema completo de promociones para petstore con autenticación JWT, API REST y GraphQL, incluyendo **sistema de eliminación con papelera temporal**.

## 📋 Tabla de Contenidos

- [Descripción](#-descripción)
- [Características](#-características)
- [Tecnologías](#️-tecnologías)
- [Instalación](#-instalación)
- [Configuración](#️-configuración)
- [Ejecución](#-ejecución)
- [API REST Endpoints](#-api-rest-endpoints)
- [GraphQL API](#-graphql-api)
- [Base de Datos](#️-base-de-datos)
- [Sistema de Papelera Temporal](#-sistema-de-papelera-temporal)
- [Estructura del Proyecto](#-estructura-del-proyecto)
- [Troubleshooting](#-troubleshooting)

## 🆕 **Características Principales**

### ✅ **Sistema de Eliminación con Papelera Temporal** 
- **Eliminación segura**: Las promociones se mueven a papelera temporal (no eliminación directa)
- **Papelera temporal**: Promociones eliminadas se conservan 30 días con purga automática
- **Auditoría completa**: Registro automático de usuario, fecha de eliminación y restauración
- **Restauración**: Posibilidad de recuperar promociones via REST y GraphQL
- **Estado INACTIVE**: Las promociones eliminadas cambian a estado INACTIVE automáticamente

### ✅ **API Dual: REST + GraphQL** 
- **REST API**: Endpoints completos para todas las operaciones CRUD
- **GraphQL API**: Queries y mutations con JWT real (no tokens falsos)
- **Autenticación**: JWT compartido entre REST y GraphQL
- **Paridad funcional**: Ambas APIs tienen las mismas capacidades

### ✅ **Base de Datos Optimizada**
- **PostgreSQL**: Con triggers, funciones y auditoría automática
- **HikariCP**: Pool de conexiones optimizado para producción
- **Triggers avanzados**: Manejo automático de eliminación/restauración
- **Whitelist Actualizada**: Endpoints públicos configurados correctamente

---

## �🎯 Descripción

Sistema backend para gestión de promociones en petstore que incluye:
- Autenticación JWT con Spring Security (**JWT reales en GraphQL**)
- API REST y GraphQL (**GraphiQL público en ambos ambientes**)
- Gestión de promociones, categorías, productos y usuarios
- Integración con Neon Database (PostgreSQL) (**optimizada para producción**)
- Sistema de roles y permisos

## ✨ Características

- **Autenticación JWT**: Login seguro con tokens
- **API Dual**: REST + GraphQL para máxima flexibilidad
- **Base de Datos**: PostgreSQL en Neon con migraciones Flyway
- **Seguridad**: Spring Security 6.x con BCrypt
- **Capa de Mappers**: MapStruct para transformaciones seguras de datos
- **Documentación**: GraphiQL integrado para pruebas
- **Testing**: Scripts automatizados de pruebas
- **CORS**: Configurado para desarrollo frontend

## 🗺️ Arquitectura de Mappers

El proyecto implementa una **capa de mappers profesional** usando **MapStruct**:

### Mappers Implementados:
- **UserMapper**: Convierte entidades User a DTOs seguros (sin password)
- **PromotionMapper**: Mapeo completo con relaciones aplanadas
- **ProductMapper**: Transformaciones con cálculo de precios
- **CategoryMapper**: Mapeo bidireccional de categorías
- **MapperFacade**: Acceso centralizado a todos los mappers

### DTOs de Respuesta:
- **UserResponseDTO**: Usuario sin información sensible
- **PromotionResponseDTO**: Promoción con datos optimizados
- **ProductResponseDTO**: Producto con precio final calculado

### Beneficios:
- ✅ **Seguridad**: No expone passwords ni datos sensibles
- ✅ **Performance**: Relaciones aplanadas evitan lazy loading
- ✅ **Mantenibilidad**: Código generado automáticamente
- ✅ **Separación de capas**: Entities ≠ DTOs

## 🛠️ Tecnologías

- **Java 21**
- **Spring Boot 3.5.5**
- **Spring Security 6.x**
- **Spring Data JPA**
- **GraphQL Java**
- **PostgreSQL** (Neon Database)
- **Flyway** (Migraciones)
- **JWT** (Autenticación)
- **MapStruct** (Mapeo de objetos)
- **Maven** (Gestión de dependencias)

## 🌐 Endpoints con Mappers

### REST API (Usando MapStruct):
```
🔐 AUTH:
GET  /api/auth/status          → Map<String,Object>
POST /api/auth/login           → LoginResponse
GET  /api/auth/verify          → JSON (valid: boolean)
GET  /api/auth/me              → UserInfo  
POST /api/auth/logout          → JSON (message)

📦 PRODUCTS:
GET  /api/products                     → ProductDTO[]
GET  /api/products/category/{id}       → ProductDTO[]
GET  /api/products/{id}                → ProductDTO
GET  /api/products/search?name=        → ProductDTO[]
GET  /api/products/price-range?min=&max= → ProductDTO[]

🏷️ PROMOTIONS:
GET  /api/promotions                   → PromotionDTO[]
GET  /api/promotions/all               → PromotionDTO[]
GET  /api/promotions/category/{id}     → PromotionDTO[]
GET  /api/promotions/valid             → PromotionDTO[]
GET  /api/promotions/status            → Map<String,Object>

🗑️ ELIMINACIÓN Y PAPELERA:
DELETE /api/promotions/{id}?userId=           → JSON (success, message)
GET    /api/promotions/trash                  → PromotionDeletedDTO[]
GET    /api/promotions/trash/user/{userId}    → PromotionDeletedDTO[]
POST   /api/promotions/{id}/restore?userId=   → JSON (success, message)

📁 CATEGORIES:
GET  /api/categories           → CategoryDTO[]
GET  /api/categories/{id}      → CategoryDTO
POST /api/categories           → CategoryDTO
PUT  /api/categories/{id}      → CategoryDTO
DELETE /api/categories/{id}    → void
GET  /api/categories/info      → String
```

### GraphQL (Entities directas):
```graphql
query {
  products { id, name, price, category { categoryName } }
  promotions { id, title, user { userName } }
  categories { id, name, description }
}
```

**✨ Características:**
- **25 endpoints REST** implementados con MapStruct
- **21 operaciones GraphQL** (16 queries + 5 mutations) + 3 schema mappings
- **DTOs seguros** sin información sensible  
- **API Dual** para máxima flexibilidad REST + GraphQL

## �🚀 Instalación

### Prerrequisitos

- Java 21 o superior
- Maven 3.6+
- Cuenta en [Neon Database](https://neon.tech/) (gratuita)

### 1. Clonar el Repositorio

```bash
git clone https://github.com/RUTENCO/petstore-feature5-backend.git
cd petstore-feature5-backend
```

### 2. Instalar Dependencias

```bash
mvn clean install
```

## ⚙️ Configuración

### 1. Variables de Entorno (.env)

Crear archivo `.env` en la raíz del proyecto:

```env
# =========================
# NEON DATABASE CONFIG
# =========================
NEON_DATABASE_URL=postgresql://username:password@host/database?sslmode=require
NEON_USERNAME=tu_username
NEON_PASSWORD=tu_password
NEON_HOST=tu_host.neon.tech
NEON_DATABASE=tu_database_name

# =========================
# JWT CONFIG
# =========================
JWT_SECRET=miClaveSecretaMuySeguraParaJWT2024PetstoreFeature5Backend
JWT_EXPIRATION=86400000

# =========================
# SPRING PROFILES
# =========================
SPRING_PROFILES_ACTIVE=default

# =========================
# SERVER CONFIG
# =========================
SERVER_PORT=8080

# =========================
# CORS CONFIG
# =========================
CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:5173,http://localhost:8081,https://petstore-feature5-backend.onrender.com
```

### 2. 🚀 **Perfiles de Configuración**

#### **Desarrollo** (`application-dev.properties`)
```properties
# GraphQL - Completamente público
spring.graphql.graphiql.enabled=true
app.security.whitelist=/api/auth/login,/graphiql,/graphql,/actuator/health

# Base de datos local con logs SQL
spring.jpa.show-sql=true
logging.level.com.petstore.backend=DEBUG
```

#### **Producción** (`application-prod.properties`)  
```properties
# GraphQL - Público pero optimizado
spring.graphql.graphiql.enabled=true
app.security.whitelist=/api/auth/login,/graphiql,/graphql,/actuator/health

# Base de datos optimizada (HikariCP)
spring.datasource.hikari.minimum-idle=2
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.connection-timeout=20000

# JPA optimizado para producción
spring.jpa.properties.hibernate.jdbc.batch_size=25
spring.jpa.properties.hibernate.order_inserts=true
spring.jpa.properties.hibernate.order_updates=true
spring.jpa.properties.hibernate.jdbc.batch_versioned_data=true
```

### 3. Configurar Neon Database

1. Crear cuenta en [Neon](https://neon.tech/)
2. Crear nuevo proyecto
3. Copiar connection string
4. Actualizar variables en `.env`

### 4. 🔧 **Usuarios de Prueba Configurados**

| Email | Password | Role | Descripción |
|-------|----------|------|-------------|
| `alice@example.com` | `password123` | Marketing Admin | ✅ Usuario para pruebas GraphQL |
| `admin@petstore.com` | `password123` | Marketing Admin | Usuario principal |

**Ejemplo de uso**:
```bash
# REST Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "alice@example.com", "password": "password123"}'

# GraphQL Login
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -d '{"query": "mutation { login(email: \"alice@example.com\", password: \"password123\") { success token } }"}'
```

## ▶️ Ejecución

### 1. Ejecutar la Aplicación

```bash
# Ejecutar con perfil por defecto (desarrollo)
mvn spring-boot:run

# Ejecutar en modo desarrollo
mvn spring-boot:run -Dspring.profiles.active=dev

# Ejecutar en modo producción
mvn spring-boot:run -Dspring.profiles.active=prod
```

### 2. 🎯 **Verificar que está Funcionando**

La aplicación estará disponible en: `http://localhost:8080`

**Endpoints de verificación:**
- Health Check: `http://localhost:8080/actuator/health`
- **GraphiQL** (público): `http://localhost:8080/graphiql` ✅
- **GraphQL API**: `http://localhost:8080/graphql`

**✅ Prueba rápida de GraphQL**:
```bash
# Consulta pública (sin JWT)
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -d '{"query": "{ categories { categoryId categoryName } }"}'

# Login para obtener JWT real
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -d '{"query": "mutation { login(email: \"alice@example.com\", password: \"password123\") { success token } }"}'
```

### 3. 🏭 **Construir JAR para Producción**

```bash
# Construir JAR optimizado
mvn clean package -DskipTests

# Ejecutar en producción con variables de entorno
SPRING_PROFILES_ACTIVE=prod java -jar target/petstore-feature5-backend-0.0.1-SNAPSHOT.jar

# O en Windows PowerShell
$env:SPRING_PROFILES_ACTIVE="prod"; java -jar target/petstore-feature5-backend-0.0.1-SNAPSHOT.jar
```

### 4. 📊 **Estado de la Aplicación por Perfil**

#### **Desarrollo** (`dev` profile):
- 🌐 GraphiQL: **Público** en `http://localhost:8080/graphiql`
- 🔍 SQL Logs: **Habilitados** para debugging
- 🐛 Debug Logs: **Habilitados** para desarrollo

#### **Producción** (`prod` profile):
- 🌐 GraphiQL: **Público** (optimizado para pruebas)
- ⚡ HikariCP: **Optimizado** para Neon Database
- 🚀 JPA: **Configurado** para alto rendimiento
- 📝 Logs: **Solo errores** y información esencial

## 🛠️ Uso de MapStruct

### Ejemplo en Controller:
```java
@RestController
@RequestMapping("/api/products")
public class ProductController {
    
    @Autowired
    private ProductMapper productMapper;
    
    @GetMapping
    public List<ProductResponseDTO> getAllProducts() {
        List<Product> products = productService.findAll();
        return productMapper.toResponseDTOList(products); // 🔄 Mapeo automático
    }
}
```

### Ejemplo en Service:
```java
@Service
public class ProductService {
    
    @Autowired
    private MapperFacade mapperFacade; // 🎯 Acceso centralizado
    
    public ProductResponseDTO createProduct(CreateProductInput input) {
        Product entity = mapperFacade.getProductMapper().toEntity(input);
        Product saved = productRepository.save(entity);
        return mapperFacade.getProductMapper().toResponseDTO(saved);
    }
}
```

### Generación Automática:
MapStruct genera **automáticamente** las implementaciones en `/target/generated-sources/annotations/`

## 🌐 API REST Endpoints

> **📋 Endpoints Verificados**: Esta documentación muestra únicamente los endpoints que están **realmente implementados** y funcionales en el código.

### 🔐 Autenticación - `/api/auth/*` (5 endpoints)

| Método | Endpoint | Descripción | Auth | Response |
|--------|----------|-------------|------|----------|
| GET | `/api/auth/status` | Estado del servicio de autenticación | No | `Map<String,Object>` |
| POST | `/api/auth/login` | Login de usuario (Marketing Admin) | No | `LoginResponse` |
| GET | `/api/auth/verify` | Verificar validez del token JWT | JWT | `JSON (valid: boolean)` |
| GET | `/api/auth/me` | Perfil del usuario autenticado | Sí | `UserInfo` |
| POST | `/api/auth/logout` | Cerrar sesión del usuario | No | `JSON (message)` |

### 📦 Productos

| Método | Endpoint | Descripción | Auth | Response |
|--------|----------|-------------|------|----------|
| GET | `/api/products` | Listar todos los productos | No | `ProductDTO[]` |
| GET | `/api/products/category/{categoryId}` | Productos por categoría | No | `ProductDTO[]` |
| GET | `/api/products/{id}` | Obtener producto por ID | No | `ProductDTO` |
| GET | `/api/products/search?name={nombre}` | Buscar productos por nombre | No | `ProductDTO[]` |
| GET | `/api/products/price-range?minPrice={min}&maxPrice={max}` | Productos por rango de precio | No | `ProductDTO[]` |

### 🏷️ Promociones

| Método | Endpoint | Descripción | Auth | Response |
|--------|----------|-------------|------|----------|
| GET | `/api/promotions` | Promociones activas y vigentes | No | `PromotionDTO[]` |
| GET | `/api/promotions/all` | Todas las promociones (admin) | No | `PromotionDTO[]` |
| GET | `/api/promotions/category/{categoryId}` | Promociones por categoría | No | `PromotionDTO[]` |
| GET | `/api/promotions/valid` | Promociones vigentes para hoy | No | `PromotionDTO[]` |
| GET | `/api/promotions/status` | Estado del servicio | No | `Map<String,Object>` |

### �️ Eliminación con Papelera Temporal

> **🆕 Nueva funcionalidad**: Sistema de eliminación con confirmación doble y papelera temporal de 30 días.

| Método | Endpoint | Descripción | Auth | Response |
|--------|----------|-------------|------|----------|
| DELETE | `/api/promotions/{id}?userId={userId}&confirmed={boolean}` | Eliminar promoción (confirmación doble) | No | `DeletionConfirmationDTO` |
| GET | `/api/promotions/trash` | Ver papelera temporal | No | `PromotionDeletedDTO[]` |
| GET | `/api/promotions/trash/user/{userId}` | Papelera por usuario | No | `PromotionDeletedDTO[]` |
| POST | `/api/promotions/{id}/restore?userId={userId}` | Restaurar promoción | No | `DeletionConfirmationDTO` |

#### 🔄 Flujo de Eliminación

1. **Primera llamada** (`confirmed=false`): Sistema muestra advertencia
   ```
   ⚠️ "¿Seguro que deseas eliminar esta promoción? 
       Esta acción moverá el registro a la papelera temporal (30 días)."
   ```

2. **Segunda llamada** (`confirmed=true`): Confirma eliminación
   - Promoción cambia a estado `INACTIVE` (ID: 4)
   - Se mueve a tabla `promotions_deleted`
   - Se elimina de tabla principal `promotions`
   - Se registra auditoría (usuario, fecha, hora)

3. **Papelera temporal**: 30 días de retención
   - ✅ **Restaurable**: Menos de 30 días
   - ❌ **Purgable**: Más de 30 días (automático por triggers DB)

### �📁 Categorías

| Método | Endpoint | Descripción | Auth | Response |
|--------|----------|-------------|------|----------|
| GET | `/api/categories` | Listar todas las categorías | No | `CategoryDTO[]` |
| GET | `/api/categories/{id}` | Obtener categoría por ID | No | `CategoryDTO` |
| POST | `/api/categories` | Crear nueva categoría | No | `CategoryDTO` |
| PUT | `/api/categories/{id}` | Actualizar categoría existente | No | `CategoryDTO` |
| DELETE | `/api/categories/{id}` | Eliminar categoría | No | `void` |
| GET | `/api/categories/info` | Información de endpoints | No | `String` |

### 📊 Resumen de Endpoints

- **Total**: 25 endpoints REST implementados
- **Autenticación**: 5 endpoints (`/api/auth/*`)
- **Productos**: 5 endpoints (`/api/products/*`)  
- **Promociones**: 5 endpoints (`/api/promotions/*`)
- **Categorías**: 6 endpoints (`/api/categories/*`)
- **GraphQL**: 1 endpoint adicional (`/graphql`)

### 📝 Ejemplos de Uso REST

```bash
# =============================
# 🔐 AUTENTICACIÓN
# =============================

# 1. Verificar estado del servicio
curl -X GET http://localhost:8080/api/auth/status

# 2. Login de usuario
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "alice@example.com",
    "password": "password123"
  }'

# 3. Verificar token (usar token del login)
curl -X GET http://localhost:8080/api/auth/verify \
  -H "Authorization: Bearer TU_TOKEN_AQUI"

# 4. Obtener perfil del usuario autenticado
curl -X GET http://localhost:8080/api/auth/me \
  -H "Authorization: Bearer TU_TOKEN_AQUI"

# 5. Logout
curl -X POST http://localhost:8080/api/auth/logout

# =============================
# 📦 PRODUCTOS
# =============================

# Listar todos los productos
curl -X GET http://localhost:8080/api/products

# Obtener productos por categoría
curl -X GET http://localhost:8080/api/products/category/1

# Obtener producto específico
curl -X GET http://localhost:8080/api/products/1

# Buscar productos por nombre
curl -X GET "http://localhost:8080/api/products/search?name=laptop"

# Productos por rango de precios
curl -X GET "http://localhost:8080/api/products/price-range?minPrice=100&maxPrice=500"

# =============================
# 🏷️ PROMOCIONES
# =============================

# Listar promociones activas
curl -X GET http://localhost:8080/api/promotions

# Listar todas las promociones (admin)
curl -X GET http://localhost:8080/api/promotions/all

# Promociones por categoría
curl -X GET http://localhost:8080/api/promotions/category/1

# Promociones vigentes para hoy
curl -X GET http://localhost:8080/api/promotions/valid

# Estado del servicio de promociones
curl -X GET http://localhost:8080/api/promotions/status

# =============================
# 📁 CATEGORÍAS
# =============================

# Listar todas las categorías
curl -X GET http://localhost:8080/api/categories

# Obtener categoría específica
curl -X GET http://localhost:8080/api/categories/1

# Crear nueva categoría
curl -X POST http://localhost:8080/api/categories \
  -H "Content-Type: application/json" \
  -d '{
    "categoryName": "Electrónicos",
    "description": "Productos electrónicos y gadgets"
  }'

# Actualizar categoría
curl -X PUT http://localhost:8080/api/categories/1 \
  -H "Content-Type: application/json" \
  -d '{
    "categoryName": "Electrónicos Actualizados",
    "description": "Productos electrónicos y gadgets modernos"
  }'

# Eliminar categoría
curl -X DELETE http://localhost:8080/api/categories/1

# Información de endpoints
curl -X GET http://localhost:8080/api/categories/info
```

## 🔗 GraphQL API

> **Endpoint**: `http://localhost:8080/graphql`  
> **GraphiQL**: `http://localhost:8080/graphiql` (público en desarrollo Y producción)

### � Inventario GraphQL Completo

#### 🔍 **Queries Disponibles (16 queries)**

| Query | Parámetros | Descripción | Auth | Retorna |
|-------|------------|-------------|------|---------|
| `health` | - | Health check del sistema | No | `String!` |
| `currentUser` | - | Usuario autenticado actual | JWT | `User` |
| `promotions` | - | Todas las promociones | No | `[Promotion!]!` |
| `promotionsActive` | - | Solo promociones ACTIVE | No | `[Promotion!]!` |
| `promotionsExpired` | - | Solo promociones EXPIRED | No | `[Promotion!]!` |
| `promotionsScheduled` | - | Solo promociones SCHEDULE | No | `[Promotion!]!` |
| `promotionsByStatus` | `statusName: String!` | Promociones por estado específico | No | `[Promotion!]!` |
| `promotionsByCategory` | `categoryId: ID!` | Promociones por categoría | No | `[Promotion!]!` |
| `promotion` | `id: ID!` | Promoción específica por ID | No | `Promotion` |
| `deletedPromotions` | - | **Papelera temporal (30 días)** | JWT | `[PromotionDeleted!]!` |
| `deletedPromotionsByUser` | `userId: ID!` | **Papelera por usuario** | JWT | `[PromotionDeleted!]!` |
| `categories` | - | Todas las categorías | No | `[Category!]!` |
| `category` | `id: ID!` | Categoría específica por ID | No | `Category` |
| `products` | - | Todos los productos | No | `[Product!]!` |
| `productsByCategory` | `categoryId: ID!` | Productos por categoría | No | `[Product!]!` |
| `product` | `id: ID!` | Producto específico por ID | No | `Product` |

#### ⚡ **Mutations Disponibles (5 mutations)**

| Mutation | Parámetros | Descripción | Auth | Retorna |
|----------|------------|-------------|------|---------|
| `login` | `email: String!, password: String!` | **Login con JWT real** | No | `GraphQLLoginResponse!` |
| `createPromotion` | `input: PromotionInput!` | Crear nueva promoción | JWT | `Promotion!` |
| `updatePromotion` | `id: ID!, input: PromotionInput!` | Actualizar promoción existente | JWT | `Promotion!` |
| `deletePromotion` | `id: ID!, userId: ID` | **Eliminar (papelera temporal)** | JWT | `Boolean!` |
| `restorePromotion` | `id: ID!, userId: ID!` | **Restaurar desde papelera** | JWT | `Boolean!` |

### 🛡️ **Política de Seguridad GraphQL**

- **Públicas**: 14 queries + 1 mutation (`login`) = **15 operaciones públicas**
- **Protegidas**: 2 queries (`currentUser`, `deletedPromotions`, `deletedPromotionsByUser`) + 4 mutations = **6 operaciones con JWT**
- **Papelera temporal**: 2 queries + 2 mutations = **4 operaciones específicas de trash bin**
- **Schema Mappings**: 3 resolvers adicionales para relaciones (promotionProducts, categoryPromotions, categoryProducts)

### 🔐 **Autenticación JWT Real**

**✅ NOVEDAD**: GraphQL ahora genera **JWT tokens reales** usando el mismo `AuthService` que REST:

```graphql
mutation LoginReal {
  login(email: "alice@example.com", password: "password123") {
    success
    token      # ← JWT real generado por AuthService
    user {
      userId
      userName
      email
      role { roleName }
    }
  }
}
```

### 📝 **Ejemplos de Uso GraphQL**

#### 🔍 **Consultas Básicas**

```graphql
# =============================
# CONSULTAS PÚBLICAS
# =============================

query ConsultasPublicas {
  # Health check
  health
  
  # Todas las promociones con detalles completos
  promotions {
    promotionId
    promotionName
    description
    startDate
    endDate
    discountValue
    status {
      statusId
      statusName
    }
    category {
      categoryId
      categoryName
      description
    }
    user {
      userId
      userName
    }
  }
  
  # Solo promociones activas
  promotionsActive {
    promotionId
    promotionName
    discountValue
    status { statusName }
  }
  
  # Promociones por estado específico
  promotionsByStatus(statusName: "ACTIVE") {
    promotionId
    promotionName
    status { statusName }
  }
  
  # Promociones por categoría
  promotionsByCategory(categoryId: "1") {
    promotionId
    promotionName
    category { categoryName }
  }
  
  # Todas las categorías
  categories {
    categoryId
    categoryName
    description
  }
  
  # Todos los productos
  products {
    productId
    productName
    basePrice
    sku
    category {
      categoryName
    }
  }
}
```

#### 🗑️ **Sistema de Papelera Temporal**

```graphql
# =============================
# PAPELERA TEMPORAL (Requiere JWT)
# =============================

# 1. Eliminar promoción (mover a papelera)
mutation EliminarPromocion {
  deletePromotion(id: "6", userId: "1")
}

# 2. Ver papelera temporal
query VerPapelera {
  deletedPromotions {
    promotionId
    promotionName
    description
    status {
      statusId
      statusName
    }
    deletedAt
    daysUntilPurge
    deletedBy {
      userId
      userName
    }
    category {
      categoryName
    }
  }
}

# 3. Papelera filtrada por usuario
query PapeleraPorUsuario {
  deletedPromotionsByUser(userId: "1") {
    promotionId
    promotionName
    deletedAt
    daysUntilPurge
  }
}

# 4. Restaurar promoción
mutation RestaurarPromocion {
  restorePromotion(id: "6", userId: "1")
}
```

#### ⚡ **Mutations Avanzadas** 

```graphql
# =============================
# OPERACIONES ADMINISTRATIVAS (Requiere JWT)
# =============================

# Crear nueva promoción
mutation CrearPromocion {
  createPromotion(input: {
    promotionName: "Black Friday 2024"
    description: "Descuentos especiales de Black Friday"
    startDate: "2024-11-29"
    endDate: "2024-11-30"
    discountPercentage: 50.0
    statusId: "1"
    userId: "1"
    categoryId: "1"
  }) {
    promotionId
    promotionName
    status { statusName }
  }
}

# Actualizar promoción existente
mutation ActualizarPromocion {
  updatePromotion(id: "1", input: {
    promotionName: "Black Friday 2024 - Extendido"
    description: "Descuentos extendidos hasta diciembre"
    startDate: "2024-11-29"
    endDate: "2024-12-02"
    discountPercentage: 60.0
    statusId: "1"
    categoryId: "1"
  }) {
    promotionId
    promotionName
    discountValue
    status { statusName }
  }
}
```

### 🚀 **Consultas Relacionales Avanzadas**

```graphql
# Consulta completa con todas las relaciones
query ConsultaCompleta {
  promotions {
    promotionId
    promotionName
    discountValue
    
    # Relación con Status
    status {
      statusId
      statusName
    }
    
    # Relación con User
    user {
      userId
      userName
      email
      role {
        roleId
        roleName
      }
    }
    
    # Relación con Category
    category {
      categoryId
      categoryName
      description
      
      # Productos de esta categoría
      products {
        productId
        productName
        basePrice
      }
    }
    
    # Productos asociados a esta promoción
    products {
      productId
      productName
      basePrice
      sku
    }
  }
}

### 🌟 **Capacidades Avanzadas de GraphQL**

GraphQL te permite hacer consultas **anidadas y relacionales** de forma natural. Esto significa que en una sola petición puedes obtener datos relacionados de múltiples entidades.

### **¿Por qué puedes hacer consultas tan flexibles?**

1. **Schema Relacional**: Nuestro schema define relaciones bidireccionales entre entidades
2. **Resolución Automática**: Spring GraphQL resuelve automáticamente las relaciones JPA
3. **Navegación de Grafo**: Puedes navegar por el grafo de datos en cualquier dirección

### Consultas Básicas Disponibles

```graphql
query ConsultasBasicas {
  # Salud del sistema
  health
  
  # Usuario actual (requiere autenticación)
  currentUser {
    userId
    userName
    email
    role {
      roleId
      roleName
    }
  }
  
  # Todas las promociones
  promotions {
    promotionId
    promotionName
    description
    startDate
    endDate
    discountValue
  }
  
  # Solo promociones activas (status: ACTIVE)
  promotionsActive {
    promotionId
    promotionName
    discountValue
    status {
      statusName
    }
  }
  
  # Solo promociones expiradas (status: EXPIRED)
  promotionsExpired {
    promotionId
    promotionName
    discountValue
    status {
      statusName
    }
  }
  
  # Solo promociones programadas (status: SCHEDULE)
  promotionsScheduled {
    promotionId
    promotionName
    discountValue
    startDate
    endDate
    status {
      statusName
    }
  }
  
  # Promociones por estado específico
  promotionsByStatus(statusName: "ACTIVE") {
    promotionId
    promotionName
    discountValue
    status {
      statusName
    }
  }
  
  # Promociones por categoría
  promotionsByCategory(categoryId: "1") {
    promotionId
    promotionName
    category {
      categoryName
    }
  }
  
  # Promoción específica
  promotion(id: "1") {
    promotionId
    promotionName
    description
  }
  
  # Todas las categorías
  categories {
    categoryId
    categoryName
    description
  }
  
  # Categoría específica
  category(id: "1") {
    categoryId
    categoryName
    description
  }
  
  # Todos los productos
  products {
    productId
    productName
    basePrice
    sku
  }
  
  # Productos por categoría
  productsByCategory(categoryId: "1") {
    productId
    productName
    basePrice
  }
  
  # Producto específico
  product(id: "1") {
    productId
    productName
    basePrice
    sku
  }
}
```

### 🚀 **Consultas Anidadas Avanzadas**

Estas son las consultas **realmente poderosas** que puedes hacer:

#### **1. Categorías con todos sus productos y promociones**

```graphql
query CategoriasCompletas {
  categories {
    categoryId
    categoryName
    description
    
    # Productos de esta categoría
    products {
      productId
      productName
      basePrice
      sku
      
      # Promoción aplicada al producto (si tiene)
      promotion {
        promotionId
        promotionName
        discountValue
        startDate
        endDate
        
        # Usuario que creó la promoción
        user {
          userId
          userName
          email
          role {
            roleName
          }
        }
      }
    }
    
    # Promociones específicas de esta categoría
    promotions {
      promotionId
      promotionName
      description
      discountValue
      status {
        statusName
      }
      
      # Productos afectados por esta promoción
      products {
        productId
        productName
        basePrice
      }
    }
  }
}
```

#### **2. Promociones con análisis completo**

```graphql
query PromocionesAnalisis {
  promotions {
    promotionId
    promotionName
    description
    discountValue
    startDate
    endDate
    
    # Estado de la promoción
    status {
      statusId
      statusName
    }
    
    # Usuario que la creó
    user {
      userId
      userName
      email
      role {
        roleId
        roleName
      }
    }
    
    # Categoría objetivo
    category {
      categoryId
      categoryName
      description
      
      # Todos los productos de esta categoría
      products {
        productId
        productName
        basePrice
        sku
        
        # Ver si tienen promociones diferentes
        promotion {
          promotionId
          promotionName
          discountValue
        }
      }
    }
    
    # Productos específicamente incluidos en esta promoción
    products {
      productId
      productName
      basePrice
      sku
      category {
        categoryName
      }
    }
  }
}
```

#### **3. Productos con contexto completo**

```graphql
query ProductosContextoCompleto {
  products {
    productId
    productName
    basePrice
    sku
    
    # Categoría del producto
    category {
      categoryId
      categoryName
      description
      
      # Otras promociones de esta categoría
      promotions {
        promotionId
        promotionName
        discountValue
        status {
          statusName
        }
      }
    }
    
    # Promoción específica del producto
    promotion {
      promotionId
      promotionName
      description
      discountValue
      startDate
      endDate
      
      # Estado de la promoción
      status {
        statusId
        statusName
      }
      
      # Quien creó la promoción
      user {
        userId
        userName
        email
        role {
          roleName
        }
      }
      
      # Otros productos con la misma promoción
      products {
        productId
        productName
        basePrice
        category {
          categoryName
        }
      }
    }
  }
}
```

#### **4. Usuario con todo su contexto de promociones**

```graphql
query ContextoUsuario {
  currentUser {
    userId
    userName
    email
    
    role {
      roleId
      roleName
    }
    
    # Esta consulta requiere extender el schema para incluir:
    # promotions: [Promotion!]! en el type User
    # Pero conceptualmente sería:
    # 
    # promotions {
    #   promotionId
    #   promotionName
    #   discountValue
    #   status { statusName }
    #   category { categoryName }
    #   products {
    #     productName
    #     basePrice
    #   }
    # }
  }
}
```

#### **5. Consulta de análisis de precios**

```graphql
query AnalisisPrecios {
  categories {
    categoryName
    
    products {
      productName
      basePrice
      
      promotion {
        promotionName
        discountValue
        
        # Precio calculado sería: basePrice - (basePrice * discountValue / 100)
      }
    }
    
    promotions {
      promotionName
      discountValue
      
      products {
        productName
        basePrice
      }
    }
  }
}
```

### Mutations Disponibles

#### 🔐 **Login con JWT Real**

```graphql
mutation LoginReal {
  # ✅ ACTUALIZADO: Ahora genera JWT reales usando AuthService
  login(email: "alice@example.com", password: "password123") {
    success   # true si autenticación exitosa
    token     # JWT real (no fake) - eyJhbGciOiJIUzI1NiJ9...
    user {
      userId
      userName
      email
      role {
        roleId
        roleName
      }
    }
  }
}
  
  # Crear promoción (requiere autenticación)
  createPromotion(input: {
    promotionName: "Nueva Promoción"
    description: "Descripción de la promoción"
    startDate: "2025-09-23"
    endDate: "2025-12-31"
    discountValue: 15.0
    statusId: "1"
    categoryId: "1"
  }) {
    promotionId
    promotionName
    description
    discountValue
    startDate
    endDate
    
    status {
      statusName
    }
    
    category {
      categoryName
      
      # Otros productos de esta categoría
      products {
        productName
        basePrice
      }
    }
    
    # Productos que tendrán esta promoción
    products {
      productName
      basePrice
      category {
        categoryName
      }
    }
  }
  
  # Actualizar promoción (requiere autenticación)
  updatePromotion(id: "1", input: {
    promotionName: "Promoción Actualizada"
    description: "Nueva descripción"
    startDate: "2025-09-25"
    endDate: "2025-12-31"
    discountValue: 20.0
    statusId: "1"
    categoryId: "2"
  }) {
    promotionId
    promotionName
    discountValue
    
    category {
      categoryName
    }
    
    status {
      statusName
    }
  }
  
  # Eliminar promoción (requiere autenticación)
  deletePromotion(id: "1")
}
```

### 🎯 **Casos de Uso Prácticos**

#### **🆕 Consultas por Estado de Promociones**
```graphql
# Solo promociones activas
query PromocionesActivas {
  promotionsActive {
    promotionId
    promotionName
    discountValue
    startDate
    endDate
    status { statusName }
    category { categoryName }
  }
}

# Solo promociones expiradas
query PromocionesExpiradas {
  promotionsExpired {
    promotionId
    promotionName
    discountValue
    endDate
    status { statusName }
    category { categoryName }
  }
}

# Solo promociones programadas (futuras)
query PromocionesProgramadas {
  promotionsScheduled {
    promotionId
    promotionName
    discountValue
    startDate
    endDate
    status { statusName }
    category { categoryName }
  }
}

# Consulta flexible por cualquier estado
query PromocionePorEstado($estado: String!) {
  promotionsByStatus(statusName: $estado) {
    promotionId
    promotionName
    discountValue
    status { statusName }
  }
}
```

#### **Dashboard de Administración Completo**
```graphql
query DashboardCompleto {
  # Promociones activas
  activas: promotionsActive {
    promotionId
    promotionName
    discountValue
    status { statusName }
  }
  
  # Promociones expiradas
  expiradas: promotionsExpired {
    promotionId
    promotionName
    endDate
    status { statusName }
  }
  
  # Promociones programadas
  programadas: promotionsScheduled {
    promotionId
    promotionName
    startDate
    status { statusName }
  }
  
  # Categorías
  categories {
    categoryId
    categoryName
    products { productId }
  }
}
```

#### **Catálogo de Productos con Precios**
```graphql
query CatalogoCompleto {
  categories {
    categoryName
    
    products {
      productName
      basePrice
      
      promotion {
        discountValue
        startDate
        endDate
        status { statusName }
      }
    }
  }
}
```

#### **Reporte de Promociones Activas**
```graphql
query ReportePromocionesActivas {
  promotionsActive {
    promotionName
    discountValue
    startDate
    endDate
    
    category {
      categoryName
      
      products {
        productName
        basePrice
      }
    }
    
    user {
      userName
      role { roleName }
    }
  }
}
```

### 🔑 **Ejemplo con Headers de Autenticación**

Para queries que requieren autenticación, usar JWT real obtenido del login:

```json
{
  "Authorization": "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhbGljZUBleGFtcGxlLmNvbSIsImlhdCI6MTc1OTM3NDIyNSwiZXhwIjoxNzU5NDYwNjI1fQ.GUb6B9oaZgBAo-TEe2yM7zpv4pimgt5C5763-5ph0Kg"
}
```

**Proceso completo**:
1. Hacer `login` mutation para obtener token JWT real
2. Usar el token en header `Authorization: Bearer <token>`
3. Acceder a queries protegidas como `currentUser`

**🆕 Ejemplos PowerShell - Consultas por Estado**:
```powershell
# Promociones activas (sin JWT requerido)
Invoke-WebRequest -Uri "http://localhost:8080/graphql" -Method POST -Headers @{"Content-Type"="application/json"} -Body '{"query":"{ promotionsActive { promotionId promotionName discountValue status { statusName } } }"}'

# Promociones expiradas
Invoke-WebRequest -Uri "http://localhost:8080/graphql" -Method POST -Headers @{"Content-Type"="application/json"} -Body '{"query":"{ promotionsExpired { promotionId promotionName endDate status { statusName } } }"}'

# Promociones programadas
Invoke-WebRequest -Uri "http://localhost:8080/graphql" -Method POST -Headers @{"Content-Type"="application/json"} -Body '{"query":"{ promotionsScheduled { promotionId promotionName startDate status { statusName } } }"}'

# Promociones por estado específico
Invoke-WebRequest -Uri "http://localhost:8080/graphql" -Method POST -Headers @{"Content-Type"="application/json"} -Body '{"query":"{ promotionsByStatus(statusName: \"ACTIVE\") { promotionId promotionName status { statusName } } }"}'

# Dashboard completo con todos los estados
Invoke-WebRequest -Uri "http://localhost:8080/graphql" -Method POST -Headers @{"Content-Type"="application/json"} -Body '{"query":"{ activas: promotionsActive { promotionId promotionName } expiradas: promotionsExpired { promotionId promotionName } programadas: promotionsScheduled { promotionId promotionName } }"}'
```

**Ejemplo con PowerShell - JWT para consultas protegidas**:
```powershell
# 1. Login para obtener JWT
$loginResponse = Invoke-WebRequest -Uri "http://localhost:8080/graphql" -Method POST -Headers @{"Content-Type"="application/json"} -Body '{"query":"mutation { login(email: \"alice@example.com\", password: \"password123\") { success token } }"}'

# 2. Usar JWT en consulta protegida  
Invoke-WebRequest -Uri "http://localhost:8080/graphql" -Method POST -Headers @{"Content-Type"="application/json"; "Authorization"="Bearer <TOKEN_AQUI>"} -Body '{"query":"{ currentUser { userId userName email } }"}'
```

### 🗑️ **Ejemplos del Sistema de Eliminación**

**Primera llamada - Mostrar advertencia:**
```powershell
# Intentar eliminar promoción (primera llamada)
Invoke-WebRequest -Uri "http://localhost:8080/api/promotions/1?userId=1&confirmed=false" -Method DELETE -Headers @{"Content-Type"="application/json"}

# Respuesta esperada:
# {
#   "success": false,
#   "warningMessage": "¿Seguro que deseas eliminar esta promoción? Esta acción moverá el registro a la papelera temporal (30 días).",
#   "promotionId": 1,
#   "promotionName": "Black Friday 2024",
#   "actionRequired": "Para confirmar la eliminación, realiza una segunda llamada con el parámetro 'confirmed=true'",
#   "daysInTrash": 30
# }
```

**Segunda llamada - Confirmar eliminación:**
```powershell
# Confirmar eliminación (segunda llamada)
Invoke-WebRequest -Uri "http://localhost:8080/api/promotions/1?userId=1&confirmed=true" -Method DELETE -Headers @{"Content-Type"="application/json"}

# Respuesta esperada:
# {
#   "success": true,
#   "message": "Promoción eliminada exitosamente y movida a la papelera temporal.",
#   "promotionId": 1,
#   "promotionName": "Black Friday 2024",
#   "daysInTrash": 30
# }
```

**Ver papelera temporal:**
```powershell
# Ver todas las promociones en papelera
Invoke-WebRequest -Uri "http://localhost:8080/api/promotions/trash" -Method GET -Headers @{"Content-Type"="application/json"}

# Ver papelera por usuario específico
Invoke-WebRequest -Uri "http://localhost:8080/api/promotions/trash/user/1" -Method GET -Headers @{"Content-Type"="application/json"}
```

**Restaurar promoción:**
```powershell
# Restaurar promoción desde papelera
Invoke-WebRequest -Uri "http://localhost:8080/api/promotions/1/restore?userId=1" -Method POST -Headers @{"Content-Type"="application/json"}

# Respuesta esperada:
# {
#   "success": true,
#   "message": "Promoción restaurada exitosamente desde la papelera temporal.",
#   "promotionId": 2,
#   "promotionName": "Black Friday 2024"
# }
```

### 📊 **Resumen GraphQL**

- **Total Operaciones**: **21 operaciones GraphQL** (15 queries + 6 mutations)
- **Endpoint único**: `/graphql` para todas las operaciones
- **GraphiQL público**: Disponible en desarrollo y producción
- **JWT compartido**: Mismo sistema de autenticación que REST
- **Consultas relacionales**: Navegación completa por grafo de datos
- **Papelera temporal**: Sistema completo de eliminación/restauración

## 🗑️ Sistema de Papelera Temporal

> **🆕 Funcionalidad principal**: Sistema de eliminación con confirmación doble y papelera temporal de 30 días.

### ⚙️ **Flujo de Eliminación Completo**

1. **Eliminación soft**: Promoción cambia status a `INACTIVE` (ID: 4)
2. **Movimiento a papelera**: Se crea registro en tabla `promotions_deleted`
3. **Auditoría automática**: Se registra usuario, fecha y hora
4. **Retención temporal**: 30 días de conservación
5. **Purga automática**: Triggers de base de datos eliminan registros vencidos

### 🔗 **Endpoints Disponibles**

#### **REST API**
- `DELETE /api/promotions/{id}?userId={userId}` - Eliminar promoción
- `GET /api/promotions/trash` - Ver papelera completa  
- `GET /api/promotions/trash/user/{userId}` - Ver papelera por usuario
- `POST /api/promotions/{id}/restore?userId={userId}` - Restaurar promoción

#### **GraphQL API**
- `deletePromotion(id, userId)` - Eliminar promoción
- `deletedPromotions` - Ver papelera completa
- `deletedPromotionsByUser(userId)` - Ver papelera por usuario  
- `restorePromotion(id, userId)` - Restaurar promoción

### 🗃️ **Estructura de Datos**

#### **PromotionDeletedDTO** - Respuesta completa
```json
{
  "promotionId": 6,
  "promotionName": "Beauty Week",
  "description": "Promoción de productos de belleza",
  "startDate": "2024-10-01",
  "endDate": "2024-10-31", 
  "discountValue": 25.0,
  "statusId": 4,
  "statusName": "INACTIVE",
  "status": {
    "statusId": 4,
    "statusName": "INACTIVE"
  },
  "categoryId": 2,
  "categoryName": "Beauty",
  "category": {
    "categoryId": 2,
    "categoryName": "Beauty"
  },
  "userId": 1,
  "userName": "Alice Johnson", 
  "user": {
    "userId": 1,
    "userName": "Alice Johnson"
  },
  "deletedAt": "2024-10-29T15:30:45",
  "deletedById": 1,
  "deletedByName": "Alice Johnson",
  "deletedBy": {
    "userId": 1,
    "userName": "Alice Johnson"
  },
  "daysUntilPurge": 29
}
```

### 🎯 **Estados de Promociones**

| Estado | ID | Descripción | En Papelera |
|--------|----|-----------  |-------------|
| `ACTIVE` | 1 | Promoción activa y visible | ❌ No |
| `EXPIRED` | 2 | Promoción vencida pero visible | ❌ No |
| `SCHEDULE` | 3 | Promoción programada para futuro | ❌ No |
| `INACTIVE` | 4 | **Promoción eliminada (papelera)** | ✅ **Sí** |

### 🔄 **Proceso de Restauración**

1. **Consultar papelera**: Ver promociones eliminadas disponibles
2. **Verificar elegibilidad**: Solo promociones con < 30 días
3. **Ejecutar restauración**: Llamar endpoint de restauración
4. **Cambio automático**: Status cambia de `INACTIVE` a `ACTIVE`
5. **Remoción de papelera**: Se elimina de tabla `promotions_deleted`
6. **Auditoría**: Se registra la restauración en logs

### 💾 **Base de Datos Subyacente**

#### **Triggers Implementados**
- `trg_promotions_soft_delete` - Maneja eliminación y cambio de status
- `trg_promotions_deleted_guard` - Previene duplicados en papelera
- `trg_promotions_audit` - Registra todas las operaciones

#### **Funciones de Base de Datos**
- `fn_set_actor(user_id)` - Establece contexto de usuario
- `fn_restore_promotion(promo_id, user_id)` - Restaura promoción completa  
- `fn_purge_deleted_promotions()` - Limpieza automática (30 días)

## 🗄️ Base de Datos

### Ejecutar Data Seed

```bash
# 1. Conectar a tu base de datos Neon
psql "postgresql://username:password@host/database?sslmode=require"

# 2. Ejecutar el script de datos
\i data-seed.sql

# O usando herramientas gráficas como pgAdmin, DBeaver, etc.
# Simplemente ejecutar el contenido de data-seed.sql
```

## 🔄 REST vs GraphQL: Estrategia de Mappers

### 🎯 **¿Por qué doble estrategia?**

| Aspecto | REST (con DTOs) | GraphQL (con Entities) |
|---------|-----------------|------------------------|
| **Seguridad** | ✅ DTOs sin passwords | ⚠️ Entities completas |
| **Performance** | ✅ Relaciones aplanadas | ✅ Solo campos pedidos |
| **Frontend** | 🎯 Datos optimizados | 🎯 Queries flexibles |
| **Mantenimiento** | ✅ MapStruct automático | ✅ Schema GraphQL |

### **Recomendación de Uso:**
- **REST**: Apps móviles, APIs públicas, integraciones
- **GraphQL**: Admin panels, reportes complejos, desarrollo rápido

### Esquema de Base de Datos

#### Tablas Principales

- **roles**: Roles de usuario (Marketing Admin)
- **statuses**: Estados de promociones (ACTIVE, EXPIRED, SCHEDULE)
- **categories**: Categorías de productos
- **users**: Usuarios del sistema
- **promotions**: Promociones con descuentos
- **products**: Productos del catálogo

#### Usuario de Prueba

```
Email: admin@petstore.com
Password: password123
Role: Marketing Admin
```

## 📁 Archivos de Prueba

### 1. `frontend-jwt-guide.js`

**Propósito**: Guía completa para implementar autenticación JWT en el frontend.

**Contiene**:
- Funciones de login (REST y GraphQL)
- Manejo de tokens
- Peticiones autenticadas
- Funciones para todas las entidades
- Ejemplos de uso
- Validaciones y utilidades

**Uso**:
```javascript
// Incluir en tu proyecto frontend
<script src="frontend-jwt-guide.js"></script>

// Usar las funciones
loginUserGraphQL('admin@petstore.com', 'password123')
  .then(result => {
    if (result.success) {
      console.log('Login exitoso');
      // Continuar con tu aplicación
    }
  });
```

### 2. `test-api.js`

**Propósito**: Suite automatizada de pruebas para verificar que todos los endpoints funcionen.

**Incluye**:
- Tests de autenticación (REST y GraphQL)
- Tests de promociones (CRUD)
- Tests de categorías y productos
- Logging detallado de resultados

**Uso**:
```bash
# Ejecutar todas las pruebas
node test-api.js

# Solo pruebas de login
node test-api.js --login-only

# Solo pruebas de promociones
node test-api.js --promotions-only
```

### 3. `test-api.html`

**Propósito**: Interfaz web para probar GraphQL de forma interactiva.

**Características**:
- Login visual
- Pruebas de queries y mutations
- Interfaz amigable para QA
- No requiere herramientas adicionales

**Uso**:
Abrir `test-api.html` en el navegador y seguir las instrucciones.

### 4. `data-seed.sql`

**Propósito**: Datos de prueba para poblar la base de datos.

**Incluye**:
- Roles y estados
- 10 categorías de productos
- 11 usuarios de prueba
- 10 promociones con diferentes estados
- 10 productos con relaciones

## 📂 Estructura del Proyecto

```
petstore-feature5-backend/
├── 📁 src/main/java/com/petstore/backend/
│   ├── 📁 config/          # Configuraciones (Security, CORS, JWT, GraphQL)
│   ├── 📁 controller/      # Controllers REST (Auth, Promotions)
│   ├── 📁 dto/            # DTOs para transferencia de datos
│   ├── 📁 entity/         # Entidades JPA (User, Promotion, Category, etc.)
│   ├── 📁 exception/      # Manejo global de excepciones
│   ├── 📁 graphql/        # Resolvers GraphQL
│   ├── 📁 repository/     # Repositorios JPA
│   ├── 📁 service/        # Lógica de negocio
│   └── 📁 util/           # Utilidades (JWT, validaciones)
├── 📁 src/main/resources/
│   ├── 📁 db/migration/   # Scripts Flyway (migraciones)
│   ├── 📁 graphql/        # Esquemas GraphQL (.graphqls)
│   └── 📄 application*.properties  # Configuraciones por perfil
├── 📁 src/test/           # Tests unitarios e integración
├── 📄 data-seed.sql       # Datos de prueba para la BD
├── 📄 frontend-jwt-guide.js  # Guía completa para frontend
├── 📄 test-api.js         # Suite de pruebas automatizadas
├── 📄 test-api.html       # Interfaz de pruebas web
├── 📄 .env               # Variables de entorno
├── 📄 pom.xml            # Configuración Maven
└── 📄 README.md          # Esta documentación
```

### Descripción de Carpetas

- **config/**: Configuraciones de Spring (Security, CORS, JWT, GraphQL)
- **controller/**: Endpoints REST organizados por funcionalidad
- **dto/**: Objetos de transferencia de datos para requests/responses
- **entity/**: Modelos de base de datos con anotaciones JPA
- **exception/**: Manejo centralizado de errores
- **graphql/**: Resolvers y lógica específica de GraphQL
- **repository/**: Interfaces de acceso a datos con Spring Data JPA
- **service/**: Lógica de negocio y reglas de la aplicación
- **util/**: Clases auxiliares (JWT utilities, validadores, etc.)

## 🚨 Troubleshooting

### ✅ **Problemas Resueltos Recientemente**

#### **GraphQL Token Falso → JWT Real**
- **Problema**: GraphQL devolvía `fake-jwt-token` 
- **✅ Solución**: Ahora usa `AuthService.authenticateMarketingAdmin()` para JWT reales

#### **GraphiQL Restringido en Producción**
- **Problema**: GraphiQL inaccesible en modo producción
- **✅ Solución**: Configurado como público en `SecurityConfig` para ambos ambientes

#### **Errores de Conexión JDBC en Producción**
- **Problema**: `Connection timeout`, `Pool exhausted` en Neon Database
- **✅ Solución**: HikariCP optimizado con `minimum-idle=2`, `maximum-pool-size=10`

### Problemas Comunes

#### 1. Error de Conexión a Base de Datos

```
Error: Connection refused / Connection timeout
```

**Solución**:
- Verificar variables en `.env`
- Comprobar que Neon DB está activo
- Validar connection string
- **NUEVO**: Verificar configuración HikariCP en `application-prod.properties`

#### 2. Token JWT Inválido

```
Error: 401 Unauthorized
```

**Solución**:
- Verificar que JWT_SECRET esté configurado
- Comprobar formato del token en headers: `Authorization: Bearer <token>`
- Validar que el token no haya expirado
- **NUEVO**: Asegurar usar JWT real de GraphQL login, no tokens falsos

#### 3. CORS Errors

```
Error: CORS policy blocks request
```

**Solución**:
- Actualizar `CORS_ALLOWED_ORIGINS` en `.env`
- Verificar configuración en `CorsConfig.java`

#### 4. GraphQL Schema Error

```
Error: Schema validation failed / Field undefined
```

**Solución**:
- Verificar `schema.graphqls` en resources/graphql
- Comprobar que los resolvers estén implementados
- **NUEVO**: Usar nombres correctos: `categoryId` (no `id`), `userName` (no `username`)

#### 5. **NUEVO**: GraphiQL No Accesible

```
Error: 403 Forbidden en /graphiql
```

**Solución**:
- Verificar que `app.security.whitelist` incluya `/graphiql`
- Comprobar configuración en `SecurityConfig.java`
- GraphiQL debe ser público en ambos perfiles (dev/prod)

### Logs Útiles

```bash
# Ver logs de la aplicación
mvn spring-boot:run | grep -E "(ERROR|WARN|INFO)"

# Logs de base de datos
mvn spring-boot:run | grep -E "JPA|SQL|DataSource"

# Logs de seguridad
mvn spring-boot:run | grep -E "Security|JWT|Auth"
```

### Comandos de Verificación

```bash
# Verificar Java version
java -version

# Verificar Maven
mvn -version

# Comprobar conexión a BD
psql "tu_connection_string_aqui" -c "SELECT version();"

# Test de conectividad
curl http://localhost:8080/actuator/health
```

## 📞 Soporte

Para problemas o preguntas:

1. Revisar logs de la aplicación
2. Comprobar la sección de Troubleshooting
3. Verificar que todas las variables de entorno estén configuradas
4. Probar con los archivos de test incluidos

---

## 📋 **Documentación Verificada**

> **🔍 Endpoints Verificados**: Esta documentación ha sido **actualizada automáticamente** para reflejar únicamente los endpoints que están realmente implementados en el código.

**📊 Resumen de Verificación:**
- **Metodología**: Análisis automático de anotaciones `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping`
- **Controllers verificados**: `AuthController`, `ProductController`, `PromotionController`, `CategoryController`
- **Total de endpoints**: 25 endpoints REST confirmados como implementados
- **Total de operaciones GraphQL**: 21 operaciones (16 queries + 5 mutations) + 3 schema mappings
- **GraphQL**: 1 endpoint adicional verificado

**✅ Estado de Implementación:**
- **AuthController**: 5/5 endpoints ✓
- **ProductController**: 5/5 endpoints ✓  
- **PromotionController**: 5/5 endpoints ✓
- **CategoryController**: 6/6 endpoints ✓

---

## 🏆 Credits

Desarrollado para el sistema de promociones de Petstore con tecnologías modernas de Spring Boot y GraphQL.

**Versión**: 0.0.1-SNAPSHOT  
**Última actualización**: octubre 2025

## 📚 Documentación OpenAPI / Swagger

Se ha integrado OpenAPI (springdoc) para exponer la documentación interactiva de la API REST.

- Swagger UI estará disponible después de levantar la aplicación en:

  - `http://localhost:8080/swagger-ui.html`
  - o `http://localhost:8080/swagger-ui/index.html`

- La UI permite probar endpoints REST, ver modelos (DTOs) y añadir el token JWT a la autorización.

Cómo usar la autorización JWT en Swagger UI:

1. Hacer login en `/api/auth/login` y obtener el token (campo `token` en la respuesta).
2. Abrir Swagger UI en `http://localhost:8080/swagger-ui/index.html`.
3. Pulsar el botón "Authorize" y pegar el token con el prefijo `Bearer `, por ejemplo:

   Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...

Notas:
- La configuración usa `springdoc-openapi-starter-webmvc-ui` y un `OpenApiConfig` con un esquema de seguridad `bearerAuth` para JWT.
- Si la ruta de Swagger no aparece, comprobar que la dependencia se descargó correctamente y que el perfil activo no esté bloqueando acceso a endpoints públicos.
