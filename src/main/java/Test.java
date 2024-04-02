import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class Test {

    Coin coin = new Coin();

    private LinkedHashMap<String, List<CandleDTO>> candleMap = new LinkedHashMap<>();
    private LinkedHashMap<String, List<CandleDTO>> dayCandleMap = new LinkedHashMap<>();
    public int currentRow = 0;
    int rowSize = 0;
    static String filePath = "C:\\Users\\Hojun\\Desktop\\git\\CoinSimulation\\";

    public static void main(String[] args) throws IOException {
        Test simulator = new Test();
        FileInputStream fis = new FileInputStream(filePath + "data.xlsx");
        simulator.setExelDataToCandleMap(fis);
        fis.close();
        FileInputStream fis2 = new FileInputStream(filePath + "data(day).xlsx");
        simulator.dayMapSetter(fis2);
        fis2.close();
        Iterator<String> coinNames = simulator.candleMap.keySet().iterator();
        Map<String, List<CandleDTO>> risingCoinMap = new HashMap<>();
        while (coinNames.hasNext()) {
            String coinName = coinNames.next();
            risingCoinMap.put(coinName, simulator.dayPercentRaiseCoin(coinName));
        }

        coinNames = simulator.candleMap.keySet().iterator();
        while (coinNames.hasNext()){
            String coinName = coinNames.next();
            if(risingCoinMap.keySet().contains(coinName)){
                List<CandleDTO> candleDTOS = risingCoinMap.get(coinName);
                for (CandleDTO candleDTO : candleDTOS) {
                    candleDTO.getTimeKST();
                }

            }
        }


        while (simulator.currentRow <= simulator.rowSize) { // 현재 행(시간) 이 전체 행보다 작을때 반복.


            simulator.currentRow += 1;
        }
    }

    public List<CandleDTO> dayPercentRaiseCoin(String coinName) {
        List<CandleDTO> candleDTOS = dayCandleMap.get(coinName);
        List<CandleDTO> risingCandle = getDayPercentage(candleDTOS);
        return risingCandle;
    }

    public List<CandleDTO> getDayPercentage(List<CandleDTO> candleList) {
        List<CandleDTO> result = new ArrayList<>();
        for (int i = candleList.size()-2; i>=0; i--) {
            double raisePercentage = getRaisePercentage(candleList.get(i + 1), candleList.get(i));
            if(raisePercentage>10){
                result.add(candleList.get(i));
            }
        }
        return result;
    }

    private double getRaisePercentage(CandleDTO past, CandleDTO today) {
        double pastPrice = past.getTradePrice();
        double todayPrice = today.getTradePrice();
        double diff = pastPrice - todayPrice;
        double dayPercent = (-1) * Math.round(diff / pastPrice * 10000) / 100.0;
        return dayPercent;
    }

//
//
//

    // 단일 코인이 구매 타겟인지 확인
    public boolean isThisTarget(String coinName) {
        List<CandleDTO> candleList = candleMap.get(coinName);
        List<Double> percentList = makeRescent10PercentList(candleList, "high");
        CandleDTO nowCandle = candleList.get(currentRow);
        List<CandleDTO> twoDayList = coin.dayCandleDtos(coinName, 10);
        double first = twoDayList.get(1).getTradePrice();
        double second = candleList.get(0).getTradePrice();
        double diff = first - second;
        double dayPercent = (-1) * Math.round(diff / first * 10000) / 100.0;

        if (dayPercent > 10) {
            System.out.println();
            System.out.println("---------------------------------------");
            System.out.println("코인 이름 : " + coinName);
            System.out.println(percentList);
            System.out.println("측정날짜 : " + twoDayList.get(0).getTimeKST());
            System.out.println("하루 상승치: " + dayPercent);
            System.out.println("구매가격 : " + nowCandle.getTradePrice());
            System.out.println("구매 행 : " + (rowSize - currentRow));
            System.out.println("구매 시간 : " + nowCandle.getTimeKST());
            return true;
        }
        return false;
    }


    // 종가를 이용해서 단일 코인의 최근 10분간의 상승률 퍼센트를 구한다.
    public List<Double> makeRescent10PercentList(List<CandleDTO> candleList, String type) {

        List<Double> percentList = new ArrayList<>();
        if (currentRow < 10) {
            currentRow = 10;
        }
        for (int j = currentRow - 1; j > currentRow - 10; j--) {
            if (j >= candleList.size() - 1) {
                break;
            }
            double first = candleList.get(j).getTradePrice();
            double second = candleList.get(j + 1).getTradePrice();
            if (j == currentRow - 1) {
                if (type.equals("high")) {
                    second = candleList.get(j + 1).getHighPrice();
                }
                if (type.equals("low")) {
                    second = candleList.get(j + 1).getLowPrice();
                }
            }
            double diff = first - second;
            double percent = (-1) * Math.round(diff / candleList.get(j + 1).getTradePrice() * 10000) / 100.0;
            percentList.add(percent);
        }
        return percentList;
    }

    // Exel 데이터를 필드 candelMap 으로 변환하는 함수.
    public void setExelDataToCandleMap(FileInputStream fis) throws IOException {
        HSSFWorkbook workbook = new HSSFWorkbook(fis);
        int sheetNum = workbook.getNumberOfSheets();
        for (int i = 0; i < sheetNum; i++) {
            HSSFSheet sheet = workbook.getSheetAt(i);
            int rowNum = sheet.getPhysicalNumberOfRows(); // 해당 시트의 행의 개수
            rowSize = rowNum;
            List<CandleDTO> candleList = new ArrayList<>(Arrays.asList(new CandleDTO[rowSize - 1]));
            for (int j = 1; j < rowNum; j++) {
                HSSFRow row = sheet.getRow(j); // 각 행을 읽어온다
                if (row == null) { // 현재 행에 아무 데이터도 없으면 빠져나옴.
                    break;
                }
                CandleDTO dto = makeCandleDtoWithExelRow(row);
                candleList.set(rowNum - j - 1, dto);
            }
            candleMap.put(sheet.getSheetName(), candleList);
        }
    }

    public void dayMapSetter(FileInputStream fis) throws IOException {
        HSSFWorkbook workbook = new HSSFWorkbook(fis);
        int sheetNum = workbook.getNumberOfSheets();
        for (int i = 0; i < sheetNum; i++) {
            HSSFSheet sheet = workbook.getSheetAt(i);
            int rowNum = sheet.getPhysicalNumberOfRows(); // 해당 시트의 행의 개수
            rowSize = rowNum;
            List<CandleDTO> candleList = new ArrayList<>(Arrays.asList(new CandleDTO[rowSize - 1]));
            for (int j = 1; j < rowNum; j++) {
                HSSFRow row = sheet.getRow(j); // 각 행을 읽어온다
                if (row == null) { // 현재 행에 아무 데이터도 없으면 빠져나옴.
                    break;
                }
                CandleDTO dto = makeCandleDtoWithExelRow(row);
                candleList.set(rowNum - j - 1, dto);
            }
            dayCandleMap.put(sheet.getSheetName(), candleList);
        }
    }

    // 엑셀 row를 이용해서 CandleDTO 만드는 함수
    public CandleDTO makeCandleDtoWithExelRow(HSSFRow row) {
        CandleDTO candleDTO = new CandleDTO();
        candleDTO.setMarket(row.getCell(0).getStringCellValue());
        candleDTO.setTimeUTC(row.getCell(1).getStringCellValue());
        candleDTO.setTimeKST(row.getCell(2).getStringCellValue());
        candleDTO.setOpeningPrince(row.getCell(3).getNumericCellValue());
        candleDTO.setHighPrice(row.getCell(4).getNumericCellValue());
        candleDTO.setLowPrice(row.getCell(5).getNumericCellValue());
        candleDTO.setTradePrice(row.getCell(6).getNumericCellValue());
        candleDTO.setAccumulateTradePrice(row.getCell(7).getStringCellValue());
        candleDTO.setAccumulateTradeVolume(row.getCell(8).getStringCellValue());
        return candleDTO;
    }

}