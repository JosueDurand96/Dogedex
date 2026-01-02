# 🔴 Problema: Notificaciones No Llegan al Registrar Can Perdido

## Situación Actual

✅ **Funciona:**
- El registro de mascota perdida se guarda correctamente en el backend
- Los tokens FCM se están registrando (aunque puede haber errores, pero algunos tokens deben estar guardados)

❌ **No funciona:**
- Las notificaciones push NO se envían cuando se registra una mascota perdida
- No llegan notificaciones ni al dispositivo que envía ni a otros dispositivos

## 🔍 Causa del Problema

**El backend NO tiene implementado el envío de notificaciones push** cuando se registra una mascota perdida.

El endpoint `/api/v1/Mascota/registrarMascotaPerdida` solo está guardando la información en la base de datos, pero **NO está enviando notificaciones** a los dispositivos.

---

## ✅ Solución: Implementar Envío de Notificaciones en el Backend

Necesitas agregar código en el backend para enviar notificaciones después de registrar la mascota perdida.

### Paso 1: Verificar que hay Tokens Registrados

Primero, verifica que hay tokens en la base de datos:

```sql
SELECT COUNT(*) FROM device_tokens WHERE activo = TRUE AND plataforma = 'android';
```

Si el resultado es 0, entonces no hay tokens registrados y necesitas primero solucionar el endpoint de registrar tokens.

Si hay tokens (resultado > 0), continúa al Paso 2.

### Paso 2: Implementar Envío de Notificaciones

Necesitas modificar el endpoint `/api/v1/Mascota/registrarMascotaPerdida` para que después de guardar la mascota perdida, envíe notificaciones.

**Código de ejemplo (Node.js/Express):**

```javascript
const admin = require('firebase-admin');

// Asegúrate de que Firebase Admin SDK esté inicializado
// (ver IMPLEMENTACION_NOTIFICACIONES_BACKEND.md)

app.post('/api/v1/Mascota/registrarMascotaPerdida', async (req, res) => {
  try {
    const datosCanPerdido = req.body;
    
    // 1. Guardar en la base de datos (tu código actual)
    const resultado = await db.query(
      `INSERT INTO mascotas_perdidas (...) VALUES (...)`,
      [...]
    );
    
    // 2. ENVIAR NOTIFICACIONES (NUEVO - AGREGAR ESTO)
    await enviarNotificacionesCanPerdido(datosCanPerdido);
    
    // 3. Responder éxito
    res.status(201).json({
      mensaje: 'Mascota perdida registrada exitosamente',
      id: resultado.insertId
    });
    
  } catch (error) {
    console.error('Error al registrar mascota perdida:', error);
    res.status(500).json({ error: 'Error interno del servidor' });
  }
});

// Función para enviar notificaciones
async function enviarNotificacionesCanPerdido(datosCanPerdido) {
  try {
    // 1. Obtener TODOS los tokens activos
    const tokens = await db.query(
      'SELECT token FROM device_tokens WHERE activo = TRUE AND plataforma = ?',
      ['android']
    );
    
    if (tokens.length === 0) {
      console.log('⚠️ No hay tokens registrados para enviar notificaciones');
      return;
    }
    
    // 2. Preparar el mensaje
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
      tokens: tokens.map(row => row.token)
    };
    
    // 3. Enviar notificaciones
    const response = await admin.messaging().sendMulticast(message);
    
    console.log(`✅ Notificaciones enviadas: ${response.successCount} exitosas, ${response.failureCount} fallidas`);
    
    // 4. Manejar tokens inválidos (opcional pero recomendado)
    if (response.failureCount > 0) {
      const failedTokens = [];
      response.responses.forEach((resp, idx) => {
        if (!resp.success) {
          failedTokens.push(tokens[idx].token);
          console.error(`❌ Error en token: ${resp.error?.message}`);
        }
      });
      
      if (failedTokens.length > 0) {
        await db.query(
          'UPDATE device_tokens SET activo = FALSE WHERE token IN (?)',
          [failedTokens]
        );
      }
    }
    
  } catch (error) {
    console.error('❌ Error al enviar notificaciones:', error);
    // No lanzar el error para que no afecte el registro del can perdido
  }
}
```

**Código de ejemplo (Python):**

```python
from firebase_admin import messaging

def enviar_notificaciones_can_perdido(datos_can_perdido):
    try:
        # 1. Obtener todos los tokens activos
        tokens_query = db.query(
            "SELECT token FROM device_tokens WHERE activo = TRUE AND plataforma = %s",
            ('android',)
        )
        tokens = [row['token'] for row in tokens_query]
        
        if not tokens:
            print('⚠️ No hay tokens registrados')
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
        
    except Exception as e:
        print(f'❌ Error al enviar notificaciones: {e}')

# En tu endpoint de registrar mascota perdida:
@app.route('/api/v1/Mascota/registrarMascotaPerdida', methods=['POST'])
def registrar_mascota_perdida():
    try:
        datos_can_perdido = request.json
        
        # 1. Guardar en BD (tu código actual)
        # ...
        
        # 2. ENVIAR NOTIFICACIONES (NUEVO)
        enviar_notificaciones_can_perdido(datos_can_perdido)
        
        # 3. Responder
        return jsonify({'mensaje': 'Mascota perdida registrada exitosamente'}), 201
        
    except Exception as e:
        return jsonify({'error': 'Error interno del servidor'}), 500
```

---

## 📋 Checklist de Implementación

- [ ] **Firebase Admin SDK instalado** en el backend
- [ ] **Credenciales de Firebase** descargadas y configuradas
- [ ] **Firebase Admin SDK inicializado** correctamente
- [ ] **Función `enviarNotificacionesCanPerdido`** implementada
- [ ] **Integrada en el endpoint** `/api/v1/Mascota/registrarMascotaPerdida`
- [ ] **Tokens en la BD** (verificar que hay tokens registrados)
- [ ] **Probar** registro de can perdido
- [ ] **Verificar logs** del backend para ver si se envían notificaciones
- [ ] **Verificar** que las notificaciones lleguen a los dispositivos

---

## 🔍 Verificaciones Importantes

### 1. Verificar que hay Tokens en la BD

```sql
SELECT COUNT(*) FROM device_tokens WHERE activo = TRUE AND plataforma = 'android';
```

**Si es 0:** Necesitas primero solucionar el registro de tokens.

**Si es > 0:** Puedes continuar con la implementación.

### 2. Verificar que Firebase Admin SDK está Configurado

Asegúrate de que:
- Firebase Admin SDK está instalado
- Las credenciales están configuradas
- Firebase Admin SDK está inicializado

### 3. Verificar Logs del Backend

Después de implementar, cuando registres un can perdido, revisa los logs del backend:

```
✅ Notificaciones enviadas: X exitosas, Y fallidas
```

Si ves este log, las notificaciones se están enviando.

Si ves errores, revisa:
- Credenciales de Firebase
- Inicialización de Firebase Admin SDK
- Formato del mensaje

---

## 🧪 Prueba Paso a Paso

1. **Verificar tokens en BD:**
   ```sql
   SELECT token FROM device_tokens WHERE activo = TRUE LIMIT 5;
   ```

2. **Implementar función de envío de notificaciones** (código de arriba)

3. **Integrar en el endpoint** de registrar mascota perdida

4. **Registrar un can perdido** desde la app

5. **Revisar logs del backend:**
   - Debe aparecer: "✅ Notificaciones enviadas: X exitosas"
   - Si hay errores, revisarlos

6. **Verificar en dispositivos:**
   - La notificación debe llegar a todos los dispositivos
   - Título: "Can perdido"
   - Mensaje: "Distrito: [distrito]\nNombre del can: [nombre]"

---

## 📚 Documentación Completa

Para más detalles sobre cómo configurar Firebase Admin SDK y la implementación completa, revisa:

- **`IMPLEMENTACION_NOTIFICACIONES_BACKEND.md`** - Guía completa de implementación
- **`SOLUCION_BACKEND_NOTIFICACIONES.md`** - Configuración del endpoint de tokens

---

## 🎯 Resumen

**Problema:** Las notificaciones no se envían porque el backend no tiene código para enviarlas.

**Solución:** Agregar código en el backend para:
1. Obtener todos los tokens de la BD
2. Enviar notificaciones usando Firebase Admin SDK
3. Integrar en el endpoint de registrar mascota perdida

**Próximo paso:** Implementar el código de ejemplo de arriba en tu backend. 🚀

