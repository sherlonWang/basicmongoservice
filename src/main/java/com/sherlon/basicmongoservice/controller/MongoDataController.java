package com.sherlon.basicmongoservice.controller;

import com.sherlon.basicmongoservice.business.MongoDataBusiness;
import com.sherlon.basicmongoservice.vo.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author :  sherlonWang
 * @description :  mongo服务控制器
 * @date: 2020-08-21
 */
@RestController
@RequestMapping(value = "/mongo")
public class MongoDataController {
    @Autowired
    MongoDataBusiness mongoDataBusiness;

    /**
     * 插入数据
     *
     * @param jsonObject     json对象字符串 {"A1":1,"A2":2}
     * @param collectionName 集合名称
     * @return 返回带_id的map
     */
    @PostMapping(value = "/save")
    public JsonResult<Map<String, Object>> save(String collectionName, String jsonObject) {
        Map<String, Object> result = mongoDataBusiness.insert(collectionName,jsonObject);
        return new JsonResult<>(result);
    }

    /**
     * 批量插入数据
     *
     * @param jsonArray      json对象数组字符串串
     * @param collectionName 集合名称
     * @return 返回带_id的map集合
     */
    @PostMapping(value = "/saveAll")
    public JsonResult<List<Map>> saveAll(String collectionName,String jsonArray) {
        List<Map> result = mongoDataBusiness.batchInsert(collectionName,jsonArray);
        return new JsonResult<>(result);
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
    @GetMapping(value = "/query")
    public JsonResult<List<Map<String, Object>>> query(String collectionName, Integer page, Integer pageSize, String projection, String filter, String sort) {
        List<Map<String, Object>> result = mongoDataBusiness.query(collectionName, page, pageSize, projection, filter, sort);
        return new JsonResult<>(result);
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
    @PostMapping(value = "/update")
    public JsonResult<Long> update(String collectionName, String filterJson, String updateJson, Boolean multi) {
        long result = mongoDataBusiness.updateDocument(collectionName, filterJson, updateJson, multi);
        return new JsonResult<>(result);
    }

    /**
     * 替换文档
     *
     * @param collectionName 集合名称
     * @param filterJson     过滤条件
     * @param replaceJson    替换文档
     * @return 返回替换文档的个数
     */
    @PostMapping(value = "/replace")
    public JsonResult<Long> replace(String collectionName, String filterJson, String replaceJson) {
        long result = mongoDataBusiness.replaceDocument(collectionName, filterJson, replaceJson);
        return new JsonResult<>(result);
    }

    /**
     * 删除文档
     *
     * @param collectionName 集合名称
     * @param filterJson     查询条件
     * @param multi          是否删除多个
     * @return 返回删除文档的个数
     */
    @PostMapping(value = "/removeDocument")
    public JsonResult<Long> removeDocument(String collectionName, String filterJson, Boolean multi) {
        long result = mongoDataBusiness.removeDocument(collectionName, filterJson, multi);
        return new JsonResult<>(result);
    }

    /**
     * 删除集合
     *
     * @param collectionName 集合名称
     * @return true or false
     */
    @PostMapping(value = "removeCollection")
    public JsonResult<Boolean> removeCollection(String collectionName){
        boolean result = mongoDataBusiness.removeCollection(collectionName);
        return new JsonResult<>(result);
    }


    /**
     * 数据文件导入
     *
     * @param file           文件流
     * @param collectionName 集合名称
     * @param separator      文件内容分隔符
     * @param fileType       文件数值类型 int,string,double,float
     * @return 导入成功返回集合名称，否则返回错误信息
     */
    @PostMapping(value = "/import")
    public JsonResult<List<String>> importFile(MultipartFile file, String collectionName, String separator, String fileType) {
        // 区分是文档类文件还是excel等二进制文件
        if (file == null || file.getSize() == 0) {
            throw  new RuntimeException("文件不能为空");
        }
        String suffix = Objects.requireNonNull(file.getOriginalFilename()).substring(file.getOriginalFilename().lastIndexOf("."));
        if (!suffix.equalsIgnoreCase(".xls") && !suffix.equalsIgnoreCase(".xlsx")) {
            List<String> result = mongoDataBusiness.textFileImport(file, collectionName, separator, fileType);
            return new JsonResult<>(result);
        }
        List<String> ret = mongoDataBusiness.excelFileImport(file, collectionName);
        return new JsonResult<>(ret);
    }
}
