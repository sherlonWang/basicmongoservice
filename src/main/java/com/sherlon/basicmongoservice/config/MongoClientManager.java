package com.sherlon.basicmongoservice.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * @author :  sherlonWang
 * @description :  mongo连接实例管理类
 * @date: 2020-08-19
 */
@Component
public class MongoClientManager implements Serializable {

    /**
     * mongo-driver 连接信息
     */
    private static String uri;

    /**
     * 默认数据库名称
     */
    private static String database;

    public static String getUri() {
        return uri;
    }

    /**
     * 关联application.yml中的参数值
     * @param uri 连接信息
     */
    @Value("${mongodriver.uri}")
    public void setUri(String uri) {
        MongoClientManager.uri = uri;
    }

    public static String getDatabase() {
        return database;
    }

    /**
     * 关联application.yml中的参数值
     * @param database 默认数据库名称
     */
    @Value("${mongodriver.database}")
    public void setDatabase(String database) {
        MongoClientManager.database = database;
    }

    /**
     * 创建mongo数据库实例
     * @return 数据库实例
     */
    public static MongoDatabase createDatabase(){
        MongoClient mongoClient = MongoClients.create(uri);
        return mongoClient.getDatabase(database);
    }

    /**
     * 创建mongo数据库实例
     * @param database 数据库名称
     * @return 数据库实例
     */
    public static MongoDatabase createDatabase(String database){
        MongoClient mongoClient = MongoClients.create(uri);
        return mongoClient.getDatabase(database);
    }

}
