# 🔍 Diagnóstico: Error al Registrar Token (Actualizado)

## Logs Actuales

```
FCM Registration Token: c628WBsGQ9SnuPizracm5c:APA91bFlABdubQ-7S6UQQRrvnr_WBsphjZRUDIKfUVTTl9lo1y8yN09kgJBlrajg3HtxZnb_7dzCHZ9IrqV6bu5C1hhIlwJiuuplWW9B7kAfRMOSUPeia58
Error al registrar token: 2131886231
Request enviado - Token: c628WBsGQ9SnuPizracm5c:..., idUsuario: 197415377947142, plataforma: android
```

## Análisis

El token se está generando correctamente, pero el backend está devolviendo un error HTTP.

**Para ver el código HTTP exacto, busca en Logcat:**
```
Filtro: makeNetworkCall
```

Deberías ver algo como:
```
E/makeNetworkCall: HTTP Error: Code=400, Body=...
```
o
```
E/makeNetworkCall: HTTP Error: Code=500, Body=...
```

---

## 🔍 Pasos de Diagnóstico

### Paso 1: Ver el Código HTTP Exacto

1. En Android Studio, abre **Logcat**
2. Filtra por: `makeNetworkCall`
3. Ejecuta la app e inicia sesión
4. Busca la línea que dice: `HTTP Error: Code=XXX, Body=...`

**El código HTTP te dirá exactamente qué está pasando:**
- **400**: El formato del request no es correcto
- **401**: No autorizado
- **404**: El endpoint no existe
- **500**: Error en el servidor
- **422**: Error de validación

---

## 🛠️ Verificaciones en el Backend

### 1. Verificar que el Endpoint Existe

El endpoint debe ser:
```
POST /api/v1/DeviceToken/registrarToken
```

### 2. Verificar el Formato del Request

El backend debe recibir:
```json
{
  "token": "c628WBsGQ9SnuPizracm5c:APA91bF...",
  "idUsuario": 197415377947142,
  "plataforma": "android"
}
```

**Verificar:**
- ✅ Los nombres de los campos: `token`, `idUsuario`, `plataforma` (exactamente así)
- ✅ `idUsuario` es un número (Long/Number)
- ✅ `plataforma` es exactamente `"android"` (string, minúsculas)

### 3. Verificar la Tabla en la Base de Datos

```sql
-- Verificar que la tabla existe
SHOW TABLES LIKE 'device_tokens';

-- Ver estructura
DESCRIBE device_tokens;

-- Debe tener estos campos (o similares):
-- id, token, idUsuario, plataforma, fechaCreacion, fechaActualizacion, activo
```

**Importante:** El campo `idUsuario` debe aceptar números grandes (BIGINT), ya que el ID es `197415377947142` (muy grande).

### 4. Verificar Logs del Backend

Revisa los logs del servidor cuando se hace la petición:
- ¿Qué error específico está lanzando?
- ¿Hay excepciones?
- ¿Hay errores de validación?

---

## 🔧 Problemas Comunes y Soluciones

### Problema: Error 400 - Bad Request

**Causa:** El formato del JSON no coincide.

**Solución:**
```javascript
// Verificar que el backend espera estos campos exactos
app.post('/api/v1/DeviceToken/registrarToken', async (req, res) => {
  const { token, idUsuario, plataforma } = req.body;
  
  console.log('Request recibido:', { token, idUsuario, plataforma });
  
  // Validar
  if (!token || !plataforma) {
    return res.status(400).json({ error: 'Token y plataforma son requeridos' });
  }
  
  // ... resto del código
});
```

### Problema: Error 500 - Internal Server Error

**Causa:** Error en el servidor (BD, tipo de dato, etc.).

**Verificar:**
1. Tipo de dato de `idUsuario` en la BD (debe ser BIGINT)
2. Conexión a la base de datos
3. Errores de sintaxis SQL

**Solución:**
```sql
-- Si idUsuario es INT, cambiarlo a BIGINT
ALTER TABLE device_tokens MODIFY COLUMN idUsuario BIGINT;
```

### Problema: Error 422 - Unprocessable Entity

**Causa:** Validación fallida.

**Verificar:** Restricciones de la base de datos (NOT NULL, UNIQUE, etc.)

---

## 🧪 Probar el Endpoint Directamente

### Usando cURL:

```bash
curl -X POST https://dogedex-backend-tesis-2025.onrender.com/api/v1/DeviceToken/registrarToken \
  -H "Content-Type: application/json" \
  -d '{
    "token": "c628WBsGQ9SnuPizracm5c:APA91bFlABdubQ-7S6UQQRrvnr_WBsphjZRUDIKfUVTTl9lo1y8yN09kgJBlrajg3HtxZnb_7dzCHZ9IrqV6bu5C1hhIlwJiuuplWW9B7kAfRMOSUPeia58",
    "idUsuario": 197415377947142,
    "plataforma": "android"
  }'
```

### Usando Postman/Thunder Client:

**Request:**
```
POST https://dogedex-backend-tesis-2025.onrender.com/api/v1/DeviceToken/registrarToken
Content-Type: application/json

{
  "token": "c628WBsGQ9SnuPizracm5c:APA91bFlABdubQ-7S6UQQRrvnr_WBsphjZRUDIKfUVTTl9lo1y8yN09kgJBlrajg3HtxZnb_7dzCHZ9IrqV6bu5C1hhIlwJiuuplWW9B7kAfRMOSUPeia58",
  "idUsuario": 197415377947142,
  "plataforma": "android"
}
```

**Respuesta esperada (éxito):**
```json
{
  "mensaje": "Token registrado exitosamente"
}
```

**Si hay error, verás:**
- Código HTTP (400, 500, etc.)
- Mensaje de error específico

---

## 📋 Checklist de Verificación

- [ ] El endpoint `/api/v1/DeviceToken/registrarToken` existe
- [ ] Acepta POST requests
- [ ] Content-Type: `application/json`
- [ ] Campos esperados: `token`, `idUsuario`, `plataforma`
- [ ] `idUsuario` acepta números grandes (BIGINT)
- [ ] Tabla `device_tokens` existe
- [ ] La tabla tiene los campos correctos
- [ ] Logs del backend muestran información útil
- [ ] Probar desde Postman/Thunder Client funciona

---

## 🎯 Próximo Paso

**Comparte:**
1. El código HTTP que aparece en los logs (filtro: `makeNetworkCall`)
2. El mensaje de error del backend (si aparece)
3. La respuesta del endpoint cuando lo pruebas desde Postman

Con esa información podré ayudarte a solucionarlo específicamente. 🔍




