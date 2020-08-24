package com.sherlon.basicmongoservice.util;

import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author :  sherlonWang
 * @description :  Excel读写相关工具类
 * @date: 2020-03-04
 */
public class ExcelFileUtil {

    /**
     * 判断是否为excel类型的文件
     * @param file
     * @return
     */
    public static boolean isExcel(File file) {
        if (file == null || !file.exists()) {
            throw new RuntimeException("文件不存在");
        }
        String fileName = file.getName();
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        if (!suffix.equalsIgnoreCase("xls") && !suffix.equalsIgnoreCase("xlsx")) {
            return false;
        }
        return true;
    }

    /**
     * 判断是否为excel类型的文件
     *
     * @param path
     * @return
     */
    public static boolean isExcel(String path) {
        if (path == null || path.trim().equals("")) {
            throw new RuntimeException("路径不能为空");
        }
        File file = new File(path);
        return isExcel(file);
    }

    /**
     * 获取workbook
     *
     * @param file
     * @return
     */
    public static Workbook getWorkbook(File file) {
        if (file == null || !file.exists()) {
            throw new RuntimeException("文件不存在");
        }
        boolean isExcel = isExcel(file);
        if (!isExcel) {
            throw new RuntimeException("不是excel类型的文件");
        }
        try {
            return WorkbookFactory.create(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取workbook
     *
     * @param path
     * @return
     */
    public static Workbook getWorkbook(String path) {
        if (path == null || path.trim().equals("")) {
            throw new RuntimeException("路径不能为空");
        }
        File file = new File(path);
        if (!file.exists()) {
            throw new RuntimeException("文件不存在");
        }
        return getWorkbook(file);
    }

    /**
     * 计算workbook有多少个sheet
     *
     * @param workbook
     * @return
     */
    public static int sheetCount(Workbook workbook) {
        if (workbook == null) {
            throw new RuntimeException("工作簿为空");
        }
        return workbook.getNumberOfSheets();
    }

    /**
     * 根据sheet下标获取sheet
     *
     * @param workbook   工作簿
     * @param sheetIndex sheet下标
     * @return
     */
    public static Sheet getSheetByIndex(Workbook workbook, int sheetIndex) {
        if (workbook == null) {
            throw new RuntimeException("工作薄为空");
        }
        int sheetCount = sheetCount(workbook);
        if (sheetIndex >= sheetCount) {
            throw new RuntimeException("sheet下标越界");
        }
        return workbook.getSheetAt(sheetIndex);
    }


    /**
     * 根据sheet名称获取sheet
     *
     * @param workbook  工作簿
     * @param sheetName sheet名称
     * @return
     */
    public static Sheet getSheetByName(Workbook workbook, String sheetName) {
        if (workbook == null) {
            throw new RuntimeException("工作薄为空");
        }
        try {
            return workbook.getSheet(sheetName);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("sheet名称不存在");
        }
    }

    /**
     * 获取sheet表头
     *
     * @param sheet  sheet
     * @param rowPos 表头所在行下标
     * @return 表头集合
     */
    public static List<String> getSheetHeader(Sheet sheet, Integer rowPos) {
        if (sheet == null) {
            throw new RuntimeException("Sheet不能为空");
        }
        if (rowPos == null) {
            rowPos = 0;
        }
        int rowNum = sheet.getLastRowNum();
        if (rowPos >= rowNum) {
            throw new RuntimeException("sheet所选行超出范围");
        }
        List<String> list = new ArrayList<>();
        Row row = sheet.getRow(rowPos);
        int colNum = sheet.getRow(rowPos).getPhysicalNumberOfCells();
        for (int i = 0; i < colNum; i++) {
            list.add(String.valueOf(getCellFormatValue(row.getCell(i))));
        }
        return list;
    }

    public static List<String> getSheetHeader(Sheet sheet) {
        return getSheetHeader(sheet, 0);
    }

    /**
     * 根据header或取sheet数据 key:col,value:cellValue
     * @param sheet sheet页
     * @param pos 从第pos行开始解析，pos默认为1
     * @return
     */
    public static List<Map<String, String>> getSimpleSheetData(Sheet sheet, Integer pos) {
        if (sheet == null) {
            throw new RuntimeException("Sheet不能为空");
        }
        List<Map<String, String>> result = new ArrayList<>();
        int rowNum = sheet.getLastRowNum();
        int colNum = sheet.getRow(pos - 1).getPhysicalNumberOfCells();
        List<String> cols = getSheetHeader(sheet, pos - 1);
        for (int i = pos; i <= rowNum; i++) {
            Row row = sheet.getRow(i);
            if (row == null) {
                continue;
            }
            Map<String, String> map = new LinkedHashMap<>();
            for (int j = 0; j < colNum; j++) {
                map.put(cols.get(j), String.valueOf(getCellFormatValue(row.getCell(j))));
            }
            result.add(map);
        }
        return result;
    }

    public static List<Map<String, String>> getSimpleSheetData(Sheet sheet) {
        return getSimpleSheetData(sheet, 1);
    }

    /**
     * 按行获取sheet数据
     * @param sheet sheet页
     * @param pos 数据开始行,默认为1
     * @return
     */
    public static List<List<String>> getSimpleSheetDataByRow(Sheet sheet,Integer pos){
        if (sheet == null) {
            throw new RuntimeException("Sheet不能为空");
        }
        List<List<String>> result = new ArrayList<>();
        int rowNum = sheet.getLastRowNum();
        int colNum = sheet.getRow(pos - 1).getPhysicalNumberOfCells();
        for (int i = pos-1;i<rowNum;i++) {
            Row row = sheet.getRow(i);
            if (row == null) {
                continue;
            }
            List<String> rows = new ArrayList<>();
            for (int j = 0;j<colNum;j++) {
                String cellValue = String.valueOf(getCellFormatValue(row.getCell(j)));
                rows.add(cellValue);
            }
            result.add(rows);
        }
        return result;
    }

    public static List<List<String>> getSimpleSheetDataByRow(Sheet sheet){
        return getSimpleSheetDataByRow(sheet,1);
    }

    /**
     * 按列获取sheet数据
     * @param sheet sheet页
     * @param pos 数据开始行,默认为1
     * @return
     */
    public static List<List<String>> getSimpleSheetDataByColumn(Sheet sheet,Integer pos){
        if (sheet == null) {
            throw new RuntimeException("Sheet不能为空");
        }
        List<List<String>> result = new ArrayList<>();
        // 注：获取行编号，从0开始
        int rowNum = sheet.getLastRowNum();
        int colNum = sheet.getRow(pos - 1).getPhysicalNumberOfCells();
        for (int j = 0;j<colNum;j++) {
            List<String> colDatas = new ArrayList<>();
            for (int i = pos-1;i<=rowNum;i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }
                String cellValue = String.valueOf(getCellFormatValue(row.getCell(j)));
                colDatas.add(cellValue);
            }
            result.add(colDatas);
        }
        return result;
    }

    public static List<List<String>> getSimpleSheetDataByColumn(Sheet sheet){
        return getSimpleSheetDataByColumn(sheet,1);
    }


    /**
     * 读取单元格数据
     *
     * @param cell
     * @return
     */
    public static Object getCellFormatValue(Cell cell) {
        Object cellValue;
        if (cell == null) {
            return "";
        }
        /**
         * _NONE(-1),
         * NUMERIC(0),
         * STRING(1),
         * FORMULA(2),
         * BLANK(3),
         * BOOLEAN(4),
         * ERROR(5);
         */
        switch (cell.getCellTypeEnum()) {
            case _NONE: {
                cellValue = "_NONE";
                break;
            }
            case NUMERIC: {
                cellValue = cell.getNumericCellValue();
                break;
            }
            case STRING: {
                cellValue = cell.getStringCellValue();
                break;
            }
            case FORMULA: {
                cellValue = cell.getCellFormula();
                break;
            }
            case BLANK: {
                cellValue = "";
                break;
            }
            case BOOLEAN: {
                cellValue = cell.getBooleanCellValue();
                break;
            }
            case ERROR: {
                cellValue = cell.getErrorCellValue();
                break;
            }
            default: {
                cellValue = "";
                break;
            }
        }
        return cellValue;
    }

}
