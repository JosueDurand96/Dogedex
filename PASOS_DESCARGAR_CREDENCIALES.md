# 📥 Pasos para Descargar Credenciales de Firebase Admin SDK

## 🎯 Desde la Página Actual de Firebase Console

Estás viendo la página principal de tu proyecto "Tesis-2026". Para descargar las credenciales del backend:

### Paso 1: Ir a Configuración del Proyecto

1. Busca el ícono de **⚙️ (engranaje/rueda)** en la parte superior izquierda, al lado del nombre del proyecto "Tesis-2026"
2. Haz clic en **"Configuración del proyecto"** o **"Project settings"**

### Paso 2: Ir a la Pestaña "Cuentas de servicio"

1. En la página de configuración, verás varias pestañas en la parte superior:
   - General
   - Cloud Messaging
   - **Cuentas de servicio** ← Haz clic aquí
   - Integraciones
   - etc.

2. Haz clic en la pestaña **"Cuentas de servicio"** o **"Service accounts"**

### Paso 3: Generar Nueva Clave Privada

1. En la sección "Cuentas de servicio", verás información sobre la cuenta de servicio de Firebase
2. Busca el botón **"Generar nueva clave privada"** o **"Generate new private key"**
3. Haz clic en ese botón
4. Se abrirá un diálogo de confirmación
5. Haz clic en **"Generar clave"** o **"Generate key"**

### Paso 4: Descargar el Archivo

1. Se descargará automáticamente un archivo JSON
2. El nombre del archivo será algo como:
   ```
   tesis-2026-firebase-adminsdk-xxxxx-xxxxx.json
   ```
3. **Guarda este archivo de forma segura** - NO lo subas a repositorios públicos

---

## 📍 Ruta Completa en Firebase Console

```
Firebase Console
  → Proyecto "Tesis-2026"
    → ⚙️ Configuración del proyecto (Project settings)
      → Pestaña "Cuentas de servicio" (Service accounts)
        → Botón "Generar nueva clave privada" (Generate new private key)
          → Descargar archivo JSON
```

---

## 🔍 Si No Encuentras el Botón

Si no ves la opción "Cuentas de servicio" o "Generar nueva clave privada":

1. Asegúrate de estar en la página de **"Configuración del proyecto"** (no en otra sección)
2. Busca en la parte superior las pestañas: General, Cloud Messaging, **Cuentas de servicio**, etc.
3. Si usas la versión en español, busca **"Cuentas de servicio"**
4. Si usas la versión en inglés, busca **"Service accounts"**

---

## ✅ Después de Descargar

Una vez que tengas el archivo JSON:

1. **Colócalo en tu proyecto backend** (en una ubicación segura)
2. **Agrégalo al `.gitignore`** para no subirlo a repositorios
3. **Úsalo para inicializar Firebase Admin SDK** en tu código backend

---

## 📝 Ejemplo de Uso

Una vez descargado, lo usarás así en tu backend:

**Node.js:**
```javascript
const admin = require('firebase-admin');
const serviceAccount = require('./tesis-2026-firebase-adminsdk-xxxxx.json');

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount)
});
```

**Python:**
```python
import firebase_admin
from firebase_admin import credentials

cred = credentials.Certificate('tesis-2026-firebase-adminsdk-xxxxx.json')
firebase_admin.initialize_app(cred)
```

---

## ⚠️ Recordatorio de Seguridad

- ❌ NO subas este archivo a GitHub
- ❌ NO lo compartas públicamente
- ✅ Agrégalo al `.gitignore`
- ✅ Guárdalo de forma segura

---

¡Sigue estos pasos y tendrás las credenciales necesarias para configurar Firebase en tu backend! 🚀

