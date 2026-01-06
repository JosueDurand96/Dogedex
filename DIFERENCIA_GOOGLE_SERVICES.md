# 📄 Diferencia: google-services.json vs Credenciales del Backend

## ❌ NO uses google-services.json en el Backend

El archivo `google-services.json` que tienes en tu proyecto Android es **SOLO para la aplicación Android/cliente**.

---

## ✅ Lo que NECESITAS en el Backend

Para el backend necesitas un **archivo DIFERENTE**: las **Credenciales de Firebase Admin SDK**.

### Diferencias:

| Característica | google-services.json (Android) | Credenciales Admin SDK (Backend) |
|---------------|-------------------------------|----------------------------------|
| **Ubicación** | `app/google-services.json` | Descargar desde Firebase Console |
| **Propósito** | Configuración de la app Android | Autenticación del servidor |
| **Contenido** | Configuración del proyecto | Claves privadas de servicio |
| **Uso** | Plugin de Google Services (Android) | Firebase Admin SDK (Backend) |
| **Nombre del archivo** | `google-services.json` | `tesis-2026-firebase-adminsdk-xxxxx-xxxxx.json` |
| **Seguridad** | Puede estar en repositorio | NO debe estar en repositorio |

---

## 📥 Cómo Obtener las Credenciales para el Backend

### Paso 1: Ir a Firebase Console

1. Ve a [Firebase Console](https://console.firebase.google.com/)
2. Selecciona tu proyecto: **Tesis-2026**

### Paso 2: Ir a Cuentas de Servicio

1. Haz clic en el ícono de **⚙️ Configuración** (arriba a la izquierda)
2. Selecciona **"Configuración del proyecto"**
3. Ve a la pestaña **"Cuentas de servicio"** (Service accounts)

### Paso 3: Generar Nueva Clave Privada

1. Haz clic en **"Generar nueva clave privada"** o **"Generate new private key"**
2. Se abrirá un diálogo de confirmación
3. Haz clic en **"Generar clave"** o **"Generate key"**
4. Se descargará un archivo JSON

### Paso 4: Nombre del Archivo

El archivo descargado tendrá un nombre como:
```
tesis-2026-firebase-adminsdk-xxxxx-xxxxx.json
```

**Este es el archivo que necesitas en tu backend, NO el google-services.json**

---

## 🔍 Comparación de Archivos

### google-services.json (para Android):

```json
{
  "project_info": {
    "project_number": "717780545218",
    "project_id": "tesis-2026",
    ...
  },
  "client": [
    {
      "client_info": {
        "mobilesdk_app_id": "1:717780545218:android:4e5222ba563ead7c94155b",
        ...
      },
      "api_key": [
        {
          "current_key": "AIzaSyDUuZZInYMuiMxRgna3p0Jv9puWreNnEo0"
        }
      ],
      ...
    }
  ]
}
```

### Credenciales Admin SDK (para Backend):

```json
{
  "type": "service_account",
  "project_id": "tesis-2026",
  "private_key_id": "xxxxx",
  "private_key": "-----BEGIN PRIVATE KEY-----\n...\n-----END PRIVATE KEY-----\n",
  "client_email": "firebase-adminsdk-xxxxx@tesis-2026.iam.gserviceaccount.com",
  "client_id": "xxxxx",
  "auth_uri": "https://accounts.google.com/o/oauth2/auth",
  "token_uri": "https://oauth2.googleapis.com/token",
  "auth_provider_x509_cert_url": "https://www.googleapis.com/oauth2/v1/certs",
  "client_x509_cert_url": "https://www.googleapis.com/robot/v1/metadata/x509/..."
}
```

**Nota:** Este archivo contiene una clave privada (`private_key`), por eso es más sensible.

---

## ✅ Resumen

### En tu App Android:
- ✅ Usa `google-services.json` (ya lo tienes)
- ✅ Se coloca en `app/google-services.json`
- ✅ El plugin de Google Services lo lee automáticamente

### En tu Backend:
- ✅ Usa el archivo de credenciales Admin SDK (descargar desde Firebase Console)
- ✅ Se coloca en una ubicación segura del backend
- ✅ Se usa para inicializar Firebase Admin SDK
- ❌ NO uses `google-services.json`

---

## 🎯 Pasos para Configurar el Backend

1. ✅ Ya tienes `google-services.json` en Android (correcto)
2. ⬜ Descargar credenciales Admin SDK desde Firebase Console
3. ⬜ Instalar Firebase Admin SDK en el backend
4. ⬜ Usar las credenciales Admin SDK para inicializar Firebase Admin SDK
5. ⬜ Implementar envío de notificaciones

---

## ⚠️ Recordatorio

**NO subas el archivo de credenciales Admin SDK a repositorios públicos.**

Agrégalo al `.gitignore`:
```
# Firebase Admin SDK credentials
*firebase-adminsdk*.json
firebase-admin-credentials.json
```

¡El archivo `google-services.json` que ya tienes es solo para Android! 🚀



