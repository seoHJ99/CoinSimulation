import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import javax.management.RuntimeErrorException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Exel {

    private double MONEY = 10_000;
    static String filePath = "C:\\Users\\Hojun\\Desktop\\simulation\\";

    public void makeExelFile(String fileName) throws IOException, RuntimeErrorException {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(filePath + fileName);
            fis.close();
            System.out.println("기존에 해당 이름의 파일이 존재합니다.");
            throw new IllegalArgumentException("기존에 해당 이름의 파일이 존재합니다.");
        } catch (FileNotFoundException e) {
            HSSFWorkbook workbook = new HSSFWorkbook(); // 새 엑셀 생성
            try {
                FileOutputStream fileoutputstream = new FileOutputStream(filePath + fileName);
                workbook.write(fileoutputstream);
                fileoutputstream.close();
                System.out.println("엑셀파일 생성 성공");
                fileoutputstream.close();
            } catch (IOException d) {
                throw new RuntimeException(d);
            }
        }
    }

    public double readExelFile(FileInputStream fis) {
        try {
            HSSFWorkbook workbook = new HSSFWorkbook(fis);
            int sheetNum = workbook.getNumberOfSheets();
            String buyingCoin = "";
            double buyingPrice = 0;
            for (int i = 0; i < sheetNum; i++) {
                HSSFSheet sheet;
                if (buyingCoin.equals("")) { // 만약 산 코인이 없다면 sheet를 계속 돔
                    sheet = workbook.getSheetAt(i);
                } else { // 산 코인이 존재하면 해당 코인만 감시
                    i--; // 코인을 팔때까지 계속 반복문 돌아야함.
                    sheet = workbook.getSheet(buyingCoin);
                }

                int currentRow = 1;
                int rows = sheet.getPhysicalNumberOfRows(); // 해당 시트의 행의 개수
                List<Double> priceList = new ArrayList<>();
                for (int rowIndex = currentRow; rowIndex < currentRow + 10; rowIndex++) {
                    if (currentRow > rows) { // 현재 행이 실제 행보다 크면 빠져나옴
                        break;
                    }

                    HSSFRow row = sheet.getRow(rowIndex); // 각 행을 읽어온다
                    if (row == null) { // 현재 행에 아무 데이터도 없으면 빠져나옴.
                        break;
                    }

                    HSSFCell cell = row.getCell(6); // 종가만 읽어온다.
                    if (cell == null) {
                        continue;
                    }
                    // 읽어온 종가를 priceList 에 넣음
                    priceList.add(cell.getNumericCellValue());
                    currentRow++;
                }

                List<Double> percentList = new ArrayList<>();
                // 종가 리스트를 이용해서 각 분마다 상승률 퍼센트를 구한다.
                for (int j = 0; j < priceList.size() - 1; j++) {


                    double diff = priceList.get(j) - priceList.get(j + 1);
                    double percent = Math.round(diff / priceList.get(j + 1) * 10000) / 100.0;


                    percentList.add(percent);
                }

                // 구매조건
                for(int j=0; j<percentList.size()-1; j++){
                    if (buyingCoin.equals("") && priceList.get(0) > 150 && percentList.get(j) >1.5 && percentList.get(j+1) > 1.5) {
                        System.out.println(sheet.getSheetName());
                        buyingCoin = sheet.getSheetName();
                        buyingPrice = priceList.get(0);
                    }
                }


                // 판매조건
                if (!buyingCoin.equals("")) {
                    for(int j =0; j<priceList.size(); j++){
                        double nowPrice = priceList.get(j);
                        if (nowPrice > buyingPrice * 1.015) {
                            MONEY = MONEY * 1.015;
                            buyingPrice = 0;
                            System.out.println(buyingCoin);
                            buyingCoin = "";
                        }
                        if (nowPrice > buyingPrice * 0.98) {
                            MONEY = MONEY * 0.98;
                            buyingPrice = 0;
                            buyingCoin = "";
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return MONEY;
    }

    public void writeColumn(HSSFSheet sheet) {
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
        FileInputStream fis = new FileInputStream(filePath + fileName);
        HSSFWorkbook workbook = new HSSFWorkbook(fis);
        HSSFSheet sheet = null;
        try {
            sheet = workbook.createSheet(sheetName);    // sheet 생성
            writeColumn(sheet);
        } catch (IllegalArgumentException e) {
            sheet = workbook.getSheet(sheetName);
        }
        HSSFRow curRow;
        int lastRowNumber = sheet.getLastRowNum();
        int row = candleDTOS.size();    // list 크기
        for (int i = lastRowNumber + 1; i < row + lastRowNumber; i++) {
            CandleDTO dto = candleDTOS.get(i - lastRowNumber - 1);
            curRow = sheet.createRow(i);    // row 생성
            curRow.createCell(0).setCellValue(dto.getMarket());    // row에 각 cell 저장
            curRow.createCell(1).setCellValue(dto.getTimeUTC());
            curRow.createCell(2).setCellValue(dto.getTimeKST());
            curRow.createCell(3).setCellValue(dto.getOpeningPrince());
            curRow.createCell(4).setCellValue(dto.getHighPrice());
            curRow.createCell(5).setCellValue(dto.getLowPrice());
            curRow.createCell(6).setCellValue(dto.getTradePrice());
            curRow.createCell(7).setCellValue(dto.getAccumulateTradePrice());
            curRow.createCell(8).setCellValue(dto.getAccumulateTradeVolume());
        }
        fis.close();
        FileOutputStream fos = new FileOutputStream(filePath + fileName);
        workbook.write(fos);
        fos.close();
    }
}
