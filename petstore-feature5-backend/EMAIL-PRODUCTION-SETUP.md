# 🔧 Configuración de Email con Resend en Producción (Render)

## Variables de Entorno Requeridas

Para que el sistema de notificaciones funcione correctamente en Render con **Resend API**, debes configurar las siguientes variables de entorno:

### **📧 Configuración de Email (Resend API)**

```bash
RESEND_API_KEY=re_KsUD3cH5_Fgt6TA5xspRi9UacScJAbJYV
RESEND_FROM=onboarding@resend.dev
RESEND_TO=petstorenotifications@gmail.com
RESEND_FROM_NAME=PetStore Notifications
```

### **� Configuración de Resend**

1. Ve a tu cuenta de Resend: https://resend.com/
2. En el plan gratuito solo puedes enviar desde `onboarding@resend.dev`
3. Solo puedes enviar a un email verificado (en este caso: `petstorenotifications@gmail.com`)
4. Tienes límite de 100 emails por día

### **🌐 Variables de Entorno en Render**

1. Ve a tu servicio en Render Dashboard
2. Ve a "Environment" 
3. Agrega las siguientes variables:

```
RESEND_API_KEY=re_KsUD3cH5_Fgt6TA5xspRi9UacScJAbJYV
RESEND_FROM=onboarding@resend.dev
RESEND_TO=petstorenotifications@gmail.com
RESEND_FROM_NAME=PetStore Notifications
```

### **🧪 Endpoints de Diagnóstico**

Una vez desplegado, puedes usar estos endpoints para diagnosticar problemas:

```bash
# Verificar configuración de Resend
GET https://tu-app.onrender.com/api/debug/email-config

# Probar Resend API
POST https://tu-app.onrender.com/api/debug/test-resend

# Enviar email de prueba
POST https://tu-app.onrender.com/api/debug/send-test-email
```

### **🚨 Problemas Comunes y Soluciones**

#### **1. "API Key de Resend no configurado"**
- **Causa**: Variable `RESEND_API_KEY` no configurada en Render
- **Solución**: Verificar que la variable esté configurada correctamente

#### **2. "Error 401 - Unauthorized"**
- **Causa**: API Key inválido o expirado
- **Solución**: Regenerar API Key en Resend Dashboard

#### **3. "Error 403 - Domain not verified"**
- **Causa**: Intento de usar dominio no verificado en plan gratuito
- **Solución**: Usar solo `onboarding@resend.dev` como remitente

#### **4. "Rate limit exceeded"**
- **Causa**: Superaste el límite de 100 emails/día
- **Solución**: Esperar al siguiente día o actualizar plan

### **✅ Ventajas de Resend vs SMTP**

- ✅ **No bloqueado por Render** (API HTTP vs SMTP)
- ✅ **Configuración más simple** (solo API key)
- ✅ **Mejor deliverability** (infraestructura profesional)
- ✅ **Logs detallados** de entrega
- ✅ **Sin problemas de firewall**

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
