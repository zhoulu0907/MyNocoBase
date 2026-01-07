# NocoBase Java+React 重构技术设计文档

## 文档信息

| 项目 | 内容 |
|------|------|
| 文档名称 | NocoBase Java+React 重构技术设计文档 |
| 版本 | 1.0.0 |
| 编写日期 | 2026-01-06 |
| 基于文档 | REQUIREMENTS.md, USER_MANUAL.md, API_REFERENCE.md |
| 文档状态 | 技术设计阶段 |

---

## 目录

1. [重构概述](#1-重构概述)
2. [技术架构设计](#2-技术架构设计)
3. [后端架构设计](#3-后端架构设计)
4. [前端架构设计](#4-前端架构设计)
5. [数据库设计](#5-数据库设计)
6. [核心模块设计](#6-核心模块设计)
7. [接口设计](#7-接口设计)
8. [安全设计](#8-安全设计)
9. [部署架构](#9-部署架构)
10. [实施计划](#10-实施计划)

---

## 1. 重构概述

### 1.1 重构目标

基于NocoBase需求文档（REQUIREMENTS.md），将现有的Node.js+Koa技术栈重构为Java+React技术栈，实现以下目标：

- 保持所有功能特性不变（F-001至F-064）
- 提升系统性能和可维护性
- 增强企业级应用能力
- 保持前端React技术栈不变
- 实现后端企业级Java架构

### 1.2 技术栈对比

| 层级 | 原技术栈 | 新技术栈 | 变更说明 |
|------|---------|---------|---------|
| **前端** | React 18+ | React 18+ | 保持不变 |
| **前端UI库** | Ant Design 5+ | Ant Design 5+ | 保持不变 |
| **前端表单** | Formily 1.0+ | Formily 1.0+ | 保持不变 |
| **后端语言** | Node.js | Java 17/21 | 升级为强类型 |
| **后端框架** | Koa 2+ | Spring Boot 3.x | 企业级框架 |
| **ORM** | Sequelize 6+ | Spring Data JPA | 成熟ORM方案 |
| **认证** | JWT | Spring Security + JWT | 增强安全性 |
| **缓存** | Redis | Spring Data Redis | 保持不变 |
| **数据库** | PostgreSQL 12+ | PostgreSQL 12+ | 保持不变 |
| **构建工具** | Yarn | Maven 3.9+ | 标准化构建 |

### 1.3 重构原则

1. **功能等价性**: 所有功能点保持不变
2. **接口兼容性**: REST API接口保持兼容
3. **数据兼容性**: 数据库结构保持兼容
4. **渐进式迁移**: 支持分模块渐进式重构
5. **性能优先**: 性能指标优于原系统

---

## 2. 技术架构设计

### 2.1 整体架构

```
┌─────────────────────────────────────────────────────────────┐
│                        前端层 (React)                        │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  React 18 + Ant Design 5 + Formily + React Router 6  │  │
│  └──────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
                              ↓ HTTP/REST
┌─────────────────────────────────────────────────────────────┐
│                    单体应用层 (Spring Boot)                    │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  REST API | 统一认证 | 路由分发 | 业务模块   │  │
│  └──────────────────────────────────────────────────────┘  │
│                                                       │
│  业务模块 (包结构):                                     │
│  ┌──────────┬──────────┬──────────┬──────────┐  │
│  │ 认证模块 │ 用户模块 │ 数据模型 │ 权限工作流AI │  │
│  │  (auth)   │  (user)   │  (data)   │  (permission/wf/ai/file)  │
│  └──────────┴──────────┴──────────┴──────────┘  │
└─────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────┐
│                       数据层                                 │
│  ┌──────────────────┬──────────────────┬──────────────────┐ │
│  │  PostgreSQL      │  Redis           │  MinIO/OSS       │ │
│  │  (主数据库)      │  (缓存/会话)     │  (文件存储)      │ │
│  └──────────────────┴──────────────────┴──────────────────┘ │
└─────────────────────────────────────────────────────────────┘
```

### 2.2 分层架构

#### 2.2.1 表现层 (Presentation Layer)

- **技术**: React 18 + Ant Design 5
- **职责**: 用户界面展示和交互
- **组件**: 页面、区块、表单、表格

#### 2.2.2 单体应用层 (Monolithic Application Layer)

- **技术**: Spring Boot 3.x + Spring MVC
- **职责**: 统一应用入口、REST API、路由分发
- **特性**: 统一鉴权、模块化包结构、统一配置

#### 2.2.3 业务模块层 (Business Module Layer)

- **技术**: Spring Boot 3.x + Spring MVC
- **职责**: 业务逻辑实现
- **模块**: 用户管理、数据模型、权限控制、工作流、AI集成

#### 2.2.4 数据访问层 (Data Access Layer)

- **技术**: Spring Data JPA + Hibernate
- **职责**: 数据持久化
- **特性**: Repository模式、查询优化、事务管理

#### 2.2.5 基础设施层 (Infrastructure Layer)

- **技术**: Spring Security + Redis + PostgreSQL
- **职责**: 安全、缓存、存储
- **特性**: 认证授权、分布式缓存、文件存储

---

## 3. 后端架构设计

### 3.1 项目结构

```
nocobase-java/
├── nocobase-server/            # 单体应用主模块
├── nocobase-auth/              # 认证模块
├── nocobase-user/              # 用户模块
├── nocobase-data/              # 数据模型模块
├── nocobase-permission/        # 权限模块
├── nocobase-workflow/          # 工作流模块
├── nocobase-ai/                # AI模块
├── nocobase-file/              # 文件模块
└── nocobase-common/            # 公共模块
    ├── nocobase-common-core/   # 核心工具类
    ├── nocobase-common-security/ # 安全模块
    ├── nocobase-common-redis/  # Redis模块
    ├── nocobase-common-database/ # 数据库模块
    └── nocobase-common-plugin/ # 插件系统
```

### 3.2 核心包结构

```
com.nocobase.[module_name]/
├── controller/          # REST控制器
├── service/            # 业务服务层
│   └── impl/          # 服务实现
├── repository/         # 数据访问层
├── entity/            # JPA实体
├── dto/               # 数据传输对象
│   ├── request/      # 请求DTO
│   └── response/     # 响应DTO
├── vo/                # 视图对象
├── converter/         # 对象转换器
├── validator/         # 参数校验器
├── exception/         # 异常处理
├── config/            # 配置类
├── security/          # 安全配置
├── plugin/            # 插件系统
└── util/              # 工具类
```

### 3.3 核心技术选型

| 技术 | 版本 | 用途 |
|------|------|------|
| **Spring Boot** | 3.2.0 | 基础框架 |
| **Spring Security** | 6.2.0 | 安全框架 |
| **Spring Data JPA** | 3.2.0 | ORM框架 |
| **Spring Cloud Gateway** | 4.1.0 | API网关 |
| **Hibernate** | 6.4.0 | JPA实现 |
| **Jackson** | 2.16.0 | JSON处理 |
| **Lombok** | 1.18.30 | 简化代码 |
| **MapStruct** | 1.6.0 | 对象映射 |
| **SpringDoc** | 2.3.0 | API文档 |
| **jjwt** | 0.12.3 | JWT处理 |

---

## 4. 前端架构设计

### 4.1 前端技术栈保持

**决策**: 前端完全保持现有技术栈不变，原因：

1. React技术栈成熟稳定
2. 与NocoBase现有UI组件高度耦合
3. 重构成本低、风险小
4. 团队学习成本为零

### 4.2 前端项目结构

```
nocobase-admin/
├── src/
│   ├── components/       # 通用组件
│   │   ├── blocks/      # 区块组件
│   │   ├── actions/     # 操作组件
│   │   └── fields/      # 字段组件
│   ├── pages/           # 页面组件
│   ├── hooks/           # 自定义Hooks
│   ├── services/        # API服务
│   ├── stores/          # 状态管理
│   ├── utils/           # 工具函数
│   ├── constants/       # 常量定义
│   ├── types/           # TypeScript类型
│   └── styles/          # 样式文件
├── public/
└── package.json
```

### 4.3 前后端集成

**通信方式**: REST API + JWT认证

**API客户端配置**:

```typescript
// src/services/api.ts
import axios, { AxiosInstance } from 'axios';

const apiClient: AxiosInstance = axios.create({
  baseURL: process.env.REACT_APP_API_BASE_URL || 'http://localhost:8080/api',
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// 请求拦截器：添加JWT Token
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// 响应拦截器：处理错误
apiClient.interceptors.response.use(
  (response) => response.data,
  (error) => {
    if (error.response?.status === 401) {
      // Token过期，跳转登录
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export default apiClient;
```

---

## 5. 数据库设计

### 5.1 核心数据表

基于REQUIREMENTS.md中的功能需求，设计核心数据表：

#### 5.1.1 用户相关表

```sql
-- 用户表
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    nickname VARCHAR(255),
    avatar VARCHAR(500),
    phone VARCHAR(20),
    status VARCHAR(20) DEFAULT 'active',
    system BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP
);

-- 角色表
CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) UNIQUE NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    default BOOLEAN DEFAULT FALSE,
    status VARCHAR(20) DEFAULT 'active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP
);

-- 用户角色关联表
CREATE TABLE users_roles (
    user_id BIGINT REFERENCES users(id),
    role_id BIGINT REFERENCES roles(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, role_id)
);
```

#### 5.1.2 数据模型相关表

```sql
-- 数据集（集合）表
CREATE TABLE collections (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) UNIQUE NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    table_name VARCHAR(255) UNIQUE NOT NULL,
    options JSONB,
    hidden BOOLEAN DEFAULT FALSE,
    sortable BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP
);

-- 字段表
CREATE TABLE fields (
    id BIGSERIAL PRIMARY KEY,
    collection_id BIGINT REFERENCES collections(id),
    name VARCHAR(255) NOT NULL,
    type VARCHAR(100) NOT NULL,
    interface VARCHAR(100),
    ui_schema JSONB,
    options JSONB,
    description TEXT,
    required BOOLEAN DEFAULT FALSE,
    unique BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP
);

-- 关系表
CREATE TABLE relations (
    id BIGSERIAL PRIMARY KEY,
    source_collection_id BIGINT REFERENCES collections(id),
    target_collection_id BIGINT REFERENCES collections(id),
    field_id BIGINT REFERENCES fields(id),
    target_field_id BIGINT REFERENCES fields(id),
    type VARCHAR(50) NOT NULL, -- oneToOne, oneToMany, manyToMany
    name VARCHAR(255) NOT NULL,
    foreign_key VARCHAR(255),
    through_table VARCHAR(255),
    options JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

#### 5.1.3 权限相关表

```sql
-- 权限表
CREATE TABLE permissions (
    id BIGSERIAL PRIMARY KEY,
    role_id BIGINT REFERENCES roles(id),
    resource_name VARCHAR(255) NOT NULL,
    action_name VARCHAR(100) NOT NULL,
    conditions JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 权限策略表
CREATE TABLE permission_strategies (
    id BIGSERIAL PRIMARY KEY,
    permission_id BIGINT REFERENCES permissions(id),
    type VARCHAR(50) NOT NULL, -- allow, deny
    scope JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

#### 5.1.4 工作流相关表

```sql
-- 工作流表
CREATE TABLE workflows (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    key VARCHAR(255) UNIQUE NOT NULL,
    type VARCHAR(50) NOT NULL, -- request, action
    status VARCHAR(20) DEFAULT 'active',
    trigger_config JSONB,
    config JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP
);

-- 工作流执行记录表
CREATE TABLE workflow_executions (
    id BIGSERIAL PRIMARY KEY,
    workflow_id BIGINT REFERENCES workflows(id),
    status VARCHAR(20) DEFAULT 'pending',
    context JSONB,
    result JSONB,
    error_message TEXT,
    started_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ended_at TIMESTAMP
);
```

#### 5.1.5 插件相关表

```sql
-- 插件表
CREATE TABLE plugins (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) UNIQUE NOT NULL,
    version VARCHAR(50) NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    enabled BOOLEAN DEFAULT FALSE,
    installed BOOLEAN DEFAULT FALSE,
    options JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### 5.2 JPA实体映射

#### 5.2.1 用户实体

```java
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 255)
    private String username;

    @Column(unique = true, nullable = false, length = 255)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(length = 255)
    private String nickname;

    @Column(length = 500)
    private String avatar;

    @Column(length = 20)
    private String phone;

    @Column(length = 20)
    private String status = "active";

    @Column(name = "system")
    private Boolean system = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // 多对多关系：用户-角色
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "users_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    // 一对多关系：用户创建的数据集
    @OneToMany(mappedBy = "createdBy")
    private List<Collection> collections;
}
```

#### 5.2.2 数据集实体

```java
@Entity
@Table(name = "collections")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Collection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 255)
    private String name;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(unique = true, nullable = false, length = 255)
    private String tableName;

    @Column(columnDefinition = "JSONB")
    private String options;

    @Column
    private Boolean hidden = false;

    @Column
    private Boolean sortable = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // 一对多关系：数据集的字段
    @OneToMany(mappedBy = "collection", cascade = CascadeType.ALL)
    private List<Field> fields;

    // 一对多关系：数据集的关系
    @OneToMany(mappedBy = "sourceCollection")
    private List<Relation> sourceRelations;

    @OneToMany(mappedBy = "targetCollection")
    private List<Relation> targetRelations;

    // 多对一关系：创建者
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id")
    private User createdBy;
}
```

---

## 6. 核心模块设计

### 6.1 认证授权模块

#### 6.1.1 Spring Security配置

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**", "/api/public/**").permitAll()
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }
}
```

#### 6.1.2 JWT过滤器

```java
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);
        userEmail = jwtService.extractUsername(jwt);

        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

            if (jwtService.isTokenValid(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
```

### 6.2 数据模型管理模块

#### 6.2.1 Collection服务

```java
@Service
@RequiredArgsConstructor
@Transactional
public class CollectionServiceImpl implements CollectionService {

    private final CollectionRepository collectionRepository;
    private final FieldRepository fieldRepository;
    private final DynamicTableManager dynamicTableManager;

    @Override
    public CollectionDTO createCollection(CollectionCreateRequest request) {
        // 1. 验证集合名称唯一性
        if (collectionRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Collection name already exists");
        }

        // 2. 创建Collection实体
        Collection collection = Collection.builder()
            .name(request.getName())
            .title(request.getTitle())
            .description(request.getDescription())
            .tableName(request.getTableName())
            .options(request.getOptions())
            .build();

        collection = collectionRepository.save(collection);

        // 3. 动态创建数据库表
        dynamicTableManager.createTable(collection);

        // 4. 创建字段
        if (request.getFields() != null) {
            request.getFields().forEach(fieldRequest -> {
                Field field = Field.builder()
                    .collection(collection)
                    .name(fieldRequest.getName())
                    .type(fieldRequest.getType())
                    .interface(fieldRequest.getInterface())
                    .options(fieldRequest.getOptions())
                    .required(fieldRequest.getRequired())
                    .unique(fieldRequest.getUnique())
                    .build();
                fieldRepository.save(field);

                // 动态添加表字段
                dynamicTableManager.addColumn(collection, field);
            });
        }

        return CollectionMapper.toDTO(collection);
    }

    @Override
    public CollectionDTO getCollection(Long id) {
        Collection collection = collectionRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Collection not found"));
        return CollectionMapper.toDTO(collection);
    }

    @Override
    public PageResult<CollectionDTO> listCollections(CollectionQueryRequest request) {
        Pageable pageable = PageRequest.of(
            request.getPage() - 1,
            request.getPageSize()
        );

        Page<Collection> page = collectionRepository.findAll(pageable);

        return PageResult.<CollectionDTO>builder()
            .data(page.getContent().stream()
                .map(CollectionMapper::toDTO)
                .collect(Collectors.toList()))
            .total(page.getTotalElements())
            .page(request.getPage())
            .pageSize(request.getPageSize())
            .totalPage(page.getTotalPages())
            .build();
    }
}
```

#### 6.2.2 动态表管理器

```java
@Component
@RequiredArgsConstructor
public class DynamicTableManager {

    private final DataSource dataSource;
    private final JpaMetamodel jpaMetamodel;

    public void createTable(Collection collection) {
        String tableName = collection.getTableName();

        StringBuilder sql = new StringBuilder();
        sql.append("CREATE TABLE ").append(tableName).append(" (");
        sql.append("id BIGSERIAL PRIMARY KEY, ");
        sql.append("created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, ");
        sql.append("updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, ");
        sql.append("created_by_id BIGINT, ");
        sql.append("updated_by_id BIGINT");
        sql.append(")");

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql.toString());
        } catch (SQLException e) {
            throw new SystemException("Failed to create table: " + tableName, e);
        }
    }

    public void addColumn(Collection collection, Field field) {
        String tableName = collection.getTableName();
        String columnName = field.getName();
        String columnType = mapFieldTypeToSQL(field.getType());

        StringBuilder sql = new StringBuilder();
        sql.append("ALTER TABLE ").append(tableName);
        sql.append(" ADD COLUMN ").append(columnName).append(" ").append(columnType);

        if (field.getRequired()) {
            sql.append(" NOT NULL");
        }

        if (field.getUnique()) {
            sql.append(" UNIQUE");
        }

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql.toString());
        } catch (SQLException e) {
            throw new SystemException("Failed to add column: " + columnName, e);
        }
    }

    private String mapFieldTypeToSQL(String fieldType) {
        return switch (fieldType) {
            case "string" -> "VARCHAR(255)";
            case "text" -> "TEXT";
            case "integer" -> "BIGINT";
            case "decimal" -> "DECIMAL(20,2)";
            case "boolean" -> "BOOLEAN";
            case "date" -> "DATE";
            case "datetime" -> "TIMESTAMP";
            case "json" -> "JSONB";
            case "uid" -> "VARCHAR(20)";
            default -> throw new IllegalArgumentException("Unknown field type: " + fieldType);
        };
    }
}
```

### 6.3 插件系统设计

#### 6.3.1 插件接口定义

```java
public interface Plugin {

    /**
     * 插件名称（唯一标识）
     */
    String getName();

    /**
     * 插件显示名称
     */
    String getTitle();

    /**
     * 插件描述
     */
    String getDescription();

    /**
     * 插件版本
     */
    String getVersion();

    /**
     * 插件初始化
     */
    void initialize() throws PluginException;

    /**
     * 插件启动
     */
    void start() throws PluginException;

    /**
     * 插件停止
     */
    void stop() throws PluginException;

    /**
     * 插件卸载
     */
    void unload() throws PluginException;

    /**
     * 获取插件依赖
     */
    default List<PluginDependency> getDependencies() {
        return Collections.emptyList();
    }

    /**
     * 插件配置
     */
    default void configure(Map<String, Object> config) throws PluginException {
        // 默认实现：无配置
    }
}
```

#### 6.3.2 插件管理器

```java
@Component
@RequiredArgsConstructor
public class PluginManager {

    private final Map<String, Plugin> plugins = new ConcurrentHashMap<>();
    private final PluginRepository pluginRepository;
    private final ApplicationContext applicationContext;

    /**
     * 加载插件
     */
    public void loadPlugin(Plugin plugin) throws PluginException {
        // 1. 检查插件依赖
        validateDependencies(plugin);

        // 2. 初始化插件
        plugin.initialize();

        // 3. 注册插件
        plugins.put(plugin.getName(), plugin);

        // 4. 保存到数据库
        PluginEntity entity = PluginEntity.builder()
            .name(plugin.getName())
            .version(plugin.getVersion())
            .title(plugin.getTitle())
            .description(plugin.getDescription())
            .enabled(true)
            .installed(true)
            .build();
        pluginRepository.save(entity);

        // 5. 启动插件
        plugin.start();
    }

    /**
     * 启用插件
     */
    public void enablePlugin(String pluginName) throws PluginException {
        Plugin plugin = plugins.get(pluginName);
        if (plugin == null) {
            throw new PluginNotFoundException("Plugin not found: " + pluginName);
        }

        plugin.start();

        PluginEntity entity = pluginRepository.findByName(pluginName)
            .orElseThrow(() -> new PluginNotFoundException("Plugin not found"));
        entity.setEnabled(true);
        pluginRepository.save(entity);
    }

    /**
     * 禁用插件
     */
    public void disablePlugin(String pluginName) throws PluginException {
        Plugin plugin = plugins.get(pluginName);
        if (plugin == null) {
            throw new PluginNotFoundException("Plugin not found: " + pluginName);
        }

        plugin.stop();

        PluginEntity entity = pluginRepository.findByName(pluginName)
            .orElseThrow(() -> new PluginNotFoundException("Plugin not found"));
        entity.setEnabled(false);
        pluginRepository.save(entity);
    }

    /**
     * 验证插件依赖
     */
    private void validateDependencies(Plugin plugin) throws PluginException {
        for (PluginDependency dep : plugin.getDependencies()) {
            Plugin depPlugin = plugins.get(dep.getName());
            if (depPlugin == null) {
                throw new PluginException("Missing dependency: " + dep.getName());
            }

            if (!versionMatches(depPlugin.getVersion(), dep.getVersion())) {
                throw new PluginException("Version mismatch for dependency: " + dep.getName());
            }
        }
    }

    private boolean versionMatches(String actualVersion, String requiredVersion) {
        // 实现版本匹配逻辑
        return true;
    }
}
```

### 6.4 工作流引擎设计

#### 6.4.1 工作流定义

```java
@Entity
@Table(name = "workflows")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Workflow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(unique = true, nullable = false)
    private String key;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkflowType type;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private WorkflowStatus status = WorkflowStatus.ACTIVE;

    @Column(columnDefinition = "JSONB")
    private String triggerConfig;

    @Column(columnDefinition = "JSONB")
    private String config;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}

public enum WorkflowType {
    REQUEST,  // 请求类型（可配置触发器）
    ACTION    // 动作类型（手动触发）
}

public enum WorkflowStatus {
    ACTIVE,
    INACTIVE,
    DRAFT
}
```

#### 6.4.2 工作流执行器

```java
@Service
@RequiredArgsConstructor
public class WorkflowExecutor {

    private final WorkflowRepository workflowRepository;
    private final WorkflowExecutionRepository executionRepository;
    private final NodeExecutorFactory nodeExecutorFactory;

    /**
     * 执行工作流
     */
    public WorkflowExecutionResult execute(WorkflowTrigger trigger) {
        // 1. 查找匹配的工作流
        List<Workflow> workflows = workflowRepository.findByTriggerType(trigger.getType());

        // 2. 为每个工作流创建执行记录
        for (Workflow workflow : workflows) {
            executeWorkflow(workflow, trigger.getContext());
        }

        return WorkflowExecutionResult.builder()
            .executedCount(workflows.size())
            .build();
    }

    /**
     * 执行单个工作流
     */
    private void executeWorkflow(Workflow workflow, Map<String, Object> context) {
        // 创建执行记录
        WorkflowExecution execution = WorkflowExecution.builder()
            .workflow(workflow)
            .status(ExecutionStatus.RUNNING)
            .context(context)
            .startedAt(LocalDateTime.now())
            .build();
        executionRepository.save(execution);

        try {
            // 解析工作流配置
            WorkflowConfig config = parseWorkflowConfig(workflow.getConfig());

            // 执行工作流节点
            Map<String, Object> result = executeNodes(config.getNodes(), context);

            // 更新执行状态为成功
            execution.setStatus(ExecutionStatus.COMPLETED);
            execution.setResult(result);
            execution.setEndedAt(LocalDateTime.now());
            executionRepository.save(execution);

        } catch (Exception e) {
            // 更新执行状态为失败
            execution.setStatus(ExecutionStatus.FAILED);
            execution.setErrorMessage(e.getMessage());
            execution.setEndedAt(LocalDateTime.now());
            executionRepository.save(execution);

            throw new WorkflowExecutionException("Workflow execution failed", e);
        }
    }

    /**
     * 执行节点
     */
    private Map<String, Object> executeNodes(List<NodeConfig> nodes, Map<String, Object> context) {
        Map<String, Object> result = new HashMap<>(context);

        for (NodeConfig node : nodes) {
            NodeExecutor executor = nodeExecutorFactory.getExecutor(node.getType());
            result = executor.execute(node, result);
        }

        return result;
    }
}
```

---

## 7. 接口设计

### 7.1 REST API规范

#### 7.1.1 API路径规范

```
基础路径: /api/v1

认证相关: /api/v1/auth/*
用户管理: /api/v1/users/*
数据模型: /api/v1/collections/*
权限管理: /api/v1/permissions/*
角色管理: /api/v1/roles/*
工作流: /api/v1/workflows/*
文件管理: /api/v1/files/*
插件管理: /api/v1/plugins/*
系统配置: /api/v1/system/*
```

#### 7.1.2 统一响应格式

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    private Integer code;
    private String message;
    private T data;
    private Long timestamp;

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
            .code(200)
            .message("Success")
            .data(data)
            .timestamp(System.currentTimeMillis())
            .build();
    }

    public static <T> ApiResponse<T> error(Integer code, String message) {
        return ApiResponse.<T>builder()
            .code(code)
            .message(message)
            .timestamp(System.currentTimeMillis())
            .build();
    }
}
```

### 7.2 核心接口定义

#### 7.2.1 认证接口

```java
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "认证", description = "用户认证相关接口")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public ApiResponse<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ApiResponse.success(response);
    }

    @PostMapping("/register")
    @Operation(summary = "用户注册")
    public ApiResponse<RegisterResponse> register(@RequestBody @Valid RegisterRequest request) {
        RegisterResponse response = authService.register(request);
        return ApiResponse.success(response);
    }

    @PostMapping("/logout")
    @Operation(summary = "用户登出")
    public ApiResponse<Void> logout() {
        authService.logout();
        return ApiResponse.success(null);
    }

    @PostMapping("/refresh")
    @Operation(summary = "刷新Token")
    public ApiResponse<RefreshTokenResponse> refresh(@RequestBody @Valid RefreshTokenRequest request) {
        RefreshTokenResponse response = authService.refreshToken(request);
        return ApiResponse.success(response);
    }
}
```

#### 7.2.2 数据模型接口

```java
@RestController
@RequestMapping("/api/v1/collections")
@RequiredArgsConstructor
@Tag(name = "数据模型", description = "数据模型管理接口")
public class CollectionController {

    private final CollectionService collectionService;

    @PostMapping
    @Operation(summary = "创建数据集")
    @PreAuthorize("hasAuthority('collection:create')")
    public ApiResponse<CollectionDTO> create(@RequestBody @Valid CollectionCreateRequest request) {
        CollectionDTO dto = collectionService.createCollection(request);
        return ApiResponse.success(dto);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取数据集详情")
    @PreAuthorize("hasAuthority('collection:view')")
    public ApiResponse<CollectionDTO> get(@PathVariable Long id) {
        CollectionDTO dto = collectionService.getCollection(id);
        return ApiResponse.success(dto);
    }

    @GetMapping
    @Operation(summary = "获取数据集列表")
    @PreAuthorize("hasAuthority('collection:view')")
    public ApiResponse<PageResult<CollectionDTO>> list(@Valid CollectionQueryRequest request) {
        PageResult<CollectionDTO> result = collectionService.listCollections(request);
        return ApiResponse.success(result);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新数据集")
    @PreAuthorize("hasAuthority('collection:update')")
    public ApiResponse<CollectionDTO> update(
        @PathVariable Long id,
        @RequestBody @Valid CollectionUpdateRequest request
    ) {
        CollectionDTO dto = collectionService.updateCollection(id, request);
        return ApiResponse.success(dto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除数据集")
    @PreAuthorize("hasAuthority('collection:delete')")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        collectionService.deleteCollection(id);
        return ApiResponse.success(null);
    }
}
```

#### 7.2.3 数据记录接口

```java
@RestController
@RequestMapping("/api/v1/collections/{collectionName}/records")
@RequiredArgsConstructor
@Tag(name = "数据记录", description = "数据记录管理接口")
public class RecordController {

    private final RecordService recordService;

    @PostMapping
    @Operation(summary = "创建记录")
    @PreAuthorize("hasAuthority('record:create')")
    public ApiResponse<Map<String, Object>> create(
        @PathVariable String collectionName,
        @RequestBody Map<String, Object> data
    ) {
        Map<String, Object> result = recordService.createRecord(collectionName, data);
        return ApiResponse.success(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取记录详情")
    @PreAuthorize("hasAuthority('record:view')")
    public ApiResponse<Map<String, Object>> get(
        @PathVariable String collectionName,
        @PathVariable Long id
    ) {
        Map<String, Object> result = recordService.getRecord(collectionName, id);
        return ApiResponse.success(result);
    }

    @GetMapping
    @Operation(summary = "获取记录列表")
    @PreAuthorize("hasAuthority('record:view')")
    public ApiResponse<PageResult<Map<String, Object>>> list(
        @PathVariable String collectionName,
        @Valid RecordQueryRequest request
    ) {
        PageResult<Map<String, Object>> result = recordService.listRecords(collectionName, request);
        return ApiResponse.success(result);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新记录")
    @PreAuthorize("hasAuthority('record:update')")
    public ApiResponse<Map<String, Object>> update(
        @PathVariable String collectionName,
        @PathVariable Long id,
        @RequestBody Map<String, Object> data
    ) {
        Map<String, Object> result = recordService.updateRecord(collectionName, id, data);
        return ApiResponse.success(result);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除记录")
    @PreAuthorize("hasAuthority('record:delete')")
    public ApiResponse<Void> delete(
        @PathVariable String collectionName,
        @PathVariable Long id
    ) {
        recordService.deleteRecord(collectionName, id);
        return ApiResponse.success(null);
    }
}
```

---

## 8. 安全设计

### 8.1 认证机制

#### 8.1.1 JWT Token设计

**Access Token**:
- 有效期: 2小时
- 存储位置: LocalStorage
- 包含信息: 用户ID、用户名、角色列表

**Refresh Token**:
- 有效期: 7天
- 存储位置: HttpOnly Cookie
- 用于刷新Access Token

#### 8.1.2 密码策略

- 最小长度: 8个字符
- 复杂度要求: 必须包含大小写字母、数字
- 加密算法: BCrypt (salt rounds = 10)
- 过期策略: 90天强制修改

### 8.2 权限控制

#### 8.2.1 RBAC权限模型

```
用户 (User)
  ↓ N:M
角色 (Role)
  ↓ 1:N
权限 (Permission)
  ↓
资源 + 操作
```

#### 8.2.2 权限注解

```java
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasAuthority(#permission)")
public @interface RequirePermission {
    String value();
    String type() default "resource";
}

// 使用示例
@RequirePermission(value = "collection:create", type = "resource")
public ApiResponse<CollectionDTO> create(@RequestBody CollectionCreateRequest request) {
    // ...
}
```

### 8.3 安全防护

#### 8.3.1 防护措施

| 威胁 | 防护措施 |
|------|---------|
| SQL注入 | JPA参数化查询 |
| XSS | 输入过滤、输出转义 |
| CSRF | CSRF Token验证 |
| CSRF | SameSite Cookie属性 |
| 暴力破解 | 登录限流（5次/分钟） |
| 会话劫持 | HttpOnly Cookie + HTTPS |
| 敏感数据泄露 | AES-256加密存储 |

---

## 9. 部署架构

### 9.1 容器化部署

#### 9.1.1 Docker Compose配置

```yaml
version: '3.8'

services:
  # PostgreSQL数据库
  postgres:
    image: postgres:14-alpine
    container_name: nocobase-postgres
    environment:
      POSTGRES_DB: nocobase
      POSTGRES_USER: nocobase
      POSTGRES_PASSWORD: nocobase_password
    ports:
      - "5432:5432"
    volumes:
      - postgres-data:/var/lib/postgresql/data
    networks:
      - nocobase-network

  # Redis缓存
  redis:
    image: redis:7-alpine
    container_name: nocobase-redis
    ports:
      - "6379:6379"
    volumes:
      - redis-data:/data
    networks:
      - nocobase-network

  # API网关
  gateway:
    build: ./nocobase-gateway
    container_name: nocobase-gateway
    ports:
      - "8080:8080"
    environment:
      SPRING_PROFILES_ACTIVE: docker
      EUREKA_CLIENT_ENABLED: false
    depends_on:
      - auth-service
      - user-service
      - data-service
    networks:
      - nocobase-network

  # 认证服务
  auth-service:
    build: ./nocobase-auth
    container_name: nocobase-auth
    ports:
      - "8081:8081"
    environment:
      SPRING_PROFILES_ACTIVE: docker
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/nocobase
      SPRING_REDIS_HOST: redis
    depends_on:
      - postgres
      - redis
    networks:
      - nocobase-network

  # 用户服务
  user-service:
    build: ./nocobase-user
    container_name: nocobase-user
    ports:
      - "8082:8082"
    environment:
      SPRING_PROFILES_ACTIVE: docker
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/nocobase
      SPRING_REDIS_HOST: redis
    depends_on:
      - postgres
      - redis
    networks:
      - nocobase-network

  # 数据模型服务
  data-service:
    build: ./nocobase-data
    container_name: nocobase-data
    ports:
      - "8083:8083"
    environment:
      SPRING_PROFILES_ACTIVE: docker
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/nocobase
      SPRING_REDIS_HOST: redis
    depends_on:
      - postgres
      - redis
    networks:
      - nocobase-network

  # 前端应用
  admin:
    build: ./nocobase-admin
    container_name: nocobase-admin
    ports:
      - "3000:80"
    depends_on:
      - gateway
    networks:
      - nocobase-network

volumes:
  postgres-data:
  redis-data:

networks:
  nocobase-network:
    driver: bridge
```

### 9.2 生产部署建议

#### 9.2.1 Kubernetes部署

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: nocobase-gateway
  namespace: nocobase
spec:
  replicas: 3
  selector:
    matchLabels:
      app: nocobase-gateway
  template:
    metadata:
      labels:
        app: nocobase-gateway
    spec:
      containers:
      - name: gateway
        image: nocobase/gateway:latest
        ports:
        - containerPort: 8080
        resources:
          requests:
            memory: "512Mi"
            cpu: "500m"
          limits:
            memory: "1Gi"
            cpu: "1000m"
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "production"
---
apiVersion: v1
kind: Service
metadata:
  name: nocobase-gateway
  namespace: nocobase
spec:
  selector:
    app: nocobase-gateway
  ports:
  - port: 80
    targetPort: 8080
  type: LoadBalancer
```

---

## 10. 实施计划

### 10.1 实施阶段

#### 阶段1: 需求分析与准备（2周）

**目标**: 完成详细需求分析和技术准备

**任务**:
1. 需求评审和细化
2. 技术栈选型确认
3. 开发环境搭建
4. 团队技术培训
5. 代码规范制定
6. CI/CD流程建立

**交付物**:
- 需求分析文档
- 技术架构文档
- 开发规范手册
- CI/CD配置

#### 阶段2: 基础架构搭建（3周）

**目标**: 完成后端基础架构和核心模块

**任务**:
1. 项目结构创建
2. Spring Boot基础配置
3. 数据库设计与初始化
4. 认证授权模块
5. 用户管理模块
6. API网关搭建
7. 插件系统框架

**交付物**:
- 可运行的Spring Boot应用
- 认证授权功能
- 用户管理功能
- 插件系统框架

#### 阶段3: 核心功能开发（6周）

**目标**: 完成核心业务功能模块

**任务**:
1. 数据模型管理（2周）
   - 数据集CRUD
   - 字段管理
   - 关系管理
   - 动态表创建

2. 权限管理（1周）
   - 角色管理
   - 权限配置
   - ACL实现

3. 工作流引擎（2周）
   - 工作流定义
   - 触发器实现
   - 节点执行器
   - 执行记录

4. AI集成（1周）
   - AI服务集成
   - 工作流AI节点

**交付物**:
- 完整的核心功能模块
- 单元测试覆盖率≥70%
- API文档

#### 阶段4: 前端适配与集成（2周）

**目标**: 前端与后端API集成

**任务**:
1. API客户端适配
2. 接口联调
3. 功能测试
4. UI调整
5. 性能优化

**交付物**:
- 完整的前后端集成系统
- 端到端测试

#### 阶段5: 测试与部署（2周）

**目标**: 系统测试和生产部署

**任务**:
1. 集成测试
2. 性能测试
3. 安全测试
4. 用户验收测试
5. 生产环境部署
6. 文档完善

**交付物**:
- 测试报告
- 部署文档
- 用户手册
- 运维手册

### 10.2 里程碑

| 里程碑 | 时间节点 | 交付内容 | 验收标准 |
|--------|---------|---------|---------|
| M1: 基础架构完成 | 第2周末 | 基础架构代码 | 应用可启动，认证功能可用 |
| M2: 数据模型完成 | 第6周末 | 数据模型模块 | 可创建数据表和字段 |
| M3: 核心功能完成 | 第11周末 | 所有核心模块 | 功能完整，测试通过 |
| M4: 前后端集成完成 | 第13周末 | 集成系统 | 端到端测试通过 |
| M5: 系统上线 | 第15周末 | 生产系统 | 性能达标，文档齐全 |

### 10.3 风险管理

#### 10.3.1 技术风险

| 风险 | 影响 | 概率 | 应对措施 |
|------|------|------|---------|
| 插件系统复杂度高 | 高 | 中 | 分阶段实现，先核心后扩展 |
| 动态表性能问题 | 高 | 中 | 引入缓存，优化查询 |
| 工作流引擎实现复杂 | 高 | 高 | 参考成熟方案，简化设计 |
| 前后端接口兼容性 | 中 | 低 | 保持接口兼容，渐进式迁移 |

#### 10.3.2 项目风险

| 风险 | 影响 | 概率 | 应对措施 |
|------|------|------|---------|
| 团队Java经验不足 | 高 | 中 | 提前培训，引入专家 |
| 需求变更频繁 | 中 | 高 | 严格变更控制流程 |
| 进度延期 | 中 | 中 | 预留缓冲时间，灵活调整 |

---

## 附录

### A. 技术术语表

| 术语 | 英文 | 说明 |
|------|------|------|
| 插件系统 | Plugin System | 允许动态扩展系统功能的架构 |
| 工作流引擎 | Workflow Engine | 执行和管理业务流程的引擎 |
| 数据模型 | Data Model | 数据库结构的抽象表示 |
| 访问控制列表 | ACL | Access Control List，权限控制机制 |
| 基于角色的访问控制 | RBAC | Role-Based Access Control |

### B. 参考文档

- Spring Boot官方文档: https://spring.io/projects/spring-boot
- Spring Security文档: https://docs.spring.io/spring-security/
- PostgreSQL文档: https://www.postgresql.org/docs/
- NocoBase需求文档: REQUIREMENTS.md
- NocoBase用户手册: USER_MANUAL.md
- NocoBase API文档: API_REFERENCE.md

---

**文档结束**

本技术设计文档基于NocoBase的三份核心文档生成，为Java+React技术栈的系统重构提供了完整的技术方案和实施计划。
