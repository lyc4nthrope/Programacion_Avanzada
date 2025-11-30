# 🏠 Accommodation Platform - Backend

Sistema backend completo para plataforma de alojamientos tipo Airbnb, desarrollado con Spring Boot 3.5.5 y Java 21.

## 📋 Tabla de Contenidos

- [Características](#-características)
- [Tecnologías](#-tecnologías)
- [Requisitos Previos](#-requisitos-previos)
- [Instalación](#-instalación)
- [Configuración](#-configuración)
- [Estructura del Proyecto](#-estructura-del-proyecto)
- [API Endpoints](#-api-endpoints)
- [Pruebas](#-pruebas)
- [Despliegue](#-despliegue)

---

## 🚀 Características

### Gestión de Usuarios
- ✅ Registro y autenticación
- ✅ Perfiles de usuario (Guest, Host, Admin)
- ✅ Activar/Desactivar usuarios
- ✅ Soft delete
- ✅ Recuperación de contraseña con código temporal

### Gestión de Alojamientos
- ✅ CRUD completo de alojamientos
- ✅ Estados: ACTIVE, INACTIVE, DELETED
- ✅ Filtros por ciudad, precio, estado
- ✅ Galería de fotos con Cloudinary
- ✅ Sistema de calificaciones

### Sistema de Reservas
- ✅ Creación y gestión de reservas
- ✅ Estados: PENDING, CONFIRMED, CANCELLED, COMPLETED
- ✅ Validación de disponibilidad
- ✅ Cálculo automático de precios

### Pagos
- ✅ Registro de pagos
- ✅ Estados: PENDING, COMPLETED, FAILED, REFUNDED
- ✅ Múltiples métodos de pago
- ✅ Reembolsos

### Sistema de Reseñas
- ✅ Reseñas vinculadas a reservas
- ✅ Calificaciones de 1 a 5 estrellas
- ✅ Respuestas del anfitrión
- ✅ Actualización automática de rating promedio

### Comunicación
- ✅ Sistema de chat en tiempo real
- ✅ Mensajes leídos/no leídos
- ✅ Chat entre usuarios

### Extras
- ✅ Sistema de favoritos
- ✅ Calendario de disponibilidad
- ✅ Perfiles de anfitrión
- ✅ Gestión de imágenes con Cloudinary
- ✅ Envío de emails (recuperación de contraseña, confirmaciones)

---

## 🛠 Tecnologías

- **Java 21** (Temurin JDK)
- **Spring Boot 3.5.5**
    - Spring Data JPA
    - Spring Web
    - Spring Validation
    - Spring Security (BCrypt)
- **MariaDB 3.3.0**
- **Hibernate**
- **Lombok** (reducción de código boilerplate)
- **MapStruct** (mapeo de DTOs)
- **Cloudinary** (gestión de imágenes)
- **Simple Java Mail** (envío de emails)
- **SpringDoc OpenAPI** (documentación Swagger)

---

## 📦 Requisitos Previos

Antes de comenzar, asegúrate de tener instalado:

1. **Java 21** (JDK Temurin recomendado)
   ```bash
   java -version
   # Debe mostrar: openjdk version "21..."
   ```

2. **MariaDB 10.x** o superior
   ```bash
   mysql --version
   ```

3. **Gradle 8.14** (incluido con el wrapper)

4. **Cuenta de Cloudinary** (para imágenes)
    - Regístrate en: https://cloudinary.com

5. **Cuenta de Email SMTP** (Gmail recomendado)

---

## 🔧 Instalación

### 1. Clonar el repositorio
```bash
git clone https://github.com/tu-usuario/accommodation-backend.git
cd accommodation-backend
```

### 2. Crear base de datos
```sql
CREATE DATABASE accommodation_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. Configurar variables de entorno

Crea o edita `src/main/resources/application.properties`:

```properties
# Server
server.port=8080

# Database
spring.datasource.url=jdbc:mariadb://localhost:3306/accommodation_db
spring.datasource.username=root
spring.datasource.password=tu_password
spring.datasource.driver-class-name=org.mariadb.jdbc.Driver

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MariaDBDialect

# Cloudinary
cloudinary.cloud-name=tu_cloud_name
cloudinary.api-key=tu_api_key
cloudinary.api-secret=tu_api_secret
cloudinary.folder=accommodation_photos

# Email
mail.host=smtp.gmail.com
mail.port=587
mail.username=tu_email@gmail.com
mail.password=tu_app_password

# Springdoc
springdoc.api-docs.path=/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
```

### 4. Compilar el proyecto
```bash
./gradlew clean build
```

### 5. Ejecutar la aplicación
```bash
./gradlew bootRun
```

La aplicación estará disponible en: **http://localhost:8080**

---

## 📁 Estructura del Proyecto

```
backend/
├── src/main/java/co/edu/uniquindio/application/
│   ├── config/              # Configuraciones
│   │   └── AsyncConfig.java
│   ├── controllers/         # Controladores REST
│   │   ├── UserController.java
│   │   ├── AccommodationController.java
│   │   ├── ReservationController.java
│   │   └── ...
│   ├── dto/                 # Data Transfer Objects
│   │   ├── create/
│   │   ├── edit/
│   │   └── ...
│   ├── exceptions/          # Excepciones personalizadas
│   ├── mappers/             # MapStruct mappers
│   ├── models/
│   │   ├── entitys/        # Entidades JPA
│   │   ├── enums/          # Enumeraciones
│   │   └── vo/             # Value Objects
│   ├── repositories/        # Repositorios JPA
│   ├── services/            # Interfaces de servicios
│   │   └── impl/           # Implementaciones
│   └── MainApplication.java
├── src/main/resources/
│   ├── application.properties
│   └── openapi.yaml
├── src/test/resources/      # Archivos .http para pruebas
└── build.gradle
```

---

## 🌐 API Endpoints

### Usuarios (`/api/users`)
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/users` | Listar todos los usuarios |
| GET | `/api/users/active` | Listar usuarios activos |
| GET | `/api/users/{id}` | Obtener usuario por ID |
| GET | `/api/users/email/{email}` | Obtener usuario por email |
| POST | `/api/users` | Crear usuario |
| PUT | `/api/users/{id}` | Actualizar usuario |
| PUT | `/api/users/{id}/activate` | Activar usuario |
| PUT | `/api/users/{id}/deactivate` | Desactivar usuario |
| DELETE | `/api/users/{id}` | Eliminar usuario |
| DELETE | `/api/users/{id}/soft` | Soft delete |

### Alojamientos (`/api/accommodations`)
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/accommodations` | Listar alojamientos |
| GET | `/api/accommodations/active` | Listar activos |
| GET | `/api/accommodations/{id}` | Obtener por ID |
| POST | `/api/accommodations` | Crear alojamiento |
| PUT | `/api/accommodations/{id}` | Actualizar |
| PUT | `/api/accommodations/{id}/activate` | Activar |
| PUT | `/api/accommodations/{id}/deactivate` | Desactivar |
| DELETE | `/api/accommodations/{id}` | Eliminar |
| DELETE | `/api/accommodations/{id}/soft` | Soft delete |

### Reservas (`/api/reservations`)
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/reservations` | Listar todas |
| GET | `/api/reservations/{id}` | Obtener por ID |
| GET | `/api/reservations/accommodation/{id}` | Por alojamiento |
| GET | `/api/reservations/guest/{id}` | Por huésped |
| POST | `/api/reservations` | Crear reserva |
| PUT | `/api/reservations/{id}` | Actualizar |
| PUT | `/api/reservations/{id}/confirm` | Confirmar |
| PUT | `/api/reservations/{id}/cancel` | Cancelar |

### Recuperación de Contraseña (`/api/password-reset`)
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/password-reset/request` | Solicitar código |
| POST | `/api/password-reset/validate` | Validar y cambiar |
| GET | `/api/password-reset/check/{code}` | Verificar código |

### Imágenes (`/api/images`)
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/images` | Subir imagen |
| DELETE | `/api/images?id={publicId}` | Eliminar imagen |

**Ver todos los endpoints:** http://localhost:8080/swagger-ui.html

---

## 🧪 Pruebas

### Usando archivos .http

El proyecto incluye archivos `.http` en `src/test/resources/` para pruebas manuales:

- `users.http` - Pruebas de usuarios
- `accommodations.http` - Pruebas de alojamientos
- `reservations.http` - Pruebas de reservas
- `password-reset.http` - Recuperación de contraseña
- `images.http` - Subida de imágenes
- Y más...

**Uso con IntelliJ IDEA:**
1. Abrir cualquier archivo `.http`
2. Click en ▶️ al lado de cada petición
3. Ver resultado en el panel inferior

### Ejemplo de prueba:

```http
### Crear usuario
POST http://localhost:8080/api/users
Content-Type: application/json

{
  "name": "Juan Pérez",
  "email": "juan@email.com",
  "password": "Password123",
  "phone": "3001234567",
  "dateBirth": "1990-05-15",
  "role": "GUEST"
}
```

---

## 🚀 Despliegue

### Producción con JAR

```bash
# 1. Compilar
./gradlew clean build

# 2. El JAR estará en:
build/libs/backend-0.0.1-SNAPSHOT.jar

# 3. Ejecutar
java -jar build/libs/backend-0.0.1-SNAPSHOT.jar
```

### Docker (Opcional)

```dockerfile
FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

```bash
docker build -t accommodation-backend .
docker run -p 8080:8080 accommodation-backend
```

---

## 📝 Notas Importantes

### Seguridad
- Las contraseñas se encriptan con BCrypt
- Los códigos de recuperación expiran en 15 minutos
- Implementar JWT para autenticación en producción

### Base de Datos
- Hibernate crea automáticamente las tablas
- Usa `spring.jpa.hibernate.ddl-auto=validate` en producción

### Emails
- Gmail requiere "App Password" (no contraseña normal)
- Activar "Acceso de apps menos seguras" o usar OAuth2

### Imágenes
- Cloudinary tiene límite gratuito de 25 GB/mes
- Las URLs son públicas

---

## 👥 Contribuidores

- Cristhian Osorio - Desarrollador Principal

---

## 📄 Licencia

Este proyecto es de uso académico para el curso de Programación Avanzada 2025-2.

---

## 🆘 Soporte

¿Problemas? Contacta a: cristhian@uniquindio.edu.co