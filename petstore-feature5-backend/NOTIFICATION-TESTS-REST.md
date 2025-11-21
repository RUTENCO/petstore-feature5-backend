# Pruebas del Sistema de Notificaciones

## 1. Configuración Inicial

### Variables de entorno
```
BASE_URL=http://localhost:8080
JWT_TOKEN=your_jwt_token_here
USER_ID=1
```

## 2. Pruebas REST

### 2.1 Obtener token de autenticación
```bash
curl -X POST $BASE_URL/api/auth/login \
-H "Content-Type: application/json" \
-d '{
  "email": "admin@petstore.com",
  "password": "admin123"
}'
```

### 2.2 Activar consentimiento para emails promocionales
```bash
curl -X POST $BASE_URL/api/notifications/consent \
-H "Content-Type: application/json" \
-H "Authorization: Bearer $JWT_TOKEN" \
-d '{
  "userId": 11,
  "notificationType": "EMAIL_PROMOTION",
  "consentGiven": true
}'
```

### 2.3 Verificar consentimiento
```bash
curl -X GET $BASE_URL/api/notifications/consent/1/EMAIL_PROMOTION \
-H "Authorization: Bearer $JWT_TOKEN"
```

### 2.4 Desactivar consentimiento
```bash
curl -X POST $BASE_URL/api/notifications/consent \
-H "Content-Type: application/json" \
-H "Authorization: Bearer $JWT_TOKEN" \
-d '{
  "userId": 11,
  "notificationType": "EMAIL_PROMOTION",
  "consentGiven": false
}'
```

### 2.5 Estado del servicio
```bash
curl -X GET $BASE_URL/api/notifications/status
```

## 3. Respuestas esperadas

### 3.1 Consentimiento activado exitosamente
```json
{
  "success": true,
  "message": "Consentimiento actualizado exitosamente",
  "userId": 11,
  "notificationType": "EMAIL_PROMOTION",
  "consentGiven": true
}
```

### 3.2 Verificación de consentimiento
```json
{
  "success": true,
  "userId": 11,
  "notificationType": "EMAIL_PROMOTION",
  "hasActiveConsent": true
}
```

### 3.3 Estado del servicio
```json
{
  "service": "Notification Service",
  "status": "active",
  "timestamp": "2025-11-20T15:30:00",
  "supportedTypes": ["EMAIL_PROMOTION", "EMAIL_GENERAL"],
  "endpoints": [
    "POST /api/notifications/consent - Actualizar consentimiento",
    "GET /api/notifications/consent/{userId}/{type} - Verificar consentimiento",
    "GET /api/notifications/status - Estado del servicio"
  ]
}
```

## 4. Casos de error

### 4.1 Usuario no autenticado
```bash
curl -X POST $BASE_URL/api/notifications/consent \
-H "Content-Type: application/json" \
-d '{
  "userId": 1,
  "notificationType": "EMAIL_PROMOTION",
  "consentGiven": true
}'
```
**Respuesta esperada:** HTTP 401 Unauthorized

### 4.2 Tipo de notificación inválido
```bash
curl -X GET $BASE_URL/api/notifications/consent/1/INVALID_TYPE \
-H "Authorization: Bearer $JWT_TOKEN"
```
**Respuesta esperada:** HTTP 400 Bad Request

### 4.3 Usuario ID inválido
```bash
curl -X GET $BASE_URL/api/notifications/consent/999/EMAIL_PROMOTION \
-H "Authorization: Bearer $JWT_TOKEN"
```
**Respuesta esperada:** HTTP 404 Not Found o consentimiento null
