# 动态数据 CRUD API 使用指南

## 概述

`nocobase-data` 模块提供了完整的动态数据 CRUD 服务，支持对任意动态表进行增删改查操作，无需预定义实体类。

## 架构设计

### 1. ApiResponse - 统一响应格式

位置：`nocobase-common/src/main/java/com/nocobase/common/response/ApiResponse.java`

```json
{
  "code": 200,
  "message": "Success",
  "data": {},
  "timestamp": 1234567890
}
```

**响应码**：
- `200`：成功
- `400`：请求参数错误
- `404`：资源不存在
- `500`：服务器内部错误

### 2. DataRecordService - 动态数据服务

位置：`nocobase-data/src/main/java/com/nocobase/data/service/DataRecordService.java`

**核心特性**：
- 使用 `JdbcTemplate` 执行动态 SQL
- 通过 `CollectionMapper` 获取集合到物理表名的映射
- 支持完整的 CRUD 操作
- 无需预定义实体类

### 3. DataController - REST API

位置：`nocobase-data/src/main/java/com/nocobase/data/controller/DataController.java`

**基础路径**：`/api/v1/data`

## API 端点

### 1. 创建数据

**请求**：
```http
POST /api/v1/data/{collectionName}
Content-Type: application/json

{
  "field1": "value1",
  "field2": 123,
  "field3": true
}
```

**响应**：
```json
{
  "code": 200,
  "message": "Record created successfully",
  "data": 1,
  "timestamp": 1234567890
}
```

### 2. 查询数据列表

**请求**（无条件）：
```http
GET /api/v1/data/{collectionName}
```

**请求**（有条件）：
```http
GET /api/v1/data/{collectionName}?status=active&type=premium
```

**响应**：
```json
{
  "code": 200,
  "message": "Success",
  "data": [
    {
      "id": 1,
      "field1": "value1",
      "field2": 123
    },
    {
      "id": 2,
      "field1": "value2",
      "field2": 456
    }
  ],
  "timestamp": 1234567890
}
```

### 3. 查询单条数据

**请求**：
```http
GET /api/v1/data/{collectionName}/{id}
```

**响应**：
```json
{
  "code": 200,
  "message": "Success",
  "data": {
    "id": 1,
    "field1": "value1",
    "field2": 123
  },
  "timestamp": 1234567890
}
```

### 4. 更新数据

**请求**：
```http
PUT /api/v1/data/{collectionName}/{id}
Content-Type: application/json

{
  "field1": "updatedValue",
  "field2": 999
}
```

**响应**：
```json
{
  "code": 200,
  "message": "Record updated successfully",
  "data": 1,
  "timestamp": 1234567890
}
```

### 5. 删除数据

**请求**：
```http
DELETE /api/v1/data/{collectionName}/{id}
```

**响应**：
```json
{
  "code": 200,
  "message": "Record deleted successfully",
  "data": 1,
  "timestamp": 1234567890
}
```

### 6. 统计数据量

**请求**：
```http
GET /api/v1/data/{collectionName}/count
```

**响应**：
```json
{
  "code": 200,
  "message": "Success",
  "data": 100,
  "timestamp": 1234567890
}
```

### 7. 检查数据是否存在

**请求**：
```http
GET /api/v1/data/{collectionName}/{id}/exists
```

**响应**：
```json
{
  "code": 200,
  "message": "Success",
  "data": true,
  "timestamp": 1234567890
}
```

## 技术实现细节

### 集合名称到表名的映射

服务首先从 `nocobase_collections` 表中查找集合名称，获取对应的物理表名：

```java
private String getTableName(String collectionName) {
    Collection collection = collectionMapper.selectOneByCondition(
        COLLECTION.NAME.eq(collectionName)
    );
    return collection.getTableName();
}
```

### 动态 SQL 构建

使用 `StringBuilder` 动态构建 SQL 语句，支持任意字段：

**INSERT 示例**：
```sql
INSERT INTO orders (customer_name, amount, status) VALUES (?, ?, ?) RETURNING id
```

**UPDATE 示例**：
```sql
UPDATE orders SET customer_name = ?, amount = ?, status = ? WHERE id = ?
```

**SELECT 示例**：
```sql
SELECT * FROM orders WHERE status = ? AND amount > ?
```

### 参数化查询

所有 SQL 语句都使用参数化查询（`?` 占位符），防止 SQL 注入：

```java
jdbcTemplate.queryForObject(
    "SELECT * FROM " + tableName + " WHERE id = ?",
    id
);
```

## 集成 Swagger/OpenAPI

所有端点都有完整的 API 文档，访问：
```
http://localhost:8080/swagger-ui.html
```

**标签**：`Dynamic Data`

## 测试示例

### 使用 cURL

**创建数据**：
```bash
curl -X POST http://localhost:8080/api/v1/data/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customer_name": "张三",
    "amount": 1000,
    "status": "pending"
  }'
```

**查询数据**：
```bash
curl http://localhost:8080/api/v1/data/orders
```

**更新数据**：
```bash
curl -X PUT http://localhost:8080/api/v1/data/orders/1 \
  -H "Content-Type: application/json" \
  -d '{
    "status": "completed",
    "amount": 1200
  }'
```

**删除数据**：
```bash
curl -X DELETE http://localhost:8080/api/v1/data/orders/1
```

## 错误处理

### 集合不存在

```json
{
  "code": 400,
  "message": "Collection not found: invalid_collection",
  "data": null,
  "timestamp": 1234567890
}
```

### 记录不存在

```json
{
  "code": 404,
  "message": "Record not found with id: 999",
  "data": null,
  "timestamp": 1234567890
}
```

### 服务器错误

```json
{
  "code": 500,
  "message": "Failed to create record: Table 'orders' doesn't exist",
  "data": null,
  "timestamp": 1234567890
}
```

## 下一步

- [ ] 添加分页支持
- [ ] 添加排序功能
- [ ] 添加复杂查询条件（AND/OR 嵌套）
- [ ] 添加事务支持
- [ ] 添加批量操作
- [ ] 添加数据验证

## 相关文件

- `nocobase-common/src/main/java/com/nocobase/common/response/ApiResponse.java` - 统一响应类
- `nocobase-data/src/main/java/com/nocobase/data/service/DataRecordService.java` - 动态数据服务
- `nocobase-data/src/main/java/com/nocobase/data/controller/DataController.java` - REST API 控制器
- `nocobase-data/src/main/java/com/nocobase/data/entity/Collection.java` - 集合元数据实体
- `nocobase-data/src/main/java/com/nocobase/data/mapper/CollectionMapper.java` - 集合 Mapper

---

**更新时间**：2026-01-07
