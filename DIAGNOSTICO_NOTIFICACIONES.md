# 🔍 Diagnóstico: Notificaciones Push No Llegan

Ya tienes Firebase configurado, pero las notificaciones no llegan. Esta guía te ayudará a encontrar el problema.

---

## 📋 Checklist de Diagnóstico

Sigue estos pasos en orden para identificar el problema:

---

## ✅ Paso 1: Verificar que google-services.json es Real

### Verificar el archivo:

1. Abre el archivo: `app/google-services.json`
2. Busca estos valores:
   - `"project_number"`: Debe ser un número real (ej: "717780545218"), **NO** "000000000000"
   - `"project_id"`: Debe ser un ID real (ej: "tesis-2026"), **NO** "dogedex-placeholder"
   - `"mobilesdk_app_id"`: Debe ser real (ej: "1:717780545218:android:..."), **NO** "1:000000000000:android:..."

**Si ves valores con "000000000000" o "placeholder":**
- ⚠️ Aún tienes el archivo placeholder
- Descarga el archivo REAL desde Firebase Console
- Reemplázalo en `app/google-services.json`
- Rebuild el proyecto

---

## ✅ Paso 2: Verificar que el Token FCM se Obtiene

### En Android Studio Logcat:

1. Ejecuta la app en tu dispositivo
2. Filtra Logcat por: `FCMTokenManager`
3. Inicia sesión en la app
4. Busca estos logs:

```
✅ DEBE APARECER:
D/FCMTokenManager: FCM Registration Token: [token muy largo aquí]
D/FCMTokenManager: Token registrado exitosamente: [mensaje]
```

**Si NO ves estos logs:**
- El token no se está obteniendo
- Verifica que `google-services.json` sea el archivo REAL
- Reinstala la app después de reemplazar el archivo

**Si SÍ ves estos logs:**
- ✅ El token se está obteniendo correctamente
- Copia el token completo para usarlo en las pruebas
- Continúa al Paso 3

---

## ✅ Paso 3: Probar Notificación desde Firebase Console

Esta prueba confirma si Firebase está funcionando correctamente.

### Proceso:

1. **Obtén el token FCM** del paso anterior (cópialo completo)

2. **Ve a Firebase Console:**
   - Firebase Console → Tu proyecto "Tesis-2026"
   - Cloud Messaging (en el menú lateral)
   - Haz clic en "Enviar mensaje de prueba" o "Send test message"

3. **Pega el token** en el campo correspondiente

4. **Escribe un mensaje de prueba** (ej: "Prueba de notificación")

5. **Haz clic en "Test" o "Enviar"**

### Resultados Posibles:

**✅ La notificación LLEGA al dispositivo:**
- Firebase está funcionando correctamente
- El problema está en el BACKEND (no está enviando notificaciones)
- Ve al Paso 4

**❌ La notificación NO LLEGA:**
- Hay un problema con Firebase o la app
- Verifica:
  - Permisos de notificaciones en el dispositivo
  - Conexión a internet
  - Que la app esté en primer plano o segundo plano (no cerrada)
  - Revisa los logs en Logcat para ver errores

---

## ✅ Paso 4: Verificar Registro de Token en el Backend

El backend debe recibir y guardar el token correctamente.

### Verificar en Logs de la App:

Busca en Logcat (filtro: `FCMTokenManager`):

```
✅ Token registrado exitosamente: [mensaje del servidor]
```

**Si ves un ERROR:**
```
❌ Error al registrar token: [código de error]
```
- El backend no está recibiendo el token
- Verifica la conexión con el backend
- Verifica que el endpoint `/api/v1/DeviceToken/registrarToken` esté funcionando

### Verificar en Base de Datos del Backend:

1. Conéctate a la base de datos de tu backend
2. Busca la tabla de device tokens (puede llamarse `device_tokens`, `tokens`, `fcm_tokens`, etc.)
3. Verifica que exista una entrada con:
   - `token`: El token FCM que viste en los logs
   - `idUsuario`: Tu ID de usuario
   - `plataforma`: "android"

**Si NO hay entrada en la BD:**
- El backend no está guardando el token
- Revisa los logs del backend para ver errores
- Verifica que el endpoint funcione correctamente

**Si SÍ hay entrada:**
- ✅ El token está guardado
- Continúa al Paso 5

---

## ✅ Paso 5: Verificar que el Backend Envía Notificaciones

Este es el paso más importante. El backend debe enviar notificaciones cuando se registra un can perdido.

### Verificar Código del Backend:

El backend debe tener código similar a esto cuando se registra un can perdido:

```javascript
// Pseudocódigo - debe adaptarse a tu lenguaje del backend
async function registrarMascotaPerdida(datos) {
  // 1. Guardar la mascota perdida en la BD
  const mascota = await db.save(datos);
  
  // 2. Obtener TODOS los tokens de TODOS los usuarios (o usuarios suscritos)
  const tokens = await db.query(
    "SELECT token FROM device_tokens WHERE ..." // Obtener tokens relevantes
  );
  
  // 3. Enviar notificación a TODOS los tokens
  for (const tokenRow of tokens) {
    await admin.messaging().send({
      token: tokenRow.token,
      notification: {
        title: "🐕 ¡Can Perdido Reportado!",
        body: `Se reportó ${datos.nombre} perdido en ${datos.lugarPerdida}`
      },
      data: {
        type: "lost_dog",
        nombre: datos.nombre,
        lugarPerdida: datos.lugarPerdida,
        // ... otros datos
      }
    });
  }
}
```

### Problemas Comunes en el Backend:

1. **El backend NO envía notificaciones:**
   - No hay código para enviar notificaciones
   - Necesitas agregarlo

2. **El backend solo envía al último token:**
   - Está usando UPDATE en lugar de INSERT
   - Solo guarda un token por usuario
   - Necesitas modificar para guardar múltiples tokens

3. **El backend solo envía a un token:**
   - Está enviando solo al primer token
   - Debe enviar a TODOS los tokens

4. **El backend no tiene las credenciales de Firebase:**
   - Necesitas descargar el archivo de credenciales de Firebase
   - En Firebase Console → Configuración → Cuentas de servicio
   - Generar nueva clave privada (JSON)
   - Usar este archivo en el backend para autenticarse con Firebase

---

## ✅ Paso 6: Verificar Permisos de Notificaciones

### En el Dispositivo Android:

1. Ve a: **Configuración → Apps → Dogedex → Notificaciones**
2. Verifica que las notificaciones estén **habilitadas**

### En Android 13+ (API 33+):

La app debe pedir el permiso `POST_NOTIFICATIONS` en tiempo de ejecución.

**Verifica en el código:**
- El permiso está declarado en `AndroidManifest.xml` ✅ (ya lo tienes)
- La app debe pedir el permiso al usuario la primera vez

**Si el permiso no se ha concedido:**
- Desinstala y reinstala la app
- La app debería pedir el permiso automáticamente

---

## ✅ Paso 7: Verificar Estado de la App

Las notificaciones pueden comportarse diferente según el estado de la app:

### Estados de la App:

1. **App en primer plano (abierta y visible):**
   - Las notificaciones se reciben en `onMessageReceived()`
   - Debes mostrar la notificación manualmente (ya lo haces en el código) ✅

2. **App en segundo plano (abierta pero no visible):**
   - Las notificaciones deben llegar normalmente
   - Se muestran en la barra de notificaciones

3. **App cerrada (killada):**
   - Las notificaciones deben llegar
   - Se muestran en la barra de notificaciones
   - Al hacer clic, se abre la app

**Probar:**
- Deja la app en segundo plano
- Envía una notificación desde Firebase Console
- Debe llegar y mostrarse en la barra de notificaciones

---

## 🎯 Resumen de Problemas Más Comunes

### Problema 1: El backend no envía notificaciones
**Solución:** Agregar código en el backend para enviar notificaciones cuando se registra un can perdido.

### Problema 2: El backend solo guarda un token por usuario
**Solución:** Modificar la BD para permitir múltiples tokens por usuario (relación uno-a-muchos).

### Problema 3: El backend solo envía a un token
**Solución:** Modificar el código para enviar a TODOS los tokens guardados.

### Problema 4: El backend no tiene credenciales de Firebase
**Solución:** Descargar el archivo JSON de credenciales desde Firebase Console → Cuentas de servicio.

### Problema 5: Permisos de notificaciones no concedidos
**Solución:** Verificar en configuración del dispositivo y/o reinstalar la app.

---

## 🧪 Prueba Completa Paso a Paso

1. **Ejecuta la app** en un dispositivo
2. **Inicia sesión** con tu cuenta
3. **Verifica en Logcat:** Debe aparecer "Token registrado exitosamente"
4. **Copia el token FCM** de los logs
5. **Ve a Firebase Console → Cloud Messaging → Enviar mensaje de prueba**
6. **Pega el token y envía** una notificación de prueba
7. **Verifica:** ¿Llega la notificación?
   - ✅ SÍ → El problema está en el backend
   - ❌ NO → El problema está en Firebase o la app
8. **Registra un can perdido** desde la app
9. **Verifica:** ¿Llega la notificación a otros dispositivos?
   - ✅ SÍ → ¡Todo funciona!
   - ❌ NO → El backend no está enviando notificaciones

---

## 📞 Próximos Pasos

Basado en los resultados de este diagnóstico:

- **Si Firebase Console funciona pero el backend no:** Necesitas revisar/agregar código en el backend
- **Si Firebase Console no funciona:** Necesitas revisar configuración de Firebase o permisos
- **Si el token no se registra:** Revisa la conexión con el backend y el endpoint de registro

¿En qué paso encontraste el problema? Comparte los resultados y te ayudo a solucionarlo específicamente.


