# 🔧 Solución: Backend - Notificaciones Push

## 🔴 Problema Identificado

El endpoint `/api/v1/DeviceToken/registrarToken` **NO EXISTE** en tu backend (error 404).

**Sin este endpoint:**
- ❌ Los tokens FCM no se registran en el backend
- ❌ El backend no puede enviar notificaciones
- ❌ Las notificaciones NO llegan a ningún dispositivo

---

## ✅ Solución: Implementar Endpoint en el Backend

Tu backend necesita implementar el endpoint para registrar tokens y enviar notificaciones.

### Paso 1: Crear Endpoint para Registrar Tokens

**Endpoint requerido:** `POST /api/v1/DeviceToken/registrarToken`

**Request Body:**
```json
{
  "token": "dEN4WQ9xRZi0VkBqxzz1_G:APA91bHrnBGWxisV3hUnN7lT359fa5yZQEWSj36SW61neX9GYVaMXuIS5qkxTCJQL1op1IpkQJ3O4e5NMgGES57o5wqeNVNAzeuOGcgOQvC2FaDDjb2T2F0",
  "idUsuario": 123,
  "plataforma": "android"
}
```

**Response (éxito):**
```json
{
  "mensaje": "Token registrado exitosamente"
}
```

**Implementación (ejemplo en Node.js/Express):**

```javascript
// Endpoint para registrar tokens FCM
app.post('/api/v1/DeviceToken/registrarToken', async (req, res) => {
  try {
    const { token, idUsuario, plataforma } = req.body;
    
    // Validar datos
    if (!token || !plataforma) {
      return res.status(400).json({ error: 'Token y plataforma son requeridos' });
    }
    
    // IMPORTANTE: Guardar múltiples tokens por usuario
    // Si el token ya existe para este usuario, actualizar
    // Si no existe, crear nuevo registro
    const existingToken = await db.query(
      'SELECT * FROM device_tokens WHERE token = ? AND idUsuario = ?',
      [token, idUsuario]
    );
    
    if (existingToken.length > 0) {
      // Token ya existe, actualizar fecha
      await db.query(
        'UPDATE device_tokens SET fechaActualizacion = NOW() WHERE token = ? AND idUsuario = ?',
        [token, idUsuario]
      );
    } else {
      // Token nuevo, insertar
      await db.query(
        'INSERT INTO device_tokens (token, idUsuario, plataforma, fechaCreacion, fechaActualizacion) VALUES (?, ?, ?, NOW(), NOW())',
        [token, idUsuario, plataforma]
      );
    }
    
    res.status(200).json({ mensaje: 'Token registrado exitosamente' });
  } catch (error) {
    console.error('Error al registrar token:', error);
    res.status(500).json({ error: 'Error interno del servidor' });
  }
});
```

### Paso 2: Crear Tabla en Base de Datos

**Estructura de tabla recomendada:**

```sql
CREATE TABLE device_tokens (
  id INT PRIMARY KEY AUTO_INCREMENT,
  token VARCHAR(255) NOT NULL,
  idUsuario BIGINT,
  plataforma VARCHAR(50) NOT NULL, -- 'android', 'ios', etc.
  fechaCreacion DATETIME NOT NULL,
  fechaActualizacion DATETIME NOT NULL,
  activo BOOLEAN DEFAULT TRUE,
  INDEX idx_token (token),
  INDEX idx_idUsuario (idUsuario),
  INDEX idx_activo (activo)
);
```

**Notas importantes:**
- ✅ Permitir **múltiples tokens por usuario** (un usuario puede tener varios dispositivos)
- ✅ El campo `token` debe ser único o tener un índice
- ✅ El campo `idUsuario` puede ser NULL inicialmente (antes del login)
- ✅ Agregar índice en `idUsuario` para búsquedas rápidas

### Paso 3: Configurar Firebase Admin SDK en el Backend

El backend necesita autenticarse con Firebase para enviar notificaciones.

#### 3.1: Descargar Credenciales de Firebase

1. Ve a Firebase Console → Tu proyecto "Tesis-2026"
2. Ve a **Configuración del proyecto** (⚙️)
3. Ve a la pestaña **"Cuentas de servicio"**
4. Haz clic en **"Generar nueva clave privada"**
5. Se descargará un archivo JSON (ej: `tesis-2026-firebase-adminsdk-xxxxx.json`)
6. **Guarda este archivo de forma segura** (NO lo subas a repositorios públicos)

#### 3.2: Instalar Firebase Admin SDK

**Node.js:**
```bash
npm install firebase-admin
```

**Python:**
```bash
pip install firebase-admin
```

**Java:**
```xml
<dependency>
    <groupId>com.google.firebase</groupId>
    <artifactId>firebase-admin</artifactId>
    <version>9.2.0</version>
</dependency>
```

#### 3.3: Inicializar Firebase Admin SDK

**Node.js:**
```javascript
const admin = require('firebase-admin');
const serviceAccount = require('./path/to/tesis-2026-firebase-adminsdk-xxxxx.json');

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount)
});
```

**Python:**
```python
import firebase_admin
from firebase_admin import credentials

cred = credentials.Certificate('path/to/tesis-2026-firebase-adminsdk-xxxxx.json')
firebase_admin.initialize_app(cred)
```

**Java:**
```java
FileInputStream serviceAccount = new FileInputStream("path/to/tesis-2026-firebase-adminsdk-xxxxx.json");

FirebaseOptions options = new FirebaseOptions.Builder()
    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
    .build();

FirebaseApp.initializeApp(options);
```

### Paso 4: Implementar Envío de Notificaciones

Cuando se registre una mascota perdida, el backend debe enviar notificaciones a todos los dispositivos.

**Endpoint:** Después de registrar una mascota perdida (en tu endpoint existente)

**Implementación (Node.js):**

```javascript
// Después de guardar la mascota perdida en la BD
async function enviarNotificacionMascotaPerdida(datosMascota) {
  try {
    // 1. Obtener TODOS los tokens activos de TODOS los usuarios
    // (O solo de usuarios suscritos, según tu lógica de negocio)
    const tokens = await db.query(
      'SELECT token FROM device_tokens WHERE activo = TRUE AND plataforma = ?',
      ['android']
    );
    
    if (tokens.length === 0) {
      console.log('No hay tokens registrados para enviar notificaciones');
      return;
    }
    
    // 2. Preparar el mensaje
    const message = {
      notification: {
        title: '🐕 ¡Can Perdido Reportado!',
        body: `Se reportó ${datosMascota.nombre} perdido en ${datosMascota.lugarPerdida}. Ayuda a encontrarlo.`
      },
      data: {
        type: 'lost_dog',
        nombre: datosMascota.nombre,
        lugarPerdida: datosMascota.lugarPerdida,
        raza: datosMascota.raza || '',
        color: datosMascota.color || '',
        fechaPerdida: datosMascota.fechaPerdida || ''
      },
      // Enviar a múltiples tokens usando sendMulticast
      tokens: tokens.map(row => row.token)
    };
    
    // 3. Enviar notificaciones
    const response = await admin.messaging().sendMulticast(message);
    
    console.log(`Notificaciones enviadas: ${response.successCount} exitosas, ${response.failureCount} fallidas`);
    
    // 4. (Opcional) Manejar tokens inválidos
    if (response.failureCount > 0) {
      const failedTokens = [];
      response.responses.forEach((resp, idx) => {
        if (!resp.success) {
          failedTokens.push(tokens[idx].token);
        }
      });
      
      // Marcar tokens inválidos como inactivos
      if (failedTokens.length > 0) {
        await db.query(
          'UPDATE device_tokens SET activo = FALSE WHERE token IN (?)',
          [failedTokens]
        );
      }
    }
  } catch (error) {
    console.error('Error al enviar notificaciones:', error);
  }
}

// Usar en tu endpoint de registrar mascota perdida
app.post('/api/v1/Mascota/registrarMascotaPerdida', async (req, res) => {
  try {
    // ... código existente para guardar la mascota ...
    
    // Después de guardar exitosamente, enviar notificaciones
    await enviarNotificacionMascotaPerdida(datosMascota);
    
    res.status(201).json({ mensaje: 'Mascota perdida registrada exitosamente' });
  } catch (error) {
    res.status(500).json({ error: 'Error al registrar mascota perdida' });
  }
});
```

**Implementación (Python):**

```python
from firebase_admin import messaging

def enviar_notificacion_mascota_perdida(datos_mascota):
    try:
        # 1. Obtener todos los tokens activos
        tokens = db.query(
            "SELECT token FROM device_tokens WHERE activo = TRUE AND plataforma = %s",
            ('android',)
        )
        
        if not tokens:
            print('No hay tokens registrados')
            return
        
        # 2. Preparar mensaje
        message = messaging.MulticastMessage(
            notification=messaging.Notification(
                title='🐕 ¡Can Perdido Reportado!',
                body=f'Se reportó {datos_mascota["nombre"]} perdido en {datos_mascota["lugarPerdida"]}. Ayuda a encontrarlo.'
            ),
            data={
                'type': 'lost_dog',
                'nombre': datos_mascota['nombre'],
                'lugarPerdida': datos_mascota['lugarPerdida'],
                # ... otros datos
            },
            tokens=[row['token'] for row in tokens]
        )
        
        # 3. Enviar
        response = messaging.send_multicast(message)
        print(f'Enviadas: {response.success_count}, Fallidas: {response.failure_count}')
        
    except Exception as e:
        print(f'Error al enviar notificaciones: {e}')
```

---

## 📋 Checklist de Implementación

- [ ] **Crear tabla `device_tokens` en la base de datos**
- [ ] **Crear endpoint `POST /api/v1/DeviceToken/registrarToken`**
- [ ] **Descargar credenciales de Firebase (archivo JSON)**
- [ ] **Instalar Firebase Admin SDK en el backend**
- [ ] **Inicializar Firebase Admin SDK con las credenciales**
- [ ] **Implementar función para enviar notificaciones**
- [ ] **Integrar envío de notificaciones en endpoint de registrar mascota perdida**
- [ ] **Probar registro de token desde la app**
- [ ] **Probar envío de notificaciones**

---

## 🧪 Pruebas

### Prueba 1: Registrar Token

1. Ejecuta la app
2. Inicia sesión
3. Revisa los logs del backend
4. Verifica que el token se guarde en la BD

### Prueba 2: Enviar Notificación

1. Registra una mascota perdida desde la app
2. Revisa los logs del backend
3. Debe aparecer: "Notificaciones enviadas: X exitosas"
4. La notificación debe llegar al dispositivo

---

## ⚠️ Notas Importantes

1. **Múltiples tokens por usuario:** Un usuario puede tener varios dispositivos. Guarda TODOS los tokens.

2. **Enviar a todos los tokens:** Cuando envíes notificaciones, obtén TODOS los tokens activos y envíales la notificación.

3. **Manejar tokens inválidos:** Si un token es inválido, márcalo como inactivo para no intentar enviar de nuevo.

4. **Seguridad:** El archivo de credenciales de Firebase es SENSIBLE. NO lo subas a repositorios públicos.

5. **Topic vs Tokens:** Puedes usar topics (temas) de Firebase para enviar a grupos de usuarios, pero tokens individuales te dan más control.

---

## 🎯 Resumen

**Problema:** El endpoint `/api/v1/DeviceToken/registrarToken` no existe (404).

**Solución:**
1. Crear el endpoint para registrar tokens
2. Crear la tabla en la BD
3. Configurar Firebase Admin SDK
4. Implementar envío de notificaciones
5. Integrar en el flujo de registrar mascota perdida

¡Una vez implementado esto, las notificaciones deberían funcionar! 🎉

