import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class Simulator {

    private LinkedHashMap<String, List<CandleDTO>> candleMap = new LinkedHashMap<>();

    // 현재 시점, 즉 몇번째 row인지
    public int currentRow = 0;
    private double buyingPrice;
    public String buyingCoin = "";
    private LocalDateTime buyingDate;
    public double MONEY = 10_000;
    private double minusLine = 0.96;
    private double plusLine = 1.01;
    int rowSize = 0;
    static String filePath = "C:\\Users\\Hojun\\Desktop\\git\\CoinSimulation\\";

    public final int DATA_DAYS = 8;

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        Coin coin = new Coin();
        Exel exel = new Exel();
        Simulator simulator = new Simulator();
        exel.makeExelFile("data.xlsx");
        exel.makeAllCoin24hourExelData(simulator.DATA_DAYS);
//        exel.makeExelFile("data(day).xlsx");
//        simulator.saveDaysCandleExel(simulator.DATA_DAYS);
//        FileInputStream fis = new FileInputStream(filePath + "data.xlsx");
//        simulator.setExelDataToCandleMap(fis);
//        while (simulator.currentRow <= simulator.rowSize) { // 현재 행(시간) 이 전체 행보다 작을때 반복.
//            if (!simulator.buySomething()) { // 구매한 코인이 없으면 구매 코인 찾기
//                simulator.findTarget();
//            } else {
//                simulator.sellCoin();
////                simulator.sellCoinByNext();
//            }
//            simulator.currentRow += 1;
//        }
//        System.out.println(new BigDecimal(simulator.MONEY));
//        System.out.println("산 코인:" + simulator.buyingCoin);
//        fis.close();
    }

    public boolean buySomething() {
        return !buyingCoin.equals("");
    }

    public void sellCoinByNext() {
        List<CandleDTO> candleList = candleMap.get(buyingCoin);
        List<Double> priceList = candleList.stream()
                .map(CandleDTO::getTradePrice)
                .toList();
        List<Double> percentList = makeRescent10PercentList(candleList, "high");

        if (currentRow >= priceList.size()) {
            return;
        }
        double afterLowPrice = 0;
        double afterHighPrice = 0;
        if (currentRow + 1 < candleList.size()) {
            afterLowPrice = candleList.get(currentRow + 1).getLowPrice();
            afterHighPrice = candleList.get(currentRow + 1).getHighPrice();
        }

        if ((percentList.get(0) + percentList.get(1) + percentList.get(2) < -2) || percentList.get(1) < -4
                || afterLowPrice < buyingPrice * 0.92) {
            double percentage = ((afterLowPrice - buyingPrice) / buyingPrice) * 100;
            System.out.println();
            System.out.println("-------- 하락세 전환 --------");
            if (afterLowPrice < buyingPrice) {
                System.out.println("------손해-------");
            } else {
                System.out.println("------이익-------");
            }
            System.out.println("이전 수익 : " + MONEY);
            MONEY = MONEY * ((100D + percentage) / 100D);
            System.out.println("현재 수익 : " + MONEY);
            System.out.println("상승치 : " + percentage);
            System.out.println("구매가격 : " + buyingPrice);
            buyingPrice = 0;
            buyingCoin = "";
            System.out.println("판매가격 : " + afterLowPrice);
            System.out.println("판매 행 : " + (rowSize - currentRow));
            System.out.println("판매 시간 : " + candleList.get(currentRow).getTimeKST());
            System.out.println("다음 최저가 : " + afterLowPrice);
            System.out.println("다음 최고가 : " + afterHighPrice);
            System.out.println("==========================================================");
        }
    }

    // 구매 코인을 판매함
    public void sellCoin() {
        List<CandleDTO> candleList = candleMap.get(buyingCoin);
        List<Double> priceList = candleList.stream()
                .map(CandleDTO::getTradePrice)
                .toList();
        List<Double> lowPriceList = candleList.stream()
                .map(CandleDTO::getLowPrice)
                .collect(Collectors.toList());
        List<Double> highPriceList = candleList.stream()
                .map(CandleDTO::getHighPrice)
                .collect(Collectors.toList());
        if (currentRow >= priceList.size()) {
            return;
        }

        LocalDateTime nowTime = LocalDateTime.parse(candleList.get(currentRow).getTimeKST());
        double nowPrice = priceList.get(currentRow);
        double lowestPrice = lowPriceList.get(currentRow);
        double highestPrice = highPriceList.get(currentRow);
        double afterLowPrice = 0;
        double afterHighPrice = 0;
        if (currentRow + 1 < candleList.size()) {
            afterLowPrice = candleList.get(currentRow + 1).getLowPrice();
            afterHighPrice = candleList.get(currentRow + 1).getHighPrice();
        }
        if (highestPrice > buyingPrice * plusLine) {
            System.out.println();
            System.out.println("-------- 이익 --------");
            MONEY = MONEY * plusLine;
            buyingPrice = 0;
            buyingCoin = "";
            System.out.println("현재 수익금: " + MONEY);
            System.out.println("판매가격 : " + nowPrice);
            System.out.println("판매 행 : " + (rowSize - currentRow));
            System.out.println("판매 시간 : " + candleList.get(currentRow).getTimeKST());
            System.out.println("다음 최저가 : " + afterLowPrice);
            System.out.println("다음 최고가 : " + afterHighPrice);
            System.out.println("==========================================================");
//        } else if (lowestPrice < afterLowPrice * minusLine) {
//            System.out.println();
//            System.out.println("-------- 일단 판매 --------");
//            MONEY = MONEY * minusLine;
//            buyingPrice = 0;
//            buyingCoin = "";
//            System.out.println("판매가격 : " + nowPrice);
//            System.out.println("판매 행 : " + (rowSize - currentRow));
//            System.out.println("판매 시간 : " + candleList.get(currentRow).getTimeKST());
//            System.out.println("다음 최저가 : " +afterLowPrice);
//            System.out.println("다음 최고가 : " +afterHighPrice);
//            System.out.println("==========================================================");
        }
//        else if (lowestPrice < buyingPrice * minusLine) {
//            MONEY = MONEY * minusLine;
//            buyingPrice = 0;
//            buyingCoin = "";
//            System.out.println("************************************************************");
//            System.out.println("-------- 손해 --------");
//            System.out.println("판매가격 : " + nowPrice);
//            System.out.println("판매 행 : " + (rowSize - currentRow));
//            System.out.println("판매 시간 : " + candleList.get(currentRow).getTimeKST());
//            System.out.println("다음 최저가 : " +afterLowPrice);
//            System.out.println("다음 최고가 : " +afterHighPrice);
//            System.out.println("==========================================================");
//        }
        else if (nowTime.isAfter(buyingDate.plusDays(1))) {
            System.out.println("---------구매한 지 하루 지남----------");
            System.out.println("구매가: " + buyingPrice);
            System.out.println("구매 시간: " + buyingDate);
            System.out.println("현재 시간: " + nowTime);
            System.out.println("판매가 :" + nowPrice);
            double percentage = ((nowPrice - buyingPrice) / buyingPrice) * 100;
            System.out.println("수익률: " + percentage);
            percentage = 100 + percentage;
            System.out.println(percentage);
            MONEY = MONEY*percentage/100;
            System.out.println("현재 금액 : " + MONEY);
            buyingCoin = "";
            buyingPrice = 0;
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



    public String findTargetByNext() {
        Iterator<String> coinNames = candleMap.keySet().iterator();
        while (coinNames.hasNext()) {
            String coinName = coinNames.next();
            if (isThisTarget2(coinName)) {
                buyingCoin = coinName;
                return coinName;
            }
        }
        return "";
    }

    // 단일 코인이 구매 타겟인지 확인
    public boolean isThisTarget(String coinName) {
        List<CandleDTO> candleList = candleMap.get(coinName);

        List<Double> percentList = makeRescent10PercentList(candleList, "high");
//        if(coinName.equals("KRW-IOTA")&& candleList.get(currentRow).getTimeKST().contains("2023-11-30T09:30:00")){
//            System.out.println(percentList);
//        }
        if (
                percentList.size() > 3

//                && percentList.get(0) + percentList.get(1) > 7
//                && percentList.get(1) <3
//                        && percentList.get(0) > 0
//                        && percentList.get(1) > 0 && percentList.get(2) > 0 && percentList.get(3) + percentList.get(4) > 0
//                        && percentList.get(4) > 0
                        && percentList.get(1) + percentList.get(2) + percentList.get(3)
                        + percentList.get(4)+ percentList.get(5) > 5
                        && percentList.get(1) > 1
//                && percentList.get(0) > 2
//                && percentList.get(1) + percentList.get(2) > 3
//                && percentList.get(1) > 0
//                && percentList.get(2) > 0
//                && percentList.get(3) > 0
//                && percentList.get(0) > 2
//                && percentList.get(1) + percentList.get(2) > 4
//                && percentList.get(1) > 0
//                && percentList.get(2) > 1.5
//                && percentList.get(3) > 1.5
//                ||
//                percentList.size() > 3 && percentList.get(1)>2 && percentList.get(2)>2 && percentList.get(0)>1
//                ||
//                percentList.size() > 3 && percentList.get(0)>3 && percentList.get(1)>1.5 && percentList.get(2)>1.5

//                && percentList.get(3) > 1.5
//                && percentList.get(4) > 1
//                && percentList.get(1) + percentList.get(2) > 5
//                && percentList.get(0) + percentList.get(1) > 6
        ) {// 구매 조건
            System.out.println();
            System.out.println("구매");
            System.out.println(percentList);
            System.out.println("코인 이름 : " + coinName);
            System.out.println("구매가격 : " + candleList.get(currentRow).getTradePrice());
            System.out.println("구매 행 : " + (rowSize - currentRow));
            System.out.println("구매 시간 : " + candleList.get(currentRow).getTimeKST());
            buyingDate = LocalDateTime.parse(candleList.get(currentRow).getTimeKST());
            buyingPrice = candleList.get(currentRow).getTradePrice();
            return true;
        }
        return false;
    }

    public boolean isThisTarget2(String coinName) {
        List<CandleDTO> candleList = candleMap.get(coinName);

        List<Double> percentList = makeRescent10PercentList(candleList, "");
//        if(coinName.equals("KRW-IOTA")&& candleList.get(currentRow).getTimeKST().contains("2023-11-30T09:30:00")){
//            System.out.println(percentList);
//        }
        if (percentList.size() > 3
                && percentList.get(0) + percentList.get(1) > 7
                && percentList.get(1) < 3
        ) {// 구매 조건
            System.out.println();
            System.out.println("구매");
            System.out.println(percentList);
            System.out.println("코인 이름 : " + coinName);
            System.out.println("구매가격 : " + candleList.get(currentRow).getTradePrice());
            System.out.println("구매 행 : " + (rowSize - currentRow));
            System.out.println("구매 시간 : " + candleList.get(currentRow).getTimeKST());
            buyingPrice = candleList.get(currentRow).getTradePrice();
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





}