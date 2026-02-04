# 🏦 Sistema Bancario - API REST

API REST de sistema bancario construida con Spring Boot que simula operaciones financieras reales: gestión de clientes, cuentas, depósitos, retiros y transferencias con validaciones de límites y restricciones.

---

## 📦 Tech Stack

| Tecnología | Uso |
|---|---|
| Java 25 | Lenguaje |
| Spring Boot 4.0.2 | Framework principal |
| Spring Data JPA / Hibernate | ORM y persistencia |
| PostgreSQL 16.11 | Base de datos |
| Docker Compose | Contenedor de base de datos |
| Lombok | Reducción de boilerplate |
| Jakarta Validation | Bean Validation en DTOs |
| JUnit 5 + Mockito | Tests unitarios |

---

## 🏗️ Arquitectura

```
Controllers  →  Services  →  Repositories (JPA)  →  PostgreSQL
   (API)       (lógica +        (queries)           (3 tablas)
              validaciones)
```

Arquitectura en capas con separación clara de responsabilidades:

- **Controllers** — Endpoints REST, validación de entrada con `@Valid`, manejo de respuestas HTTP
- **Services** — Lógica de negocio, validaciones de límites, actualización de saldos, transaccionalidad
- **Mappers** — Conversión entre DTOs y entidades JPA en ambas direcciones
- **Repositories** — Interfaces Spring Data JPA con queries derivadas y custom
- **Entidades JPA** — Representan las tablas con relaciones y anotaciones de persistencia

---

## 🗂️ Estructura del Proyecto

```
src/main/java/com/example/banco/
├── controller/          # Endpoints REST
├── service/             # Lógica de negocio y validaciones
│   └── interfaces/      # Contratos de servicios
├── repository/          # Interfaces JPA
├── model/               # Entidades JPA
│   └── enums/           # Enumeraciones del dominio
├── mapper/              # Conversión DTO ↔ Entidad
├── dto/
│   ├── request/         # DTOs de entrada con Bean Validation
│   └── response/        # DTOs de salida
└── exception/           # Excepciones personalizadas y GlobalExceptionHandler

src/test/java/com/example/banco/
└── service/
    └── TransaccionServiceTest.java   # 12 tests unitarios
```

---

## 📋 Módulos

### Clientes
Gestión de clientes con estados ACTIVO/INACTIVO. Un cliente puede tener múltiples cuentas.

### Cuentas
Cuentas bancarias con tipos CORRIENTE y AHORRO, cada una con límites diferentes:

**Cuenta CORRIENTE:**
- Límite de retiro diario: $50,000
- Saldo mínimo: $500
- Depósito inicial mínimo: $1,000
- Límite de transferencia: $100,000

**Cuenta AHORRO:**
- Límite de retiro diario: $30,000
- Saldo mínimo: $1,000
- Depósito inicial mínimo: $2,000
- Límite de transferencia: $50,000

Estados de cuenta:
```
ACTIVA → BLOQUEADA → ACTIVA  (reversible)
ACTIVA → CERRADA              (estado final)
```

Al crear una cuenta se genera automáticamente un número único (`CTA-00001`, `CTA-00002`, etc.).

### Transacciones
Tres tipos de operaciones financieras con validaciones automáticas:

**DEPOSITO** — Solo cuenta destino, incrementa saldo

**RETIRO** — Solo cuenta origen, decrementa saldo con validaciones:
- Saldo suficiente
- No violar saldo mínimo
- No exceder límite de retiro diario

**TRANSFERENCIA** — Cuenta origen y destino, mueve dinero entre cuentas con validaciones:
- Todas las del retiro para la cuenta origen
- No transferir más del límite por operación
- Ambas cuentas deben estar activas
- No transferir a la misma cuenta

Máquina de estados:
```
PENDIENTE → COMPLETADA
    ↓
  FALLIDA
```

---

## 🔌 Endpoints

### Clientes
| Método | Endpoint | Descripción |
|---|---|---|
| POST | `/api/clientes` | Crear cliente |
| GET | `/api/clientes` | Listar todos |
| GET | `/api/clientes/id/{id}` | Buscar por ID |
| GET | `/api/clientes/dni/{dni}` | Buscar por DNI |
| PUT | `/api/clientes/{id}/activar` | Activar cliente |
| PUT | `/api/clientes/{id}/desactivar` | Desactivar cliente |

### Cuentas
| Método | Endpoint | Descripción |
|---|---|---|
| POST | `/api/cuentas` | Crear cuenta con depósito inicial |
| GET | `/api/cuentas` | Listar todas |
| GET | `/api/cuentas/id/{id}` | Buscar por ID |
| GET | `/api/cuentas/nro-cuenta/{nroCuenta}` | Buscar por número |
| GET | `/api/cuentas/cliente/{id}` | Buscar por cliente |
| PUT | `/api/cuentas/estado/{id}` | Cambiar estado |

### Transacciones
| Método | Endpoint | Descripción |
|---|---|---|
| POST | `/api/transacciones/deposito` | Depositar dinero |
| POST | `/api/transacciones/retiro` | Retirar dinero |
| POST | `/api/transacciones/transferencia` | Transferir entre cuentas |
| GET | `/api/transacciones` | Listar todas |
| GET | `/api/transacciones/{id}` | Buscar por ID |
| GET | `/api/transacciones/cuenta/{id}` | Buscar por cuenta |
| GET | `/api/transacciones/estado/{estado}` | Buscar por estado |

---

## ⚙️ Configuración y Ejecución

### Prerrequisitos
- Java 25+
- Docker y Docker Compose
- Maven

### 1. Clonar el repositorio

```bash
git clone https://github.com/IgnacioG6/BancoBackend.git
cd BancoBackend
```

### 2. Levantar PostgreSQL con Docker

```bash
docker-compose up -d
```

Esto levanta PostgreSQL 16.11 en el puerto 5432 con las credenciales configuradas.

### 3. Verificar configuración

El archivo `application.properties` ya está configurado para conectarse a la base de datos:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/banco_db
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.jpa.hibernate.ddl-auto=update
```

### 4. Ejecutar la aplicación

```bash
mvn spring-boot:run
```

### 5. Verificar

La API estará disponible en: `http://localhost:8080/api/`

---

## 🧪 Tests

Ejecutar los tests unitarios:

```bash
mvn test
```

El proyecto incluye 12 tests unitarios para `TransaccionService` que cubren:
- Operaciones exitosas (depósito, retiro, transferencia)
- Validaciones de saldo suficiente y saldo mínimo
- Validaciones de límites diarios y límites por operación
- Validaciones de estados de cuenta
- Casos de error y excepciones

---

## ✅ Validaciones (Bean Validation)

Todos los DTOs de entrada tienen validación declarativa. Si algún campo no cumple, la API retorna 400 con los errores:

```json
{
    "nombre": "El nombre no puede estar vacío",
    "depositoInicial": "El depósito inicial mínimo es de $1000"
}
```

---

## 🔒 Transaccionalidad

Las operaciones críticas usan `@Transactional` para garantizar atomicidad:
- Creación de cuenta con depósito inicial
- Depósitos (actualización saldo + registro transacción)
- Retiros (actualización saldo + registro transacción)
- Transferencias (actualización 2 saldos + registro transacción)

Si alguna operación falla, toda la transacción se revierte automáticamente.

---

## 📊 Diseño de Base de Datos

**3 tablas principales:**

**clientes**
- id, nombre, dni (unique), email, telefono, estado

**cuentas**
- id, nro_cuenta, tipo, saldo, estado, límites (retiro diario, saldo mínimo, transferencia)
- FK: cliente_id

**transacciones**
- id, tipo, estado, monto, fecha_hora, descripcion
- FK: cuenta_origen_id (nullable), cuenta_destino_id (nullable)

Las foreign keys son nullable porque:
- Un depósito solo tiene cuenta destino (origen es null)
- Un retiro solo tiene cuenta origen (destino es null)
- Una transferencia tiene ambas

---

## 🚀 Posibles Mejoras Futuras

- [ ] Spring Security + JWT para autenticación y autorización
- [ ] Paginación con `Pageable` en endpoints de listado
- [ ] Intereses automáticos en cuentas de ahorro (scheduled job)
- [ ] Sistema de notificaciones cuando el saldo baja de un umbral
- [ ] Estadísticas y reportes (total dinero en sistema, cuenta con mayor saldo, etc.)
- [ ] Tests de integración con `@SpringBootTest`
- [ ] API de consulta de saldo histórico por rango de fechas
- [ ] Exportación de movimientos a PDF/Excel

---

## 📝 Ejemplo de Uso

### 1. Crear un cliente
```bash
POST /api/clientes
{
    "nombre": "Juan Pérez",
    "dni": "12345678",
    "email": "juan@example.com",
    "telefono": "1234567890"
}
```

### 2. Crear cuenta para ese cliente
```bash
POST /api/cuentas
{
    "idCliente": 1,
    "tipo": "CORRIENTE",
    "depositoInicial": 5000
}
```

### 3. Hacer un depósito
```bash
POST /api/transacciones/deposito
{
    "idCuenta": 1,
    "monto": 2000
}
```

### 4. Hacer un retiro
```bash
POST /api/transacciones/retiro
{
    "idCuenta": 1,
    "monto": 1000
}
```

### 5. Transferir a otra cuenta
```bash
POST /api/transacciones/transferencia
{
    "idCuentaOrigen": 1,
    "idCuentaDestino": 2,
    "monto": 500
}
```

---
