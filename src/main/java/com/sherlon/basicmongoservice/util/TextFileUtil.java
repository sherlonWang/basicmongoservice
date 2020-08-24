package com.sherlon.basicmongoservice.util;

import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.util.StrUtil;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author :  sherlonWang
 * @description :  处理文本类文件工具类
 * @date: 2020-03-05
 */
public class TextFileUtil {

    /**
     * 获取数据表头
     * @param inputStream 输入流
     * @param pos  表头所在行
     * @param separator 分隔符
     * @return
     */
    public static List<String> getHeader(InputStream inputStream, Integer pos, String separator) {
        if (inputStream == null) {
            throw new RuntimeException("文件为空");
        }
        // 空字符正则
        if (StrUtil.isBlank(separator)) {
            separator = "\\s+";
        }
//        FileReader fileReader = new FileReader(file, "utf-8");
//        BufferedReader bufferedReader = fileReader.getReader();

        Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        BufferedReader bufferedReader = new BufferedReader( reader);
        int index = 0;
        String line;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                index++;
                if (index < pos || StrUtil.isBlank(line)) {
                    continue;
                }
                line = line.trim();
                return Arrays.asList(line.split(separator));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new ArrayList<>();
    }

    public static List<String> getHeader(InputStream inputStream, String separator) {
        return getHeader(inputStream,0,separator);
    }


        /**
         * 获取数据表头
         * @param file 文本类文件
         * @param pos  表头所在行
         * @param separator 分隔符
         * @return
         */
    public static List<String> getHeader(File file, Integer pos, String separator) {
        if (file == null || !file.exists()) {
            throw new RuntimeException("文件不存在");
        }
        // 空字符正则
        if (StrUtil.isBlank(separator)) {
            separator = "\\s+";
        }
        FileReader fileReader = new FileReader(file, "utf-8");
        BufferedReader bufferedReader = fileReader.getReader();
        int index = 0;
        String line;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                index++;
                if (index < pos || StrUtil.isBlank(line)) {
                    continue;
                }
                line = line.trim();
                return Arrays.asList(line.split(separator));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (bufferedReader!=null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return new ArrayList<>();
    }

    /**
     * 获取数据表头
     * @param file 文本类文件
     * @param separator 分隔符
     * @return
     */
    public static List<String> getHeader(File file, String separator) {
        return getHeader(file,0,separator);
    }

    /**
     * 解析文本类文件
     * @param file 文本文件
     * @param pos 开始行数 不传默认为1
     * @param separator 分隔符
     * @return
     */
    public static List<Map<String, String>> getTextFileData(File file, Integer pos, String separator) {
        if (file == null || !file.exists()) {
            throw new RuntimeException("文件不存在");
        }
        if (pos == null || pos < 2) {
            pos = 1;
        }
        if (StrUtil.isBlank(separator)) {
            separator = "\\s+";
        }
        List<Map<String,String>> list = new ArrayList<>();
        List<String> cols = getHeader(file, pos, separator);
        FileReader fileReader = new FileReader(file, "utf-8");
        BufferedReader bufferedReader = fileReader.getReader();
        int index = 0;
        boolean skipHeader = false;
        String line;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                index++;
                if (StrUtil.isBlank(line)) {
                    continue;
                }
                if (index < pos) {
                    continue;
                }
                if (skipHeader) {
                    String[] arr = line.trim().split(separator);
                    Map<String,String> map = new LinkedHashMap<>();
                    for (int i = 0; i < cols.size(); i++) {
                        map.put(cols.get(i),arr[i]);
                    }
                    list.add(map);
                }else {
                    skipHeader = true;
                }
            }
            return list;
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (bufferedReader!=null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 解析文本类文件
     * @param file 文本文件
     * @param separator 分隔符
     * @return
     */
    public static List<Map<String, String>> getTextFileData(File file, String separator) {
        return getTextFileData(file,1,separator);
    }
}
