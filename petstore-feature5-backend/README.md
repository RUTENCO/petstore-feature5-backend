# 🛍️ Petstore Feature 5 Backend

Sistema completo de promociones para petstore con autenticación JWT, API REST y GraphQL.

## 📋 Tabla de Contenidos

- [Descripción](#-descripción)
- [Características](#-características)
- [Tecnologías](#️-tecnologías)
- [Instalación](#-instalación)
- [Configuración](#️-configuración)
- [Ejecución](#-ejecución)
- [API Endpoints](#-api-endpoints)
- [GraphQL](#-graphql)
- [Base de Datos](#️-base-de-datos)
- [Archivos de Prueba](#-archivos-de-prueba)
- [Estructura del Proyecto](#-estructura-del-proyecto)
- [Troubleshooting](#-troubleshooting)

## 🎯 Descripción

Sistema backend para gestión de promociones en petstore que incluye:
- Autenticación JWT con Spring Security
- API REST y GraphQL
- Gestión de promociones, categorías, productos y usuarios
- Integración con Neon Database (PostgreSQL)
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
- **21 endpoints REST** implementados con MapStruct
- **DTOs seguros** sin información sensible  
- **GraphQL** para consultas flexibles y relacionales

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
CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:5173,http://localhost:8081
```

### 2. Configurar Neon Database

1. Crear cuenta en [Neon](https://neon.tech/)
2. Crear nuevo proyecto
3. Copiar connection string
4. Actualizar variables en `.env`

## ▶️ Ejecución

### 1. Ejecutar la Aplicación

```bash
# Ejecutar con perfil por defecto
mvn spring-boot:run

# O con perfil específico
mvn spring-boot:run -Dspring.profiles.active=default
```

### 2. Verificar que está Funcionando

La aplicación estará disponible en: `http://localhost:8080`

**Endpoints de verificación:**
- Health Check: `http://localhost:8080/actuator/health`
- GraphiQL: `http://localhost:8080/graphiql`

### 3. Construir JAR para Producción

```bash
# Construir JAR
mvn clean package

# Ejecutar JAR
java -jar target/petstore-feature5-backend-0.0.1-SNAPSHOT.jar
```

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

## 🌐 API Endpoints

> **📋 Endpoints Verificados**: Esta documentación muestra únicamente los endpoints que están **realmente implementados** en el código.

### 🔐 Autenticación

| Método | Endpoint | Descripción | Auth | Response |
|--------|----------|-------------|------|----------|
| GET | `/api/auth/status` | Estado del servicio de autenticación | No | `Map<String,Object>` |
| POST | `/api/auth/login` | Login de usuario | No | `LoginResponse` |
| GET | `/api/auth/verify` | Verificar validez del token JWT | Sí | `JSON (valid: boolean)` |
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

### 📁 Categorías

| Método | Endpoint | Descripción | Auth | Response |
|--------|----------|-------------|------|----------|
| GET | `/api/categories` | Listar todas las categorías | No | `CategoryDTO[]` |
| GET | `/api/categories/{id}` | Obtener categoría por ID | No | `CategoryDTO` |
| POST | `/api/categories` | Crear nueva categoría | No | `CategoryDTO` |
| PUT | `/api/categories/{id}` | Actualizar categoría existente | No | `CategoryDTO` |
| DELETE | `/api/categories/{id}` | Eliminar categoría | No | `void` |
| GET | `/api/categories/info` | Información de endpoints | No | `String` |

### 📊 Resumen de Endpoints

- **Total**: 21 endpoints REST implementados
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

## 🔗 GraphQL

### Endpoint GraphQL

- **URL**: `http://localhost:8080/graphql`
- **GraphiQL**: `http://localhost:8080/graphiql`

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
  
  # Solo promociones activas
  promotionsActive {
    promotionId
    promotionName
    discountValue
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

```graphql
mutation {
  # Login (no requiere autenticación previa)
  login(email: "admin@petstore.com", password: "password123") {
    success
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

#### **Dashboard de Administración**
```graphql
query Dashboard {
  promotions {
    promotionId
    promotionName
    discountValue
    status { statusName }
    category { categoryName }
    products { productName }
  }
  
  categories {
    categoryId
    categoryName
    products { productId }
    promotions { promotionId }
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

### Ejemplo con Headers de Autenticación

Para queries que requieren autenticación, agregar header:

```json
{
  "Authorization": "Bearer TU_TOKEN_JWT_AQUI"
}
```

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

### Problemas Comunes

#### 1. Error de Conexión a Base de Datos

```
Error: Connection refused
```

**Solución**:
- Verificar variables en `.env`
- Comprobar que Neon DB está activo
- Validar connection string

#### 2. Token JWT Inválido

```
Error: 401 Unauthorized
```

**Solución**:
- Verificar que JWT_SECRET esté configurado
- Comprobar formato del token en headers
- Validar que el token no haya expirado

#### 3. CORS Errors

```
Error: CORS policy blocks request
```

**Solución**:
- Actualizar `CORS_ALLOWED_ORIGINS` en `.env`
- Verificar configuración en `CorsConfig.java`

#### 4. GraphQL Schema Error

```
Error: Schema validation failed
```

**Solución**:
- Verificar `schema.graphqls` en resources/graphql
- Comprobar que los resolvers estén implementados

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
- **Total de endpoints**: 21 endpoints REST confirmados como implementados
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
**Última actualización**: Diciembre 2024  
**Documentación**: Verificada automáticamente
