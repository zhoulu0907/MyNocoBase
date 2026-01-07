# NocoBase API 接口说明书

## 文档信息

| 项目 | 内容 |
|------|------|
| 文档名称 | NocoBase API 接口说明书 |
| 版本 | 1.0.0 |
| 编写日期 | 2026-01-06 |
| API 版本 | v1.0 |
| 基础 URL | `/api` |
| 文档状态 | 正式发布 |

---

## 目录

1. [API 概述](#1-api-概述)
2. [认证机制](#2-认证机制)
3. [通用规范](#3-通用规范)
4. [数据表 API](#4-数据表-api)
5. [用户管理 API](#5-用户管理-api)
6. [权限管理 API](#6-权限管理-api)
7. [工作流 API](#7-工作流-api)
8. [文件管理 API](#8-文件管理-api)
9. [系统配置 API](#9-系统配置-api)
10. [错误处理](#10-错误处理)
11. [SDK 使用](#11-sdk-使用)

---

## 1. API 概述

### 1.1 API 架构

NocoBase 采用 RESTful API 设计风格，为每个数据表自动生成 CRUD 接口。

```
API 基础路径: /api

示例接口:
GET    /api/users          # 获取用户列表
GET    /api/users/:id      # 获取用户详情
POST   /api/users          # 创建用户
PUT    /api/users/:id      # 更新用户
DELETE /api/users/:id      # 删除用户
```

### 1.2 技术特性

| 特性 | 说明 |
|------|------|
| **RESTful 设计** | 遵循 REST 架构风格 |
| **JSON 格式** | 请求和响应使用 JSON 格式 |
| **JWT 认证** | 基于 Token 的身份验证 |
| **权限控制** | 细粒度的 ACL 权限验证 |
| **关联查询** | 支持嵌套资源查询 |
| **过滤排序** | 丰富的查询参数支持 |
| **分页支持** | 支持页码和游标分页 |

### 1.3 响应格式

#### 成功响应

```json
{
  "data": { /* 响应数据 */ },
  "meta": { /* 元数据（可选）*/ }
}
```

#### 列表响应

```json
{
  "data": [ /* 数据列表 */ ],
  "meta": {
    "count": 100,
    "page": 1,
    "pageSize": 20,
    "totalPage": 5
  }
}
```

#### 错误响应

```json
{
  "errors": [
    {
      "message": "错误描述",
      "code": "ERROR_CODE",
      "details": { /* 详细信息 */ }
    }
  ]
}
```

---

## 2. 认证机制

### 2.1 JWT Token 认证

NocoBase 使用 JWT (JSON Web Token) 进行身份验证。

#### Token 结构

```
Header.Payload.Signature

示例:
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.
eyJ1c2VyX2lkIjoxLCJleHAiOjE2MDk0NTkyMDB9.
SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c
```

### 2.2 获取 Token

#### 用户登录

```http
POST /api/users:login
Content-Type: application/json

{
  "email": "admin@nocobase.com",
  "password": "admin123"
}
```

**响应**:

```json
{
  "data": {
    "user": {
      "id": 1,
      "email": "admin@nocobase.com",
      "nickname": "Admin"
    },
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }
}
```

### 2.3 使用 Token

#### 在请求头中携带 Token

```http
GET /api/users
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### 2.4 登出

```http
POST /api/users:logout
Authorization: Bearer <token>
```

---

## 3. 通用规范

### 3.1 请求方法

| 方法 | 说明 | 幂等性 |
|------|------|--------|
| **GET** | 获取资源 | ✅ |
| **POST** | 创建资源 | ❌ |
| **PUT** | 全量更新资源 | ✅ |
| **PATCH** | 部分更新资源 | ❌ |
| **DELETE** | 删除资源 | ✅ |

### 3.2 状态码

| 状态码 | 说明 | 使用场景 |
|--------|------|---------|
| **200** | OK | 请求成功 |
| **201** | Created | 资源创建成功 |
| **204** | No Content | 删除成功 |
| **400** | Bad Request | 请求参数错误 |
| **401** | Unauthorized | 未认证 |
| **403** | Forbidden | 无权限 |
| **404** | Not Found | 资源不存在 |
| **422** | Unprocessable Entity | 数据验证失败 |
| **500** | Internal Server Error | 服务器错误 |

### 3.3 通用查询参数

#### 列表查询

| 参数 | 类型 | 说明 | 示例 |
|------|------|------|------|
| `page` | number | 页码（从 1 开始） | `page=1` |
| `pageSize` | number | 每页数量 | `pageSize=20` |
| `sort` | string | 排序字段 | `sort=-createdAt` |
| `fields` | string | 返回字段 | `fields=id,name` |
| `filter` | string | 过滤条件 | `filter=status=active` |
| `appends` | string | 附加字段 | `appends=count` |

#### 过滤器语法

```javascript
// 等于
filter=status,active

// 不等于
filter=status.$ne(active)

// 大于
filter=age.$gt(18)

// 包含
filter=name.$like(%john%)

// 或条件
filter=$or(status,active,pending)

// 且条件
filter=$and(status,active,age.$gte(18))
```

#### 排序语法

```javascript
// 升序
sort=createdAt

// 降序
sort=-createdAt

// 多字段排序
sort=-createdAt,name
```

---

## 4. 数据表 API

### 4.1 CRUD 操作

#### 4.1.1 获取列表

```http
GET /api/:resource
Authorization: Bearer <token>
```

**查询参数**:

| 参数 | 说明 | 默认值 |
|------|------|--------|
| `page` | 页码 | 1 |
| `pageSize` | 每页数量 | 20 |
| `sort` | 排序 | -createdAt |
| `filter` | 过滤 | - |
| `fields` | 返回字段 | 全部 |

**示例**:

```http
GET /api/posts?page=1&pageSize=10&sort=-createdAt&filter=status=published
```

**响应**:

```json
{
  "data": [
    {
      "id": 1,
      "title": "Hello World",
      "content": "This is my first post",
      "status": "published",
      "createdAt": "2026-01-06T10:00:00Z",
      "updatedAt": "2026-01-06T10:00:00Z"
    }
  ],
  "meta": {
    "count": 1,
    "page": 1,
    "pageSize": 10,
    "totalPage": 1
  }
}
```

#### 4.1.2 获取详情

```http
GET /api/:resource/:id
Authorization: Bearer <token>
```

**示例**:

```http
GET /api/posts/1
```

**响应**:

```json
{
  "data": {
    "id": 1,
    "title": "Hello World",
    "content": "This is my first post",
    "status": "published",
    "createdAt": "2026-01-06T10:00:00Z",
    "updatedAt": "2026-01-06T10:00:00Z"
  }
}
```

#### 4.1.3 创建记录

```http
POST /api/:resource
Authorization: Bearer <token>
Content-Type: application/json
```

**请求体**:

```json
{
  "title": "New Post",
  "content": "Post content here",
  "status": "draft"
}
```

**响应** (201 Created):

```json
{
  "data": {
    "id": 2,
    "title": "New Post",
    "content": "Post content here",
    "status": "draft",
    "createdAt": "2026-01-06T11:00:00Z",
    "updatedAt": "2026-01-06T11:00:00Z"
  }
}
```

#### 4.1.4 更新记录

##### PUT (全量更新)

```http
PUT /api/:resource/:id
Authorization: Bearer <token>
Content-Type: application/json
```

**请求体**:

```json
{
  "title": "Updated Title",
  "content": "Updated content",
  "status": "published"
}
```

##### PATCH (部分更新)

```http
PATCH /api/:resource/:id
Authorization: Bearer <token>
Content-Type: application/json
```

**请求体**:

```json
{
  "status": "published"
}
```

**响应** (200 OK):

```json
{
  "data": {
    "id": 1,
    "title": "Updated Title",
    "content": "Updated content",
    "status": "published",
    "updatedAt": "2026-01-06T12:00:00Z"
  }
}
```

#### 4.1.5 删除记录

```http
DELETE /api/:resource/:id
Authorization: Bearer <token>
```

**响应** (204 No Content):

```
< 空 >
```

---

## 5. 用户管理 API

### 5.1 用户登录

```http
POST /api/users:login
Content-Type: application/json
```

**请求体**:

```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

**响应**:

```json
{
  "data": {
    "user": {
      "id": 1,
      "email": "user@example.com",
      "nickname": "User",
      "avatar": null
    },
    "token": "jwt_token_here"
  }
}
```

### 5.2 用户注册

```http
POST /api/users:register
Content-Type: application/json
```

**请求体**:

```json
{
  "email": "newuser@example.com",
  "password": "password123",
  "nickname": "New User"
}
```

### 5.3 获取当前用户

```http
GET /api/users:me
Authorization: Bearer <token>
```

**响应**:

```json
{
  "data": {
    "id": 1,
    "email": "user@example.com",
    "nickname": "User",
    "roles": ["admin"]
  }
}
```

### 5.4 更新个人信息

```http
PATCH /api/users:profile
Authorization: Bearer <token>
Content-Type: application/json
```

**请求体**:

```json
{
  "nickname": "New Nickname",
  "avatar": "avatar_url"
}
```

### 5.5 修改密码

```http
POST /api/users:changePassword
Authorization: Bearer <token>
Content-Type: application/json
```

**请求体**:

```json
{
  "oldPassword": "old_password",
  "newPassword": "new_password"
}
```

---

## 6. 权限管理 API

### 6.1 角色管理

#### 6.1.1 获取角色列表

```http
GET /api/roles
Authorization: Bearer <token>
```

**响应**:

```json
{
  "data": [
    {
      "name": "admin",
      "title": "管理员",
      "description": "系统管理员",
      "permissions": {}
    }
  ]
}
```

#### 6.1.2 创建角色

```http
POST /api/roles
Authorization: Bearer <token>
Content-Type: application/json
```

**请求体**:

```json
{
  "name": "editor",
  "title": "编辑",
  "description": "内容编辑角色"
}
```

---

## 7. 工作流 API

### 7.1 工作流管理

#### 7.1.1 获取工作流列表

```http
GET /api/workflows
Authorization: Bearer <token>
```

#### 7.1.2 创建工作流

```http
POST /api/workflows
Authorization: Bearer <token>
Content-Type: application/json
```

**请求体**:

```json
{
  "title": "订单审批流程",
  "type": "request",
  "trigger": {
    "type": "event",
    "config": {
      "collection": "orders",
      "mode": "create"
    }
  }
}
```

---

## 8. 文件管理 API

### 8.1 上传文件

```http
POST /api/files:upload
Authorization: Bearer <token>
Content-Type: multipart/form-data
```

**请求参数**:

| 参数 | 类型 | 说明 |
|------|------|------|
| `file` | File | 要上传的文件 |

**响应**:

```json
{
  "data": {
    "id": "1",
    "url": "/uploads/file.jpg",
    "filename": "file.jpg",
    "mimetype": "image/jpeg",
    "size": 102400
  }
}
```

### 8.2 获取文件

```http
GET /api/files/:id
Authorization: Bearer <token>
```

### 8.3 删除文件

```http
DELETE /api/files/:id
Authorization: Bearer <token>
```

---

## 9. 系统配置 API

### 9.1 获取系统配置

```http
GET /api/system/settings
Authorization: Bearer <token>
```

**响应**:

```json
{
  "data": {
    "title": "My NocoBase",
    "logo": null,
    "language": "zh-CN",
    "timezone": "Asia/Shanghai"
  }
}
```

### 9.2 更新系统配置

```http
PUT /api/system/settings
Authorization: Bearer <token>
Content-Type: application/json
```

**请求体**:

```json
{
  "title": "My Application",
  "language": "zh-CN"
}
```

---

## 10. 错误处理

### 10.1 错误响应格式

```json
{
  "errors": [
    {
      "message": "错误描述",
      "code": "ERROR_CODE",
      "details": {}
    }
  ]
}
```

### 10.2 常见错误码

| 错误码 | HTTP 状态 | 说明 |
|--------|----------|------|
| `INVALID_CREDENTIALS` | 401 | 登录凭证无效 |
| `TOKEN_EXPIRED` | 401 | Token 已过期 |
| `UNAUTHORIZED` | 401 | 未授权访问 |
| `FORBIDDEN` | 403 | 无权限访问 |
| `NOT_FOUND` | 404 | 资源不存在 |
| `VALIDATION_ERROR` | 422 | 数据验证失败 |
| `INTERNAL_ERROR` | 500 | 服务器内部错误 |

---

## 11. SDK 使用

### 11.1 JavaScript SDK

#### 安装

```bash
npm install @nocobase/sdk
```

#### 基本使用

```javascript
import { APIClient } from '@nocobase/sdk';

// 初始化客户端
const api = new APIClient({
  baseURL: 'http://localhost:13000/api',
});

// 登录
await api.auth.login('admin@nocobase.com', 'admin123');

// 获取数据
const posts = await api.request({
  url: 'posts',
  method: 'get',
});

// 创建数据
const newPost = await api.request({
  url: 'posts',
  method: 'post',
  data: {
    title: 'New Post',
    content: 'Post content',
  },
});
```

### 11.2 cURL 示例

#### 登录

```bash
curl -X POST http://localhost:13000/api/users:login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@nocobase.com",
    "password": "admin123"
  }'
```

#### 获取数据

```bash
curl -X GET http://localhost:13000/api/posts \
  -H "Authorization: Bearer YOUR_TOKEN"
```

#### 创建数据

```bash
curl -X POST http://localhost:13000/api/posts \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "New Post",
    "content": "Post content"
  }'
```

---

## 附录

### A. HTTP 状态码速查

| 状态码 | 说明 | 处理建议 |
|--------|------|---------|
| 200 | 成功 | 正常处理响应数据 |
| 201 | 创建成功 | 获取 Location 头中的资源 URL |
| 204 | 无内容 | 请求成功，无返回数据 |
| 400 | 请求错误 | 检查请求参数 |
| 401 | 未认证 | 重新登录获取 Token |
| 403 | 无权限 | 检查用户权限配置 |
| 404 | 不存在 | 检查资源 URL |
| 422 | 验证失败 | 检查请求数据格式 |
| 500 | 服务器错误 | 联系系统管理员 |

### B. 数据类型映射

| 数据库类型 | API 类型 | 示例值 |
|-----------|---------|-------|
| varchar | string | "hello" |
| text | string | "long text..." |
| integer | number | 123 |
| decimal | number | 123.45 |
| boolean | boolean | true |
| date | string | "2026-01-06" |
| datetime | string | "2026-01-06T10:00:00Z" |
| json | object | {"key": "value"} |

### C. 相关资源

- **官方文档**: https://docs.nocobase.com/api/overview
- **GitHub**: https://github.com/nocobase/nocobase
- **SDK 仓库**: https://github.com/nocobase/sdk

---

**文档结束**

如有疑问，请访问 [NocoBase 官方论坛](https://forum.nocobase.com) 获取帮助。
