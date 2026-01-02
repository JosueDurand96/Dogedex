# 🚀 Implementación: Notificaciones Push al Registrar Can Perdido

## Objetivo

Cuando se registre un can perdido, enviar una notificación push a **TODOS los dispositivos** que tengan la app instalada.

**Formato de la notificación:**
- **Título:** "Can perdido"
- **Mensaje:** "Distrito: [distrito]\nNombre del can: [nombre]"
- **Ejemplo:** 
  - Título: "Can perdido"
  - Mensaje: "Distrito: Rimac\nNombre del can: Boby"

---

## 📋 Información del Request

El endpoint que registra un can perdido es:
- **Endpoint:** `POST /api/v1/Mascota/registrarMascotaPerdida`
- **Request Body:**
```json
{
  "nombre": "Boby",
  "especie": "Perro",
  "genero": "Macho",
  "raza": "Labrador",
  "tamanio": "Grande",
  "caracter": "Amigable",
  "color": "Dorado",
  "pelaje": "Corto",
  "foto": "[base64]",
  "idUsuario": 123,
  "nombreUsuario": "Juan",
  "apellidoUsuario": "Pérez",
  "fechaPerdida": "01/01/2026 19:30:00",
  "lugarPerdida": "Calle Principal 123",
  "comentario": "Se perdió en el parque",
  "latitud": -12.0464,
  "longitud": -77.0428
}
```

**Nota:** El request actual NO incluye `distrito`, solo `lugarPerdida`. Necesitas decidir:
- Opción 1: Agregar campo `distrito` al request (recomendado)
- Opción 2: Extraer el distrito del campo `lugarPerdida` (si contiene esa información)
- Opción 3: Obtener el distrito desde las coordenadas (latitud/longitud) usando geocodificación inversa

---

## 🔧 Implementación en el Backend

### Paso 1: Configurar Firebase Admin SDK

#### 1.1: Descargar Credenciales de Firebase

1. Ve a [Firebase Console](https://console.firebase.google.com/)
2. Tu proyecto: **Tesis-2026**
3. Ve a **Configuración del proyecto** (⚙️) → **Cuentas de servicio**
4. Haz clic en **"Generar nueva clave privada"**
5. Se descargará un archivo JSON (ej: `tesis-2026-firebase-adminsdk-xxxxx.json`)
6. **Guarda este archivo de forma segura** (NO lo subas a repositorios públicos)

#### 1.2: Instalar Firebase Admin SDK

**Node.js:**
```bash
npm install firebase-admin
```

**Python:**
```bash
pip install firebase-admin
```

**Java/Maven:**
```xml
<dependency>
    <groupId>com.google.firebase</groupId>
    <artifactId>firebase-admin</artifactId>
    <version>9.2.0</version>
</dependency>
```

#### 1.3: Inicializar Firebase Admin SDK

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

---

### Paso 2: Crear Función para Enviar Notificaciones

**Node.js/Express:**

```javascript
const admin = require('firebase-admin');

/**
 * Envía notificación push a todos los dispositivos cuando se registra un can perdido
 * @param {Object} datosCanPerdido - Datos del can perdido
 */
async function enviarNotificacionCanPerdido(datosCanPerdido) {
  try {
    // 1. Obtener TODOS los tokens activos de TODOS los usuarios
    // (O filtrar por distrito/ubicación si quieres notificar solo a usuarios cercanos)
    const tokens = await db.query(
      'SELECT token FROM device_tokens WHERE activo = TRUE AND plataforma = ?',
      ['android']
    );
    
    if (tokens.length === 0) {
      console.log('No hay tokens registrados para enviar notificaciones');
      return;
    }
    
    // 2. Preparar el mensaje de la notificación
    // Si no tienes distrito en el request, puedes:
    // - Extraerlo de lugarPerdida si contiene esa información
    // - Usar geocodificación inversa con latitud/longitud
    // - O simplemente usar lugarPerdida
    const distrito = datosCanPerdido.distrito || datosCanPerdido.lugarPerdida || 'Ubicación no especificada';
    const nombre = datosCanPerdido.nombre || 'Sin nombre';
    
    const message = {
      notification: {
        title: 'Can perdido',
        body: `Distrito: ${distrito}\nNombre del can: ${nombre}`
      },
      data: {
        type: 'lost_dog',
        nombre: nombre,
        distrito: distrito,
        lugarPerdida: datosCanPerdido.lugarPerdida || '',
        raza: datosCanPerdido.raza || '',
        color: datosCanPerdido.color || '',
        fechaPerdida: datosCanPerdido.fechaPerdida || ''
      },
      // Enviar a múltiples tokens usando sendMulticast
      tokens: tokens.map(row => row.token)
    };
    
    // 3. Enviar notificaciones
    const response = await admin.messaging().sendMulticast(message);
    
    console.log(`✅ Notificaciones enviadas: ${response.successCount} exitosas, ${response.failureCount} fallidas`);
    
    // 4. Manejar tokens inválidos
    if (response.failureCount > 0) {
      const failedTokens = [];
      response.responses.forEach((resp, idx) => {
        if (!resp.success) {
          failedTokens.push(tokens[idx].token);
          console.error(`❌ Error en token ${idx}: ${resp.error?.message}`);
        }
      });
      
      // Marcar tokens inválidos como inactivos
      if (failedTokens.length > 0) {
        await db.query(
          'UPDATE device_tokens SET activo = FALSE WHERE token IN (?)',
          [failedTokens]
        );
        console.log(`🗑️ ${failedTokens.length} tokens inválidos marcados como inactivos`);
      }
    }
    
  } catch (error) {
    console.error('❌ Error al enviar notificaciones:', error);
    // No lanzar el error para que no afecte el registro del can perdido
  }
}
```

**Python:**

```python
from firebase_admin import messaging

def enviar_notificacion_can_perdido(datos_can_perdido):
    try:
        # 1. Obtener todos los tokens activos
        tokens_query = db.query(
            "SELECT token FROM device_tokens WHERE activo = TRUE AND plataforma = %s",
            ('android',)
        )
        tokens = [row['token'] for row in tokens_query]
        
        if not tokens:
            print('No hay tokens registrados')
            return
        
        # 2. Preparar mensaje
        distrito = datos_can_perdido.get('distrito') or datos_can_perdido.get('lugarPerdida') or 'Ubicación no especificada'
        nombre = datos_can_perdido.get('nombre') or 'Sin nombre'
        
        message = messaging.MulticastMessage(
            notification=messaging.Notification(
                title='Can perdido',
                body=f'Distrito: {distrito}\nNombre del can: {nombre}'
            ),
            data={
                'type': 'lost_dog',
                'nombre': nombre,
                'distrito': distrito,
                'lugarPerdida': datos_can_perdido.get('lugarPerdida', ''),
                'raza': datos_can_perdido.get('raza', ''),
                'color': datos_can_perdido.get('color', ''),
                'fechaPerdida': datos_can_perdido.get('fechaPerdida', '')
            },
            tokens=tokens
        )
        
        # 3. Enviar
        response = messaging.send_multicast(message)
        print(f'✅ Enviadas: {response.success_count}, ❌ Fallidas: {response.failure_count}')
        
        # 4. Manejar tokens inválidos
        if response.failure_count > 0:
            failed_tokens = []
            for idx, resp in enumerate(response.responses):
                if not resp.success:
                    failed_tokens.append(tokens[idx])
                    print(f'❌ Error en token {idx}: {resp.exception}')
            
            if failed_tokens:
                db.query(
                    "UPDATE device_tokens SET activo = FALSE WHERE token IN %s",
                    (tuple(failed_tokens),)
                )
                
    except Exception as e:
        print(f'❌ Error al enviar notificaciones: {e}')
```

---

### Paso 3: Integrar en el Endpoint de Registrar Can Perdido

**Node.js/Express:**

```javascript
// Endpoint para registrar can perdido
app.post('/api/v1/Mascota/registrarMascotaPerdida', async (req, res) => {
  try {
    const datosCanPerdido = req.body;
    
    // 1. Validar datos (tu validación actual)
    if (!datosCanPerdido.nombre || !datosCanPerdido.lugarPerdida) {
      return res.status(400).json({ error: 'Nombre y lugar de pérdida son requeridos' });
    }
    
    // 2. Guardar en la base de datos (tu código actual)
    const resultado = await db.query(
      `INSERT INTO mascotas_perdidas 
       (nombre, especie, genero, raza, tamanio, caracter, color, pelaje, foto, 
        idUsuario, nombreUsuario, apellidoUsuario, fechaPerdida, lugarPerdida, 
        comentario, latitud, longitud, fechaCreacion) 
       VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW())`,
      [
        datosCanPerdido.nombre,
        datosCanPerdido.especie,
        datosCanPerdido.genero,
        datosCanPerdido.raza,
        datosCanPerdido.tamanio,
        datosCanPerdido.caracter,
        datosCanPerdido.color,
        datosCanPerdido.pelaje,
        datosCanPerdido.foto,
        datosCanPerdido.idUsuario,
        datosCanPerdido.nombreUsuario,
        datosCanPerdido.apellidoUsuario,
        datosCanPerdido.fechaPerdida,
        datosCanPerdido.lugarPerdida,
        datosCanPerdido.comentario,
        datosCanPerdido.latitud,
        datosCanPerdido.longitud
      ]
    );
    
    // 3. Enviar notificaciones push (NUEVO)
    // Hacerlo de forma asíncrona para no bloquear la respuesta
    enviarNotificacionCanPerdido(datosCanPerdido).catch(err => {
      console.error('Error al enviar notificaciones (no crítico):', err);
    });
    
    // 4. Responder éxito
    res.status(201).json({
      mensaje: 'Mascota perdida registrada exitosamente',
      id: resultado.insertId
    });
    
  } catch (error) {
    console.error('Error al registrar mascota perdida:', error);
    res.status(500).json({ error: 'Error interno del servidor' });
  }
});
```

---

### Paso 4: (Opcional) Agregar Campo `distrito` al Request

Si quieres que la notificación muestre el distrito específico, puedes:

#### Opción A: Agregar campo `distrito` al request (Recomendado)

**Modificar el modelo/schema:**
```sql
ALTER TABLE mascotas_perdidas ADD COLUMN distrito VARCHAR(100);
```

**Modificar el endpoint para aceptar distrito:**
```javascript
// En el INSERT, agregar distrito
await db.query(
  `INSERT INTO mascotas_perdidas (..., distrito, ...) VALUES (..., ?, ...)`,
  [..., datosCanPerdido.distrito, ...]
);
```

**Actualizar el código Android:**
- Agregar campo `distrito` a `RegisterCanPerdidoRequest`
- Enviarlo desde el formulario

#### Opción B: Geocodificación Inversa

Si tienes latitud/longitud, puedes obtener el distrito usando geocodificación inversa:

**Node.js (usando Google Maps Geocoding API):**
```javascript
const axios = require('axios');

async function obtenerDistritoDesdeCoordenadas(lat, lng) {
  try {
    const response = await axios.get(
      `https://maps.googleapis.com/maps/api/geocode/json?latlng=${lat},${lng}&key=TU_API_KEY`
    );
    
    const results = response.data.results;
    if (results && results.length > 0) {
      // Buscar componente "administrative_area_level_2" o "sublocality"
      for (const component of results[0].address_components) {
        if (component.types.includes('administrative_area_level_2') || 
            component.types.includes('sublocality')) {
          return component.long_name;
        }
      }
    }
    return null;
  } catch (error) {
    console.error('Error en geocodificación:', error);
    return null;
  }
}

// Usar en enviarNotificacionCanPerdido
if (datosCanPerdido.latitud && datosCanPerdido.longitud) {
  const distrito = await obtenerDistritoDesdeCoordenadas(
    datosCanPerdido.latitud,
    datosCanPerdido.longitud
  );
  // Usar distrito en la notificación
}
```

---

## ✅ Checklist de Implementación

- [ ] Firebase Admin SDK instalado
- [ ] Credenciales de Firebase descargadas y configuradas
- [ ] Firebase Admin SDK inicializado en el backend
- [ ] Función `enviarNotificacionCanPerdido` implementada
- [ ] Integrada en el endpoint `/api/v1/Mascota/registrarMascotaPerdida`
- [ ] Tabla `device_tokens` existe y tiene tokens registrados
- [ ] Probar registro de can perdido
- [ ] Verificar que las notificaciones lleguen a los dispositivos

---

## 🧪 Pruebas

### 1. Verificar que hay tokens en la BD

```sql
SELECT COUNT(*) FROM device_tokens WHERE activo = TRUE AND plataforma = 'android';
```

Debe haber al menos 1 token registrado.

### 2. Registrar un can perdido

1. Desde la app, registra un can perdido
2. Revisa los logs del backend
3. Debe aparecer: "✅ Notificaciones enviadas: X exitosas"
4. La notificación debe llegar a todos los dispositivos

### 3. Verificar en los dispositivos

- La notificación debe aparecer en la barra de notificaciones
- Título: "Can perdido"
- Mensaje: "Distrito: [distrito]\nNombre del can: [nombre]"

---

## 🎯 Resumen

1. **Configurar Firebase Admin SDK** en el backend
2. **Crear función** para enviar notificaciones a todos los tokens
3. **Integrar** en el endpoint de registrar can perdido
4. **Formato:** Título "Can perdido", Mensaje con distrito y nombre
5. **Probar** que las notificaciones lleguen a todos los dispositivos

¡Listo! 🚀

