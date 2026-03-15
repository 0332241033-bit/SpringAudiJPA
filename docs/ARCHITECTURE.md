# 🏗 Arquitectura Técnica — SpringAudiJPA

## Patrón Entity Listener

### Ciclo de Vida de una Entidad JPA

```
  new product()
       │
       ▼
  [ESTADO: TRANSIENT]
  (No existe en BD, no gestionado por JPA)
       │
       │ productRepository.save(entity)
       ▼
  @PrePersist ──► AuditEntityListener.prePersist() ──► INSERT en history
       │
       ▼
  [ESTADO: PERSISTENT / MANAGED]
  (Sincronizado con BD, gestionado por EntityManager)
       │
       │ productRepository.save(entityModificada)
       ▼
  @PreUpdate ──► AuditEntityListener.preUpdate() ──► INSERT en history
       │
       ▼
  [ESTADO: PERSISTENT / MANAGED]
       │
       │ productRepository.deleteById(id)
       ▼
  @PreRemove ──► AuditEntityListener.preRemove() ──► INSERT en history
       │
       ▼
  [ESTADO: REMOVED]
  (Marcado para eliminación)
       │
       │ Commit de la transacción
       ▼
  [ESTADO: DETACHED / ELIMINADO]
```

## Decisiones de Diseño

### 1. `CrudRepository` vs `JpaRepository`

El proyecto utiliza `CrudRepository` que proporciona operaciones CRUD básicas:

```java
// Nota: 'productRepository' y 'product' no siguen las convenciones de nombres de Java
// (deberían ser 'ProductRepository' y 'Product' con mayúscula inicial).
// La convención de Java para clases e interfaces es UpperCamelCase.
public interface productRepository extends CrudRepository<product, Integer> {}
```

**`CrudRepository` ofrece:**
- `save(S entity)` — Guardar o actualizar
- `findById(ID id)` — Buscar por ID
- `existsById(ID id)` — Verificar existencia
- `findAll()` — Listar todos
- `deleteById(ID id)` — Eliminar por ID
- `count()` — Contar registros

**`JpaRepository` (alternativa) ofrece adicionalmente:**
- Paginación y ordenamiento
- `flush()` y `saveAndFlush()`
- Operaciones en lote

> Para las necesidades actuales del proyecto, `CrudRepository` es suficiente y más ligero.

### 2. `@Lazy` en el Entity Listener

```java
@RequiredArgsConstructor(onConstructor = @__(@Lazy))
@Component
public class AuditEntityListener {
    private final historyRepository historyRepository;
}
```

**¿Por qué `@Lazy`?**

Spring Boot inicializa los beans en un orden específico. El `EntityManagerFactory` (que gestiona JPA/Hibernate) se crea temprano en el proceso de arranque. Si `AuditEntityListener` intenta inyectar `historyRepository` de forma inmediata (eager), puede ocurrir una dependencia circular:

```
EntityManagerFactory necesita → AuditEntityListener
AuditEntityListener necesita → historyRepository
historyRepository necesita → EntityManagerFactory ← ¡Circular!
```

Con `@Lazy`, Spring inyecta un proxy que solo resuelve la dependencia real cuando `historyRepository` es usado por primera vez, rompiendo el ciclo.

### 3. `BigDecimal` para Precios Monetarios

```java
private BigDecimal price;
```

**El problema con `double`/`float`:**

```java
double a = 0.1 + 0.2;
System.out.println(a); // 0.30000000000000004 ← ¡Imprecisión!

BigDecimal b = new BigDecimal("0.1").add(new BigDecimal("0.2"));
System.out.println(b); // 0.3 ← Correcto
```

`BigDecimal` es mandatorio para:
- Cálculos financieros
- Precios de productos
- Tasas de interés
- Cualquier valor donde la precisión sea crítica

### 4. Flyway para Control de Versiones de BD

```
V1__create_table.sql         ← Se aplica primero
V2__create_history_table.sql ← Se aplica segundo
V3_update_history_table.sql  ← Se aplica tercero (⚠️ debería llamarse V3__update_history_table.sql)
```

> ⚠️ **Convención de Nombres en Flyway:** El formato correcto es `V{versión}__{descripción}.sql` (doble guion bajo). El archivo `V3_update_history_table.sql` no sigue esta convención. Renombrarlo a `V3__update_history_table.sql` garantiza que Flyway lo detecte correctamente en todos los entornos.

**Ventajas de Flyway:**
- 🔄 Reproducible: cualquier desarrollador obtiene el mismo esquema
- 📝 Trazable: historial completo de cambios en `flyway_schema_history`
- 🔒 Inmutable: los scripts aplicados no se pueden modificar
- 🚀 Automático: se ejecuta al iniciar la aplicación

## Mejoras Potenciales

### 1. Añadir Usuario Actual a la Auditoría

```java
// Con Spring Security
@PrePersist
private void prePersist(product product) {
    String username = SecurityContextHolder.getContext()
        .getAuthentication().getName();

    history h = new history();
    h.setName(product.getName());
    h.setDate(LocalDateTime.now());
    h.setOperation("INSERT");
    h.setUsername(username); // ← Ya existe la columna en V3
    historyRepository.save(h);
}
```

### 2. Auditoría con `@CreatedDate` / `@LastModifiedDate`

Spring Data JPA provee anotaciones nativas de auditoría:

```java
@Entity
@EntityListeners(AuditingEntityListener.class) // Spring's listener
public class product {

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @CreatedBy
    private String createdBy;

    @LastModifiedBy
    private String lastModifiedBy;
}
```

Requiere habilitar en la clase principal:
```java
@SpringBootApplication
@EnableJpaAuditing
public class SpringAudiJpaApplication { ... }
```

### 3. Añadir DTOs para Evitar Exponer Entidades

```java
// Buena práctica: nunca exponer entidades JPA directamente en la API
public record ProductRequest(String name, String description, BigDecimal price) {}
public record ProductResponse(Integer id, String name, BigDecimal price) {}
```

### 4. Manejo de Excepciones Global

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> handleNotFound(NoSuchElementException e) {
        return ResponseEntity.notFound().build();
    }
}
```
