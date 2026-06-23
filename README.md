[README.txt](https://github.com/user-attachments/files/29219180/README.txt)
# Sistema de Cine — Microservicios

Sistema de gestión para un cine, desarrollado con una arquitectura de microservicios en Spring Boot. Permite a los usuarios registrarse, consultar películas y funciones, reservar entradas, agregar productos o combos a su reserva, pagar y generar boleta, además de marcar películas favoritas y dejar comentarios.

## Integrantes

- Gabriel Valdes — HATEOAS, pruebas unitarias, Swagger, Docker, microservicios de **promociones** y **pagos**.
- Benjamin Mora — microservicios de **productos**, **comentarios** y **favoritos**.

## Microservicios

| Microservicio | Puerto | Responsabilidad |
|---|---|---|
| usuario-service | 8081 | Registro, login y gestión de usuarios y roles |
| cinefunciones-service | 8082 | Gestión de funciones de cine |
| sala-service | 8083 | Gestión de salas |
| peliculas-service | 8084 | Gestión de películas y géneros |
| reservas-service | 8085 | Creación de reservas (entradas + productos/combos) |
| pagos-service | 8086 | Procesamiento de pagos y generación de boleta (cálculo de IVA) |
| producto-service | 8087 | Catálogo de productos (cabritas, bebidas, snacks, etc.) |
| promociones-service | 8088 | Combos de productos con precio especial |
| comentarios-service | 8091 | Comentarios de usuarios sobre películas |
| favoritos-service | 8092 | Películas favoritas por usuario |

Todos los microservicios se comunican entre sí vía REST (RestTemplate), están documentados con Swagger/OpenAPI, e implementan HATEOAS en sus endpoints GET (rutas `/v2`).

## API Gateway

El API Gateway corre en el puerto **9090** y centraliza el acceso a todos los microservicios.

### Rutas v1

| Recurso | Ruta | Microservicio destino |
|---|---|---|
| Usuarios | `/api/usuarios/**` | usuario-service |
| Películas / Géneros | `/api/peliculas/**`, `/api/generos/**` | peliculas-service |
| Salas | `/api/salas/**` | sala-service |
| Funciones | `/api/funciones/**` | cinefunciones-service |
| Reservas | `/api/reservas/**` | reservas-service |
| Pagos | `/api/pagos/**` | pagos-service |
| Productos | `/api/productos/**` | producto-service |
| Combos | `/api/combos/**` | promociones-service |
| Favoritos | `/api/favoritos/**` | favoritos-service |
| Comentarios | `/api/comentarios/**` | comentarios-service |

### Rutas v2 (HATEOAS)

| Recurso | Ruta | Microservicio destino |
|---|---|---|
| Usuarios | `/usuarios/v2/**` | usuario-service |
| Películas | `/peliculas/v2/**` | peliculas-service |
| Salas | `/salas/v2/**` | sala-service |
| Funciones | `/funciones/v2/**` | cinefunciones-service |
| Reservas | `/reservas/v2/**` | reservas-service |
| Pagos | `/pagos/v2/**` | pagos-service |
| Productos | `/productos/v2/**` | producto-service |
| Combos | `/combos/v2/**` | promociones-service |
| Favoritos | `/favoritos/v2/**` | favoritos-service |
| Comentarios | `/comentarios/v2/**` | comentarios-service |

Ejemplo de uso: `GET http://localhost:9090/usuarios/v2/1` devuelve el usuario con sus enlaces de navegación (`_links`) hacia el recurso mismo y hacia el listado completo.

## Documentación Swagger

Cada microservicio expone su documentación en `/swagger-ui.html`. Accediendo directamente por su puerto:

| Microservicio | Swagger UI |
|---|---|
| usuario-service | http://localhost:8081/swagger-ui.html |
| cinefunciones-service | http://localhost:8082/swagger-ui.html |
| sala-service | http://localhost:8083/swagger-ui.html |
| peliculas-service | http://localhost:8084/swagger-ui.html |
| reservas-service | http://localhost:8085/swagger-ui.html |
| pagos-service | http://localhost:8086/swagger-ui.html |
| producto-service | http://localhost:8087/swagger-ui.html |
| promociones-service | http://localhost:8088/swagger-ui.html |
| comentarios-service | http://localhost:8091/swagger-ui.html |
| favoritos-service | http://localhost:8092/swagger-ui.html |

## Pruebas unitarias

Se implementaron pruebas unitarias (Service + Controller) con JUnit 5, Mockito y MockMvc, siguiendo la estructura Given-When-Then, en los siguientes microservicios:

- usuario-service
- pagos-service (incluye prueba específica de cálculo de IVA)
- comentarios-service
- favoritos-service
- producto-service
- promociones-service

## Tecnologías

- Java 17
- Spring Boot 3.5.14
- Spring Cloud Gateway
- Spring Data JPA + Hibernate
- Spring HATEOAS
- MySQL 8.0
- Liquibase (carga inicial de datos)
- Springdoc OpenAPI / Swagger (2.8.9+)
- JUnit 5 + Mockito
- Docker y Docker Compose
- Lombok

## Instrucciones de ejecución

### Requisitos previos

- Docker y Docker Compose instalados
- JDK 17 y Maven (si se quiere compilar manualmente fuera de Docker)

### Pasos

1. Clonar el repositorio:
   ```bash
   git clone https://github.com/gavaldesi-wq/Prueba-3.git
   cd Prueba-3
   ```

2. Compilar cada microservicio (genera el `target/*.jar` que Docker necesita):
   ```bash
   for dir in usuario-service sala-service peliculas-service cinefunciones-service reservas-service pagos-service producto-service promociones-service comentarios-service favoritos-service api-gateway; do
     cd $dir && mvn clean package -DskipTests && cd ..
   done
   ```

3. Levantar todo el sistema con Docker Compose:
   ```bash
   docker compose up --build
   ```

4. Verificar que todos los contenedores estén arriba:
   ```bash
   docker compose ps
   ```

5. Acceder al sistema a través del API Gateway en `http://localhost:9090`, o a cada microservicio directamente por su puerto individual.

### Notas

- La base de datos MySQL corre en el puerto `3307` (mapeado desde el `3306` interno del contenedor).
- Liquibase puebla automáticamente las tablas principales (productos, combos, etc.) al iniciar cada microservicio por primera vez.
- Si algún microservicio falla al iniciar por un cambio de esquema en la base de datos, puede ser necesario recrear el volumen de MySQL (`docker compose down -v`, con la salvedad de que esto borra los datos cargados).

