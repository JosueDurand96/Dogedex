# Configuración de Firebase Cloud Messaging para Android

Este documento explica cómo configurar Firebase Cloud Messaging (FCM) en la aplicación Android Dogedex.

## Pasos de Configuración

### 1. Crear Proyecto Firebase (si no existe)

1. Ve a [Firebase Console](https://console.firebase.google.com/)
2. Crea un nuevo proyecto o selecciona uno existente
3. Asegúrate de que el proyecto tenga habilitado Firebase Cloud Messaging

### 2. Agregar Aplicación Android a Firebase

1. En Firebase Console, haz clic en el ícono de Android
2. Ingresa el **Package name**: `com.durand.dogedex`
3. Ingresa el **App nickname** (opcional): "Dogedex Android"
4. Ingresa el **Debug signing certificate SHA-1** (opcional, para desarrollo)
5. Haz clic en **Registrar app**

### 3. Descargar google-services.json

1. Firebase te proporcionará un archivo `google-services.json`
2. Descarga este archivo
3. Colócalo en: `app/` (raíz del módulo app, al mismo nivel que `build.gradle.kts`)

**Estructura esperada:**
```
Dogedex/
  app/
    google-services.json  ← Aquí va el archivo
    build.gradle.kts
    src/
    ...
```

### 4. Verificar Configuración

El plugin de Google Services se aplicará automáticamente y leerá el archivo `google-services.json`.

### 5. Obtener SHA-1 para Desarrollo (Opcional pero Recomendado)

Para que las notificaciones funcionen correctamente en modo debug:

```bash
# En Windows
keytool -list -v -keystore "%USERPROFILE%\.android\debug.keystore" -alias androiddebugkey -storepass android -keypass android

# En macOS/Linux
keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android
```

Copia el SHA-1 y agrégalo en Firebase Console → Configuración del proyecto → Tus aplicaciones → Android app → Agregar huella digital.

## Funcionamiento

### Flujo de Registro de Token

1. **Al iniciar la aplicación** (`MyApplication.onCreate()`):
   - Se obtiene el token FCM automáticamente
   - Se guarda localmente en SharedPreferences
   - Se intenta registrar en el servidor (sin idUsuario aún)

2. **Después del login exitoso** (`LoginFragment`):
   - Se actualiza el token con el `idUsuario` del usuario
   - El token queda asociado al usuario en el servidor

3. **Cuando se recibe una notificación**:
   - `MyFirebaseMessagingService.onMessageReceived()` maneja la notificación
   - Se muestra una notificación local con la información del can perdido
   - Al hacer clic, se abre `UserHome`

### Características Implementadas

- ✅ Registro automático de tokens FCM
- ✅ Actualización de token con idUsuario después del login
- ✅ Manejo de notificaciones recibidas
- ✅ Canal de notificaciones para Android 8.0+
- ✅ Suscripción automática al tema "lost_dogs"
- ✅ Notificaciones con vibración y sonido
- ✅ Navegación a la app al hacer clic en la notificación

## Estructura de Notificaciones

Cuando se registra un can perdido, la notificación incluye:

- **Título**: "🐕 ¡Can Perdido Reportado!"
- **Mensaje**: "Se reportó [nombre] perdido en [lugar]. Ayuda a encontrarlo."
- **Datos adicionales**:
  - `type`: "lost_dog"
  - `nombre`: Nombre del can
  - `lugarPerdida`: Lugar donde se perdió
  - `raza`: Raza del can
  - `color`: Color del can

## Pruebas

### Probar Notificaciones Manualmente

1. Inicia sesión en la app
2. Verifica en los logs que el token se registró correctamente
3. Desde Firebase Console → Cloud Messaging → Enviar mensaje de prueba:
   - Ingresa el token FCM del dispositivo
   - Escribe un mensaje de prueba
   - Envía

### Verificar Registro de Token

Revisa los logs de Android Studio:
- Busca: `FCMTokenManager` o `FCMService`
- Deberías ver: "Token registrado exitosamente"

## Solución de Problemas

### El token no se registra

1. Verifica que `google-services.json` esté en la ubicación correcta
2. Verifica que el plugin de Google Services esté aplicado
3. Revisa los logs para ver errores de conexión

### No se reciben notificaciones

1. Verifica que el dispositivo tenga conexión a internet
2. Verifica que las notificaciones estén habilitadas en la configuración del dispositivo
3. Verifica que el token esté registrado en el servidor
4. Revisa Firebase Console para ver si hay errores al enviar

### Error: "google-services.json not found"

- Asegúrate de que el archivo esté en `app/google-services.json`
- Sincroniza el proyecto con Gradle Files
- Limpia y reconstruye el proyecto

## Notas Importantes

- El archivo `google-services.json` contiene información sensible. **NO lo subas a repositorios públicos**
- Agrega `google-services.json` al `.gitignore` si es necesario
- Para producción, necesitarás un `google-services.json` diferente con el SHA-1 del keystore de producción



