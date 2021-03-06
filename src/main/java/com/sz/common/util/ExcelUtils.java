package com.sz.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.NumberFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelUtils {

	public static String[][] readExcel(File file, int sheetNum, int startRowNum, int startColNum) throws IOException {
        FileInputStream is = new FileInputStream(file);
        String[][] string = readExcel(is, sheetNum, startRowNum, startColNum);
        return string;
    }

    public static String[][] readExcel(InputStream is, int sheetNum, int startRowNum, int startColNum) throws IOException {
        Workbook wb = new HSSFWorkbook(is);
        Sheet childSheet = wb.getSheetAt(sheetNum);
        int rows = childSheet.getLastRowNum();
        if(childSheet.getRow(startRowNum)==null){
        	return new String[0][0];
        }
        int cols = childSheet.getRow(startRowNum).getLastCellNum();
        int tempRows = rows + 1 - startRowNum;
        int tempCols = cols - startColNum;

        String[][] string = new String[tempRows][tempCols];
        int tempRowNum = startRowNum;
        for (int i = 0; i < tempRows; i++) {
            int tempColNum = startColNum;
            for (int j = 0; j < tempCols; j++) {
                String str = "";
                Cell cell=null;
                Row row=childSheet.getRow(tempRowNum);
                if(row!=null) {
                	cell = row.getCell(tempColNum);
                }
                if (cell != null) {
                    int cellType = cell.getCellType();
                    if (cellType == Cell.CELL_TYPE_STRING) {
                        str = childSheet.getRow(tempRowNum).getCell(tempColNum).getStringCellValue();
                    } else if (cellType == Cell.CELL_TYPE_NUMERIC) {
                        if (HSSFDateUtil.isCellDateFormatted(cell)) {
                            Date date = cell.getDateCellValue();
                            str = DateFormatUtils.format(date, "yyyy-MM-dd HH:mm:ss");
                        } else {
                            double v = (double) childSheet.getRow(tempRowNum).getCell(tempColNum).getNumericCellValue();
                            NumberFormat formater = NumberFormat.getInstance();
                            formater.setGroupingUsed(false);
                            formater.setMaximumFractionDigits(5);
                            str = formater.format(v);
                        }
                    }
                }
                string[i][j] = str;
                tempColNum++;
            }
            tempRowNum++;
        }
        return string;
    }
    
    /**
     * 写入excel
     * @param out
     * @param sheetTitle
     * @param headers 列标题
     * @param rowList 外层是行List，内层是列List
     * @throws IOException
     */
    public static void writeExcel(OutputStream out,String sheetTitle,String[] headers,List<List<String>> rowList,String suffix) throws IOException {
    	Workbook workbook=null;
    	if("xlsx".equals(suffix)) {
    		workbook=new XSSFWorkbook();
    	}else {
    		workbook=new HSSFWorkbook();
    	}
    	
        Sheet sheet=workbook.createSheet(sheetTitle);
        sheet.setDefaultColumnWidth(20);
        
        //样式
        CellStyle style=workbook.createCellStyle();
        style.setFillForegroundColor(HSSFColor.WHITE.index);
        style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        style.setBorderRight(HSSFCellStyle.BORDER_THIN);
        style.setBorderTop(HSSFCellStyle.BORDER_THIN);
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        //字体
        Font font=workbook.createFont();
        font.setColor(HSSFColor.BLACK.index);
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        style.setFont(font);
        
        //列头
        Row row=sheet.createRow(0);
        for (int i=0;i<headers.length;i++) {
            Cell cell=row.createCell(i);
            cell.setCellStyle(style);
            RichTextString text=null;
            if("xlsx".equals(suffix)) {
            	text=new XSSFRichTextString(headers[i]);
            }else {
            	text=new HSSFRichTextString(headers[i]);
            }
            cell.setCellValue(text);
        }
        
        //内容
        for(int i=0;i<rowList.size();i++) {
        	List<String> columnList=rowList.get(i);
        	row = sheet.createRow(i+1);
        	for(int j=0;j<headers.length;j++) {
        		String content=columnList.get(j);
        		Cell cell = row.createCell(j);
	            RichTextString text = new HSSFRichTextString(content);
	            if("xlsx".equals(suffix)) {
	            	text = new XSSFRichTextString(content);
	            }else {
	            	text = new HSSFRichTextString(content);
	            }
	            cell.setCellValue(text);
        	}
        }
        workbook.write(out);
        out.close();
    }
    
}
