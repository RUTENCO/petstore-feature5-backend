# 🔧 Configuración de Email en Producción (Render)

## Variables de Entorno Requeridas

Para que el sistema de notificaciones funcione correctamente en Render, debes configurar las siguientes variables de entorno:

### **📧 Configuración de Email (Gmail)**

```bash
EMAIL_HOST=smtp.gmail.com
EMAIL_PORT=587
EMAIL_USERNAME=tu-email@gmail.com
EMAIL_PASSWORD=tu-app-password
EMAIL_FROM=noreply@petstore.com
EMAIL_FROM_NAME=PetStore Notifications
```

### **🔒 Configuración de App Password para Gmail**

1. Ve a tu cuenta de Google: https://myaccount.google.com/
2. Activa la verificación en 2 pasos
3. Ve a "Seguridad" → "Verificación en 2 pasos" → "Contraseñas de aplicaciones"
4. Genera una nueva contraseña de aplicación
5. Usa esa contraseña en `MAIL_PASSWORD` (no tu contraseña normal)

### **🌐 Variables de Entorno en Render**

1. Ve a tu servicio en Render Dashboard
2. Ve a "Environment" 
3. Agrega las siguientes variables:

```
EMAIL_HOST=smtp.gmail.com
EMAIL_PORT=587
EMAIL_USERNAME=tu-email@gmail.com
EMAIL_PASSWORD=abcd-efgh-ijkl-mnop
EMAIL_FROM=noreply@petstore.com
EMAIL_FROM_NAME=PetStore Notifications
```

### **🧪 Endpoints de Diagnóstico**

Una vez desplegado, puedes usar estos endpoints para diagnosticar problemas:

```bash
# Verificar configuración de email
GET https://tu-app.onrender.com/api/debug/email-config

# Probar conexión SMTP
POST https://tu-app.onrender.com/api/debug/test-smtp

# Enviar email de prueba
POST https://tu-app.onrender.com/api/debug/send-test-email?email=tu-email@example.com
```

### **🚨 Problemas Comunes y Soluciones**

#### **1. "Error al enviar email" en base de datos**
- **Causa**: Variables de entorno no configuradas o incorrectas
- **Solución**: Verificar que todas las variables están configuradas en Render

#### **2. "Connection refused" o "Timeout"**
- **Causa**: Render bloquea conexiones SMTP salientes
- **Solución**: Usar servicios como SendGrid, Mailgun, o AWS SES

#### **3. "Authentication failed"**
- **Causa**: Contraseña incorrecta o falta App Password
- **Solución**: Generar App Password en Gmail

#### **4. Emails no llegan a la bandeja**
- **Causa**: Filtros de spam o problemas de reputación
- **Solución**: Verificar carpeta de spam, usar dominio verificado

### **🔄 Alternativas Recomendadas para Producción**

Si Gmail no funciona en Render, considera usar servicios profesionales:

#### **SendGrid (Recomendado)**
```bash
EMAIL_HOST=smtp.sendgrid.net
EMAIL_PORT=587
EMAIL_USERNAME=apikey
EMAIL_PASSWORD=tu-sendgrid-api-key
```

#### **Mailgun**
```bash
EMAIL_HOST=smtp.mailgun.org
EMAIL_PORT=587
EMAIL_USERNAME=postmaster@tu-dominio.mailgun.org
EMAIL_PASSWORD=tu-mailgun-password
```

### **📝 Verificación Paso a Paso**

1. **Configurar variables en Render**
2. **Redesplegar la aplicación**
3. **Verificar configuración**: `GET /api/debug/email-config`
4. **Probar SMTP**: `POST /api/debug/test-smtp`
5. **Enviar email de prueba**: `POST /api/debug/send-test-email`
6. **Probar notificaciones desde el frontend**

### **📞 Soporte**

Si los problemas persisten después de seguir estos pasos:
1. Revisar logs de Render
2. Usar los endpoints de debug para identificar el error específico
3. Considerar migrar a un servicio de email profesional
