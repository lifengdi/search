# Elasticsearch 搜索示例项目

JAVA版本：1.8.0_121

ES版本：6.8.2


## 接口调用须知
- 接口统一前缀：/search
- 默认端口：8080

## 接口文档

### 初始化索引、mapping

URL：/index/init

请求方式：GET

### 保存Store索引

URL：/store/index

请求方式：POST

### 搜索

URL：/store/search

请求方式：POST

### 查询数量

URL：/store/count

请求方式：POST

### 根据ID获取数据

URL：/store/get/{id}

请求方式：GET

### 批量更新

URL：/store/sync/bulk/update

请求方式：POST
