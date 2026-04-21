
# Twitter Clone


Aplicación web full-stack inspirada en Twitter, con autenticación OAuth2 via Auth0, feed público en tiempo real y registro de usuarios. Construida como monolito Spring Boot con una arquitectura de microservicios planificada sobre AWS Lambda.

---

## Descripción de la Arquitectura

El sistema actual funciona como un monolito Spring Boot que expone una API REST consumida por el frontend React. La autenticación se delega completamente a Auth0 mediante JWT.

```
[React Frontend] ←→ [Spring Boot Monolith] ←→ [PostgreSQL]
                            ↑
                        [Auth0 JWT]
```

La carpeta `microservices/` contiene la evolución planificada hacia funciones serverless en AWS Lambda:

- **User Service** — gestión y registro de usuarios
- **Posts Service** — creación y consulta de posts
- **Feed Service** — agregación del feed global

---

## Estructura del Proyecto

```
twitter-clone/
├── backend/
│   ├── monolith/              # Spring Boot + Maven
│   └── microservices/
│       ├── user-service/
│       ├── posts-service/
│       └── feed-service/
├── frontend/                  # React + Vite
└── README.md
```

---

## Tecnologías Utilizadas

| Capa            | Tecnología                        |
|-----------------|-----------------------------------|
| Frontend        | React 18, Vite, Axios             |
| Backend         | Spring Boot 3, Spring Security    |
| Base de Datos   | PostgreSQL                        |
| Autenticación   | Auth0 (JWT / PKCE)                |
| Despliegue      | AWS Lambda (microservicios)       |

---

## Endpoints de la API

| Método   | Endpoint      | Requiere Auth | Descripción                              |
|----------|---------------|---------------|------------------------------------------|
| `GET`    | `/api/stream` | No            | Obtener todos los posts                  |
| `GET`    | `/api/posts`  | No            | Obtener todos los posts (alias)          |
| `POST`   | `/api/posts`  | Sí            | Crear un post (máx. 140 caracteres)      |
| `GET`    | `/api/me`     | Sí            | Obtener información del usuario actual   |
| `PATCH`  | `/api/me`     | Sí            | Actualizar nombre de usuario             |

---

## Swagger UI

La documentación interactiva de la API está disponible en:
[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

<img width="1906" height="901" alt="image" src="https://github.com/user-attachments/assets/29313f8a-6893-4943-91ba-299947989063" />


---

## Instalación y Ejecución Local

### Requisitos Previos

- Java 17
- Node.js 18+
- PostgreSQL corriendo en `localhost:5432`
- Cuenta de Auth0

### Variables de Entorno

Crea un archivo `backend/monolith/src/main/resources/application.properties` basado en `.env.example`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/twitter
spring.datasource.username=TU_USUARIO_PG
spring.datasource.password=TU_PASSWORD_PG

spring.security.oauth2.resourceserver.jwt.issuer-uri=https://TU_DOMINIO.us.auth0.com/
auth0.audience=TU_AUDIENCE

cors.allowed-origins=http://localhost:5173
```

Crea un archivo `frontend/.env` basado en `frontend/.env.example`:

```env
VITE_AUTH0_DOMAIN=TU_DOMINIO.us.auth0.com
VITE_AUTH0_CLIENT_ID=TU_CLIENT_ID
VITE_AUTH0_AUDIENCE=TU_AUDIENCE
VITE_API_URL=http://localhost:8080
```

### Ejecutar Backend

```bash
cd backend/monolith
mvn spring-boot:run
```

### Ejecutar Frontend

```bash
cd frontend
npm install
npm run dev
```

---

## Configuración de Auth0

1. **Aplicación SPA** — crea una aplicación de tipo Single Page Application en Auth0 para el frontend React. Configura `http://localhost:5173` como Allowed Callback URL, Logout URL y Web Origin.

2. **API (Audience)** — crea una API en Auth0 con el identificador `https://twitter-clone-api`. Este valor se usa como `audience` tanto en el frontend como en Spring Boot.

3. **Validación JWT en Spring Boot** — el backend valida automáticamente los tokens usando el issuer `https://dev-gtvauehvyprmkd80.us.auth0.com/`. No se requiere configuración adicional.

---

## Demo

**Video Demo:** (https://youtu.be/Pbg9uP7BEWw)


---

## Pruebas Realizadas

El proyecto incluye pruebas unitarias e de integración con JUnit 5 y Mockito, verificando:

- Endpoints públicos (`GET /api/posts`) accesibles sin token
- Endpoints protegidos con JWT válido retornan datos correctos
- Endpoints protegidos sin token retornan `401 Unauthorized`
- Validación de máximo 140 caracteres al crear un post
- Unicidad del nombre de usuario (retorna `409 Conflict` si ya existe)

```bash
cd backend/monolith
mvn test
```

- Samuel Antonio Gil Romero 
- TDSE - Transformación Digital y Soluciones Empresariales 
- Escuela Colombiana de Ingeniería Julio Garavito 
