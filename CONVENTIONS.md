# NocoBase Java Refactoring Conventions

## 1. Architecture Strategy
- **Style**: Modular Monolith (Maven Multi-module).
- **Runtime**: Single Spring Boot Application (`nocobase-boot`).
- **JDK Version**: **JDK 17 LTS**.
- **Build Tool**: Maven.

## 2. Module Structure
- `nocobase-core`: Shared utilities, base entities, core interfaces (Plugin, Extension), database configs.
- `nocobase-module-*`: Business features (User, Auth, Workflow). These are **libraries**, not standalone services.
- `nocobase-boot`: The executable Spring Boot application that aggregates all modules.

## 3. Tech Stack
- **Framework**: Spring Boot 3.2+
- **Database**: PostgreSQL 12+
- **ORM**: MyBatis-Flex (Preferred for dynamic schemas) or Spring Data JPA.
- **Utils**: Hutool, Jackson, Lombok.

## 4. Coding Rules
- **DTOs**: Use Java 17 `record` types where appropriate.
- **Transactions**: Rely on local `@Transactional`.
- **Reference**: Follow schema definitions in `docs/JAVA_REACT_DESIGN.md` but ignore the "Microservice" deployment sections.

## 5. 系统工程参考
整个重构开发参考NocoBase项目，项目路径在：/Users/kanten/vswork/nocobase

## 6. 使用中文进行回答