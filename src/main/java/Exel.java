import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class Exel {

    static String filePath = "C:\\Users\\Hojun\\Desktop\\simulation\\";

    public void makeExelFile(String fileName){
        HSSFWorkbook workbook = new HSSFWorkbook(); // 새 엑셀 생성
        HSSFSheet sheet = workbook.createSheet("2023_10_31"); // 새 시트(Sheet) 생성
        try{
            FileOutputStream fileoutputstream = new FileOutputStream(filePath + fileName);
            workbook.write(fileoutputstream);
            fileoutputstream.close();
            System.out.println("엑셀파일 생성 성공");
            fileoutputstream.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public  void readExelFile(String name){
        try {
            FileInputStream fis = new FileInputStream(filePath + name);
            HSSFWorkbook workbook = new HSSFWorkbook(fis);
            HSSFSheet sheet = workbook.getSheetAt(0); // 해당 엑셀파일의 시트(Sheet) 수
            int rows = sheet.getPhysicalNumberOfRows(); // 해당 시트의 행의 개수
            for (int rowIndex = 0; rowIndex < rows; rowIndex++) {
                HSSFRow row = sheet.getRow(rowIndex); // 각 행을 읽어온다
                if (row != null) {
                    int cells = row.getPhysicalNumberOfCells();
                    System.out.println(cells);
                    for (int columnIndex = 0; columnIndex <= cells; columnIndex++) {
                        HSSFCell cell = row.getCell(columnIndex); // 셀에 담겨있는 값을 읽는다.
                        if(cell == null){
                            continue;
                        }
                        String value = "";
                        switch (cell.getCellType()) { // 각 셀에 담겨있는 데이터의 타입을 체크하고 해당 타입에 맞게 가져온다.
                            case HSSFCell.CELL_TYPE_NUMERIC:
                                value = cell.getNumericCellValue() + "";
                                break;
                            case HSSFCell.CELL_TYPE_STRING:
                                value = cell.getStringCellValue() + "";
                                break;
                            case HSSFCell.CELL_TYPE_BLANK:
                                value = cell.getBooleanCellValue() + "";
                                break;
                            case HSSFCell.CELL_TYPE_ERROR:
                                value = cell.getErrorCellValue() + "";
                                break;
                        }
                        System.out.println(value);
                    }
                }
            }
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void writeColumn(HSSFSheet sheet){
        HSSFRow curRow;
        curRow = sheet.createRow(0);    // row 생성
        curRow.createCell(0).setCellValue("이름");
        curRow.createCell(1).setCellValue("미국 시간");
        curRow.createCell(2).setCellValue("한국 시간");
        curRow.createCell(3).setCellValue("시가");
        curRow.createCell(4).setCellValue("고가");
        curRow.createCell(5).setCellValue("저가");
        curRow.createCell(6).setCellValue("종가");
        curRow.createCell(7).setCellValue("누적 거래 금액");
        curRow.createCell(8).setCellValue("누적 거래량");
    }

    public void writeExcelFile(String fileName, String sheetName, List<CandleDTO> candleDTOS) throws EncryptedDocumentException, IOException {
        FileOutputStream fos = new FileOutputStream(filePath+fileName);
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet(sheetName);    // sheet 생성
        writeColumn(sheet);
        HSSFRow curRow;
        int row = candleDTOS.size();    // list 크기
        for (int i = 1; i < row; i++) {
            CandleDTO dto = candleDTOS.get(i);
            curRow = sheet.createRow(i);    // row 생성
            curRow.createCell(0).setCellValue(candleDTOS.get(i).getMarket());    // row에 각 cell 저장
            curRow.createCell(1).setCellValue(candleDTOS.get(i).getTimeUTC());
            curRow.createCell(2).setCellValue(candleDTOS.get(i).getTimeKST());
            curRow.createCell(3).setCellValue(candleDTOS.get(i).getOpeningPrince());
            curRow.createCell(4).setCellValue(candleDTOS.get(i).getHighPrice());
            curRow.createCell(5).setCellValue(candleDTOS.get(i).getLowPrice());
            curRow.createCell(6).setCellValue(candleDTOS.get(i).getTradePrice());
            curRow.createCell(7).setCellValue(candleDTOS.get(i).getAccumulateTradePrice());
            curRow.createCell(8).setCellValue(candleDTOS.get(i).getAccumulateTradeVolume());
        }
        workbook.write(fos);
        fos.close();
    }
}
