package com.javagi.controller;

import cn.afterturn.easypoi.entity.vo.TemplateExcelConstants;
import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.afterturn.easypoi.excel.entity.TemplateExportParams;
import com.javagi.model.Student;
import com.javagi.model.StudentExcel;
import com.javagi.service.StudentService;
import com.javagi.utils.ExportUtil;
import com.spire.doc.FileFormat;
import com.spire.doc.Section;
import com.spire.doc.documents.Paragraph;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Description
 * @Author kuiwang
 * @Date 2019/7/1 15:27
 * @Version 1.0
 */
@RequestMapping("/export")
@RestController
public class StudentController extends ExportUtil {

    private static final String EXPORT_USER_TEST = "学生信息报表";
    private static final String EXPORT_STUDENT_TEST_MODEL = "doc/学生信息导出模板.xls";
    private static final String EXPORT_USER_TEST_WORD = "doc/测试导出word文档.docx";

    @Autowired
    private StudentService studentService;

    /**
     * @Description  普通导出excel
     * @Author kuiwang
     * @Date 17:57 2019/7/1
     * @param request
     * @param response
     * @Return
     */
    @GetMapping("/exportExcel")
    public void exportExcel(HttpServletRequest request, HttpServletResponse response) throws Exception{
        List<Student> students = studentService.getAllStudents();
        List<StudentExcel> studentExcels = new ArrayList<>();
        students.forEach( student -> {
            System.out.println(student.getName());
            StudentExcel studentExcel = new StudentExcel();
            BeanUtils.copyProperties(student, studentExcel);
            studentExcels.add(studentExcel);
        });

        Workbook workbook = ExcelExportUtil.exportExcel(new ExportParams("学生信息报表", "学生"), StudentExcel.class, studentExcels);
        response.setHeader("content-disposition", "attachment;filename=" + "学生信息报表" + EXPORT_SUFFIX_EXCEL);
        response.setContentType("application/octet-stream");
        response.flushBuffer();
        workbook.write(response.getOutputStream());
        //this.writeToExcel("学生信息", "学生详情信息", StudentExcel.class, studentExcels, request, response);
        System.out.println("-==========成功导出 EXCEL  表格========");
    }

    /**
     * @Description  根据excel模板导出表格 ok
     * @Author kuiwang
     * @Date 17:57 2019/7/1
     * @param request
     * @param response
     * @Return
     */
    @GetMapping("/exportAll")
    public String exportExcelAll(HttpServletRequest request, HttpServletResponse response) {
        List<Student> students = studentService.getAllStudents();
        Map<String, Object> map = new HashMap<>();
        map.put("students", students);
        this.writeToExcelTemplate(EXPORT_USER_TEST, EXPORT_STUDENT_TEST_MODEL, map, request, response);
        return "Success!";
    }

    /**
     * @Description  根据excel模板导出合并表格 ok
     * @Author kuiwang
     * @Date 17:57 2019/7/1
     * @param request
     * @param response
     * @Return
     */
    @GetMapping("/writeToExcelTemplateNeedMerge")
    public String writeToExcelTemplateNeedMerge(HttpServletRequest request, HttpServletResponse response) {
        List<Student> students = studentService.getAllStudents();
        Map<String, Object> map = new HashMap<>();
        map.put("students", students);
        this.writeToExcelTemplateNeedMerge(EXPORT_USER_TEST, EXPORT_STUDENT_TEST_MODEL, map,
                3, 2, 2+students.size(), new int[]{1,2}, request, response);
        return "Success!";
    }


    /**
     * @Description 普通导出pdf ok
     * @Author kuiwang
     * @Date 18:01 2019/7/1
     * @param request
     * @param response
     * @Return
     */
    @GetMapping("/exportPdf")
    public String exportPdf(HttpServletRequest request, HttpServletResponse response) {
        List<Student> students = studentService.getAllStudents();
        List<StudentExcel> studentExcels = new ArrayList<>();
        students.forEach( student -> {
            StudentExcel studentExcel = new StudentExcel();
            BeanUtils.copyProperties(student, studentExcel);
            studentExcels.add(studentExcel);
        });
        this.writeToPdf("学生信息", "学生详情信息", StudentExcel.class, studentExcels, request, response);
        return "Success!";
    }

    /**
     * @Description 根据模板导出pdf ok
     * @Author kuiwang
     * @Date 10:43 2019/7/2
     * @param request
     * @param response
     * @Return
     */
    @GetMapping("/exportPdfTemplate")
    public String exportPdfTemplate(HttpServletRequest request, HttpServletResponse response) {
        // 文字
        Map<String,String> map = new HashMap<>();
        map.put("id","王谋仁");
        map.put("one","2018年1月1日");
        map.put("two","晴朗");
        map.put("zq_xm","打羽毛球");
        map.put("cm_xm","打羽毛球");
        map.put("tj_xm","打羽毛球");

        // 图片
        Map<String,String> map2 =new HashMap<>();
        map2.put("img","c:/50336.jpg");

        // 调用
        Map<String,Object> o=new HashMap();
        o.put("charMap",map);
        o.put("imgMap",map2);

        String pdfTempLatePath = "doc/要导出pdf模板.pdf";
        this.writeToPdfTemplate(pdfTempLatePath, "导出后新pdf名称", o, request, response);
        return "Success!";
    }


    /**
     * @Description  根据模板导出word
     * @Author kuiwang
     * @Date 11:43 2019/7/25
     * @param request
     * @param response
     * @Return
     */
    @GetMapping("/exprotWordTemplate")
    public String exprotWordTemplate(HttpServletRequest request, HttpServletResponse response){
        long l = System.currentTimeMillis();
        Map<String, Object> map = new HashMap<String, Object>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:MM:ss");
        map.put("department", "Easypoi");
        map.put("person", "JueYue");
        map.put("time", sdf.format(new Date()));
        map.put("me","JueYue");
        map.put("date", "2015-01-03");
        this.writeToWordTemplate("要导出word的名称", EXPORT_USER_TEST_WORD, map, request, response);
        return "导出word 成功，耗时:" + (System.currentTimeMillis() - l) + " 毫秒";
    }

    /**
     * @Description  根据word模板导出pdf
     * @Author kuiwang
     * @Date 11:43 2019/7/25
     * @param request
     * @param response
     * @Return
     */
    @GetMapping("/writeWordTemplateToPdf")
    public String writeWordTemplateToPdf(HttpServletRequest request, HttpServletResponse response){
        long l = System.currentTimeMillis();
        Map<String, Object> map = new HashMap<String, Object>();
        SimpleDateFormat sdf = new SimpleDateFormat();
        map.put("department", "Easypoi");
        map.put("person", "JueYue");
        map.put("time", sdf.format(new Date()));
        map.put("me","JueYue");
        map.put("date", "2015-01-03");
        this.writeWordTemplateToPdf("要导出word的名称", EXPORT_USER_TEST_WORD, map, request, response);
        return "导出pdf 成功，耗时:" + (System.currentTimeMillis() - l) + " 毫秒";
    }

    /**
     * @Description     测试
     * @Author kuiwang
     * @Date 13:18 2019/9/24
     * @param file
     * //@param pdfPath
     * @param request
     * @param response
     * @Return
     */
    @GetMapping("/wordToPdf")
    public static String wordToPdf(MultipartFile file, HttpServletRequest request, HttpServletResponse response) {
        long l = System.currentTimeMillis();
        try {
            String originalFilename = file.getOriginalFilename();
            String fileName = originalFilename.substring(0, originalFilename.lastIndexOf('.'));
            ByteArrayInputStream bis = new ByteArrayInputStream(file.getBytes());
            // 合成后的doc
            com.spire.doc.Document doc = new com.spire.doc.Document();
            // 空白页的doc
            com.spire.doc.Document blankDoc = new com.spire.doc.Document();
            Section section = blankDoc.addSection();
            Paragraph paragraph = section.addParagraph();
            paragraph.appendText("添加空白页做去除水印");
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            blankDoc.saveToFile(os, FileFormat.Docx);
            // 添加空白页
            ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
            doc.loadFromStream(is, FileFormat.Docx);
            doc.insertTextFromStream(bis, FileFormat.Docx);
            os.close();
            is.close();
            blankDoc.close();

            // 转换为pdf文档
            ByteArrayOutputStream pdfBos = new ByteArrayOutputStream();
            doc.setJPEGQuality(40);
            doc.saveToFile(pdfBos, FileFormat.PDF);
            ByteArrayInputStream pdfBis = new ByteArrayInputStream(pdfBos.toByteArray());
            pdfBos.close();
            pdfBis.close();
            doc.close();

            // 去除pdf第一空白页并导出
            String pdfPath = "D:\\QRCodeTemp";
            pdfPath = pdfPath + File.separator + fileName + ".pdf";
            PDDocument pdDocument = PDDocument.load(pdfBis);
            pdDocument.removePage(0);
            pdDocument.save(pdfPath);
            pdDocument.close();

            logger.error("导出pdf成功！耗时："+(System.currentTimeMillis() - l) + " ms");
        } catch (Exception e) {
            logger.error("导出pdf失败！此事必有蹊跷，定当测查！", e);
        }
        return "======导出pdf成功==耗时：" + (System.currentTimeMillis() - l) + " ms";
    }


   //上传文件的保存
    public static void uploadFile(byte[] file, String filePath, String fileName) throws Exception {
        File targetFile = new File(filePath);
        if (!targetFile.exists()) {
            targetFile.mkdirs();
        }
        FileOutputStream out = new FileOutputStream(filePath + File.separator + fileName);
        out.write(file);
        out.flush();
        out.close();
    }
    // 导入全部数据
    @RequestMapping("importData")
    public void importData(@RequestParam(value = "file") MultipartFile file, HttpServletRequest request) throws Exception {
        String fileName = file.getOriginalFilename();
        // 下面的filepath 获取的是上传到tomcat 数据缓存目录中的位置
        // String filePath = request.getServletContext().getRealPath("uploads");
        // 这个filepath 是项目中的static 下的自定义文件存放目录
        String filePath = "src/main/resources/static/uploads";
        uploadFile(file.getBytes(), filePath, fileName);
        ImportParams params = new ImportParams();
        params.setTitleRows(1);
        params.setHeadRows(1);
        long t = System.currentTimeMillis();
        List<Student> list = ExcelImportUtil.importExcel(new File(filePath + File.separator + fileName), Student.class, params);
        /*easypoiService.saveAll(list);*/
        long t1 = System.currentTimeMillis();
        System.out.println(t1 - t);
    }

}
