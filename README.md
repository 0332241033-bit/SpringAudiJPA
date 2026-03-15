<div align="center">

# 🔍 SpringAudiJPA

### Sistema de Auditoría JPA con Spring Boot

[![Java](https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org/projects/jdk/17/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5.5-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.x-4479A1?style=for-the-badge&logo=mysql&logoColor=white)](https://www.mysql.com/)
[![Flyway](https://img.shields.io/badge/Flyway-Migrations-CC0200?style=for-the-badge&logo=flyway&logoColor=white)](https://flywaydb.org/)
[![Lombok](https://img.shields.io/badge/Lombok-Enabled-red?style=for-the-badge&logo=lombok&logoColor=white)](https://projectlombok.org/)
[![Maven](https://img.shields.io/badge/Maven-Build-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white)](https://maven.apache.org/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg?style=for-the-badge)](LICENSE)

<p align="center">
  <strong>API REST de gestión de productos con trazabilidad completa de operaciones mediante JPA Entity Listeners.</strong>
</p>

[📋 Descripción](#-descripción) •
[🏗 Arquitectura](#-arquitectura) •
[🚀 Inicio Rápido](#-inicio-rápido) •
[📡 API Endpoints](#-api-endpoints) •
[🔎 Sistema de Auditoría](#-sistema-de-auditoría) •
[🗄 Base de Datos](#-base-de-datos) •
[🤝 Contribuir](#-contribuir)

</div>

---

## 📋 Descripción

**SpringAudiJPA** es una aplicación Spring Boot que demuestra cómo implementar un sistema de **auditoría automática** de entidades JPA. Cada vez que un producto es creado, actualizado o eliminado, el sistema registra automáticamente esa operación en una tabla de historial, sin necesidad de modificar la lógica de negocio existente.

### ✨ Características Principales

| Característica | Descripción |
|---|---|
| 🔄 **Auditoría Automática** | Registra INSERT, UPDATE y DELETE de forma transparente mediante `@EntityListeners` |
| 🛡 **No-invasiva** | La lógica de auditoría está completamente separada de la lógica de negocio |
| 📦 **API REST Completa** | Endpoints CRUD para gestión de productos |
| 🗄 **Migraciones con Flyway** | Control de versiones del esquema de base de datos |
| ♻️ **Código Limpio** | Uso de Lombok para reducir código repetitivo (boilerplate) |

---

## 🏗 Arquitectura

### Diagrama de Capas

```
┌─────────────────────────────────────────────────────────────┐
│                        Cliente HTTP                          │
│              (Postman / curl / Frontend)                     │
└──────────────────────────┬──────────────────────────────────┘
                           │  HTTP Request
                           ▼
┌─────────────────────────────────────────────────────────────┐
│                    Capa de Controlador                        │
│                   ProductController                          │
│         POST /create  │  GET /findById                       │
│         PUT /update   │  DELETE /delete                      │
└──────────────────────────┬──────────────────────────────────┘
                           │  Invoca repositorio
                           ▼
┌─────────────────────────────────────────────────────────────┐
│                    Capa de Repositorio                        │
│    productRepository (CrudRepository)                        │
│    historyRepository (CrudRepository)                        │
└──────────────────────────┬──────────────────────────────────┘
                           │  JPA / Hibernate
                           ▼
┌──────────────────────────────────────────┐
│           Capa de Entidades              │
│                                          │
│   ┌──────────────┐  ┌────────────────┐  │
│   │   product    │  │    history     │  │
│   │ ──────────── │  │ ────────────── │  │
│   │ id           │  │ id             │  │
│   │ name         │  │ name           │  │
│   │ description  │  │ operation      │  │
│   │ price        │  │ date           │  │
│   │ operation    │  │ username       │  │
│   │ dateEvent    │  └────────────────┘  │
│   └──────┬───────┘                      │
│          │ @EntityListeners              │
│          ▼                              │
│   ┌─────────────────────────┐           │
│   │   AuditEntityListener   │           │
│   │  @PrePersist  ──► INSERT │           │
│   │  @PreUpdate   ──► UPDATE │           │
│   │  @PreRemove   ──► DELETE │           │
│   └─────────────────────────┘           │
└──────────────────────────┬──────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────────┐
│                     MySQL Database                           │
│                                                             │
│    ┌─────────────────┐       ┌─────────────────────┐       │
│    │    products      │       │       history        │       │
│    │─────────────────│       │─────────────────────│       │
│    │ id (PK)         │       │ id (PK)             │       │
│    │ name            │       │ name                │       │
│    │ description     │  ──►  │ operation           │       │
│    │ price           │       │ date                │       │
│    │ operation       │       │ username            │       │
│    │ date_event      │       └─────────────────────┘       │
│    └─────────────────┘                                      │
└─────────────────────────────────────────────────────────────┘
```

### Flujo de Auditoría

```
   Petición HTTP
        │
        ▼
  ProductController
        │
        ▼
  productRepository.save() / delete()
        │
        ▼
  JPA/Hibernate detecta el ciclo de vida
        │
        ├──────► @PrePersist  ──► AuditEntityListener.prePersist()
        ├──────► @PreUpdate   ──► AuditEntityListener.preUpdate()
        └──────► @PreRemove   ──► AuditEntityListener.preRemove()
                                         │
                                         ▼
                              Crea registro en `history`
                              (name, operation, date)
```

---

## 🛠 Tecnologías Utilizadas

| Tecnología | Versión | Propósito |
|---|---|---|
| ☕ **Java** | 17 | Lenguaje de programación principal |
| 🍃 **Spring Boot** | 3.5.5 | Framework principal de la aplicación |
| 🗃 **Spring Data JPA** | Incluida en Spring Boot | ORM y acceso a datos |
| 🔧 **Hibernate** | Incluida en Spring Boot | Implementación de JPA |
| 🐬 **MySQL** | 8.x | Motor de base de datos relacional |
| 🛫 **Flyway** | Incluida en Spring Boot | Migraciones y versionado de BD |
| 🔨 **Lombok** | Incluida en Spring Boot | Reducción de código boilerplate |
| 🏗 **Maven** | 3.x | Gestión de dependencias y build |

---

## 📂 Estructura del Proyecto

```
SpringAudiJPA/
├── 📁 src/
│   ├── 📁 main/
│   │   ├── 📁 java/com/example/SpringAudiJPA/
│   │   │   ├── 📄 SpringAudiJpaApplication.java     ← Punto de entrada
│   │   │   │
│   │   │   ├── 📁 controller/
│   │   │   │   └── 📄 productController.java        ← Endpoints REST (CRUD)
│   │   │   │
│   │   │   ├── 📁 entity/
│   │   │   │   ├── 📄 product.java                  ← Entidad principal auditada
│   │   │   │   └── 📄 history.java                  ← Registro de auditoría
│   │   │   │
│   │   │   ├── 📁 listener/
│   │   │   │   └── 📄 AuditEntityListener.java      ← Interceptor JPA de ciclo de vida
│   │   │   │
│   │   │   └── 📁 repository/
│   │   │       ├── 📄 productRepository.java        ← Repositorio de productos
│   │   │       └── 📄 historyRepository.java        ← Repositorio del historial
│   │   │
│   │   └── 📁 resources/
│   │       ├── 📄 application.properties            ← Configuración de la aplicación
│   │       └── 📁 db/migration/
│   │           ├── 📄 V1__create_table.sql          ← Crea tabla products
│   │           ├── 📄 V2__create_history_table.sql  ← Crea tabla history
│   │           └── 📄 V3_update_history_table.sql   ← Añade columna username
│   │
│   └── 📁 test/
│       └── 📄 SpringAudiJpaApplicationTests.java    ← Tests de integración
│
├── 📄 pom.xml                                       ← Dependencias Maven
└── 📄 README.md                                     ← Esta documentación
```

---

## 🚀 Inicio Rápido

### ✅ Prerrequisitos

Asegúrate de tener instalado lo siguiente antes de comenzar:

- **Java 17+** → [Descargar OpenJDK 17](https://openjdk.org/projects/jdk/17/)
- **Maven 3.8+** → [Instalar Maven](https://maven.apache.org/install.html)
- **MySQL 8.x** → [Instalar MySQL](https://dev.mysql.com/downloads/mysql/)
- **Git** → [Instalar Git](https://git-scm.com/downloads)
- **Postman** _(opcional, para pruebas de API)_ → [Descargar Postman](https://www.postman.com/downloads/)

Verifica las versiones instaladas:

```bash
java -version
# java version "17.x.x" ...

mvn -version
# Apache Maven 3.x.x ...

mysql --version
# mysql  Ver 8.x.x ...
```

### 📥 Instalación

**1. Clona el repositorio:**

```bash
git clone https://github.com/0332241033-bit/SpringAudiJPA.git
cd SpringAudiJPA/SpringAudiJPA
```

**2. Crea la base de datos en MySQL:**

```sql
-- Conéctate a MySQL
mysql -u root -p

-- Crea la base de datos
CREATE DATABASE jpa_audit_db;

-- Verifica que fue creada
SHOW DATABASES;
```

**3. Configura las credenciales en `application.properties`:**

```properties
# src/main/resources/application.properties
spring.application.name=SpringAudiJPA

# ── Configuración de la Base de Datos ──────────────────────────
spring.datasource.url=jdbc:mysql://localhost:3306/jpa_audit_db
spring.datasource.username=root
spring.datasource.password=TU_CONTRASEÑA_AQUI
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# ── Configuración de Hibernate ─────────────────────────────────
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

> ⚠️ **Nota de Seguridad:** Nunca subas credenciales reales a un repositorio público. Considera usar variables de entorno o un archivo `.env` en producción.

**4. Compila y ejecuta la aplicación:**

```bash
# Desde la carpeta SpringAudiJPA/
mvn clean install
mvn spring-boot:run
```

**5. Verifica que la aplicación está corriendo:**

```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::               (v3.5.5)

...
Started SpringAudiJpaApplication in X.XXX seconds
```

La API estará disponible en: `http://localhost:8080`

> 💡 **¿Primera ejecución?** Flyway aplicará automáticamente las migraciones SQL y creará las tablas necesarias en la base de datos.

---

## 📡 API Endpoints

### Base URL

```
http://localhost:8080
```

### 📦 Productos

#### ➕ Crear Producto

```http
POST /create
Content-Type: application/json
```

**Request Body:**

```json
{
  "name": "Laptop Dell XPS 15",
  "description": "Laptop de alto rendimiento con procesador Intel i7",
  "price": 1299.99
}
```

**Respuesta Exitosa (201 Created):**

```
Product added successfully
```

**Ejemplo con cURL:**

```bash
curl -X POST http://localhost:8080/create \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Laptop Dell XPS 15",
    "description": "Laptop de alto rendimiento con procesador Intel i7",
    "price": 1299.99
  }'
```

---

#### 🔍 Buscar Producto por ID

```http
GET /findById?id={id}
```

**Parámetros de consulta:**

| Parámetro | Tipo | Descripción |
|---|---|---|
| `id` | `Integer` | ID único del producto |

**Ejemplo con cURL:**

```bash
curl -X GET "http://localhost:8080/findById?id=1"
```

**Respuesta Exitosa (200 OK):**

```json
{
  "id": 1,
  "name": "Laptop Dell XPS 15",
  "description": "Laptop de alto rendimiento con procesador Intel i7",
  "price": 1299.99,
  "operation": "INSERT",
  "dateEvent": "2024-01-15"
}
```

---

#### ✏️ Actualizar Producto

```http
PUT /update/{id}
Content-Type: application/json
```

**Parámetros de ruta:**

| Parámetro | Tipo | Descripción |
|---|---|---|
| `id` | `Integer` | ID del producto a actualizar |

**Request Body:**

```json
{
  "name": "Laptop Dell XPS 15 Updated",
  "price": 1199.99
}
```

**Respuesta Exitosa (200 OK):**

```
Product Update
```

**Ejemplo con cURL:**

```bash
curl -X PUT http://localhost:8080/update/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Laptop Dell XPS 15 Updated",
    "price": 1199.99
  }'
```

---

#### 🗑 Eliminar Producto

```http
DELETE /delete/{id}
```

**Parámetros de ruta:**

| Parámetro | Tipo | Descripción |
|---|---|---|
| `id` | `Integer` | ID del producto a eliminar |

**Respuesta Exitosa (200 OK):**

```
Product Deleted
```

**Ejemplo con cURL:**

```bash
curl -X DELETE http://localhost:8080/delete/1
```

---

### 📊 Resumen de Endpoints

| Método | Endpoint | Descripción | Código HTTP |
|---|---|---|---|
| `POST` | `/create` | Crear un nuevo producto | `201 Created` |
| `GET` | `/findById?id={id}` | Buscar producto por ID | `200 OK` |
| `PUT` | `/update/{id}` | Actualizar producto existente | `200 OK` |
| `DELETE` | `/delete/{id}` | Eliminar producto | `200 OK` |

---

## 🔎 Sistema de Auditoría

### ¿Cómo Funciona?

El sistema de auditoría utiliza el mecanismo de **JPA Entity Listeners** para interceptar automáticamente las operaciones de ciclo de vida de las entidades, sin modificar el código de negocio.

#### 1. Configuración del Listener en la Entidad

```java
// entity/product.java
@Entity
@Table(name = "products")
@EntityListeners(AuditEntityListener.class)  // ← Registra el listener
public class product {
    // ...
}
```

#### 2. El Listener de Auditoría

```java
// listener/AuditEntityListener.java
@Component
public class AuditEntityListener {

    private final historyRepository historyRepository;

    // 🟢 Se ejecuta ANTES de insertar (INSERT)
    @PrePersist
    private void prePersist(product product) {
        saveHistory(product.getName(), "INSERT");
    }

    // 🟡 Se ejecuta ANTES de actualizar (UPDATE)
    @PreUpdate
    private void preUpdate(product product) {
        saveHistory(product.getName(), "UPDATE");
    }

    // 🔴 Se ejecuta ANTES de eliminar (DELETE)
    @PreRemove
    private void preRemove(product product) {
        saveHistory(product.getName(), "DELETE");
    }

    private void saveHistory(String name, String operation) {
        history h = new history();
        h.setName(name);
        h.setDate(LocalDateTime.now());
        h.setOperation(operation);
        historyRepository.save(h);
    }
}
```

#### 3. Anotaciones de Ciclo de Vida JPA

| Anotación | Momento de Ejecución | Operación Registrada |
|---|---|---|
| `@PrePersist` | Antes de `INSERT` en BD | `"INSERT"` |
| `@PreUpdate` | Antes de `UPDATE` en BD | `"UPDATE"` |
| `@PreRemove` | Antes de `DELETE` en BD | `"DELETE"` |

#### 4. Registro en la Tabla `history`

Cada operación genera automáticamente un registro en la tabla `history`:

```sql
-- Resultado después de crear un producto
SELECT * FROM history;

+----+-------------------+-----------+---------------------+----------+
| id | name              | operation | date                | username |
+----+-------------------+-----------+---------------------+----------+
|  1 | Laptop Dell XPS 15| INSERT    | 2024-01-15 10:30:00 | NULL     |
|  2 | Laptop Dell XPS 15| UPDATE    | 2024-01-15 11:00:00 | NULL     |
|  3 | Laptop Dell XPS 15| DELETE    | 2024-01-15 11:30:00 | NULL     |
+----+-------------------+-----------+---------------------+----------+
```

---

## 🗄 Base de Datos

### Esquema

#### Tabla `products`

```sql
CREATE TABLE products (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(255),
    description TEXT,
    price       DECIMAL(15, 2),
    operation   VARCHAR(255),
    date_event  TIMESTAMP
);
```

| Columna | Tipo | Descripción |
|---|---|---|
| `id` | `INT` (PK, Auto) | Identificador único del producto |
| `name` | `VARCHAR(255)` | Nombre del producto |
| `description` | `TEXT` | Descripción detallada |
| `price` | `DECIMAL(15,2)` | Precio con 2 decimales (uso de `BigDecimal` en Java) |
| `operation` | `VARCHAR(255)` | Última operación realizada (INSERT/UPDATE) |
| `date_event` | `TIMESTAMP` | Fecha y hora del último evento |

#### Tabla `history`

```sql
CREATE TABLE history (
    id        INT AUTO_INCREMENT PRIMARY KEY,
    name      VARCHAR(255),
    operation VARCHAR(255),
    date      TIMESTAMP,
    username  VARCHAR(255)
);
```

| Columna | Tipo | Descripción |
|---|---|---|
| `id` | `INT` (PK, Auto) | Identificador único del registro |
| `name` | `VARCHAR(255)` | Nombre del producto auditado |
| `operation` | `VARCHAR(255)` | Tipo de operación: INSERT, UPDATE o DELETE |
| `date` | `TIMESTAMP` | Fecha y hora exacta del evento |
| `username` | `VARCHAR(255)` | Usuario que realizó la operación (para uso futuro) |

### 🛫 Migraciones Flyway

Flyway gestiona automáticamente las versiones del esquema de base de datos. Los scripts se ubican en `src/main/resources/db/migration/` y se ejecutan en orden al iniciar la aplicación.

| Versión | Archivo | Descripción |
|---|---|---|
| `V1` | `V1__create_table.sql` | Crea la tabla `products` |
| `V2` | `V2__create_history_table.sql` | Crea la tabla `history` |
| `V3` | `V3_update_history_table.sql` | Añade columna `username` a `history` |

> ⚠️ **Nota de Convención:** La convención de Flyway requiere doble guion bajo entre la versión y el nombre del archivo (p. ej., `V3__update_history_table.sql`). El archivo `V3_update_history_table.sql` usa un solo guion bajo, lo que puede causar que Flyway no lo detecte automáticamente. Se recomienda renombrar a `V3__update_history_table.sql` para seguir las buenas prácticas.

> 💡 **¿Cómo funciona Flyway?** Al arrancar la aplicación, Flyway revisa qué migraciones ya fueron aplicadas (en la tabla `flyway_schema_history`) y ejecuta solo las nuevas. Esto garantiza que todos los entornos (desarrollo, staging, producción) tengan el mismo esquema.

---

## 💡 Conceptos Clave

### 🔑 Convenciones de Nombres Java

```java
// ⚠️ El proyecto usa nombres en minúsculas para clases (no es la convención de Java)
public class product { ... }              // Debería ser: Product
public interface productRepository { ... } // Debería ser: ProductRepository
public class history { ... }              // Debería ser: History

// ✅ La convención estándar de Java para clases e interfaces es UpperCamelCase
// Ejemplo correcto: public class Product { ... }
```

> 💡 Renombrar las clases siguiendo **UpperCamelCase** es una mejora recomendada para seguir las convenciones de Java y mejorar la legibilidad del código.

### 🔑 ¿Por qué `BigDecimal` para precios?

```java
private BigDecimal price; // ✅ Correcto para valores monetarios
// private double price;  // ❌ Evitar: imprecisión de punto flotante
```

`BigDecimal` garantiza precisión exacta en operaciones aritméticas con dinero, evitando errores de redondeo típicos de `float` y `double`.

### 🔑 ¿Por qué `@RequiredArgsConstructor` en el Listener?

```java
@RequiredArgsConstructor(onConstructor = @__(@Lazy))
@Component
public class AuditEntityListener {
    private final historyRepository historyRepository; // Inyectado por Lombok
}
```

- **`@RequiredArgsConstructor`**: Lombok genera el constructor con todas las dependencias `final`.
- **`@Lazy`**: Previene problemas de dependencias circulares en el contexto de Spring durante la inicialización de Hibernate.

### 🔑 Separación de Responsabilidades

```
productController  ──► Solo gestiona la API REST
productRepository  ──► Solo accede a la BD
AuditEntityListener ──► Solo registra auditoría (transparente)
```

El controlador no sabe nada de la auditoría. Esto sigue el principio **Single Responsibility (SRP)** de SOLID.

---

## ⚙️ Configuración Avanzada

### Variables de Entorno (Recomendado para Producción)

En lugar de escribir credenciales directamente en `application.properties`, usa variables de entorno:

```properties
# application.properties
spring.datasource.url=${DB_URL:jdbc:mysql://localhost:3306/jpa_audit_db}
spring.datasource.username=${DB_USERNAME:root}
spring.datasource.password=${DB_PASSWORD:}
```

Luego en el sistema operativo o en tu servicio de despliegue:

```bash
export DB_URL=jdbc:mysql://mi-servidor:3306/jpa_audit_db
export DB_USERNAME=mi_usuario
export DB_PASSWORD=mi_contrasena_segura
```

### Configuración de Hibernate para Desarrollo

```properties
# Ver las consultas SQL en consola (solo para desarrollo)
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# Deshabilitar en producción para mejorar rendimiento
# spring.jpa.show-sql=false
```

---

## 🤝 Contribuir

¡Las contribuciones son bienvenidas! Sigue estos pasos:

1. **Fork** el repositorio
2. **Crea** una rama para tu feature:
   ```bash
   git checkout -b feature/nueva-funcionalidad
   ```
3. **Realiza** tus cambios siguiendo las convenciones del proyecto
4. **Ejecuta** las pruebas:
   ```bash
   mvn test
   ```
5. **Commit** con un mensaje descriptivo:
   ```bash
   git commit -m "feat: añade endpoint para listar todos los productos"
   ```
6. **Push** a tu rama:
   ```bash
   git push origin feature/nueva-funcionalidad
   ```
7. **Abre** un Pull Request describiendo los cambios realizados

### Convenciones de Commits

| Prefijo | Uso |
|---|---|
| `feat:` | Nueva funcionalidad |
| `fix:` | Corrección de bug |
| `docs:` | Cambios en documentación |
| `refactor:` | Refactorización de código |
| `test:` | Añade o modifica tests |

---

## 📚 Recursos Adicionales

- 📖 [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)
- 📖 [Spring Data JPA Reference](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- 📖 [JPA Entity Lifecycle Events](https://docs.oracle.com/javaee/7/tutorial/persistence-entitylisteners.htm)
- 📖 [Flyway Documentation](https://flywaydb.org/documentation/)
- 📖 [Lombok Features](https://projectlombok.org/features/)
- 📖 [BigDecimal Best Practices](https://www.baeldung.com/java-bigdecimal-biginteger)
- 🎥 [Spring Boot JPA Auditing - Baeldung](https://www.baeldung.com/database-auditing-jpa)

---

## 📄 Licencia

Este proyecto está bajo la Licencia **MIT**. Consulta el archivo [LICENSE](LICENSE) para más detalles.

---

<div align="center">

**Hecho con ❤️ usando Spring Boot y Java**

⭐ Si este proyecto te fue útil, ¡dale una estrella en GitHub!

</div>
