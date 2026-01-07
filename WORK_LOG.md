# 工作日志 - 2026-01-07

## 项目初始化

### 时间: 2026-01-07 上午

### 完成的任务

#### 1. Maven 项目结构创建
- ✅ 创建根 pom.xml（Spring Boot 3.2.0, JDK 17）
- ✅ 配置 Maven 多模块项目（Monorepo 单体架构）
- ✅ 定义所有子模块（nocobase-server, nocobase-auth, nocobase-user, nocobase-data, nocobase-permission, nocobase-workflow, nocobase-ai, nocobase-file, nocobase-common）

#### 2. 子模块创建
- ✅ nocobase-gateway（后移除，改为单体架构）
- ✅ nocobase-auth 模块
- ✅ nocobase-user 模块
- ✅ nocobase-data 模块
- ✅ nocobase-permission 模块
- ✅ nocobase-workflow 模块
- ✅ nocobase-ai 模块
- ✅ nocobase-file 模块
- ✅ nocobase-common 模块

#### 3. 各模块 pom.xml 配置
- ✅ nocobase-common：配置 Lombok, Jackson, Spring Data JPA, JWT, SpringDoc, PostgreSQL Driver
- ✅ nocobase-server：配置为单体应用主模块，依赖所有业务模块
- ✅ nocobase-auth/user/data/permission/workflow/ai/file：配置为 library 模式，移除 spring-boot-maven-plugin

#### 4. 单体应用启动类
- ✅ 创建 nocobase-server/src/main/java/com/nocobase/server/NocoBaseApplication.java
- ✅ 配置包扫描（scanBasePackages）：包含所有业务模块
- ✅ 移除各业务模块的独立启动类（AuthApplication, UserApplication 等）

#### 5. 配置文件创建
- ✅ nocobase-server/src/main/resources/application.yml（统一配置，端口 8080）
- ✅ 配置 PostgreSQL 数据库连接（localhost:5432/nocobase）
- ✅ 配置 Redis 缓存（localhost:6379）
- ✅ 配置 JPA ddl-auto: update
- ✅ 配置文件上传限制（max-file-size: 100MB）
- ✅ 移除各业务模块的 application.yml（统一到 server 模块）

#### 6. Docker 环境配置
- ✅ 创建 docker-compose.yml
- ✅ 配置 PostgreSQL 14（端口 5432）
- ✅ 配置 Redis 7（端口 6379）
- ✅ 配置环境变量（POSTGRES_DB, POSTGRES_USER, POSTGRES_PASSWORD）
- ✅ 配置数据卷（postgres-data, redis-data）
- ✅ 配置健康检查

#### 7. 动态表管理器实现
- ✅ 创建 nocobase-data/src/main/java/com/nocobase/data/manager/DynamicTableManager.java
- ✅ 实现 createTable 方法（动态创建 Collection 表）
- ✅ 实现 addColumn 方法（动态添加字段）
- ✅ 实现 tableExists 方法（检查表是否存在）
- ✅ 实现 listTables 方法（列出所有表）
- ✅ 使用 JdbcTemplate 执行原生 SQL
- ✅ 兼容 PostgreSQL 语法

#### 8. 单元测试用例（JUnit 5）
- ✅ 创建 nocobase-data/src/test/java/com/nocobase/data/manager/DynamicTableManagerTest.java
- ✅ 编写测试用例（创建订单表、添加价格字段、列出所有表）
- ✅ 配置测试依赖（spring-boot-starter-test, junit-jupiter）
- ✅ 使用 @SpringBootTest 注解
- ✅ 使用真实 PostgreSQL 数据库测试（符合用户需求）

#### 9. 构建验证
- ✅ 运行 mvn clean install 成功
- ✅ 所有模块编译通过
- ✅ nocobase-data 模块构建成功（包含 DynamicTableManager）

#### 10. Git 仓库初始化
- ✅ git init 初始化仓库
- ✅ git add . 添加所有文件
- ✅ git commit 创建初始提交（feat: 初始化 NocoBase Java 单体项目）
- ✅ 配置 GitHub 用户名：zhoulu0907

#### 11. GitHub 仓库创建
- ✅ 手动在 GitHub 网站创建仓库
- ✅ 仓库名称：MyNocoBase
- ✅ 描述：NocoBase Java 单体应用
- ✅ 可见性：Public

#### 12. 推送到远程仓库
- ✅ 配置远程仓库：https://github.com/zhoulu0907/MyNocoBase.git
- ✅ git push origin master 成功
- ✅ 远程仓库已同步（Everything up-to-date）

---

## 项目结构

```
MyNocoBase/
├── pom.xml                           # Maven 根配置（Spring Boot 3.2.0）
├── docker-compose.yml                # Docker 配置（PostgreSQL 14 + Redis 7）
├── WORK_LOG.md                      # 工作日志（本文件）
├── nocobase-server/                  # 单体应用主模块（端口 8080）
│   └── src/main/java/com/nocobase/server/NocoBaseApplication.java
│   └── src/main/resources/application.yml
├── nocobase-auth/                    # 认证模块（library）
├── nocobase-user/                     # 用户模块（library）
├── nocobase-data/                      # 数据模型模块（library）✨
│   ├── src/main/java/com/nocobase/data/manager/DynamicTableManager.java ✨
│   └── src/test/java/com/nocobase/data/manager/DynamicTableManagerTest.java
├── nocobase-permission/                # 权限模块（library）
├── nocobase-workflow/                 # 工作流模块（library）
├── nocobase-ai/                           # AI 模块（library）
├── nocobase-file/                     # 文件模块（library）
└── nocobase-common/                   # 公共模块（library）
    ├── Lombok, Jackson, Spring Data JPA
    ├── JWT, PostgreSQL Driver
    └── SpringDoc OpenAPI
```

---

## 下一步计划

### 待开发功能
- [ ] 用户认证模块（User Service）
  - [ ] 用户实体（User Entity）
  - [ ] 用户 Repository（Spring Data JPA）
  - [ ] 用户 Service（业务逻辑）
  - [ ] 用户 Controller（REST API）
  - [ ] 初始用户数据（默认管理员账户）

- [ ] 登录注册接口
  - [ ] 登录端点（POST /api/v1/auth/login）
  - [ ] 注册端点（POST /api/v1/auth/register）
  - [ ] JWT Token 生成和验证
  - [ ] 密码加密（BCrypt）

### 技术栈确认
- ✅ Spring Boot 3.2.0
- ✅ JDK 17
- ✅ Maven 多模块项目
- ✅ 单体架构（非微服务）
- ✅ PostgreSQL 14
- ✅ Redis 7
- ✅ Lombok
- ✅ Spring Data JPA（ORM）
- ✅ SpringDoc OpenAPI（API 文档）

### 已解决的问题
1. ✅ 微服务架构改为单体架构（符合项目需求）
2. ✅ 所有模块配置为 library 模式
3. ✅ 统一应用入口（nocobase-server）
4. ✅ 配置文件统一到 server 模块
5. ✅ 各业务模块移除独立启动类
6. ✅ 动态表管理器实现完成

---

## GitHub 仓库

**仓库地址**：https://github.com/zhoulu0907/MyNocoBase

**分支**：master

**最新提交**：feat: 初始化 NocoBase Java 单体项目

---

## 总结

今天完成了 NocoBase Java 项目的完整初始化工作：
1. ✅ 从微服务架构调整为单体架构
2. ✅ 创建完整的 Maven 多模块项目结构
3. ✅ 实现动态表管理器（DynamicTableManager）
4. ✅ 编写单元测试用例
5. ✅ 配置 Docker 开发环境（PostgreSQL + Redis）
6. ✅ 推送项目到 GitHub 远程仓库

项目现在可以正常开发，所有更改已同步到 GitHub！

---

## MyBatis-Flex 迁移与领域层实现

### 时间: 2026-01-07 上午晚些时候

#### 13. ORM 框架迁移：Spring Data JPA → MyBatis-Flex

**背景与决策**
- ✅ 决策迁移：从 Spring Data JPA 迁移到 MyBatis-Flex
- ✅ 原因：MyBatis-Flex 提供更灵活的 SQL 控制、更优的性能、更简洁的 API
- ✅ 版本选择：MyBatis-Flex 1.11.1（最新稳定版）

**技术栈变更**
- ❌ 移除：Spring Data JPA（spring-boot-starter-data-jpa）
- ✅ 新增：MyBatis-Flex Spring Boot Starter（mybatis-flex-spring-boot3-starter）
- ✅ 配置：nocobase-common/pom.xml 中引入 MyBatis-Flex 依赖（供所有业务模块使用）

**实施步骤**
- ✅ 更新 nocobase-common/pom.xml：添加 MyBatis-Flex Starter（Spring Boot 3 兼容版本）
- ✅ 更新根 pom.xml：定义 MyBatis-Flex 版本 1.11.1
- ✅ 移除 nocobase-user/pom.xml 中重复的 Spring Data JPA 依赖
- ✅ 修正所有实体类的注解包导入路径

**实体类注包路径修正**
- ✅ User.java：`javax.persistence.*` → `com.mybatis.flex.core.*`
- ✅ Role.java：`javax.persistence.*` → `com.mybatis.flex.core.*`
- ✅ Permission.java：`javax.persistence.*` → `com.mybatis.flex.core.*`

**编译验证**
- ✅ 执行 `mvn clean compile`：编译成功
- ✅ 所有实体类、Mapper 接口编译通过
- ✅ 无 JPA 注解处理器错误

**提交记录**
- ✅ commit 1d748bb: "refactor: 从 Spring Data JPA 迁移到 MyBatis-Flex"
- ✅ 推送到 GitHub：同步成功

#### 14. nocobase-user 模块领域层实现（MyBatis-Flex）

**实体层（Entity）**
- ✅ User.java：用户实体（id, username, email, password, createdAt, updatedAt）
- ✅ Role.java：角色实体（id, name, description, createdAt, updatedAt）
- ✅ Permission.java：权限实体（id, name, resource, action, createdAt, updatedAt）
- ✅ UserRole.java：用户-角色关联实体（多对多关系表）

**Mapper 层（数据访问接口）**
- ✅ UserMapper.java：继承 `BaseMapper<User>`
- ✅ RoleMapper.java：继承 `BaseMapper<Role>`
- ✅ UserRoleMapper.java：继承 `BaseMapper<UserRole>`
- ✅ 注解：`@Mapper` 标记为 MyBatis Mapper 接口

**Service 层（业务逻辑）**
- ✅ UserService.java：用户服务接口，继承 `IService<User>`
- ✅ UserServiceImpl.java：用户服务实现类
  - 继承 `ServiceImpl<UserMapper, User>`（MyBatis-Flex 提供的基础实现）
  - 实现 `UserService` 接口
  - 使用 `@Service` 注解标记为 Spring Bean

**技术要点**
- ✅ MyBatis-Flex 提供的 `BaseMapper` 包含常用 CRUD 方法（insert, delete, update, select）
- ✅ MyBatis-Flex 提供的 `IService` 和 `ServiceImpl` 提供丰富的业务层方法
- ✅ 使用 `@Mapper` 注解标记 Mapper 接口（MyBatis 扫描）
- ✅ 使用 `@Service` 注解标记 Service 实现类（Spring IoC）

**遇到的问题与解决**
- 🔴 问题：ServiceImpl 泛型参数错误 `class UserServiceImpl extends ServiceImpl<User>`
- ✅ 解决：修正为 `class UserServiceImpl extends ServiceImpl<UserMapper, User>`
- 🎯 原因：MyBatis-Flex 的 ServiceImpl 需要两个泛型参数（Mapper 类型, Entity 类型）

**编译验证**
- ✅ 执行 `mvn clean compile`：编译成功
- ✅ 所有实体、Mapper、Service 编译通过
- ✅ MyBatis-Flex 集成成功

**提交记录**
- ✅ commit e56d3b9: "feat: 实现 nocobase-user 模块领域层 (MyBatis-Flex)"
- ✅ 推送到 GitHub：同步成功

#### 15. Git 配置优化

**.gitignore 文件创建**
- ✅ 忽略 Maven 构建产物：`target/`
- ✅ 忽略 IDE 配置文件：`.idea/`, `.vscode/`, `*.iml`
- ✅ 忽略 macOS 系统文件：`.DS_Store`
- ✅ 忽略 Claude Code 配置：`.claude/`
- ✅ 忽略日志文件：`*.log`

**Git 追踪清理**
- ✅ 从 Git 仓库中移除不应追踪的文件（.claude/, target/）
- ✅ 更新 .gitignore 以防止未来误提交

**提交记录**
- ✅ commit e8ecadc: "fix: 清理 nocobase-user 模块依赖和 Git 追踪"
- ✅ 推送到 GitHub：同步成功

#### 16. nocobase-data 模块领域层实现（MyBatis-Flex）

**实体层（Entity）**
- ✅ Collection.java：集合实体（动态表的元数据）
- ✅ Field.java：字段实体（动态表的字段定义）

**Mapper 层（数据访问接口）**
- ✅ CollectionMapper.java：继承 `BaseMapper<Collection>`
- ✅ FieldMapper.java：继承 `BaseMapper<Field>`

**Service 层（业务逻辑）**
- ✅ CollectionService.java：集合服务接口，继承 `IService<Collection>`
- ✅ CollectionServiceImpl.java：集合服务实现类，继承 `ServiceImpl<CollectionMapper, Collection>`
- ✅ FieldService.java：字段服务接口，继承 `IService<Field>`
- ✅ FieldServiceImpl.java：字段服务实现类，继承 `ServiceImpl<FieldMapper, Field>`

**DTO 层（数据传输对象）**
- ✅ CollectionDTO.java：集合数据传输对象
- ✅ FieldDTO.java：字段数据传输对象

**现有功能保留**
- ✅ DynamicTableManager.java：保留原有的动态表管理器（使用 JdbcTemplate）

**状态**
- ✅ 已创建文件，尚未提交到 Git
- 📝 待测试：Service 层的业务逻辑测试
- 📝 待提交：nocobase-data 模块领域层实现

---

## 技术栈更新（2026-01-07）

### 核心技术
- ✅ Spring Boot 3.2.0
- ✅ JDK 17
- ✅ Maven 多模块项目
- ✅ 单体架构（非微服务）
- ✅ PostgreSQL 14
- ✅ Redis 7
- ✅ Lombok 1.18.30
- ✅ MyBatis-Flex 1.11.1（ORM 框架）✨
- ✅ SpringDoc OpenAPI 2.3.0

### 架构模式
- ✅ 领域驱动设计（DDD）：Entity → Mapper → Service → Controller
- ✅ MyBatis-Flex BaseMapper：提供基础 CRUD 操作
- ✅ MyBatis-Flex IService/ServiceImpl：提供业务层基础方法

---

## 下一步计划

### 待开发功能
- [ ] nocobase-data 模块领域层测试
  - [ ] CollectionService 测试用例
  - [ ] FieldService 测试用例
  - [ ] DTO 对象转换测试

- [ ] nocobase-user 模块 Controller 层
  - [ ] UserController（用户 CRUD API）
  - [ ] RoleController（角色 CRUD API）
  - [ ] PermissionController（权限 CRUD API）

- [ ] 认证授权模块（nocobase-auth）
  - [ ] JWT Token 生成和验证
  - [ ] 登录端点（POST /api/v1/auth/login）
  - [ ] 注册端点（POST /api/v1/auth/register）
  - [ ] 密码加密（BCrypt）

- [ ] nocobase-data 模块 Controller 层
  - [ ] CollectionController（集合管理 API）
  - [ ] FieldController（字段管理 API）
  - [ ] DynamicTableController（动态表操作 API）

### 待解决的问题
1. ✅ MyBatis-Flex 注解包路径错误 → 已修正
2. ✅ ServiceImpl 泛型参数错误 → 已修正
3. ✅ nocobase-user 模块重复依赖 → 已清理
4. ✅ Git 追踪不必要的文件 → 已优化 .gitignore

### 已实现的功能
1. ✅ 项目初始化（Maven 多模块 + Spring Boot 3.2.0）
2. ✅ 动态表管理器（DynamicTableManager）
3. ✅ 从 Spring Data JPA 迁移到 MyBatis-Flex
4. ✅ nocobase-user 模块领域层（Entity + Mapper + Service）
5. ✅ nocobase-data 模块领域层（Entity + Mapper + Service + DTO）

---

## GitHub 更新记录

**最新提交**：
- e8ecadc: fix: 清理 nocobase-user 模块依赖和 Git 追踪
- e56d3b9: feat: 实现 nocobase-user 模块领域层 (MyBatis-Flex)
- 1d748bb: refactor: 从 Spring Data JPA 迁移到 MyBatis-Flex
- 349d321: feat: 初始化 NocoBase Java 单体项目

**仓库地址**：https://github.com/zhoulu0907/MyNocoBase

**分支**：master

---

## 经验总结

### MyBatis-Flex 迁移经验
1. **版本兼容性**：必须使用 `mybatis-flex-spring-boot3-starter` 以兼容 Spring Boot 3
2. **注解包路径**：MyBatis-Flex 注解位于 `com.mybatis.flex.core.*` 包（非 `javax.persistence.*`）
3. **ServiceImpl 泛型**：`ServiceImpl<Mapper, Entity>` 需要两个泛型参数（Mapper 类型, Entity 类型）
4. **依赖管理**：MyBatis-Flex 应通过 nocobase-common 模块统一引入，避免重复依赖

### 领域层实现经验
1. **三层架构**：Entity（实体）→ Mapper（数据访问）→ Service（业务逻辑）
2. **BaseMapper**：继承 MyBatis-Flex 的 `BaseMapper<Entity>` 获得基础 CRUD 方法
3. **IService/ServiceImpl**：继承 MyBatis-Flex 的 `IService<Entity>` 和 `ServiceImpl<Mapper, Entity>` 获得丰富业务方法
4. **注解使用**：
   - `@Mapper`：标记 Mapper 接口（MyBatis 扫描）
   - `@Service`：标记 Service 实现类（Spring IoC）

### Git 管理经验
1. **.gitignore 重要性**：尽早配置，避免提交构建产物和 IDE 配置
2. **依赖清理**：避免在子模块中重复引入已在 common 模块定义的依赖
3. **提交信息规范**：使用清晰的前缀（feat: / fix: / refactor:）

---

## 项目当前状态

### 已完成模块
- ✅ nocobase-common：公共模块（Lombok, Jackson, MyBatis-Flex, JWT, PostgreSQL, SpringDoc）
- ✅ nocobase-user：用户模块领域层（Entity + Mapper + Service）
- 🟡 nocobase-data：数据模块领域层（Entity + Mapper + Service + DTO，待测试和提交）

### 待开发模块
- ⏳ nocobase-auth：认证授权模块
- ⏳ nocobase-permission：权限管理模块
- ⏳ nocobase-workflow：工作流模块
- ⏳ nocobase-ai：AI 模块
- ⏳ nocobase-file：文件管理模块
- ⏳ nocobase-server：单体应用主模块（整合所有业务模块）

### 待提交文件
```
nocobase-auth/src/                      # 认证模块源码（未追踪）
nocobase-data/src/main/java/com/nocobase/data/dto/   # DTO 层（未追踪）
nocobase-data/src/main/java/com/nocobase/data/entity/ # 实体层（未追踪）
nocobase-data/src/main/java/com/nocobase/data/mapper/ # Mapper 层（未追踪）
nocobase-data/src/main/java/com/nocobase/data/service/ # Service 层（未追踪）
```

---

## 今天的主要成就

1. ✅ **ORM 框架迁移**：成功从 Spring Data JPA 迁移到 MyBatis-Flex
2. ✅ **nocobase-user 模块**：完成领域层实现（Entity + Mapper + Service）
3. ✅ **nocobase-data 模块**：完成领域层实现（Entity + Mapper + Service + DTO）
4. ✅ **Git 优化**：创建 .gitignore，清理不必要的追踪文件
5. ✅ **编译验证**：所有模块编译通过，无错误

---

**更新时间**：2026-01-07 晚间

---

## 动态数据 CRUD 服务实现

### 时间: 2026-01-07 下午

#### 17. 统一响应类创建

**ApiResponse 类**
- ✅ 位置：`nocobase-common/src/main/java/com/nocobase/common/response/ApiResponse.java`
- ✅ 功能：提供统一的 API 响应格式
- ✅ 响应码支持：200（成功）、400（请求错误）、404（不存在）、500（服务器错误）
- ✅ 泛型支持：`<T>` 用于返回任意类型数据
- ✅ 时间戳：自动记录响应时间

**静态工厂方法**
- `success(T data)`：成功响应（带数据）
- `success()`：成功响应（无数据）
- `success(String message, T data)`：成功响应（自定义消息）
- `error(Integer code, String message)`：错误响应（自定义状态码）
- `error(String message)`：错误响应（默认 500）
- `badRequest(String message)`：错误响应（400）
- `notFound(String message)`：错误响应（404）

#### 18. 动态数据 CRUD 服务实现

**DataRecordService 类**
- ✅ 位置：`nocobase-data/src/main/java/com/nocobase/data/service/DataRecordService.java`
- ✅ 依赖：`CollectionMapper`（集合元数据）、`JdbcTemplate`（动态 SQL 执行）
- ✅ 无实体依赖：使用 `Map<String, Object>` 传递动态数据

**核心方法**
- `createRecord(collectionName, data)`：动态插入数据（返回生成 ID）
- `queryRecords(collectionName, condition)`：条件查询（支持动态 WHERE 子句）
- `queryAllRecords(collectionName)`：查询所有数据
- `queryById(collectionName, id)`：查询单条数据（按 ID）
- `updateRecord(collectionName, id, data)`：更新数据（按 ID）
- `deleteRecord(collectionName, id)`：删除数据（按 ID）
- `countRecords(collectionName)`：统计数据量
- `exists(collectionName, id)`：检查数据是否存在

**技术实现**
- 使用 `JdbcTemplate` 执行动态 SQL（防止 SQL 注入）
- 集合名称到物理表名的映射（通过 `CollectionMapper`）
- 动态构建 INSERT、UPDATE、SELECT 语句
- 参数化查询（使用 `?` 占位符）

#### 19. REST API 控制器实现

**DataController 类**
- ✅ 位置：`nocobase-data/src/main/java/com/nocobase/data/controller/DataController.java`
- ✅ 基础路径：`/api/v1/data`
- ✅ Swagger 文档：完整的 OpenAPI 3.0 注解

**REST API 端点**

| 方法 | 路径 | 描述 |
|------|--------|------|
| POST | `/api/v1/data/{collectionName}` | 创建数据 |
| GET | `/api/v1/data/{collectionName}` | 查询列表（支持条件） |
| GET | `/api/v1/data/{collectionName}/{id}` | 查询详情 |
| PUT | `/api/v1/data/{collectionName}/{id}` | 更新数据 |
| DELETE | `/api/v1/data/{collectionName}/{id}` | 删除数据 |
| GET | `/api/v1/data/{collectionName}/count` | 统计数量 |
| GET | `/api/v1/data/{collectionName}/{id}/exists` | 检查存在性 |

**错误处理**
- `IllegalArgumentException`：返回 400（请求参数错误）
- 记录不存在：返回 404（资源不存在）
- `Exception`：返回 500（服务器内部错误）

#### 20. 编译验证

**编译结果**
- ✅ `mvn clean compile`：全部模块编译成功
- ✅ 无编译错误
- ✅ 无警告信息

**模块验证**
- ✅ nocobase-common：编译通过
- ✅ nocobase-data：编译通过
- ✅ nocobase-user：编译通过
- ✅ nocobase-auth：编译通过
- ✅ nocobase-server：编译通过

#### 21. API 文档创建

**DATA_CRUD_API.md**
- ✅ 位置：`nocobase-data/DATA_CRUD_API.md`
- ✅ 内容：完整的 API 使用指南
- ✅ 示例：cURL 命令示例
- ✅ 响应格式：所有端点的响应示例
- ✅ 错误处理：常见错误场景说明

---

## 技术亮点

### 1. 动态 SQL 构建

**INSERT 示例**：
```java
// 输入：{"name": "张三", "amount": 1000, "status": "pending"}
// 生成：INSERT INTO orders (name, amount, status) VALUES (?, ?, ?) RETURNING id
```

**UPDATE 示例**：
```java
// 输入：{"status": "completed", "amount": 1200}, id=1
// 生成：UPDATE orders SET status = ?, amount = ? WHERE id = ?
```

**SELECT 示例**：
```java
// 输入：{"status": "active", "type": "premium"}
// 生成：SELECT * FROM orders WHERE status = ? AND type = ?
```

### 2. 集合名称映射

通过 `nocobase_collections` 表实现逻辑名称到物理表名的映射：

| 集合名称 | 物理表名 |
|----------|----------|
| orders | app_orders_v1 |
| users | app_users_v1 |
| products | catalog_products |

### 3. 参数化查询

所有 SQL 语句都使用 `?` 占位符，防止 SQL 注入：

```java
// 安全
jdbcTemplate.queryForObject(
    "SELECT * FROM " + tableName + " WHERE id = ?",
    Long.class,
    id
);

// 不安全（字符串拼接）- 未使用
// "SELECT * FROM " + tableName + " WHERE id = " + id
```

---

## 下一步计划

### 待开发功能
- [ ] nocobase-data 模块 Controller 层
  - [ ] CollectionController（集合管理 API）
  - [ ] FieldController（字段管理 API）
  - [ ] DynamicTableController（动态表操作 API）

- [ ] 动态数据服务增强
  - [ ] 分页支持（Page 对象）
  - [ ] 排序功能（ORDER BY）
  - [ ] 复杂查询条件（AND/OR 嵌套）
  - [ ] 事务支持（@Transactional）
  - [ ] 批量操作（批量插入、批量更新）

- [ ] nocobase-user 模块 Controller 层
  - [ ] UserController（用户 CRUD API）
  - [ ] RoleController（角色 CRUD API）
  - [ ] PermissionController（权限 CRUD API）

- [ ] 认证授权模块（nocobase-auth）
  - [ ] JWT Token 生成和验证
  - [ ] 登录端点（POST /api/v1/auth/login）
  - [ ] 注册端点（POST /api/v1/auth/register）
  - [ ] 密码加密（BCrypt）

### 待提交的更改
```
新增文件：
nocobase-common/src/main/java/com/nocobase/common/response/ApiResponse.java
nocobase-data/src/main/java/com/nocobase/data/service/DataRecordService.java
nocobase-data/src/main/java/com/nocobase/data/controller/DataController.java
nocobase-data/DATA_CRUD_API.md
```

---

## 今天的主要成就

1. ✅ **统一响应格式**：创建 `ApiResponse` 类，规范所有 API 响应
2. ✅ **动态数据 CRUD 服务**：实现 `DataRecordService`，支持任意表的增删改查
3. ✅ **REST API 控制器**：实现 `DataController`，提供 7 个 REST 端点
4. ✅ **编译验证**：所有模块编译通过，无错误
5. ✅ **API 文档**：创建完整的 API 使用指南（DATA_CRUD_API.md）

---

**更新时间**：2026-01-07 下午（第二次更新）
