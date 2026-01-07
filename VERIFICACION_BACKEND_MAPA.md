# 🔍 Verificación Backend - Funcionalidad de Mapa

## 📋 Resumen

Se ha agregado la funcionalidad de mapa a las pantallas de "Registrar Can" y "Registrar can agresor". El frontend ya está enviando los campos `latitud` y `longitud` en las peticiones.

## ✅ Estado del Frontend

### Campos ya implementados en el Request:

**RegisterCanRequest** (usado en ambas pantallas):
```kotlin
@SerializedName("latitud")
var latitud: Double? = null,

@SerializedName("longitud")
var longitud: Double? = null,
```

### Endpoints que envían estos campos:

1. **POST** `/api/v1/Can/registrarCan` - Registrar can normal
2. **POST** `/api/v1/Can/registrarCanAgresivo` - Registrar can agresivo

## ⚠️ Verificaciones Necesarias en el Backend

### 1. Verificar Base de Datos

**Tabla: `Can` o `Mascota`** (dependiendo de tu esquema)

Verificar si la tabla tiene las columnas:
- `latitud` (tipo: `DOUBLE` o `DECIMAL`)
- `longitud` (tipo: `DOUBLE` o `DECIMAL`)

**Si NO existen**, necesitas crear una migración:

```sql
-- Ejemplo para MySQL/MariaDB
ALTER TABLE Can 
ADD COLUMN latitud DOUBLE NULL,
ADD COLUMN longitud DOUBLE NULL;

-- Ejemplo para PostgreSQL
ALTER TABLE Can 
ADD COLUMN latitud DOUBLE PRECISION NULL,
ADD COLUMN longitud DOUBLE PRECISION NULL;
```

### 2. Verificar Modelo/DTO en el Backend

Asegúrate de que el modelo que recibe el endpoint tenga los campos:

**Ejemplo en Node.js/Express:**
```javascript
{
  nombre: String,
  fechaNacimiento: String,
  // ... otros campos
  latitud: Number,  // Opcional
  longitud: Number  // Opcional
}
```

**Ejemplo en Spring Boot (Java):**
```java
public class RegisterCanRequest {
    private String nombre;
    // ... otros campos
    private Double latitud;  // Opcional
    private Double longitud; // Opcional
}
```

**Ejemplo en Django (Python):**
```python
class RegisterCanRequest(models.Model):
    nombre = models.CharField(max_length=255)
    # ... otros campos
    latitud = models.FloatField(null=True, blank=True)  # Opcional
    longitud = models.FloatField(null=True, blank=True)  # Opcional
```

### 3. Verificar Endpoints

Los endpoints deben aceptar estos campos opcionales:

- **POST** `/api/v1/Can/registrarCan`
- **POST** `/api/v1/Can/registrarCanAgresivo`

**Ejemplo de validación (si es necesario):**
- Si `latitud` y `longitud` están presentes, validar que sean valores válidos:
  - `latitud`: entre -90 y 90
  - `longitud`: entre -180 y 180

### 4. Verificar que se guarden en la Base de Datos

Asegúrate de que cuando recibas estos campos en el endpoint, se guarden en la base de datos:

```javascript
// Ejemplo Node.js
const can = new Can({
  nombre: req.body.nombre,
  // ... otros campos
  latitud: req.body.latitud || null,
  longitud: req.body.longitud || null
});
await can.save();
```

## 🧪 Cómo Probar

### 1. Probar con Postman o similar:

**Request:**
```json
POST /api/v1/Can/registrarCan
Content-Type: application/json

{
  "nombre": "Max",
  "fechaNacimiento": "2020-01-01",
  "especie": "Perro",
  "genero": "Macho",
  "raza": "Labrador",
  "tamanio": "Mediano",
  "caracter": "Sociable",
  "color": "Negro",
  "pelaje": "Corto",
  "esterilizado": "Si",
  "distrito": "Lima",
  "modoObtencion": "Compra",
  "razonTenencia": "Compañía",
  "foto": "base64...",
  "idUsuario": 1,
  "latitud": -12.0464,
  "longitud": -77.0428
}
```

### 2. Verificar en la base de datos:

Después de registrar, verifica que los valores se guardaron:
```sql
SELECT id, nombre, latitud, longitud FROM Can WHERE id = [ID_REGISTRADO];
```

## 📝 Checklist

- [ ] Verificar que la tabla en BD tiene columnas `latitud` y `longitud`
- [ ] Si no existen, crear migración para agregarlas
- [ ] Verificar que el modelo/DTO acepta estos campos
- [ ] Verificar que los endpoints procesan estos campos
- [ ] Verificar que se guardan en la base de datos
- [ ] Probar registro con coordenadas desde la app
- [ ] Probar registro sin coordenadas (debe funcionar también)

## 🔄 Si necesitas hacer cambios

### Opción 1: Si ya tienes las columnas
✅ Solo necesitas verificar que el backend acepta y guarda estos campos.

### Opción 2: Si NO tienes las columnas
1. Crear migración de base de datos
2. Actualizar el modelo/DTO
3. Actualizar la lógica del endpoint para guardar estos campos
4. Probar

## 📞 Notas Importantes

- Los campos `latitud` y `longitud` son **opcionales** (pueden ser `null`)
- El frontend ya está enviando estos campos cuando el usuario selecciona una ubicación en el mapa
- Si el usuario no selecciona ubicación, se envía `null` y el backend debe manejarlo correctamente
- El frontend también intenta obtener la ubicación actual automáticamente si no hay una seleccionada en el mapa

