# Solución: Notificaciones no llegan a otros dispositivos

## 🔴 Problema Principal

El archivo `google-services.json` actual es un **placeholder** (valores falsos) creado solo para permitir que el proyecto compile. **Sin un archivo real de Firebase, las notificaciones NO funcionarán**.

## 📋 Checklist de Verificación

### 1. ✅ Archivo google-services.json Real

**CRÍTICO**: El archivo actual en `app/google-services.json` es un placeholder. Necesitas:

1. Ir a [Firebase Console](https://console.firebase.google.com/)
2. Seleccionar o crear tu proyecto Firebase
3. Agregar una aplicación Android con package name: `com.durand.dogedex`
4. Descargar el archivo `google-services.json` real
5. Reemplazar el placeholder en `app/google-services.json`

**Sin esto, FCM no funcionará en ningún dispositivo.**

### 2. ✅ Verificar que cada dispositivo registra su token

Cada dispositivo Android tiene un token FCM único. Verifica en los logs de cada dispositivo:

```
Logcat filter: FCMTokenManager
```

Debes ver en cada dispositivo:
- `FCM Registration Token: [token único]`
- `Token registrado exitosamente`

### 3. ✅ Verificar que los tokens se registran con el mismo idUsuario

Después del login en cada dispositivo, el token debe registrarse con el `idUsuario`. Verifica:

- El usuario está logueado con la misma cuenta en ambos dispositivos
- Los logs muestran: `Token registrado exitosamente` después del login
- El backend tiene registrados múltiples tokens para el mismo `idUsuario`

### 4. ✅ Verificar en el Backend

El backend debe:

1. **Guardar múltiples tokens por usuario** (no sobrescribir)
   - Un usuario puede tener varios dispositivos
   - Cada dispositivo tiene su propio token FCM

2. **Enviar notificaciones a TODOS los tokens del usuario**
   - No solo al último token registrado
   - Enviar a todos los tokens asociados al mismo `idUsuario`

### 5. ✅ Permisos de Notificaciones

En cada dispositivo Android:
- Ir a Configuración → Apps → Dogedex → Notificaciones
- Verificar que las notificaciones estén habilitadas
- En Android 13+, la app debe pedir el permiso `POST_NOTIFICATIONS`

### 6. ✅ Conexión a Internet

- Ambos dispositivos deben tener conexión a internet
- FCM requiere conexión para recibir notificaciones

## 🔍 Cómo Diagnosticar el Problema

### Paso 1: Verificar Token en cada dispositivo

1. En cada dispositivo, abre la app y inicia sesión
2. En Android Studio Logcat, filtra por: `FCMTokenManager`
3. Anota el token de cada dispositivo
4. Verifica que sean diferentes (cada dispositivo tiene un token único)

### Paso 2: Verificar registro en el Backend

1. Consulta la base de datos del backend
2. Busca en la tabla de device tokens
3. Verifica que existan múltiples tokens para tu `idUsuario`
4. Cada token debe tener el mismo `idUsuario` pero diferente `token`

### Paso 3: Probar notificación desde Firebase Console

1. Ve a Firebase Console → Cloud Messaging
2. Clic en "Enviar mensaje de prueba"
3. Ingresa el token FCM de uno de los dispositivos
4. Envía una notificación de prueba
5. Repite con el token del otro dispositivo

**Si las notificaciones de prueba funcionan**: El problema está en el backend (no está enviando a todos los tokens)
**Si las notificaciones de prueba NO funcionan**: El problema es el `google-services.json` o la configuración de Firebase

### Paso 4: Verificar código del Backend

El backend debe tener lógica similar a esto:

```javascript
// Ejemplo en Node.js (pseudocódigo)
async function enviarNotificacion(idUsuario, mensaje) {
  // Obtener TODOS los tokens del usuario
  const tokens = await db.query(
    "SELECT token FROM device_tokens WHERE idUsuario = ?",
    [idUsuario]
  );
  
  // Enviar a TODOS los tokens (no solo al primero)
  const promises = tokens.map(token => 
    admin.messaging().send({
      token: token,
      notification: {
        title: mensaje.titulo,
        body: mensaje.cuerpo
      }
    })
  );
  
  await Promise.all(promises);
}
```

## 🛠️ Soluciones por Problema

### Problema: El archivo google-services.json es placeholder

**Solución:**
1. Obtén el archivo real de Firebase Console
2. Reemplaza `app/google-services.json`
3. Rebuild el proyecto
4. Reinstala la app en todos los dispositivos

### Problema: El backend solo guarda el último token

**Solución Backend:**
- Modificar la lógica para guardar múltiples tokens por usuario
- Usar una relación uno-a-muchos (un usuario, muchos tokens)
- Al registrar un token, hacer INSERT en lugar de UPDATE

### Problema: El backend solo envía al último token

**Solución Backend:**
- Al enviar notificaciones, obtener TODOS los tokens del usuario
- Enviar la notificación a cada token
- Usar `sendMulticast()` o múltiples `send()` en paralelo

### Problema: Token no se registra después del login

**Verificar:**
- Logs en Logcat: buscar `FCMTokenManager` después del login
- Verificar que `updateTokenWithUserId()` se llame después del login exitoso
- Verificar conexión con el backend (código HTTP 200)

## 📱 Prueba Rápida

1. **Dispositivo 1:**
   - Abre la app
   - Inicia sesión
   - Verifica en logs: "Token registrado exitosamente"
   - Copia el token del log

2. **Dispositivo 2:**
   - Abre la app (mismo usuario)
   - Inicia sesión
   - Verifica en logs: "Token registrado exitosamente"
   - Copia el token del log

3. **Firebase Console:**
   - Ve a Cloud Messaging → Enviar mensaje de prueba
   - Prueba con el token del dispositivo 1 → Debe llegar
   - Prueba con el token del dispositivo 2 → Debe llegar

4. **Backend:**
   - Verifica que ambos tokens estén guardados en la BD
   - Ambos deben tener el mismo `idUsuario`
   - Cuando se registre un can perdido, debe enviar a AMBOS tokens

## ⚠️ Notas Importantes

- Cada dispositivo Android tiene un token FCM único
- Un usuario puede tener múltiples dispositivos
- El backend debe guardar y enviar a TODOS los tokens del usuario
- El `google-services.json` debe ser el archivo REAL de Firebase (no placeholder)
- Las notificaciones requieren conexión a internet
- Los permisos de notificaciones deben estar habilitados en cada dispositivo




