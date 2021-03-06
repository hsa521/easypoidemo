package com.javagi.test;

import com.javagi.model.ExcelDataVO;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description   解析类
 * @Author kuiwang
 * @Date 2019/7/8 14:20
 * @Version 1.0
 */
public class ExcelReader {
    private static Logger logger = LoggerFactory.getLogger(ExcelReader.class);

    private static final String XLS = "xls";
    private static final String XLSX = "xlsx";

    /**
     * 读取Excel文件内容  上传文件file
     * @param file 上传的Excel文件
     * @return 读取结果列表，读取失败时返回null
     */
    public static List<ExcelDataVO> readExcel(MultipartFile file) {
        Workbook workbook = null;
        try {
            // 获取Excel后缀名
            String fileName = file.getOriginalFilename();
            if (fileName == null || fileName.isEmpty() || fileName.lastIndexOf(".") < 0) {
                logger.warn("解析Excel失败，因为获取到的Excel文件名非法！");
                return null;
            }
            String fileType = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
            // 获取Excel工作簿
            workbook = getWorkbook(file.getInputStream(), fileType);

            //workbook = WorkbookFactory.create(file.getInputStream());
            // 读取excel中的数据
            List<ExcelDataVO> resultDataList = parseExcel(workbook);

            return resultDataList;
        } catch (Exception e) {
            logger.warn("解析Excel失败，文件名：" + file.getOriginalFilename() + " 错误信息：" + e.getMessage());
            return null;
        } finally {
            try {
                if (null != workbook) {
                    //workbook.close();
                }
            } catch (Exception e) {
                logger.warn("关闭数据流出错！错误信息：" + e.getMessage());
                return null;
            }
        }
    }

    /**
     * 读取Excel文件内容 本地测试 fileName
     * @param fileName 要读取的Excel文件所在路径
     * @return 读取结果列表，读取失败时返回null
     */
    public static List<ExcelDataVO> readExcel(String fileName) {

        Workbook workbook = null;
        FileInputStream inputStream = null;
        try {
            // 获取Excel后缀名
            String fileType = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
            // 获取Excel文件
            File excelFile = new File(fileName);
            if (!excelFile.exists()) {
                logger.warn("指定的Excel文件不存在！");
                return null;
            }

            // 获取Excel工作簿
            inputStream = new FileInputStream(excelFile);
            workbook = getWorkbook(inputStream, fileType);

            //workbook = WorkbookFactory.create(inputStream);

            // 读取excel中的数据
            List<ExcelDataVO> resultDataList = parseExcel(workbook);
            return resultDataList;
        } catch (Exception e) {
            logger.warn("解析Excel失败，文件名：" + fileName + " 错误信息：" + e.getMessage());
            return null;
        } finally {
            try {
                if (null != workbook) {
//                    workbook.close();
                }
                if (null != inputStream) {
                    inputStream.close();
                }
            } catch (Exception e) {
                logger.warn("关闭数据流出错！错误信息：" + e.getMessage());
                return null;
            }
        }
    }

    /**
     * 根据文件后缀名类型获取对应的工作簿对象
     * @param inputStream 读取文件的输入流
     * @param fileType 文件后缀名类型（xls或xlsx）
     * @return 包含文件数据的工作簿对象
     * @throws IOException
     */
    public static Workbook getWorkbook(InputStream inputStream, String fileType) throws IOException {
        Workbook workbook = null;
        if (fileType.equalsIgnoreCase(XLS)) {
            workbook = new HSSFWorkbook(inputStream);
        } else if (fileType.equalsIgnoreCase(XLSX)) {
            workbook = new XSSFWorkbook(inputStream);
        }
        return workbook;
    }

    /**
     * 解析Excel数据
     * @param workbook Excel工作簿对象
     * @return 解析结果
     */
    private static List<ExcelDataVO> parseExcel(Workbook workbook) {
        List<ExcelDataVO> resultDataList = new ArrayList<>();
        // 解析sheet
        for (int sheetNum = 0; sheetNum < workbook.getNumberOfSheets(); sheetNum++) {
            Sheet sheet = workbook.getSheetAt(sheetNum);
            // 校验sheet是否合法
            if (sheet == null) {
                continue;
            }
            // 获取第一行数据
            int firstRowNum = sheet.getFirstRowNum();
            Row firstRow = sheet.getRow(firstRowNum);
            if (null == firstRow) {
                logger.warn("解析Excel失败，在第一行没有读取到任何数据！");
            }
            // 解析每一行的数据，构造数据对象
            int rowStart = firstRowNum + 1;
            int rowEnd = sheet.getPhysicalNumberOfRows();
            for (int rowNum = rowStart; rowNum < rowEnd; rowNum++) {
                Row row = sheet.getRow(rowNum);
                if (null == row) {
                    continue;
                }
                ExcelDataVO resultData = convertRowToData(row);
                if (null == resultData) {
                    logger.warn("第 " + row.getRowNum() + "行数据不合法，已忽略！");
                    continue;
                }
                resultDataList.add(resultData);
            }
        }

        return resultDataList;
    }

    /**
     * 提取每一行中需要的数据，构造成为一个结果数据对象
     *
     * 当该行中有单元格的数据为空或不合法时，忽略该行的数据
     *
     * @param row 行数据
     * @return 解析后的行数据对象，行数据错误时返回null
     */
    private static ExcelDataVO convertRowToData(Row row) {
        ExcelDataVO resultData = new ExcelDataVO();
        Cell cell;
        int cellNum = 0;
        cell = row.getCell(cellNum++);
        // 获取姓名
        String name = convertCellValueToString(cell);
        resultData.setName(name);
        // 获取年龄
        cell = row.getCell(cellNum++);
        String ageStr = convertCellValueToString(cell);
        if (null == ageStr || "".equals(ageStr)) {
            // 年龄为空
            resultData.setAge(null);
        } else {
            resultData.setAge(Integer.parseInt(ageStr));
        }
        // 获取居住地
        cell = row.getCell(cellNum++);
        String location = convertCellValueToString(cell);
        resultData.setLocation(location);
        // 获取职业
        cell = row.getCell(cellNum++);
        String job = convertCellValueToString(cell);
        resultData.setJob(job);

        return resultData;
    }

    /**
     * 将单元格内容转换为字符串
     * @param cell
     * @return
     */
    private static String convertCellValueToString(Cell cell) {
        if(cell==null){
            return null;
        }
        String returnValue = null;
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_NUMERIC: //数字
                Double doubleValue = cell.getNumericCellValue();

                // 格式化科学计数法，取一位整数
                DecimalFormat df = new DecimalFormat("0");
                returnValue = df.format(doubleValue);
                break;
            case Cell.CELL_TYPE_STRING: //字符串
                returnValue = cell.getStringCellValue();
                break;
            case Cell.CELL_TYPE_BOOLEAN: //布尔
                Boolean booleanValue = cell.getBooleanCellValue();
                returnValue = booleanValue.toString();
                break;
            case Cell.CELL_TYPE_BLANK: // 空值
                break;
            case Cell.CELL_TYPE_FORMULA: // 公式
                returnValue = cell.getCellFormula();
                break;
            case Cell.CELL_TYPE_ERROR: // 故障
                break;
            default:
                break;
        }
        return returnValue;
    }


    /**
     * @Description  本地测试
     * @Author kuiwang
     * @Date 14:31 2019/7/8
     * @param args
     * @Return
     */
    public static void main(String[] args) {
        //String excelFileName = "F:/TEST/test-001.xls";
        String excelFileName = "F:/TEST/test-002.xlsx";
        List<ExcelDataVO> readResult = ExcelReader.readExcel(excelFileName);
        //List<ExcelDataVO> readResult = new ArrayList<>();
        //ExcelDataVO aa = new ExcelDataVO("好", 2, "3", "4");
        //readResult.add(aa);
        readResult.forEach(re -> {
            System.out.println(re.toString());
        });
    }
}
