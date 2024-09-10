package com.hospital.uitls;

import com.hospital.dao.OptionMapper;
import com.hospital.entity.Appointment;
import com.hospital.entity.Option;
import com.hospital.entity.Seek;
import com.hospital.service.OptionService;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PDFUtils {
    private static BaseFont bf;//创建字体
    private static Font font;

    static {
        try {
            bf = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            font = new Font(bf, 12);//使用字体
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //    public static void main(String[] args) {
//        Appointment appointment = new Appointment();
//        appointment.setId(1);
//        appointment.setDepartment("dadadad");
//        appointment.setDoctorname("小明");
//        appointment.setPatientname("小王");
//        appointment.setExpenses(new BigDecimal("23.6"));
//        appointment.setTime(new Date());
//        createAppointMent(appointment);
//        createSeekInfo(new Seek("浑身不舒服","花柳病","1,2,3,4,5",12,new BigDecimal("32.6"),"张安"));
//    }
    public static String createSeekInfo(Seek seek, OptionService optionService, String path) {
    	path = unescape(path);
        List ids=PatientDoctorutils.getOptionIds(seek.getOptions());
        List<Option> options=new ArrayList<>();
        ids.forEach(id->{
          Option option =optionService.getOption((Integer)id);
            options.add(option);
        });
        Document document = new Document();
        try {
            String str="";
            for(int i=0;i<options.size();i++){
                str+=options.get(i).getName()+"----"+options.get(i).getType()+"("+options.get(i).getPrice()+"元)\n";
            }
            PdfWriter.getInstance(document, new FileOutputStream(path+seek.getPatientname()+DateUtils.date2String(new Date())+"就诊单.pdf"));
            document.open();
            PdfPTable pdfPTable = new PdfPTable(4);
            createCell("诊断书", 4, pdfPTable, font);
            createCell("患者姓名:", 2, pdfPTable, font);
            createCell(seek.getPatientname(), 2, pdfPTable, font);
            createCell("患者描述病情", 4, pdfPTable, font);
            createCell(seek.getDescribes(), 4, pdfPTable, font);
            createCell("初步诊断病情:", 2, pdfPTable, font);
            createCell(seek.getIllname(), 2, pdfPTable, font);
            createCell("需要检查的项目", 4, pdfPTable, font);
            createCell(str, 4, pdfPTable, font);
            createCell("诊断人:", 2, pdfPTable, font);
            createCell(seek.getDoctorname(), 2, pdfPTable, font);
            createCell("是否需要住院:", 2, pdfPTable, font);
            if (seek.getDays() > 0) {
                createCell(seek.getDays() + "天", 2, pdfPTable, font);
            } else {
                createCell("不需要", 2, pdfPTable, font);
            }
            createCell("总计:", 2, pdfPTable, font);
            createCell(seek.getPrice() + "  (元)", 2, pdfPTable, font);
            document.add(pdfPTable);
            document.close();
            return "已生成";
        } catch (Exception e) {
            e.printStackTrace();
            return "系统内部错误，生成失败";
        }
    }

    public static String createAppointMent(Appointment appointment,String path) {
        Document document = new Document();
        try {
        	path = unescape(path);
            PdfWriter.getInstance(document, new FileOutputStream(path+appointment.getPatientname()+DateUtils.date2String(new Date())+"挂号单.pdf"));
            document.open();
            PdfPTable pdfPTable = new PdfPTable(4);
            createCell("挂号单", 4, pdfPTable, font);
            createCell("预约号码:", 2, pdfPTable, font);
            createCell(appointment.getId() + "", 2, pdfPTable, font);
            createCell("患者姓名:", 2, pdfPTable, font);
            createCell(appointment.getPatientname(), 2, pdfPTable, font);
            createCell("预约科室:", 2, pdfPTable, font);
            createCell(appointment.getDepartment(), 2, pdfPTable, font);
            createCell("预约医生:", 2, pdfPTable, font);
            createCell(appointment.getDoctorname(), 2, pdfPTable, font);
            createCell("门诊费:", 2, pdfPTable, font);
            createCell(appointment.getExpenses() + "  (元)", 2, pdfPTable, font);
            createCell("预约时间:", 2, pdfPTable, font);
            createCell(date2String(appointment.getTime()), 2, pdfPTable, font);
            document.add(pdfPTable);
            document.close();
            return "已生成";
        } catch (Exception e) {
            e.printStackTrace();
            return "系统内部错误，生成失败";
        }
    }
    
     public static String unescape(String s) {
    	 StringBuffer sbuf = new StringBuffer();
    	 int l = s.length();
    	 int ch = -1;
    	 int b, sumb = 0;
    	 for (int i = 0, more = -1; i < l; i++) {
    	 /* Get next byte b from URL segment s */
    	 switch (ch = s.charAt(i)) {
    	 case '%':
    	 ch = s.charAt(++i);
    	 int hb = (Character.isDigit((char) ch) ? ch - '0'
    	 : 10 + Character.toLowerCase((char) ch) - 'a') & 0xF;
    	 ch = s.charAt(++i);
    	 int lb = (Character.isDigit((char) ch) ? ch - '0'
    	 : 10 + Character.toLowerCase((char) ch) - 'a') & 0xF;
    	 b = (hb << 4) | lb;
    	 break;
    	 case '+':
    	 b = ' ';
    	 break;
    	 default:
    	 b = ch;
    	 }
    	 /* Decode byte b as UTF-8, sumb collects incomplete chars */
    	 if ((b & 0xc0) == 0x80) { // 10xxxxxx (continuation byte)
    	 sumb = (sumb << 6) | (b & 0x3f); // Add 6 bits to sumb
    	 if (--more == 0)
    	 sbuf.append((char) sumb); // Add char to sbuf
    	 } else if ((b & 0x80) == 0x00) { // 0xxxxxxx (yields 7 bits)
    	 sbuf.append((char) b); // Store in sbuf
    	 } else if ((b & 0xe0) == 0xc0) { // 110xxxxx (yields 5 bits)
    	 sumb = b & 0x1f;
    	 more = 1; // Expect 1 more byte
    	 } else if ((b & 0xf0) == 0xe0) { // 1110xxxx (yields 4 bits)
    	 sumb = b & 0x0f;
    	 more = 2; // Expect 2 more bytes
    	 } else if ((b & 0xf8) == 0xf0) { // 11110xxx (yields 3 bits)
    	 sumb = b & 0x07;
    	 more = 3; // Expect 3 more bytes
    	 } else if ((b & 0xfc) == 0xf8) { // 111110xx (yields 2 bits)
    	 sumb = b & 0x03;
    	 more = 4; // Expect 4 more bytes
    	 } else /*if ((b & 0xfe) == 0xfc)*/{ // 1111110x (yields 1 bit)
    	 sumb = b & 0x01;
    	 more = 5; // Expect 5 more bytes
    	 }
    	 /* We don't test if the UTF-8 encoding is well-formed */
    	 }
    	 return sbuf.toString();
    	 }

    private static String date2String(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("YYYY年MM月dd日");
        return sdf.format(date);
    }

    private static void createCell(String text, int colspan, PdfPTable pdfPTable, Font font) {
        PdfPCell cell = new PdfPCell(new Paragraph(text, font));
        cell.setColspan(colspan);
        pdfPTable.addCell(cell);

    }
}
