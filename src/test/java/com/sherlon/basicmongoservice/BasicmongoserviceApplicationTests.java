package com.sherlon.basicmongoservice;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.sherlon.basicmongoservice.business.MongoDataBusiness;
import com.sherlon.basicmongoservice.config.MongoClientManager;
import com.sherlon.basicmongoservice.util.TextFileUtil;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
class BasicmongoserviceApplicationTests {

//    @Autowired
//    MongoTemplate mongoTemplate;

    @Autowired
    MongoDataBusiness mongoDataBusiness;

    @Test
    void contextLoads() {
    }

    @Test
    void testInsert() {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> classes = new HashMap<>();
        classes.put("class", "math");
        classes.put("score", 80);
        map.put("name", "sherlon");
        map.put("age", 28);
        map.put("hobby", new String[]{"抽烟", "喝酒", "烫头"});
        map.put("classes", classes);
//        mongoTemplate.insert(map, "mycoll");
    }

    @Test
    void test1() {
        // 连接到 mongodb 服务
        MongoDatabase db = MongoClientManager.createDatabase("mydb");
        MongoCollection<Document> collection = db.getCollection("user");
        Document document = new Document("name","wxl");
        collection.insertOne(document);
//        FindIterable<Document> findIterable = collection.find();
//        findIterable.forEach(item -> {
//            System.out.println(item.get("name"));
//        });
    }

    @Test
    void test2() {
//        String json = "{a1";
        String jsonStr = "[{\"b\":\"value2\",\"c\":\"value3\",\"a\":\"value1\"},{\"b\":\"value4\",\"c\":\"value6\",\"a\":\"value5\"}]";
        //方法一：使用工具类转换
//        JSONObject jsonObject = JSONUtil.parseObj(json);
//        JSONArray array = JSONUtil.parseArray(jsonStr);
//        List<Map> list = array.toList(Map.class);
//        System.out.println(list);
//        System.out.println(array.get(0));
        List<Map> r = mongoDataBusiness.batchInsert(jsonStr,"test9");
        System.out.println(r);
    }

    @Test
    void test3() {
        List<Map> list = new ArrayList<>();
        Map map1 = new HashMap();
        map1.put("a", "1");
        map1.put("b", "2");
        Map map2 = new HashMap();
        map2.put("a", "3");
        map2.put("b", "4");
        list.add(map1);
        list.add(map2);
        System.out.println(list);
    }

    @Test
    void test4() {
        MongoClientURI mongoClientURI = new MongoClientURI("mongodb://localhost:27017/test");
        String databaseName = mongoClientURI.getDatabase();
        System.out.println(databaseName);
    }

    @Test
    void test44() {
        String path = "/Users/sherlonwang/Desktop/test1w.txt";
//        TestDataGenerateUtil.generateFile(path, 10000, 20, " ", 1, 10);
    }

    @Test
    void test5() {
        String collectionName = "test";
        String path = "/Users/sherlonwang/Desktop/test.txt";
//        TestDataGenerateUtil.generateFile(path,100000,20," ",1,10);
        long s = System.currentTimeMillis();
        List<Map<String, String>> data = TextFileUtil.getTextFileData(new File(path), " ");
        long e = System.currentTimeMillis();
        System.out.println("读取耗时:" + (e - s) + "ms");
        String jsonArray = JSONUtil.toJsonStr(data);
        long start = System.currentTimeMillis();
        List<Map> list = mongoDataBusiness.batchInsert(jsonArray, collectionName);
        long end = System.currentTimeMillis();
        System.out.println("耗时：" + (end - start) + "ms");
//        list.forEach(item -> {
//            Map<String,Object> map = (Map<String, Object>) item;
//            ObjectId objectId = (ObjectId) map.get("_id");
//            System.out.println(objectId.toHexString());
//        });
//        System.out.println(list);
    }

    @Test
    void testBigFile(){
        String path = "/Users/sherlonwang/Desktop/test1.txt";
//        TestDataGenerateUtil.generateFile(path,1000,20,",",1,10);
//        long start = System.currentTimeMillis();
//        List<Map<String, String>> data = TextFileUtil.getTextFileData(new File(path), " ");
//        long end = System.currentTimeMillis();
//        System.out.println("耗时：" + (end - start) + "ms");
    }

    @Test
    void test55() {
        long start = System.currentTimeMillis();
        String collectionName = "test8";
        String path = "/Users/sherlonwang/Desktop/test2.txt";
//        mongoDataBusiness.importFile(path, collectionName);
        long end = System.currentTimeMillis();
        System.out.println("耗时：" + (end - start) + "ms");
    }

    @Test
    void test56() {
        String colName = "test5";
        long start = System.currentTimeMillis();
//        List<Map> result = mongoDataBusiness.findAll(colName);
//        System.out.println(result.size());
////        FindIterable<Document> result = mongoDataBusiness.getAll(colName);
//        AtomicInteger i = new AtomicInteger();
//        result.forEach(document -> {
//            i.getAndIncrement();
//        });
//        long end = System.currentTimeMillis();
//        System.out.println("耗时：" + (end - start) + "ms");
//        System.out.println(i);

    }

    @Test
    void test6() {
        String collectionName = "test1";
        Map<String, Object> dataToSave = new HashMap<>();
        Map<String, String> contact = new HashMap<>();
        contact.put("address", "wuxi");
        contact.put("email", "11@qq.com");
        dataToSave.put("name", "qws");
        dataToSave.put("age", 23);
        dataToSave.put("contact", contact);
        String jsonObject = JSONUtil.toJsonStr(dataToSave);
        Map<String, Object> result = mongoDataBusiness.insert(jsonObject, collectionName);
        System.out.println(result);
        ObjectId objectId = (ObjectId) result.get("_id");
        System.out.println(objectId.toHexString());
    }

    @Test
    void test7() {
        String collectionName = "test";
        // 分页
        int currentPage = 1;
        int pageSize = 20;
//        Query query = new Query();
////        query.skip((currentPage-1)*pageSize);
////        query.limit(pageSize);
//
//        // 查询指定字段
//        query.fields().include("A1").include("A3").include("A2"); //包含该字段
////        query.fields().exclude("address");//不包含该字段
//
//        // 条件过滤
//        Criteria criteria = new Criteria();
//        query.addCriteria(Criteria.where("A1").gt(2));
//
//        List<Map> list = mongoTemplate.find(query, Map.class, collectionName);
//        System.out.println(list);
    }

    @Test
    void test8() {
        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("a", "1");
        objectMap.put("b", "2");
        Map<String, String> stringMap = new HashMap<>();
        stringMap.put("a", "1");
        stringMap.put("b", "2");
        Map<String, Integer> integerMap = new HashMap<>();
//        integerMap.putAll(objectMap);
    }

    @Test
    void test9(){
        //GET请求
        String content = HttpUtil.get("http://localhost:8989/api/hello");
        System.out.println(content);

        String path = "/Users/sherlonwang/GitHub/mongo服务整合/测试数据/test1w.txt";
        String path2 = "/Users/sherlonwang/GitHub/mongo服务整合/mongo服务整合.md";
        Map<String,Object> map = new HashMap<>();
        map.put("name","wxl");
        map.put("age",29);
        map.put("files",new File[]{new File(path),new File(path2)});
        String result = HttpUtil.post("http://localhost:8989/api/upload",map);
        System.out.println(result);
    }

    @Test
    void test10(){
        String path1 = "/Users/sherlonwang/Desktop/test.txt";
        String path2 = "/Users/sherlonwang/Desktop/test1.txt";
        String collectionName = "file2";
        String separator = " ";
        String fileType = "string";
        File file = new File(path1);
        Map<String,Object> map = new HashMap<>();
        map.put("collectionName",collectionName);
        map.put("separator",separator);
        map.put("fileType",fileType);
        map.put("file",file);
        String result = HttpUtil.post("http://localhost:8989/api/upload",map);
        System.out.println(result);
    }

    @Test
    void test11(){
        String collectionName = "file2";
        String separator = " ";
        String fileType = "string";
        Map<String,Object> map = new HashMap<>();
        map.put("collectionName",collectionName);
        map.put("separator",separator);
        map.put("fileType",fileType);
//        Map param = new HashMap();
//        param.put("map",map);
//        String result = HttpUtil.post("http://localhost:8989/api/save",map);
//        System.out.println(result);
    }

    @Test
    void test12(){
        Document document = new Document();
        document.append("a",1).append("b",2).append("c",3);
        System.out.println(document);
    }

    @Test
    void test13(){
        MongoDatabase database = MongoClientManager.createDatabase();
        List<Map<String,Object>> list = new ArrayList<>();
        MongoCollection<Document> collection = database.getCollection("file2");
        System.out.println("总数:"+collection.countDocuments());
        FindIterable<Document> findIterable = collection.find(new Document("A1",new Document("$gt",10))).projection(new Document("A1",1).append("A2",1));
//        findIterable.forEach(document -> {
//            // 将ObjectId转为String重新装载到document中
//            String id = document.getObjectId("_id").toHexString();
//            document.put("_id",id);
//            list.add(document);
//        });
        System.out.println("过滤后的数量："+list.size());
        System.out.println(list);
    }

    @Test
    void test14(){
        Map<String,Object> map = new HashMap<>();
        map.put("A1",1);
        map.put("A2",1);
        String projection = JSONUtil.toJsonStr(map);
        Map<String,Object> param = new HashMap<>();
        param.put("collectionName","file2");
        param.put("projection",projection);
        String result = HttpUtil.get("http://localhost:8989/api/query",param);
        JSONArray array = JSONUtil.parseArray(result);
        System.out.println(array.size());
        System.out.println(result);
    }

    @Test
    void test15(){
        List<Map<String,Object>> list = new ArrayList<>();
        Map<String,Object> map1 = new HashMap<>();
        map1.put("field","A1");
        map1.put("operation",">");
        map1.put("value",0);
        map1.put("connection","and");

        Map<String,Object> map2 = new HashMap<>();
        map2.put("field","A2");
        map2.put("operation","<");
        map2.put("value",0);
        map2.put("connection","and");

        Map<String,Object> map3 = new HashMap<>();
        map3.put("field","A3");
        map3.put("operation","<");
        map3.put("value",0);
        map3.put("connection","or");

        list.add(map1);
        list.add(map2);
        list.add(map3);

        // 思路：如果有or，则添加$or文档，并将所有and放在一个文档，每个or一个文档
        String jsonArr = JSONUtil.toJsonStr(list);
        System.out.println(jsonArr);
        List<Map<String,Object>> result = mongoDataBusiness.query("file2",3,25,null,jsonArr,"{A1:-1}");
        System.out.println(result);
    }

    @Test
    void test16(){
        String collectionName = "abc";
        String id = "5f3b7b4c16cff452807212d2";
//        Map<String,Object> document = mongoDataBusiness.removeDocument(collectionName,id);
//        System.out.println(document);

    }

    @Test
    void test17(){
        String collectionName = "user";
        Map<String,Object> filterMap = new HashMap<>();
        Map<String,Object> contact = new HashMap<>();
        contact.put("email","qq.com");
        contact.put("addr","wuxi");
        filterMap.put("name","a");
//        filterMap.put("contact",contact);
        String filterJson = JSONUtil.toJsonStr(filterMap);
        System.out.println(filterJson);
        Map<String,Object> updateMap = new HashMap<>();
        updateMap.put("name","a2");
        String updateJson = JSONUtil.toJsonStr(updateMap);
        long count = mongoDataBusiness.updateDocument(collectionName,filterJson,updateJson,true);
        System.out.println("共修改了"+count+"个文档");
    }

    @Test
    void test18(){
        String collectionName = "user";
        Map<String,Object> filterMap = new HashMap<>();
//        Map<String,Object> contact = new HashMap<>();
//        contact.put("email","qq.com");
//        contact.put("addr","wuxi");
        filterMap.put("addr","sh");
//        filterMap.put("contact",contact);
        String filterJson = JSONUtil.toJsonStr(filterMap);
        System.out.println(filterJson);
        Map<String,Object> replaceMap = new HashMap<>();
//        replaceMap.put("name","wxl");
        replaceMap.put("age",27);
//        replaceMap.put("age",35);
        String replaceJson = JSONUtil.toJsonStr(replaceMap);
        long count = mongoDataBusiness.replaceDocument(collectionName,filterJson,replaceJson);
        System.out.println("共修改了"+count+"个文档");
    }

    @Test
    void test19(){
        Boolean a = null;
        System.out.println(a);
    }

    @Test
    void test20(){
        String collectionName = "user";
        Map<String,Object> filterMap = new HashMap<>();
        filterMap.put("name","b");
//        filterMap.put("contact",contact);
        String filterJson = JSONUtil.toJsonStr(filterMap);
        long count = mongoDataBusiness.removeDocument(collectionName,filterJson,true);
        System.out.println("共删除了"+count+"个文档");
    }

    @Test
    void test21(){
        String collectionName = "file2";
        mongoDataBusiness.removeCollection(collectionName);
    }

    @Test
    void test22(){
        MongoClientManager.createDatabase("mydb").drop();
    }

    @Test
    void test23(){
        // 测试插入
        String collectionName = "c1";
        String urlStr = "http://localhost:8989/api/mongo";
        List<Map<String,Object>> list = new ArrayList<>();

        Map<String,Object> document1 = new HashMap<>();
        document1.put("name","c#");
        document1.put("rank",3);
        list.add(document1);

        Map<String,Object> document2 = new HashMap<>();
        document2.put("name","c");
        document2.put("rank",3);
        list.add(document2);


        String json = JSONUtil.toJsonStr(list);
        Map<String,Object> param = new HashMap<>();
        param.put("collectionName",collectionName);
        param.put("jsonArray",json);
        String result = HttpUtil.post(urlStr+"/saveAll",param);
        System.out.println(result);
    }

    @Test
    void test24(){
        String urlStr = "http://localhost:8989/api/mongo";
        String collectionName = "c1";
        Integer page = 1;
        Integer pageSize = 25;
        String projection = "{name:1,rank:1}";
        String filter = "[{field:\"rank\",operation:\"=\",value:3,connection:\"and\"}]";
        String sort = "{name:-1}";
        Map<String,Object> param = new HashMap<>();
        param.put("collectionName",collectionName);
        param.put("page",page);
        param.put("pageSize",pageSize);
        param.put("projection",projection);
        param.put("filter",filter);
        param.put("sort",sort);
        String result = HttpUtil.get(urlStr+"/query",param);
        System.out.println(result);
    }

    @Test
    void test25(){
        String urlStr = "http://localhost:8989/api/mongo";
        String collectionName = "c1";
        Map<String,Object> filterMap = new HashMap<>();
        filterMap.put("name","python");
        String filterJson = JSONUtil.toJsonStr(filterMap);
        Map<String,Object> updateMap = new HashMap<>();
        updateMap.put("price",69);
//        updateMap.put("rank",6);
        String updateJson = JSONUtil.toJsonStr(updateMap);
        Boolean multi = true;
        Map<String,Object> param = new HashMap<>();
        param.put("collectionName",collectionName);
        param.put("filterJson",filterJson);
        param.put("replaceJson",updateJson);
        param.put("multi",multi);
        String result = HttpUtil.post(urlStr+"/replace",param);
        System.out.println(result);
    }

    @Test
    void test26(){
        String urlStr = "http://localhost:8989/api/mongo";
        String collectionName = "c1";
        Map<String,Object> filterMap = new HashMap<>();
        filterMap.put("rank",8);
        String filterJson = JSONUtil.toJsonStr(filterMap);
        Boolean multi = true;
        Map<String,Object> param = new HashMap<>();
        param.put("collectionName",collectionName);
        param.put("filterJson",filterJson);
        param.put("multi",multi);
        String result = HttpUtil.post(urlStr+"/removeDocument",param);
        System.out.println(result);
    }

    @Test
    void test27(){
        String urlStr = "http://localhost:8989/api/mongo";
        String collectionName = "mycoll";
        Map<String,Object> param = new HashMap<>();
        param.put("collectionName",collectionName);
        String result = HttpUtil.post(urlStr+"/removeCollection",param);
        System.out.println(result);
    }

    @Test
    void test28(){
        String urlStr = "http://localhost:8989/api/mongo";
        String collectionName = "mysheet";
        Map<String,Object> param = new HashMap<>();
//        File file = new File("/Users/sherlonwang/Desktop/test.txt");
        File file = new File("/Users/sherlonwang/Desktop/excel.xlsx");
        param.put("collectionName",collectionName);
        param.put("file",file);
        param.put("separator"," ");
        param.put("fileType","int");
        String result = HttpUtil.post(urlStr+"/import",param);
        System.out.println(result);
    }

}
