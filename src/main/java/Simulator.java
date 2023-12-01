

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.*;
import java.security.NoSuchAlgorithmException;

import java.util.*;
import java.util.stream.Collectors;

public class Simulator {

    private LinkedHashMap<String, List<CandleDTO>> candleMap = new LinkedHashMap<>();

    // 현재 시점, 즉 몇번째 row인지
    private int currentRow = 0;
    private double buyingPrice;
    private String buyingCoin = "";
    private double MONEY = 10_000;
    int rowSize = 0;
    static String filePath = "C:\\Users\\dev\\Desktop\\simulation\\";

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        Coin coin = new Coin();
        Exel exel = new Exel();
        Simulator simulator = new Simulator();
        exel.makeExelFile("test.xlsx");
        simulator.makeAllCoin24hourExelData();
//        FileInputStream fis = new FileInputStream(filePath + "test.xlsx");
//        simulator.setExelDataToCandleMap(fis);
//        while (true) {
//            while (!simulator.buySomething()) {
//                simulator.findTarget();
//                System.out.println("target: " + simulator.buyingCoin);
//                if(simulator.currentRow> simulator.rowSize){
//                    break;
//                }
////                System.out.println("currentRow: " + simulator.currentRow);
//            }
//            if (simulator.currentRow >= simulator.rowSize) {
//                break;
//            }
//            simulator.sellCoin();
//        }
//        System.out.println(simulator.MONEY);
//        fis.close();
    }

    public boolean buySomething() {
        return !buyingCoin.equals("");
    }

    // 구매 코인을 판매함
    public void sellCoin() {
        if(buyingCoin.equals("")){
            return;
        }
        List<CandleDTO> candleList = candleMap.get(buyingCoin);
        List<Double> priceList = candleList.stream()
                .map(CandleDTO::getTradePrice)
                .toList();
        for (int j = currentRow; j < priceList.size(); j++) {
            double nowPrice = priceList.get(j);
            if (nowPrice > buyingPrice * 1.015) {
                MONEY = MONEY * 1.015;
                System.out.println("++++++");
                buyingPrice = 0;
                buyingCoin = "";
                currentRow = j;
                break;
            }
            if (nowPrice > buyingPrice * 0.98) {
                MONEY = MONEY * 0.98;
                System.out.println("-----");
                buyingPrice = 0;
                buyingCoin = "";
                currentRow = j;
                break;
            }
        }
    }

    // 모든 코인을 구매 타겟인지 확인
    public String findTarget() {
        Iterator<String> coinNames = candleMap.keySet().iterator();
        while (coinNames.hasNext()) {
            String coinName = coinNames.next();
            if (isThisTarget(coinName)) {
                buyingCoin = coinName;
                return coinName;
            }
        }
        return "";
    }

    // 단일 코인이 구매 타겟인지 확인
    public boolean isThisTarget(String coinName) {
        List<CandleDTO> candleList = candleMap.get(coinName);
        List<Double> percentList = makeRescent10PercentList(candleList);
        for (int i = 0; i < percentList.size() - 1; i++) {
            if (candleList.get(0).getTradePrice() > 150 && percentList.get(1) > 0.5 && percentList.get(2) > 0.5) {
                return true;
            }
        }
        return false;
    }

    // 종가를 이용해서 최근 10분간의 상승률 퍼센트를 구한다.
    public List<Double> makeRescent10PercentList(List<CandleDTO> candleList) {
        List<Double> percentList = new ArrayList<>();
        for (int j = currentRow; j < currentRow + 10; j++) {
            if (j >= candleList.size() - 1) {
                break;
            }
            double diff = candleList.get(j).getTradePrice() - candleList.get(j + 1).getTradePrice();
            double percent = Math.round(diff / candleList.get(j + 1).getTradePrice() * 10000) / 100.0;
            percentList.add(percent);
        }
        currentRow += 10;
        return percentList;
    }

    // Exel 데이터를 필드 candelMap 으로 변환하는 함수.
    public void setExelDataToCandleMap(FileInputStream fis) throws IOException {
        HSSFWorkbook workbook = new HSSFWorkbook(fis);
        int sheetNum = workbook.getNumberOfSheets();
        for (int i = 0; i < sheetNum; i++) {
            HSSFSheet sheet = workbook.getSheetAt(i);
            List<CandleDTO> candleList = new ArrayList<>();
            int rowNum = sheet.getPhysicalNumberOfRows(); // 해당 시트의 행의 개수
            rowSize = rowNum;
            for (int j = 1; j < rowNum; j++) {
                HSSFRow row = sheet.getRow(j); // 각 행을 읽어온다
                if (row == null) { // 현재 행에 아무 데이터도 없으면 빠져나옴.
                    break;
                }
                CandleDTO dto = makeCandleDtoWithExelRow(row);
                candleList.add(dto);
            }
            candleMap.put(sheet.getSheetName(), candleList);
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

    public void makeAllCoin24hourExelData() throws IOException, NoSuchAlgorithmException {
        Coin coin = new Coin();
        Exel exel = new Exel();
        List<String> coinNames = coin.getNames();
        Map<String, List<CandleDTO>> map = new HashMap<>();
        for (String name : coinNames) {
            System.out.println("------------" + name + "-------------");
            List<CandleDTO> list = coin.make24hoursDtos(name, 7);
            map.put(name, list);
        }
        Iterator<String> iterator = map.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            exel.writeExcelFile("test.xlsx", key, map.get(key));
        }
    }
}