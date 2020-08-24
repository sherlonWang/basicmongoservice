## 使用

拷贝项目

```sh
git clone https://github.com/sherlonWang/basicmongoservice.git
```

设置 maven 配置文件 settings.xml 为阿里云镜像：

```xml
<!-- 阿里云镜像 -->
    <mirrors>
        <mirror>
            <id>alimaven</id>
            <name>aliyun maven</name>
            <!-- https://maven.aliyun.com/repository/public/ -->
            <url>https://maven.aliyun.com/repository/central</url>
            <mirrorOf>central</mirrorOf>
        </mirror>
    </mirrors>
```



## 接口

> 接口返回值都用 JsonResult 进行了封装（实际结果在 response.data 中），以下接口返回值仅代表实际返回类型

### 一、导入

#### 单条导入

- url

  http://ip:port/api/mongo/save

- 请求方法

  POST

- 参数

  `collectionName`：String 类型，指定集合名称

  `jsonObject`：String类型，待导入的 json 对象。如果在前端通过 url 的方式传参需要用 `encodeURI()` 转义一下。

- 返回值

  `Map<String, Object>`：返回带 _id 的 json 文档，如：

  ```json
  // jsonObjec为 "{a:1,b:3}" 则返回结果为
  {
      "state": 0,
      "msg": "ok",
      "data": {
          "a": 1,
          "b": 3,
          "_id": "5f40cd173774ff78bfa4e3bd"
      }
  }
  ```

#### 批量导入

- url

  http://ip:port/api/mongo/saveAll

- 请求方法

  POST

- 参数

  `collectionName`：String 类型，指定集合名称

  `jsonArray`：String类型，待导入的 json 对象数组字符串。如果在前端通过 url 的方式传参需要用 `encodeURI()` 转义一下。

- 返回值

  `List<Map<String, Object>>`：返回带 _id 的 json 文档集合，如：

  ```json
  // jsonArray 为 "[{a:1,b:3},{a:5,b:6}]" 则返回结果为
  {
      "state": 0,
      "msg": "ok",
      "data": [
          {
              "a": 1,
              "b": 3,
              "_id": "5f40d00b3774ff78bfa4e3bf"
          },
          {
              "a": 5,
              "b": 6,
              "_id": "5f40d00b3774ff78bfa4e3c0"
          }
      ]
  }
  ```

#### 矩阵文件导入

>支持 excel 和文本类文件

- url

  http://ip:port/api/mongo/import

- 请求方法

  POST

- 参数

  `file`：MultipartFile 类型，带导入的文件

  `collectionName`：String 类型，指定集合名称

  `separator`：String 类型，文本类文件的列分隔符（如果是excel文件可不填）

  `fileType`：String 类型，文本类文件数值类型，可供选的值有"string","int","float","double"。（如果是excel文件可不填）

- 返回值

  `List<String>`：返回集合名称数组

  注：如果是带有多个 sheet 的 excel 文件，会将每个 sheet 都存储为一个集合，每个 sheet 对应的集合名称为 collectionName+"_"+sheetName。

### 二、查询

- url

  http://ip:port/api/mongo/query

- 请求方法

  GET

- 参数

  `collectionName`：String 类型，指定集合名称

  `page`：Integer 类型，当前页

  `pageSize`：Integer 类型，每页数量

  `projection`：String 类型，显示指定字段，json对象字符串

  ​	如：`{A1:1,A2:1}` 表示只显示 A1,A2字段，`{A3:0}` 表示不显示 A3 字段。**不要同时使用 1 和 0**。

  `filter`：String 类型，过滤条件， json 对象数组字符串，示例如下：

    ```json
  [
     {
      field:'A1',
      operation:'>', 
      value:3,
      connection:'and'
    },
     {
      field:'A2',
      operation:'<', 
      value:0,
      connection:'and'
    }
  ]
    ```

    - field：String 类型，字段名称
    - operation：String 类型，操作符，支持的操作符有：
      - `= `
      - `>`
      - `<`
      - `>=`
      - `<=`
      - `!=`
      - `in`
      - `notin`
      - `like`
      - `notlike`
  - value：Object 类型，值
  - connection：String 类型，连接符，支持的连接符有：
    - `and`
    - `or`

  `sort`：String 类型，排序条件，json 对象字符串。如 `{A1:1,A2:-1}` 表示按 A1字段 升序，A2字段 降序排序。

- 返回值

  `List<Map<String, Object>>`：返回查询到的文档数据

### 三、修改

#### 更新文档

- url

  http://ip:port/api/mongo/update

- 请求方法

  POST

- 参数

  `collectionName`：String 类型，指定集合名称

  `filterJson`：String 类型，过滤条件，json 对象字符串 如 `{A1:3}` 表示查找到 A1=3 的所有文档

  `updateJson`：String 类型，更新文档，json 对象字符串 如 `{A1:30}` 表示将查找到文档的 A1 字段的值设置为 30。如果要更新的字段不存在，则会在查找到的文档中添加该字段。

  `multi`：Boolean 类型，是否更新多个，true 表示更新查找到的所有文档，false 表示更新查找到的第一个文档。

- 返回值

  `Long`：返回更新文档的个数

#### 替换文档

- url

  http://ip:port/api/mongo/replace

- 请求方法

  POST

- 参数

  `collectionName`：String 类型，指定集合名称

  `filterJson`：String 类型，过滤条件，json 对象字符串 如 `{A1:3}` 表示查找到 A1=3 的所有文档

  `replaceJson`：String 类型，替换文档，json 对象字符串 如 `{A1:30}` 表示将查到的第一个文档替换`{A1:30}` ，文档之前的所有字段都会丢失。

  只支持单个文档的替换操作。

- 返回值

  `Long`：返回替换文档的个数

### 四、删除

#### 删除文档

- url

  http://ip:port/api/mongo/removeDocument

- 请求方法

  POST

- 参数

  `collectionName`：String 类型，指定集合名称

  `filterJson`：String 类型，过滤条件，json 对象字符串 如 `{A1:3}` 表示查找到 A1=3 的所有文档

  `multi`：Boolean 类型，是否删除多个，true 表示删除查找到的所有文档，false 表示删除查找到的第一个文档。

- 返回值

  `Long`：返回删除文档的个数

#### 删除集合

- url

  http://ip:port/api/mongo/removeCollection

- 请求方法

  POST

- 参数

  `collectionName`：String 类型，指定集合名称

- 返回值

  `Boolean`：true 删除成功，false 删除失败



todo: 删除时，文档添加字段isDelete，集合删除时用一个专门的集合来管理删除状态



