# 🔧 Configuración Completa de Google/Firebase para Notificaciones

Esta guía te muestra **TODOS** los pasos necesarios para configurar Google/Firebase y que las notificaciones funcionen correctamente.

---

## 📋 Tabla de Contenidos

1. [Crear Proyecto Firebase](#1-crear-proyecto-firebase)
2. [Agregar Aplicación Android](#2-agregar-aplicación-android)
3. [Obtener SHA-1 (Importante para Desarrollo)](#3-obtener-sha-1-importante-para-desarrollo)
4. [Descargar google-services.json](#4-descargar-google-servicesjson)
5. [Habilitar Cloud Messaging API](#5-habilitar-cloud-messaging-api)
6. [Verificar Configuración](#6-verificar-configuración)

---

## 1. Crear Proyecto Firebase

### Paso 1.1: Acceder a Firebase Console

1. Ve a [Firebase Console](https://console.firebase.google.com/)
2. Inicia sesión con tu cuenta de Google
3. Si no tienes proyecto, haz clic en **"Crear un proyecto"** o **"Add project"**

### Paso 1.2: Crear Nuevo Proyecto (si no existe)

1. **Nombre del proyecto**: Ingresa "Dogedex" (o el nombre que prefieras)
2. **Google Analytics** (opcional): Puedes deshabilitarlo o habilitarlo
3. Haz clic en **"Crear proyecto"**
4. Espera a que se complete la creación (puede tardar unos segundos)
5. Haz clic en **"Continuar"**

---

## 2. Agregar Aplicación Android

### Paso 2.1: Agregar App Android al Proyecto

1. En la página principal del proyecto Firebase, haz clic en el ícono de **Android** 📱
2. Se abrirá un formulario para registrar tu app Android

### Paso 2.2: Completar el Formulario

Completa los siguientes campos:

- **Package name**: `com.durand.dogedex`
  - ⚠️ **IMPORTANTE**: Debe ser exactamente este nombre (sin espacios, sin errores)

- **App nickname** (opcional): "Dogedex Android" o "Dogedex"

- **Debug signing certificate SHA-1** (por ahora déjalo vacío, lo agregaremos después)
  - Este paso es importante para desarrollo, lo haremos en el paso 3

3. Haz clic en **"Registrar app"**

---

## 3. Obtener SHA-1 (Importante para Desarrollo)

### ¿Por qué es importante?

El SHA-1 permite que Firebase funcione correctamente en modo debug. Sin él, las notificaciones pueden no funcionar en desarrollo.

### Paso 3.1: Obtener SHA-1 en macOS

Abre la terminal y ejecuta:

```bash
keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android
```

### Paso 3.2: Copiar el SHA-1

Busca la línea que dice **"SHA1:"** y copia el valor completo. Ejemplo:

```
SHA1: A1:B2:C3:D4:E5:F6:G7:H8:I9:J0:K1:L2:M3:N4:O5:P6:Q7:R8:S9:T0
```

### Paso 3.3: Agregar SHA-1 en Firebase

1. Ve a Firebase Console → Tu proyecto
2. Haz clic en el ícono de **⚙️ Configuración** (engranaje) → **Configuración del proyecto**
3. En la sección **"Tus aplicaciones"**, encuentra tu app Android
4. Haz clic en **"Agregar huella digital"**
5. Pega el SHA-1 que copiaste
6. Haz clic en **"Guardar"**

**Nota**: Puedes agregar múltiples SHA-1 (uno para debug, otro para producción)

---

## 4. Descargar google-services.json

### Paso 4.1: Descargar el Archivo

1. En Firebase Console, ve a **Configuración del proyecto** (⚙️)
2. En la sección **"Tus aplicaciones"**, encontrarás tu app Android
3. Haz clic en **"Descargar google-services.json"**
4. El archivo se descargará automáticamente

### Paso 4.2: Colocar el Archivo en el Proyecto

1. **Mueve** el archivo `google-services.json` descargado a:
   ```
   /Users/durand/Desktop/tesis-2025/Dogedex/app/google-services.json
   ```
   
2. **Reemplaza** el archivo placeholder que está ahí actualmente

3. **Estructura correcta**:
   ```
   Dogedex/
     app/
       google-services.json  ← Aquí debe estar (archivo REAL)
       build.gradle.kts
       src/
         main/
           ...
   ```

### Paso 4.3: Sincronizar Proyecto

1. Abre Android Studio
2. Haz clic en **"Sync Project with Gradle Files"** (icono de elefante con flechas)
3. O ve a: **File → Sync Project with Gradle Files**

---

## 5. Habilitar Cloud Messaging API

### Paso 5.1: Verificar que Cloud Messaging esté Habilitado

1. En Firebase Console, ve a **Configuración del proyecto** (⚙️)
2. Ve a la pestaña **"Cloud Messaging"**
3. Verifica que **"Cloud Messaging API (Legacy)"** esté habilitado

### Paso 5.2: Habilitar API en Google Cloud (si es necesario)

1. Ve a [Google Cloud Console](https://console.cloud.google.com/)
2. Selecciona tu proyecto Firebase (mismo nombre)
3. Ve a **"APIs & Services"** → **"Library"**
4. Busca **"Firebase Cloud Messaging API"**
5. Si no está habilitada, haz clic en **"Enable"**

**Nota**: Esto normalmente se habilita automáticamente, pero verifica por si acaso.

---

## 6. Verificar Configuración

### Paso 6.1: Verificar en Android Studio

1. Abre el archivo `app/build.gradle.kts`
2. Verifica que tengas el plugin de Google Services:
   ```kotlin
   plugins {
       ...
       alias(libs.plugins.google.services)
   }
   ```
   ✅ Ya lo tienes configurado

3. Verifica las dependencias de Firebase:
   ```kotlin
   dependencies {
       ...
       implementation(platform(libs.firebase.bom))
       implementation(libs.firebase.messaging)
   }
   ```
   ✅ Ya lo tienes configurado

### Paso 6.2: Verificar google-services.json

Abre el archivo `app/google-services.json` y verifica que tenga estructura similar a:

```json
{
  "project_info": {
    "project_number": "123456789012",  ← Debe ser un número real, NO "000000000000"
    "project_id": "dogedex-xxxxx",      ← Debe ser un ID real, NO "dogedex-placeholder"
    ...
  },
  "client": [
    {
      "client_info": {
        "mobilesdk_app_id": "1:123456789012:android:abcdef123456",  ← Debe ser real
        "android_client_info": {
          "package_name": "com.durand.dogedex"
        }
      },
      ...
    }
  ]
}
```

⚠️ **Si ves valores como "000000000000" o "placeholder"**, significa que todavía tienes el archivo placeholder. Necesitas descargar el archivo real de Firebase.

### Paso 6.3: Rebuild y Probar

1. **Limpia el proyecto**: **Build → Clean Project**
2. **Reconstruye**: **Build → Rebuild Project**
3. **Ejecuta la app** en un dispositivo o emulador
4. **Inicia sesión** en la app
5. **Revisa los logs** (Logcat):
   - Filtra por: `FCMTokenManager`
   - Debes ver: `FCM Registration Token: [token largo]`
   - Debes ver: `Token registrado exitosamente`

---

## 🔍 Verificación Final

### Checklist de Verificación:

- [ ] ✅ Proyecto Firebase creado
- [ ] ✅ App Android agregada con package: `com.durand.dogedex`
- [ ] ✅ SHA-1 agregado en Firebase Console
- [ ] ✅ Archivo `google-services.json` REAL descargado y colocado en `app/`
- [ ] ✅ Cloud Messaging API habilitada
- [ ] ✅ Proyecto sincronizado con Gradle
- [ ] ✅ App compilada y ejecutada
- [ ] ✅ Token FCM obtenido (visible en logs)

---

## 🧪 Probar Notificaciones

### Desde Firebase Console:

1. Ve a Firebase Console → **Cloud Messaging**
2. Haz clic en **"Enviar mensaje de prueba"** o **"Send test message"**
3. Obtén el token FCM de tu dispositivo:
   - Ejecuta la app
   - Revisa Logcat con filtro: `FCMTokenManager`
   - Copia el token que aparece: `FCM Registration Token: [pega aquí]`
4. Pega el token en Firebase Console
5. Escribe un mensaje de prueba
6. Haz clic en **"Test"** o **"Enviar"**
7. La notificación debe llegar al dispositivo

---

## ⚠️ Problemas Comunes

### Problema: "google-services.json not found"

**Solución:**
- Verifica que el archivo esté en `app/google-services.json`
- Verifica que el nombre del archivo sea exactamente `google-services.json` (sin espacios)
- Sincroniza el proyecto con Gradle

### Problema: "Token no se genera"

**Solución:**
- Verifica que el `google-services.json` sea el archivo REAL (no placeholder)
- Verifica que el package name en Firebase coincida exactamente: `com.durand.dogedex`
- Verifica que el SHA-1 esté agregado (importante para desarrollo)
- Reinstala la app después de agregar el archivo real

### Problema: "Notificaciones no llegan"

**Solución:**
- Verifica que Cloud Messaging API esté habilitada
- Verifica que el token se registre correctamente en los logs
- Verifica permisos de notificaciones en el dispositivo
- Prueba primero desde Firebase Console (enviar mensaje de prueba)

---

## 📝 Notas Importantes

1. **El archivo `google-services.json` es SENSIBLE**: No lo subas a repositorios públicos
2. **SHA-1 diferente para producción**: Cuando generes el APK/AAB de producción, necesitarás agregar el SHA-1 del keystore de producción
3. **Un proyecto Firebase puede tener múltiples apps**: Puedes agregar apps de iOS, web, etc. al mismo proyecto
4. **Token único por dispositivo**: Cada dispositivo Android tiene su propio token FCM único

---

## 🎯 Resumen Rápido

1. ✅ Crear proyecto en [Firebase Console](https://console.firebase.google.com/)
2. ✅ Agregar app Android (package: `com.durand.dogedex`)
3. ✅ Obtener SHA-1 y agregarlo en Firebase
4. ✅ Descargar `google-services.json` REAL
5. ✅ Colocarlo en `app/google-services.json`
6. ✅ Sincronizar proyecto con Gradle
7. ✅ Rebuild y probar

¡Listo! 🎉

