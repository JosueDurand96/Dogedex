# 🔧 Cómo Usar las Credenciales de Firebase Admin SDK

## ✅ Archivo Correcto

Tienes el archivo correcto: `tesis-2026-firebase-adminsdk-fbsvc-9202d1a52e.json`

Este archivo contiene las credenciales necesarias para que tu backend se autentique con Firebase y pueda enviar notificaciones push.

---

## ⚠️ IMPORTANTE: Seguridad

**Este archivo contiene claves privadas sensibles. NO lo subas a repositorios públicos.**

### Pasos de Seguridad:

1. **Mover el archivo a tu proyecto backend** (no dejarlo en Downloads)
2. **Agregarlo al `.gitignore`** para que no se suba a Git
3. **No compartirlo públicamente**

---

## 📁 Paso 1: Mover el Archivo a tu Proyecto Backend

Mueve el archivo desde Downloads a tu proyecto backend:

```bash
# Ejemplo (ajusta la ruta a tu proyecto backend)
mv /Users/durand/Downloads/tesis-2026-firebase-adminsdk-fbsvc-9202d1a52e.json /ruta/a/tu/backend/
```

O renómbralo a algo más simple (opcional):

```bash
mv tesis-2026-firebase-adminsdk-fbsvc-9202d1a52e.json firebase-credentials.json
```

---

## 🚫 Paso 2: Agregar al .gitignore

**CRÍTICO:** Agrega este archivo al `.gitignore` de tu proyecto backend:

```gitignore
# Firebase Admin SDK credentials
*firebase-adminsdk*.json
firebase-credentials.json
firebase-admin-credentials.json
```

---

## 💻 Paso 3: Usar en tu Backend

Dependiendo del lenguaje de tu backend:

### Node.js/Express:

```javascript
const admin = require('firebase-admin');
const path = require('path');

// Ruta al archivo de credenciales
const serviceAccount = require('./tesis-2026-firebase-adminsdk-fbsvc-9202d1a52e.json');

// Inicializar Firebase Admin SDK
admin.initializeApp({
  credential: admin.credential.cert(serviceAccount)
});

console.log('✅ Firebase Admin SDK inicializado correctamente');

// Exportar para usar en otros archivos
module.exports = admin;
```

### Python/Flask/FastAPI:

```python
import firebase_admin
from firebase_admin import credentials
import os

# Ruta al archivo de credenciales
cred_path = 'tesis-2026-firebase-adminsdk-fbsvc-9202d1a52e.json'

# Inicializar Firebase Admin SDK
cred = credentials.Certificate(cred_path)
firebase_admin.initialize_app(cred)

print('✅ Firebase Admin SDK inicializado correctamente')
```

### Java/Spring Boot:

```java
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.auth.oauth2.GoogleCredentials;
import java.io.FileInputStream;
import java.io.IOException;

public class FirebaseInitializer {
    
    public static void initializeFirebase() throws IOException {
        FileInputStream serviceAccount = new FileInputStream(
            "tesis-2026-firebase-adminsdk-fbsvc-9202d1a52e.json"
        );
        
        FirebaseOptions options = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
            .build();
        
        FirebaseApp.initializeApp(options);
        System.out.println("✅ Firebase Admin SDK inicializado correctamente");
    }
}
```

---

## 🧪 Paso 4: Probar que Funciona

Crea un archivo de prueba para verificar que Firebase está configurado correctamente:

### Node.js:

```javascript
const admin = require('firebase-admin');
const serviceAccount = require('./tesis-2026-firebase-adminsdk-fbsvc-9202d1a52e.json');

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount)
});

console.log('✅ Firebase Admin SDK inicializado');
console.log('Project ID:', admin.app().options.projectId);
```

Si ves el mensaje sin errores, está funcionando correctamente.

---

## 📝 Paso 5: Usar para Enviar Notificaciones

Una vez inicializado, puedes usarlo para enviar notificaciones:

### Node.js:

```javascript
const admin = require('firebase-admin');

async function enviarNotificacion(tokens, titulo, mensaje) {
  const message = {
    notification: {
      title: titulo,
      body: mensaje
    },
    tokens: tokens // Array de tokens FCM
  };
  
  const response = await admin.messaging().sendMulticast(message);
  console.log(`✅ Notificaciones enviadas: ${response.successCount} exitosas`);
  return response;
}

// Ejemplo de uso:
const tokens = ['token1', 'token2', 'token3'];
enviarNotificacion(tokens, 'Can perdido', 'Distrito: Rimac\nNombre del can: Boby');
```

---

## 📋 Checklist

- [ ] Archivo movido a tu proyecto backend
- [ ] Archivo agregado al `.gitignore`
- [ ] Firebase Admin SDK instalado en el backend
- [ ] Firebase Admin SDK inicializado en el código
- [ ] Probado que funciona (sin errores)
- [ ] Listo para implementar envío de notificaciones

---

## 🎯 Próximos Pasos

1. **Mover el archivo** a tu proyecto backend
2. **Agregarlo al `.gitignore`**
3. **Inicializar Firebase Admin SDK** en tu backend
4. **Implementar función** para enviar notificaciones cuando se registre un can perdido
5. **Probar** que las notificaciones se envían correctamente

---

## ⚠️ Recordatorio Final

**NO subas este archivo a GitHub ni a ningún repositorio público.**

El archivo contiene claves privadas que, si se exponen, permitirían que cualquiera use tu proyecto de Firebase.

¡Ahora tienes todo lo necesario para configurar Firebase en tu backend! 🚀



