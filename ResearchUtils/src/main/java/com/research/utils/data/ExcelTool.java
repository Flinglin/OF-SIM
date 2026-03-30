package com.research.utils.data;


import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class ExcelTool {
    public static Object[][] readXlsxExcel(String path,String sheetName) throws IOException {
        InputStream stream= Files.newInputStream(Path.of(path));
        Workbook workbook=new XSSFWorkbook(stream);
        Sheet sheet=workbook.getSheet(sheetName);
        Object[][] result=readExcel(sheet);
        stream.close();
        workbook.close();
        return result;
    }
    public static void writeXlsxExcel(String path,String sheetName,Object[][] data) throws IOException {

    }

    protected static Object[][] readExcel(Sheet sheet){
        Object[][] output=null;
        int rowStart=sheet.getFirstRowNum()+1;
        int rowEnd=sheet.getLastRowNum();
        if(rowStart>rowEnd){
            return null;
        }
        output=new Object[rowEnd][];
        int cellNUm=sheet.getRow(0).getLastCellNum();
        for(int i=0;i<rowEnd;i++){
            Row row=sheet.getRow(i+1);
            if(row==null){
                continue;
            }
            output[i]=new Object[cellNUm];
            for(int j=0;j<cellNUm;j++){
                Cell cell=row.getCell(j);
                if(cell==null){
                    continue;
                }
                CellType type=cell.getCellType();
                switch(type){
                    case STRING->{
                        output[i][j]=cell.getStringCellValue();
                    }
                    case NUMERIC->{
                        if(DateUtil.isCellDateFormatted(cell)){
                            output[i][j]=cell.getLocalDateTimeCellValue();
                        }else {
                            output[i][j]=cell.getNumericCellValue();
                        }

                    }
                    case BOOLEAN->{
                        output[i][j]=cell.getBooleanCellValue();
                    }
                    case FORMULA -> {
                        output[i][j]=cell.getNumericCellValue();
                    }
                    case BLANK, ERROR ->{
                        output[i][j] = null;
                    }
                }
            }
        }
        return output;
    }


}
