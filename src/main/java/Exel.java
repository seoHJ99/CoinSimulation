import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import javax.management.RuntimeErrorException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Exel {

    static String filePath = "C:\\Users\\hojun\\Desktop\\Git\\CoinSimulation\\";

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

//    public double readExelFile(FileInputStream fis) {
//        try {
//            HSSFWorkbook workbook = new HSSFWorkbook(fis);
//            int sheetNum = workbook.getNumberOfSheets();
//            String buyingCoin = "";
//            double buyingPrice = 0;
//            for (int i = 0; i < sheetNum; i++) {
//                HSSFSheet sheet;
//                if (buyingCoin.equals("")) { // 만약 산 코인이 없다면 sheet를 계속 돔
//                    sheet = workbook.getSheetAt(i);
//                } else { // 산 코인이 존재하면 해당 코인만 감시
//                    i--; // 코인을 팔때까지 계속 반복문 돌아야함.
//                    sheet = workbook.getSheet(buyingCoin);
//                }
//
//                int currentRow = 1;
//                int rows = sheet.getPhysicalNumberOfRows(); // 해당 시트의 행의 개수
//                List<Double> priceList = new ArrayList<>();
//                for (int rowIndex = currentRow; rowIndex < currentRow + 10; rowIndex++) {
//                    if (currentRow > rows) { // 현재 행이 실제 행보다 크면 빠져나옴
//                        break;
//                    }
//
//                    HSSFRow row = sheet.getRow(rowIndex); // 각 행을 읽어온다
//                    if (row == null) { // 현재 행에 아무 데이터도 없으면 빠져나옴.
//                        break;
//                    }
//
//                    HSSFCell cell = row.getCell(6); // 종가만 읽어온다.
//                    if (cell == null) {
//                        continue;
//                    }
//                    // 읽어온 종가를 priceList 에 넣음
//                    priceList.add(cell.getNumericCellValue());
//                    currentRow++;
//                }
//
//                List<Double> percentList = new ArrayList<>();
//                // 종가 리스트를 이용해서 각 분마다 상승률 퍼센트를 구한다.
//                for (int j = 0; j < priceList.size() - 1; j++) {
//
//                    double diff = priceList.get(j) - priceList.get(j + 1);
//                    double percent = Math.round(diff / priceList.get(j + 1) * 10000) / 100.0;
//                    percentList.add(percent);
//                }
//
//                // 구매조건
//                for (int j = 0; j < percentList.size() - 1; j++) {
//                    if (buyingCoin.equals("") && priceList.get(0) > 1.50 && percentList.get(j) > 1.5 && percentList.get(j + 1) > 1.5) {
//                        System.out.println(sheet.getSheetName());
//                        buyingCoin = sheet.getSheetName();
//                        buyingPrice = priceList.get(0);
//                    }
//                }
//
//
//                // 판매조건
//                if (!buyingCoin.equals("")) {
//                    for (int j = 0; j < priceList.size(); j++) {
//                        double nowPrice = priceList.get(j);
//                        if (nowPrice > buyingPrice * 1.015) {
//                            MONEY = MONEY * 1.015;
//                            buyingPrice = 0;
//                            System.out.println(buyingCoin);
//                            buyingCoin = "";
//                        }
//                        if (nowPrice > buyingPrice * 0.98) {
//                            MONEY = MONEY * 0.98;
//                            buyingPrice = 0;
//                            buyingCoin = "";
//                        }
//                    }
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return MONEY;
//    }

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
        curRow.createCell(9).setCellValue("전날 종가 대비 상승률");
        curRow.createCell(10).setCellValue("전날 종가 대비 최대 상승률");
        curRow.createCell(11).setCellValue("전날 종가 대비 최소 상승률");
    }

    public void makeSheetRows(HSSFSheet sheet, List<CandleDTO> candleDTOS) throws EncryptedDocumentException {
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
            curRow.createCell(9).setCellValue(dto.getTradeRaisePercentage());
            curRow.createCell(10).setCellValue(dto.getHighRaisePercentage());
            curRow.createCell(11).setCellValue(dto.getLowRaisePercentage());
        }
    }

    public void writeExcelFile( String sheetName, List<CandleDTO> candleDTOS, String name) throws EncryptedDocumentException, IOException {
        FileInputStream fis = new FileInputStream(filePath + name);

        HSSFWorkbook workbook = new HSSFWorkbook(fis);
        HSSFSheet sheet = null;
        try {
            sheet = workbook.createSheet(sheetName);    // sheet 생성
            writeColumn(sheet);
        } catch (IllegalArgumentException e) {
            sheet = workbook.getSheet(sheetName);
        }
        makeSheetRows(sheet, candleDTOS);
        FileOutputStream fos = new FileOutputStream(filePath + name);
        workbook.write(fos);
        fis.close();
        fos.close();
    }

    public void makeAllCoin24hourExelData(int days) throws IOException {
        Coin coin = new Coin();
        List<String> coinNames = coin.getNames();
        for (String coinName : coinNames) {
            System.out.println(coinName);
        }
        Map<String, List<CandleDTO>> map = new HashMap<>();
        for (String name : coinNames) {
            if (!name.equals("KRW-MNT")) {
                System.out.println("------------" + name + "-------------");
                List<CandleDTO> list = coin.make24hoursDtos(name, days);
                map.put(name, list);
                System.out.println(list.size());
            }
        }
        Iterator<String> iterator = map.keySet().iterator();

        while (iterator.hasNext()) {
            String key = iterator.next();
            writeExcelFile(key, map.get(key), "data.xlsx");
        }
    }

    public void saveDaysCandleExel(int days, String fileName) throws IOException {
        Coin coin = new Coin();
        Exel exel = new Exel();
        List<String> coinNames = coin.getNames();
        for (String coinName : coinNames) {
            System.out.println(coinName);
        }
        Map<String, List<CandleDTO>> map = new HashMap<>();
        for (String name : coinNames) {
            if (!name.equals("KRW-MNT")) {
                System.out.println("------------" + name + "-------------");
                List<CandleDTO> list = coin.dayCandleDtos(name,days);
                map.put(name, list);
                System.out.println(list.size());
            }
        }
        Iterator<String> iterator = map.keySet().iterator();

        while (iterator.hasNext()) {
            String key = iterator.next();
            exel.writeExcelFile(key, map.get(key), fileName);
        }
    }

}
