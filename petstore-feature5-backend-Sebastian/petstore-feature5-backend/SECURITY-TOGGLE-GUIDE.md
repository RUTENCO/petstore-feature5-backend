# 🛡️ GUÍA RÁPIDA: Activar/Desactivar Protección de Endpoints

## 🔓 DESHABILITAR PROTECCIÓN (Para Pruebas)

### Método 1: Modificar application.properties
```properties
# Agregar /api/products/** a la whitelist
app.security.whitelist=/api/auth/**,/graphql,/graphiql,/actuator/health,/actuator/info,/h2-console/**,/test,/graphql-test,/api/products/**
```

### Método 2: Modificar SecurityConfig.java
```java
// En la sección authorizeHttpRequests, agregar:
.requestMatchers("/api/products/**").permitAll() // 🔓 PÚBLICOS PARA PRUEBAS
```

### Método 3: Deshabilitar TODA la seguridad (SOLO PARA DESARROLLO)
```java
// En SecurityConfig.java, reemplazar todo el authorizeHttpRequests con:
.authorizeHttpRequests(authz -> authz.anyRequest().permitAll())
```

---

## 🔐 HABILITAR PROTECCIÓN (Para Producción)

### Método 1: Remover de application.properties
```properties
# Remover /api/products/** de la whitelist
app.security.whitelist=/api/auth/**,/graphql,/graphiql,/actuator/health,/actuator/info,/h2-console/**,/test,/graphql-test
```

### Método 2: Modificar SecurityConfig.java
```java
// Cambiar de permitAll() a authenticated():
.requestMatchers("/api/products/**").authenticated() // 🔐 REQUIERE AUTENTICACIÓN
```

---

## 🧪 PRUEBAS SIN AUTENTICACIÓN

Una vez deshabilitada la protección, puedes probar:

```bash
# ✅ Funcionarán sin token
curl http://localhost:8080/api/products
curl http://localhost:8080/api/products/category/1
curl http://localhost:8080/api/products/1
curl "http://localhost:8080/api/products/search?name=laptop"
```

---

## 🔑 PRUEBAS CON AUTENTICACIÓN

Si quieres probar con autenticación:

```bash
# 1. Hacer login para obtener token
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@petstore.com",
    "password": "password123"
  }'

# 2. Usar el token en las peticiones
curl -X GET http://localhost:8080/api/products \
  -H "Authorization: Bearer TU_TOKEN_AQUI"
```

---

## ⚙️ CONFIGURACIÓN RECOMENDADA POR AMBIENTE

### 🏠 DESARROLLO (Pruebas locales)
- Endpoints de productos: **PÚBLICOS** (sin autenticación)
- Endpoints de promociones: **PROTEGIDOS** (con autenticación)

### 🚀 PRODUCCIÓN
- Endpoints de productos: **PROTEGIDOS** (con autenticación)
- Endpoints de promociones: **PROTEGIDOS** (con autenticación)

---

## 🔄 CAMBIOS RÁPIDOS

### Para hacer productos PÚBLICOS temporalmente:
1. Editar `application.properties`: agregar `/api/products/**` a whitelist
2. Reiniciar aplicación: `mvn spring-boot:run`

### Para hacer productos PROTEGIDOS nuevamente:
1. Editar `application.properties`: remover `/api/products/**` de whitelist
2. Reiniciar aplicación: `mvn spring-boot:run`
