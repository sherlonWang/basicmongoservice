package com.sherlon.basicmongoservice.business;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertManyResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import com.sherlon.basicmongoservice.config.MongoClientManager;
import com.sherlon.basicmongoservice.util.ExcelFileUtil;
import com.sherlon.basicmongoservice.util.TextFileUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.bson.BsonValue;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author :  sherlonWang
 * @description :  mongo相关操作
 * @date: 2020-08-18
 */
@Service
public class MongoDataBusiness {

    Logger log = LoggerFactory.getLogger(MongoDataBusiness.class);

    /**
     * 插入数据
     *
     * @param jsonObject     json对象字符串
     * @param collectionName 集合名称
     * @return 返回带_id的map
     */
    public Map<String, Object> insert(String collectionName, String jsonObject) {
        // 转换json串为对象
        JSONObject dataToSave = JSONUtil.parseObj(jsonObject);
        //  将json对象转化为待插入文档数据
        Document document = new Document(dataToSave);
        // 插入文档
        InsertOneResult insertOneResult = MongoClientManager.createDatabase().getCollection(collectionName).insertOne(document);
        // 获取插入文档后的主键_id
        String id = Objects.requireNonNull(insertOneResult.getInsertedId()).asObjectId().getValue().toHexString();
        Map<String, Object> result = new HashMap<>(dataToSave);
        // 将_id重新装载到原数据中返回
        result.put("_id", id);
        return result;
    }

    /**
     * 批量插入数据
     *
     * @param jsonArray      json对象数组字符串串
     * @param collectionName 集合名称
     * @return 返回带_id的map集合
     */
    public List<Map> batchInsert(String collectionName, String jsonArray) {
        // json串转为json数组
        JSONArray array = JSONUtil.parseArray(jsonArray);
        // 待插入的文档
        List<Document> documents = new ArrayList<>();
        List<Map> list = array.toList(Map.class);
        // 将json数组转化为待插入文档数据
        for (Map map : list) {
            Document doc = new Document(map);
            documents.add(doc);
        }
        // 批量插入文档
        InsertManyResult insertManyResult = MongoClientManager.createDatabase().getCollection(collectionName).insertMany(documents);
        Map<Integer, BsonValue> valueMap = insertManyResult.getInsertedIds();
        System.out.println(valueMap);
        // 将_id重新装载到原数据中返回
        for (int i = 0; i < list.size(); i++) {
            String id = valueMap.get(i).asObjectId().getValue().toHexString();
            Map map = list.get(i);
            map.put("_id", id);
        }
        return list;
    }

    /**
     * 文本类文件导入
     *
     * @param multipartFile  文件流
     * @param collectionName 集合名称
     * @param separator      文件内容分隔符
     * @param fileType       文件数值类型
     * @return 导入成功返回集合名称，否则返回错误信息
     */
    public List<String> textFileImport(MultipartFile multipartFile, String collectionName, String separator, String fileType) {
        List<String> result = new ArrayList<>();
        // 如果集合名称为空，默认时间戳加文件名为集合名称
        if (StrUtil.isBlank(collectionName)) {
            String filename = Objects.requireNonNull(multipartFile.getOriginalFilename()).substring(0, multipartFile.getOriginalFilename().lastIndexOf("."));
            collectionName = System.currentTimeMillis() + "_" + filename;
            result.add(collectionName);
        }
        // 默认分隔符为空字符
        if (StrUtil.isBlank(separator)) {
            separator = "\\s+";
        }
        // 默认文件数值类型为string
        if (StrUtil.isBlank(fileType)) {
            fileType = "string";
        }
        // 获取mongo数据库实例
        MongoDatabase database = MongoClientManager.createDatabase();
        Reader reader = null;
        BufferedReader br = null;
        try {
            List<String> cols = TextFileUtil.getHeader(multipartFile.getInputStream(), separator);
            reader = new InputStreamReader(multipartFile.getInputStream(), StandardCharsets.UTF_8);
            br = new BufferedReader(reader);
            String str;
            int count = 0;
            boolean bFirstLine = true;
            List<Document> documents = new ArrayList<>();
            while ((str = br.readLine()) != null) {
                // 跳过第一行表头
                if (bFirstLine) {
                    bFirstLine = false;
                    continue;
                }
                // 获取数据行
                String[] datas = str.trim().split(separator);
                // 当读到的数据行列数和表头行相等时才存入mongo
                if (datas.length == cols.size()) {
                    int index = 0;
                    Document document = new Document();
                    for (String col : cols) {
                        // 构建文档数据 转换文档数值类型
                        document.append(col, parseData(fileType, datas[index++]));
                    }
                    documents.add(document);
                }
                // 每10000条批量插入一次
                if (count == 10000) {
                    database.getCollection(collectionName).insertMany(documents);
                    documents.clear();
                    count = 0;
                }
                count++;
            }
            // 插入最后不足10000的剩余数据
            if (documents.size() > 0) {
                database.getCollection(collectionName).insertMany(documents);
                documents.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
                if (br != null) {
                    br.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * excel文件导入
     *
     * @param multipartFile  文件流
     * @param collectionName 集合名称
     * @return 导入成功返回集合名称，否则返回错误信息
     */
    public List<String> excelFileImport(MultipartFile multipartFile, String collectionName) {
        List<String> nameList = new ArrayList<>();
        // 如果集合名称为空，默认为 时间戳 + "_" + 文件名 + "_" + sheet名称
        if (StrUtil.isBlank(collectionName)) {
            String filename = Objects.requireNonNull(multipartFile.getOriginalFilename()).substring(0, multipartFile.getOriginalFilename().lastIndexOf("."));
            collectionName = System.currentTimeMillis() + "_" + filename;
        }
        // 获取mongo数据库实例
        MongoDatabase database = MongoClientManager.createDatabase();
        Workbook workbook = null;
        try {
            workbook = WorkbookFactory.create(multipartFile.getInputStream());
            int sheetCount = ExcelFileUtil.sheetCount(workbook);
            // 如果有多个sheet则遍历插入
            for (int i = 0; i < sheetCount; i++) {
                Sheet sheet = workbook.getSheetAt(i);
                if (sheet == null) {
                    continue;
                }
                String sheetName = sheet.getSheetName();
                nameList.add(collectionName + "_" + sheetName);
                // 集合名称 + sheet名称
//                collectionName = collectionName + "_" + sheetName;
                List<Document> documents = new ArrayList<>();
                int rowNum = sheet.getLastRowNum();
                int colNum = sheet.getRow(0).getPhysicalNumberOfCells();
                List<String> cols = ExcelFileUtil.getSheetHeader(sheet, 0);
                int count = 0;
                for (int j = 1; j <= rowNum; j++) {
                    Row row = sheet.getRow(j);
                    if (row == null) {
                        continue;
                    }
                    Document document = new Document();
                    for (int k = 0; k < colNum; k++) {
                        document.put(cols.get(k), ExcelFileUtil.getCellFormatValue(row.getCell(k)));
                    }
                    documents.add(document);
                    // 每10000条批量插入一次
                    if (count == 10000) {
                        database.getCollection(collectionName + "_" + sheetName).insertMany(documents);
                        documents.clear();
                        count = 0;
                    }
                    count++;
                }
                // 插入最后不足10000的剩余数据
                if (documents.size() > 0) {
                    database.getCollection(collectionName + "_" + sheetName).insertMany(documents);
                    documents.clear();
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (workbook != null) {
                    workbook.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return nameList;
    }


    /**
     * 查询数据
     *
     * @param collectionName 集合名称
     * @param page           当前页
     * @param pageSize       每页数量
     * @param projection     查询字段 json对象
     *                       {A1:1,A2:1} 显示A1，A2
     *                       {A3:0}不显示A3
     *                       1和0不能同时使用
     * @param filter         过滤条件 json对象数组 [{"field":"A1","operation":">","value":3,"connection":"and"},...]
     * @param sort           排序 json对象 {A1:1,A2:-1} 1表示升序，-1表示降序
     * @return 返回集合数据
     */
    public List<Map<String, Object>> query(String collectionName, Integer page, Integer pageSize, String projection, String filter, String sort) {
        if (StrUtil.isBlank(collectionName)) {
            log.info("集合名称不能为空");
            throw new RuntimeException("集合名称不能为空");
        }
        if (StrUtil.isBlank(projection)) {
            projection = "{}";
        }
        if (StrUtil.isBlank(filter)) {
            filter = "[]";
        }
        if (StrUtil.isBlank(sort)) {
            sort = "{}";
        }
        // 添加查询字段
        JSONObject jsonObject = JSONUtil.parseObj(projection);
        Document projectionDocument = new Document(jsonObject);
        System.out.println(projectionDocument);
        // 添加过滤条件
        JSONArray jsonArray = JSONUtil.parseArray(filter);
        List<Map> filterList = jsonArray.toList(Map.class);
        // 查询跟文档集合 包含一个and文档和若干个or文档
        List<Document> rootListDocument = new ArrayList<>();
        // 所有and条件都存储在这个文档
        Document andDocument = new Document();
        for (Map map : filterList) {
            String field = (String) map.get("field");
            String operation = (String) map.get("operation");
            Object value = map.get("value");
            String connection = (String) map.get("connection");
            // 将所有为and的条件放在一个查询文档
            if (!"or".equalsIgnoreCase(connection)) {
                andDocument.put(field, parseOperation(operation,value));
            } else {
                // 每个为or的条件作为一个查询文档
                Document document = new Document(field, parseOperation(operation,value));
                rootListDocument.add(document);
            }
        }
        // 将and条件文档添加到跟查询文档中
        rootListDocument.add(andDocument);
        Document filterDocument = new Document("$or", rootListDocument);
        // 添加排序
        JSONObject sortObject = JSONUtil.parseObj(sort);
        Document sortDocument = new Document(sortObject);
        List<Map<String, Object>> list = new ArrayList<>();
        MongoDatabase database = MongoClientManager.createDatabase();
        MongoCollection<Document> collection = database.getCollection(collectionName);
        MongoIterable<Document> mongoIterable = null;
        // 分页查询
        if (page == null || pageSize == null) {
            mongoIterable = collection.find(filterDocument).projection(projectionDocument).sort(sortDocument);
        } else {
            // 由于mongodb skip、limit、sort同时存在时，会按照sort->skip->limit的顺序执行，
            // 不是我们想要的效果，我们想要的效果是skip->limit->sort
            // 所以这里使用管道aggregate来实现执行的额顺序
            List<Document> aggregateDocuments = new ArrayList<>();
            Document filterAggregate = new Document("$match", filterDocument);
            Document projectionAggregate = new Document("$project", projectionDocument);
            Document skipAggregate = new Document("$skip", (page - 1) * pageSize);
            Document limitAggregate = new Document("$limit", pageSize);
            Document sortAggregate = new Document("$sort", sortDocument);
            if (!filterDocument.isEmpty()) {
                aggregateDocuments.add(filterAggregate);
            }
            // 查询字段为空则不添加
            if (!projectionDocument.isEmpty()) {
                aggregateDocuments.add(projectionAggregate);
            }
            aggregateDocuments.add(skipAggregate);
            aggregateDocuments.add(limitAggregate);
            if (!sortDocument.isEmpty()) {
                aggregateDocuments.add(sortAggregate);
            }
            mongoIterable = collection.aggregate(aggregateDocuments);
        }
        mongoIterable.forEach(document -> {
            // 将ObjectId转为String重新装载到document中
            String id = document.getObjectId("_id").toHexString();
            document.put("_id", id);
            list.add(document);
        });
        return list;
    }

    /**
     * 更新文档
     *
     * @param collectionName 集合名称
     * @param filterJson     过滤条件
     * @param updateJson     更新文档
     * @param multi          是否更新多个
     * @return 返回更新文档的个数
     */
    public long updateDocument(String collectionName, String filterJson, String updateJson, Boolean multi) {
        if (StrUtil.isBlank(collectionName)) {
            log.info("集合名称不能为空");
            return 0;
        }
        if (StrUtil.isBlank(filterJson) || JSONUtil.parseObj(filterJson).keySet().size() == 0) {
            log.info("查询文档为空");
            return 0;
        }
        if (StrUtil.isBlank(updateJson) || JSONUtil.parseObj(updateJson).keySet().size() == 0) {
            log.info("更新文档为空");
            return 0;
        }
        if (multi == null) {
            multi = false;
        }
        MongoCollection<Document> collection = MongoClientManager.createDatabase().getCollection(collectionName);
        Document filterDocument = new Document(JSONUtil.parseObj(filterJson));
        Document updateDocument = new Document("$set", JSONUtil.parseObj(updateJson));
        UpdateResult updateResult = null;
        if (multi) {
            updateResult = collection.updateMany(filterDocument, updateDocument);
        } else {
            updateResult = collection.updateOne(filterDocument, updateDocument);
        }
        return updateResult.getModifiedCount();
    }

    /**
     * 替换文档
     *
     * @param collectionName 集合名称
     * @param filterJson     过滤条件
     * @param replaceJson    替换文档
     * @return 返回替换文档的个数
     */
    public long replaceDocument(String collectionName, String filterJson, String replaceJson) {
        if (StrUtil.isBlank(collectionName)) {
            log.info("集合名称不能为空");
            return 0;
        }
        if (StrUtil.isBlank(filterJson) || JSONUtil.parseObj(filterJson).keySet().size() == 0) {
            log.info("查询文档为空");
            return 0;
        }
        if (StrUtil.isBlank(replaceJson) || JSONUtil.parseObj(replaceJson).keySet().size() == 0) {
            log.info("替换文档为空");
            return 0;
        }
        MongoCollection<Document> collection = MongoClientManager.createDatabase().getCollection(collectionName);
        Document filterDocument = new Document(JSONUtil.parseObj(filterJson));
        Document replaceDocument = new Document(JSONUtil.parseObj(replaceJson));
        UpdateResult updateResult = collection.replaceOne(filterDocument, replaceDocument);
        return updateResult.getModifiedCount();
    }

    /**
     * 删除文档
     *
     * @param collectionName 集合名称
     * @param filterJson     查询条件
     * @param multi          是否删除多个
     * @return 返回删除文档的个数
     */
    public long removeDocument(String collectionName, String filterJson, Boolean multi) {
        if (StrUtil.isBlank(collectionName)) {
            log.info("集合名称不能为空");
            return 0;
        }
        if (StrUtil.isBlank(filterJson)) {
            log.info("查询条件不能为空");
            return 0;
        }
        if (multi == null) {
            multi = false;
        }
        MongoCollection<Document> collection = MongoClientManager.createDatabase().getCollection(collectionName);
        Document filterDocument = new Document(JSONUtil.parseObj(filterJson));
        DeleteResult deleteResult = null;
        if (multi) {
            deleteResult = collection.deleteMany(filterDocument);
        } else {
            deleteResult = collection.deleteOne(filterDocument);
        }
        return deleteResult.getDeletedCount();
    }

    /**
     * 删除集合
     *
     * @param collectionName 集合名称
     * @return true or false
     */
    public boolean removeCollection(String collectionName) {
        MongoCollection<Document> collection = MongoClientManager.createDatabase().getCollection(collectionName);
        collection.drop();
        return true;
    }

    /**
     * 转换值类型
     *
     * @param fileType 文档数值类型
     * @param strValue 读取到的原始字符串值
     * @return 返回文档数值类型
     */
    private Object parseData(String fileType, String strValue) {
        if ("int".equalsIgnoreCase(fileType)) {
            return Integer.valueOf(strValue);
        } else if ("double".equalsIgnoreCase(fileType)) {
            return Double.valueOf(strValue);
        } else if ("float".equalsIgnoreCase(fileType)) {
            return Float.valueOf(strValue);
        }
        return strValue;
    }


    /**
     * 操作符转换
     *
     * @param operation 操作符
     * @return 返回mongo操作符
     */
    private Document parseOperation(String operation, Object value) {
        operation = operation.toLowerCase();
        switch (operation) {
            case "=":
                return new Document("$eq", value);
            case ">":
                return new Document("$gt", value);
            case "<":
                return new Document("$lt", value);
            case ">=":
                return new Document("$gte", value);
            case "<=":
                return new Document("$lte", value);
            case "!=":
                return new Document("$ne", value);
            case "in":
                return new Document("$in", value);
            case "notin":
                return new Document("notin", value);
            case "like":
                return new Document("$regex", value);
            case "notlike":
                return new Document("$not", new Document("$regex", value));
            default:
                return new Document();
        }
    }

}
