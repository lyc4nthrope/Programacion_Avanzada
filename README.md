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

## 🔑 Obtener Credenciales

Antes de configurar el proyecto, necesitas obtener las siguientes credenciales:

### 1. 📧 Gmail App Password

Para enviar emails de recuperación de contraseña, necesitas una contraseña de aplicación de Gmail.

**Pasos:**

1. Inicia sesión en tu cuenta de Gmail
2. Ve a: https://myaccount.google.com/apppasswords
3. Si no ves la opción, primero activa la verificación en dos pasos:
    - Ve a: https://myaccount.google.com/security
    - En "Verificación en dos pasos", haz clic en "Activar"
4. Regresa a "Contraseñas de aplicaciones"
5. En "Seleccionar app", elige "Correo"
6. En "Seleccionar dispositivo", elige "Otro (nombre personalizado)"
7. Escribe: "Accommodation Platform"
8. Haz clic en "Generar"
9. **Copia la contraseña de 16 caracteres** (aparece en bloques de 4)

**Ejemplo:**
```
abcd efgh ijkl mnop
```

**⚠️ Importante:**
- NO uses tu contraseña normal de Gmail
- Guarda esta contraseña en un lugar seguro
- Esta contraseña solo se muestra una vez

---

### 2. ☁️ Cloudinary (Gestión de Imágenes)

Cloudinary te permite subir y gestionar imágenes en la nube.

**Pasos:**

1. Ve a: https://cloudinary.com
2. Haz clic en "Sign Up" (Registrarse)
3. Completa el registro (puedes usar tu email de Gmail)
4. Verifica tu email
5. Una vez dentro, ve al **Dashboard**
6. Encontrarás tus credenciales en la sección "Product Environment Credentials":
```
Cloud Name: tu_cloud_name
API Key: 123456789012345
API Secret: abcdefghijklmnopqrstuvwxyz
```

**Dónde encontrar tus credenciales:**
- **Dashboard** → Parte superior → "Product Environment Credentials"

**Límites del plan gratuito:**
- 25 GB de almacenamiento
- 25 GB de ancho de banda mensual
- ✅ Suficiente para desarrollo y pruebas

---

### 3. 🔐 JWT Secret Key

Necesitas generar una clave secreta segura para firmar los tokens JWT.

**Opción 1: Generar con OpenSSL (Linux/Mac/Git Bash)**
```bash
openssl rand -base64 32
```

**Opción 2: Generar online**
- Ve a: https://generate-secret.vercel.app/32
- Copia la clave generada

**Opción 3: Crear una frase segura manualmente**
```
MiClaveSecretaSuperSeguraParaJWT2025ConMasDe32Caracteres
```

**⚠️ Requisitos:**
- **Mínimo 32 caracteres** (256 bits)
- Usa letras, números y caracteres especiales
- NUNCA compartas esta clave públicamente

---

### 4. 🗄️ MariaDB

Si no tienes MariaDB instalado:

**Windows:**
1. Descarga desde: https://mariadb.org/download/
2. Ejecuta el instalador
3. Durante la instalación:
    - Configura una contraseña para el usuario `root`
    - **Anota esta contraseña**, la necesitarás en `application.properties`

**Mac (con Homebrew):**
```bash
brew install mariadb
brew services start mariadb
mysql_secure_installation
```

**Linux (Ubuntu/Debian):**
```bash
sudo apt update
sudo apt install mariadb-server
sudo mysql_secure_installation
```

**Verificar instalación:**
```bash
mysql --version
# Debe mostrar: mysql  Ver 15.1 Distrib 10.x.x-MariaDB...
```

---

## 🔧 Configurar application.properties

Una vez tengas todas las credenciales, crea el archivo de configuración:

### Paso 1: Copiar plantilla
```bash
cd src/main/resources
cp application.properties.example application.properties
```

### Paso 2: Editar con tus credenciales

Abre `application.properties` y reemplaza los valores:
```properties
# Base de datos
spring.datasource.password=TU_PASSWORD_DE_MARIADB

# JWT
jwt.secret=TU_CLAVE_JWT_DE_32_CARACTERES

# Cloudinary
cloudinary.cloud-name=tu_cloud_name
cloudinary.api-key=123456789012345
cloudinary.api-secret=abcdefghijklmnopqrstuvwxyz

# Email
mail.username=tu_email@gmail.com
mail.password=abcd efgh ijkl mnop
```

### Paso 3: Verificar configuración

**Checklist de credenciales:**
- [ ] Contraseña de MariaDB configurada
- [ ] Clave JWT de al menos 32 caracteres
- [ ] Cloud Name de Cloudinary
- [ ] API Key de Cloudinary
- [ ] API Secret de Cloudinary
- [ ] Email de Gmail
- [ ] App Password de Gmail (16 caracteres)

---

## 🧪 Probar Configuración

### 1. Probar Base de Datos
```bash
mysql -u root -p
# Ingresa tu contraseña

CREATE DATABASE accommodation_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
SHOW DATABASES;
EXIT;
```

### 2. Probar Cloudinary
```bash
# Ejecutar la aplicación y probar:
POST http://localhost:8080/api/images
# Con un archivo de imagen
```

### 3. Probar Email
```bash
# Ejecutar y probar recuperación de contraseña:
POST http://localhost:8080/api/password-reset/request
Content-Type: application/json

{
  "email": "tu_email@gmail.com"
}
```
---

## 🔒 Seguridad

### ⚠️ NUNCA hagas esto:

❌ Subir `application.properties` a Git  
❌ Compartir tus credenciales en capturas de pantalla  
❌ Hardcodear credenciales en el código  
❌ Usar contraseñas débiles

### ✅ Buenas prácticas:

✅ Mantén `application.properties` en `.gitignore`  
✅ Usa variables de entorno en producción  
✅ Comparte solo `application.properties.example`  
✅ Rota tus credenciales periódicamente  
✅ Usa diferentes credenciales para desarrollo y producción

---

## 🆘 Solución de Problemas

### Error: "API key not found"
**Causa:** Credenciales de Cloudinary incorrectas  
**Solución:** Verifica que copiaste correctamente las credenciales del Dashboard

### Error: "Authentication failed"
**Causa:** App Password de Gmail incorrecta  
**Solución:**
- Verifica que activaste la verificación en dos pasos
- Genera una nueva App Password
- Copia sin espacios: `abcdefghijklmnop`

### Error: "JWT key must be at least 256 bits"
**Causa:** Clave JWT muy corta  
**Solución:** Genera una clave de al menos 32 caracteres

### Error: "Access denied for user 'root'@'localhost'"
**Causa:** Contraseña de MariaDB incorrecta  
**Solución:**
```bash
mysql -u root -p
# Ingresa la contraseña correcta
```

---

## 📚 Recursos Adicionales

- [Documentación de Cloudinary](https://cloudinary.com/documentation)
- [Gmail App Passwords](https://support.google.com/accounts/answer/185833)
- [MariaDB Documentation](https://mariadb.org/documentation/)
- [JWT.io - Debugger](https://jwt.io)


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

¿Problemas? Contacta a: cristhiane.osorior@uqvirtual.edu.co