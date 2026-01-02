# 🧪 Prueba Rápida del Endpoint de Token

## Prueba Directa del Endpoint

Prueba el endpoint directamente desde Postman/Thunder Client para ver el error exacto:

### Request

```
POST https://dogedex-backend-tesis-2025.onrender.com/api/v1/DeviceToken/registrarToken
Content-Type: application/json

{
  "token": "c628WBsGQ9SnuPizracm5c:APA91bFlABdubQ-7S6UQQRrvnr_WBsphjZRUDIKfUVTTl9lo1y8yN09kgJBlrajg3HtxZnb_7dzCHZ9IrqV6bu5C1hhIlwJiuuplWW9B7kAfRMOSUPeia58",
  "idUsuario": 197415377947142,
  "plataforma": "android"
}
```

### Respuestas Posibles

**✅ Éxito (200 OK):**
```json
{
  "mensaje": "Token registrado exitosamente"
}
```

**❌ Error 400 (Bad Request):**
```json
{
  "error": "Token y plataforma son requeridos"
}
```
→ El formato del request no es correcto

**❌ Error 404 (Not Found):**
```json
{
  "error": "Not Found"
}
```
→ El endpoint no existe

**❌ Error 500 (Internal Server Error):**
```json
{
  "error": "Error interno del servidor"
}
```
→ Error en el servidor (BD, código, etc.)

**❌ Error 422 (Unprocessable Entity):**
```json
{
  "error": "Validation failed"
}
```
→ Error de validación

---

## ⚠️ Nota Importante: ID Usuario Muy Grande

El `idUsuario` que estás enviando es: `197415377947142`

Este número es muy grande. Si la base de datos usa `INT` en lugar de `BIGINT`, causará un error.

**Verificar en la BD:**
```sql
DESCRIBE device_tokens;
```

Si `idUsuario` es `INT`, cambiarlo a `BIGINT`:
```sql
ALTER TABLE device_tokens MODIFY COLUMN idUsuario BIGINT;
```

---

## Comparte el Resultado

Después de probar desde Postman, comparte:
1. El código HTTP (200, 400, 404, 500, etc.)
2. El mensaje de error (si hay)
3. El cuerpo de la respuesta

Con esa información podré ayudarte a solucionarlo específicamente. 🔍


