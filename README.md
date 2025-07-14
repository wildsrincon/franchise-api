# Franchise API

API reactiva para la gestión de franquicias, sucursales y productos usando Spring Boot, MongoDB y Docker.

## 🚀 Despliegue rápido con Docker

### 1. Clona el repositorio
```bash
git clone https://github.com/wildsrincon/franchise-api.git
cd franchise-api
```

### 2. Crea el archivo `.env`
Copia este contenido en un archivo `.env` en la raíz del proyecto:
```env
MONGODB_URI=mongodb://mongodb:27017/franchise_db
```

### 3. Construye y levanta los servicios
```bash
docker compose up --build
```
Esto levantará:
- MongoDB en el puerto 27017
- La API en el puerto 8080

### 4. Accede a la API
- Swagger UI: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) o [http://localhost:8080/swagger-ui](http://localhost:8080/swagger-ui)
- Endpoints disponibles en `/api/franchises` (ver sección Endpoints principales)

### 5. Detén los servicios
```bash
docker compose down
```

---

## ⚙️ Variables de entorno
- `MONGODB_URI`: URI de conexión a MongoDB. Se define en el archivo `.env` y es usada por la app en Docker.

---

## 🧑‍💻 Para desarrollo local (sin Docker)
1. Instala Java 17 y Maven.
2. Instala y ejecuta MongoDB localmente (puerto 27017 por defecto).
3. Ejecuta:
   ```bash
   mvn spring-boot:run
   ```
4. La API estará disponible en [http://localhost:8080](http://localhost:8080)

---

## 📚 Endpoints principales
- `POST   /api/franchises` — Crear franquicia
- `GET    /api/franchises` — Listar franquicias
- `POST   /api/franchises/{franchiseId}/branches` — Agregar sucursal
- `POST   /api/franchises/{franchiseId}/branches/{branchId}/products` — Agregar producto
- `GET    /api/franchises/{franchiseId}/top-stock-products` — Producto con más stock por sucursal

Consulta la documentación Swagger para ver todos los endpoints y sus detalles.

---

## 📝 Notas
- La URI de MongoDB **no está expuesta** en los archivos de configuración, solo en `.env`.
- El sistema asigna automáticamente IDs únicos a sucursales y productos si no se envían en la petición.
- Puedes modificar el archivo `.env` para conectar a una base de datos diferente.

---

## 🛠️ Comandos útiles
- Construir el jar manualmente:
  ```bash
  ./mvnw clean package -DskipTests
  ```
- Ejecutar tests:
  ```bash
  ./mvnw test
  ```

---

## 🧑‍💼 Autor
- Wilds Rincon
- [github.com/wildsrincon/franchise-api](https://github.com/wildsrincon/franchise-api)
