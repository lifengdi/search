# Elasticsearch 搜索示例项目

JAVA版本：1.8.0_121

ES版本：6.8.2

相关博客：

[SpringBoot整合Elasticsearch详细步骤以及代码示例](https://www.lifengdi.com/archives/article/945)

[SpringBoot使用注解的方式构建Elasticsearch查询语句，实现多条件的复杂查询](https://www.lifengdi.com/archives/article/919)

[关于Elasticsearch文档的描述以及如何操作文档的详细总结](https://www.lifengdi.com/archives/article/tech/934)

[SpringBoot整合Elasticsearch游标查询（scroll）](https://www.lifengdi.com/archives/article/2119)

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

### 游标查询

URL：/store/scroll

请求方式：POST

游标查询分为开启和继续两个步骤。

`_scrollId`：继续同一个游标查询的时候此参数为必填，开启一个新的游标查询的时候，此参数为空

### 游标清除

URL：/scroll/clear/{scrollId}

请求方式：GET

若条件允许的话，尽量将游标查询及时关闭，以释放ES集群的资源，降低负担。