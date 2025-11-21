# Pruebas GraphQL del Sistema de Notificaciones

## 1. Configuración

### Endpoint GraphQL
```
POST http://localhost:8080/graphql
Content-Type: application/json
Authorization: Bearer YOUR_JWT_TOKEN
```

## 2. Queries de Prueba

### 2.1 Obtener consentimiento específico
```graphql
query GetNotificationConsent {
  notificationConsent(userId: "11", notificationType: EMAIL_PROMOTION) {
    id
    notificationType
    consentGiven
    consentDate
    lastModified
    ipAddress
    user {
      userId
      userName
      email
    }
  }
}
```

### 2.2 Obtener todos los consentimientos activos de un usuario
```graphql
query GetUserConsents {
  notificationConsentsByUser(userId: "11") {
    id
    notificationType
    consentGiven
    consentDate
    lastModified
    user {
      userName
      email
    }
  }
}
```

### 2.3 Obtener logs de notificaciones
```graphql
query GetNotificationLogs {
  notificationLogs(userId: "11") {
    id
    notificationType
    recipient
    subject
    sentAt
    status
    errorMessage
    promotion {
      promotionId
      promotionName
      discountValue
      startDate
      endDate
    }
    user {
      userName
      email
    }
  }
}
```

### 2.4 Estado del servicio de notificaciones
```graphql
query GetNotificationStatus {
  notificationStatus {
    service
    status
    timestamp
    supportedTypes
  }
}
```

## 3. Mutations de Prueba

### 3.1 Activar consentimiento para emails promocionales
```graphql
mutation UpdateNotificationConsent {
  updateNotificationConsent(input: {
    userId: "11"
    notificationType: EMAIL_PROMOTION
    consentGiven: true
  }) {
    success
    message
    consent {
      id
      notificationType
      consentGiven
      consentDate
      user {
        userName
        email
      }
    }
  }
}
```

### 3.2 Desactivar consentimiento
```graphql
mutation DisableNotificationConsent {
  updateNotificationConsent(input: {
    userId: "11"
    notificationType: EMAIL_PROMOTION
    consentGiven: false
  }) {
    success
    message
    consent {
      id
      notificationType
      consentGiven
      lastModified
    }
  }
}
```

### 3.3 Activar consentimiento para emails generales
```graphql
mutation EnableGeneralEmails {
  updateNotificationConsent(input: {
    userId: "11"
    notificationType: EMAIL_GENERAL
    consentGiven: true
  }) {
    success
    message
    consent {
      id
      notificationType
      consentGiven
    }
  }
}
```

### 3.4 Enviar notificación de prueba
```graphql
mutation SendTestNotification {
  sendTestNotification(userId: "11", promotionId: "1")
}
```

## 4. Respuestas Esperadas

### 4.1 Consentimiento actualizado exitosamente
```json
{
  "data": {
    "updateNotificationConsent": {
      "success": true,
      "message": "Consentimiento actualizado exitosamente",
      "consent": {
        "id": "1",
        "notificationType": "EMAIL_PROMOTION",
        "consentGiven": true,
        "consentDate": "2025-11-20T15:30:00",
        "user": {
          "userName": "admin",
          "email": "admin@petstore.com"
        }
      }
    }
  }
}
```

### 4.2 Estado del servicio
```json
{
  "data": {
    "notificationStatus": {
      "service": "Notification Service",
      "status": "active",
      "timestamp": "2025-11-20T15:30:00",
      "supportedTypes": ["EMAIL_PROMOTION", "EMAIL_GENERAL"]
    }
  }
}
```

### 4.3 Logs de notificaciones
```json
{
  "data": {
    "notificationLogs": [
      {
        "id": "1",
        "notificationType": "EMAIL_PROMOTION",
        "recipient": "admin@petstore.com",
        "subject": "🎉 Nueva Promoción Disponible: Descuento Black Friday",
        "sentAt": "2025-11-20T14:30:00",
        "status": "SENT",
        "errorMessage": null,
        "promotion": {
          "promotionId": "1",
          "promotionName": "Descuento Black Friday",
          "discountValue": 25.0,
          "startDate": "2025-11-20",
          "endDate": "2025-11-25"
        }
      }
    ]
  }
}
```

## 5. Casos de Error

### 5.1 Usuario no autenticado
```json
{
  "errors": [
    {
      "message": "Authentication required",
      "extensions": {
        "classification": "AUTHENTICATION"
      }
    }
  ]
}
```

### 5.2 Tipo de notificación inválido
```json
{
  "errors": [
    {
      "message": "Invalid enum value",
      "extensions": {
        "classification": "ValidationError"
      }
    }
  ]
}
```

### 5.3 Usuario no encontrado
```json
{
  "data": {
    "notificationConsent": null
  }
}
```

## 6. Variables de GraphQL

Para usar variables en tus queries, puedes estructurarlas así:

### Query con variables
```graphql
query GetNotificationConsent($userId: ID!, $type: NotificationType!) {
  notificationConsent(userId: $userId, notificationType: $type) {
    id
    consentGiven
    consentDate
  }
}
```

### Variables JSON
```json
{
  "userId": "11",
  "type": "EMAIL_PROMOTION"
}
```
