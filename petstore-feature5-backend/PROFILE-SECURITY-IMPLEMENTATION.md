# 🔐 CONFIGURACIÓN COMPLETADA: SEGURIDAD POR PERFILES

## ✅ **IMPLEMENTACION EXITOSA**

Su aplicación **Petstore Backend** ahora tiene configuración de seguridad inteligente:

### 🔓 **MODO DESARROLLO** (`dev` o `default`)
- **GraphiQL**: ✅ `http://localhost:8080/graphiql`
- **GraphQL**: ✅ Público para pruebas rápidas  
- **Actuator**: ✅ Todos los endpoints
- **Productos**: ✅ GET público, modificaciones requieren auth

### 🔒 **MODO PRODUCCIÓN** (`prod`)
- **GraphiQL**: ❌ Completamente deshabilitado
- **GraphQL**: 🔐 Requiere JWT token obligatorio
- **Actuator**: 🔐 Solo `/health` público
- **Productos**: 🔐 Lectura pública, modificaciones protegidas

## 🚀 **CÓMO USAR**

### **Ejecutar en Desarrollo:**
```bash
mvn spring-boot:run "-Dspring.profiles.active=dev"
# O simplemente: mvn spring-boot:run
```

### **Ejecutar en Producción:**
```bash
mvn spring-boot:run "-Dspring.profiles.active=prod"
```

### **Script Automático:**
```bash
# Usar el script interactivo
./run-profiles.bat
```

## 🌐 **INTEGRACIÓN FRONTEND**

### **En Desarrollo:**
```javascript
// GraphQL público - Sin token necesario
fetch('http://localhost:8080/graphql', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    query: `query { products { productName basePrice } }`
  })
});
```

### **En Producción:**
```javascript
// 1. Login primero
const login = await fetch('/api/auth/login', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    email: 'admin@petstore.com',
    password: 'password123'
  })
});
const { token } = await login.json();

// 2. GraphQL con token
fetch('/graphql', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${token}` // 🔑 OBLIGATORIO
  },
  body: JSON.stringify({
    query: `query { currentUser { userName } }`
  })
});
```

## 📊 **CONFIGURACIÓN ACTUAL**

| Endpoint | Desarrollo | Producción |
|----------|------------|------------|
| `/graphiql` | ✅ Público | ❌ Negado |
| `/graphql` | ✅ Público | 🔐 JWT |
| `/api/products` (GET) | ✅ Público | ✅ Público |
| `/api/products` (POST/PUT/DELETE) | 🔐 JWT | 🔐 JWT |
| `/api/promotions/**` | 🔐 JWT | 🔐 JWT |
| `/actuator/**` | ✅ Todos | 🔐 Solo health |

## 🔧 **VERIFICACIÓN**

Al iniciar, verá este banner:

```
🚀 ============================================
🚀 PETSTORE BACKEND - PERFIL DE SEGURIDAD  
🚀 ============================================
📋 Perfiles activos: [dev]
🔓 Modo: DESARROLLO
✅ GraphiQL: http://localhost:8080/graphiql
✅ GraphQL: http://localhost:8080/graphql (PÚBLICO)
✅ Actuator: Todos los endpoints
🚀 ============================================
```

## 🎯 **RESULTADO FINAL**

**✅ CONFIGURADO CORRECTAMENTE:**

1. **Desarrollo**: GraphQL público para pruebas rápidas
2. **Producción**: GraphQL protegido con JWT
3. **Frontend**: Puede conectarse fácilmente en ambos modos
4. **Seguridad**: Automática según el perfil activo

**Su aplicación está lista para desarrollo Y producción.**
