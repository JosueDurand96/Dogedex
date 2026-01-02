# 🔍 Diagnóstico: Error al Registrar Token

## Problema Actual

El endpoint `/api/v1/DeviceToken/registrarToken` ya existe (ya no da 404), pero está devolviendo un error HTTP.

**Logs actuales:**
```
Error al registrar token: 2131886231
```

Este código es un Resource ID de Android (`R.string.error_know`), lo que significa que el backend está devolviendo un error HTTP que no es 401.

---

## ✅ Mejora Implementada

He mejorado el logging para ver exactamente qué error está devolviendo el backend. Ahora verás en Logcat:

```
E/makeNetworkCall: HTTP Error: Code=[código HTTP], Body=[mensaje del servidor]
E/FCMTokenManager: Error al registrar token: [resource id]
E/FCMTokenManager: Request enviado - Token: [token], idUsuario: [id], plataforma: android
```

---

## 🔍 Pasos para Diagnosticar

### Paso 1: Revisar Logs Mejorados

1. **Ejecuta la app** nuevamente
2. **Inicia sesión** (para que se registre el token)
3. **Filtra Logcat por**: `makeNetworkCall` o `FCMTokenManager`
4. **Busca estas líneas**:
   ```
   E/makeNetworkCall: HTTP Error: Code=XXX, Body=...
   ```

### Paso 2: Interpretar el Código HTTP

El código HTTP te dirá qué está pasando:

#### **400 Bad Request**
- El backend está rechazando la petición
- **Posibles causas:**
  - El formato del JSON no es correcto
  - Faltan campos requeridos
  - Los tipos de datos no coinciden

#### **500 Internal Server Error**
- Error en el servidor
- **Posibles causas:**
  - Error en la base de datos
  - Error en la lógica del backend
  - Falta configuración

#### **422 Unprocessable Entity**
- El formato es correcto pero hay errores de validación
- **Posibles causas:**
  - Validación fallida en el backend
  - Restricciones de base de datos no cumplidas

#### **404 Not Found** (ya no debería aparecer)
- El endpoint no existe

---

## 🛠️ Verificaciones en el Backend

### 1. Verificar Estructura del Request

El backend debe recibir este JSON:

```json
{
  "token": "c628WBsGQ9SnuPizracm5c:APA91bF...",
  "idUsuario": 123,  // Puede ser null
  "plataforma": "android"
}
```

**Verificar:**
- ✅ El endpoint acepta `POST /api/v1/DeviceToken/registrarToken`
- ✅ El Content-Type es `application/json`
- ✅ Los nombres de los campos coinciden exactamente: `token`, `idUsuario`, `plataforma`

### 2. Verificar Manejo de `idUsuario` NULL

Si el usuario no está logueado, `idUsuario` puede ser `null`. El backend debe:
- ✅ Aceptar `idUsuario: null`
- ✅ O permitir que el campo no venga en el JSON

### 3. Verificar Respuesta del Backend

El backend debe responder con:

**Éxito (200 OK):**
```json
{
  "mensaje": "Token registrado exitosamente"
}
```

**Error:**
El backend debería devolver un código HTTP apropiado (400, 500, etc.) con un mensaje descriptivo.

### 4. Verificar Base de Datos

**Tabla requerida:**
```sql
CREATE TABLE device_tokens (
  id INT PRIMARY KEY AUTO_INCREMENT,
  token VARCHAR(255) NOT NULL,
  idUsuario BIGINT NULL,  -- Puede ser NULL
  plataforma VARCHAR(50) NOT NULL,
  fechaCreacion DATETIME NOT NULL,
  fechaActualizacion DATETIME NOT NULL,
  activo BOOLEAN DEFAULT TRUE
);
```

**Verificar:**
- ✅ La tabla existe
- ✅ Los tipos de datos coinciden
- ✅ El campo `idUsuario` permite NULL
- ✅ El campo `token` tiene suficiente tamaño (VARCHAR 255 o más)

### 5. Verificar Logs del Backend

Revisa los logs del servidor cuando se hace la petición:
- ¿Qué error específico está lanzando?
- ¿Hay excepciones en la base de datos?
- ¿Hay errores de validación?

---

## 🔧 Soluciones Comunes

### Problema: Error 400 - Bad Request

**Causa:** El formato del request no coincide con lo que espera el backend.

**Solución:**
1. Verificar que los nombres de los campos sean exactos: `token`, `idUsuario`, `plataforma`
2. Verificar que `plataforma` sea exactamente `"android"` (minúsculas)
3. Verificar que `idUsuario` pueda ser `null` o `number`

**Ejemplo de código backend (Node.js):**
```javascript
app.post('/api/v1/DeviceToken/registrarToken', async (req, res) => {
  const { token, idUsuario, plataforma } = req.body;
  
  // Validar campos requeridos
  if (!token || !plataforma) {
    return res.status(400).json({ 
      error: 'Token y plataforma son requeridos' 
    });
  }
  
  // ... resto del código
});
```

### Problema: Error 500 - Internal Server Error

**Causa:** Error en el servidor (BD, lógica, etc.)

**Solución:**
1. Revisar logs del servidor para ver el error específico
2. Verificar que la tabla `device_tokens` exista
3. Verificar que la conexión a la BD funcione
4. Verificar que no haya errores de sintaxis en el código

**Ejemplo de manejo de errores:**
```javascript
app.post('/api/v1/DeviceToken/registrarToken', async (req, res) => {
  try {
    const { token, idUsuario, plataforma } = req.body;
    
    // ... lógica ...
    
    res.status(200).json({ mensaje: 'Token registrado exitosamente' });
  } catch (error) {
    console.error('Error al registrar token:', error);
    res.status(500).json({ error: 'Error interno del servidor' });
  }
});
```

### Problema: Error de Base de Datos

**Causa:** Error al insertar/actualizar en la BD

**Solución:**
1. Verificar que la tabla exista
2. Verificar tipos de datos
3. Verificar que `idUsuario` permita NULL si es necesario
4. Verificar índices y restricciones

---

## 📋 Checklist para el Backend

- [ ] Endpoint `POST /api/v1/DeviceToken/registrarToken` existe
- [ ] Acepta Content-Type: `application/json`
- [ ] Campos esperados: `token`, `idUsuario` (opcional), `plataforma`
- [ ] Maneja `idUsuario` como NULL o número
- [ ] Tabla `device_tokens` existe en la BD
- [ ] La tabla tiene los campos correctos
- [ ] `idUsuario` permite NULL en la BD
- [ ] Respuesta de éxito: 200 OK con JSON
- [ ] Manejo de errores implementado
- [ ] Logs del servidor muestran información útil

---

## 🧪 Prueba desde Postman/Thunder Client

Para probar el endpoint directamente:

**Request:**
```
POST https://dogedex-backend-tesis-2025.onrender.com/api/v1/DeviceToken/registrarToken
Content-Type: application/json

{
  "token": "c628WBsGQ9SnuPizracm5c:APA91bF...",
  "idUsuario": null,
  "plataforma": "android"
}
```

**O con idUsuario:**
```json
{
  "token": "c628WBsGQ9SnuPizracm5c:APA91bF...",
  "idUsuario": 123,
  "plataforma": "android"
}
```

**Respuesta esperada (éxito):**
```json
{
  "mensaje": "Token registrado exitosamente"
}
```

---

## 🎯 Próximos Pasos

1. **Ejecuta la app** y revisa los logs mejorados
2. **Copia el código HTTP y el mensaje** que aparece en los logs
3. **Revisa el backend** según el código HTTP
4. **Prueba el endpoint** desde Postman/Thunder Client
5. **Comparte el código HTTP y mensaje** para diagnóstico más específico

Con los logs mejorados, ahora podrás ver exactamente qué error está devolviendo el backend. 🔍


