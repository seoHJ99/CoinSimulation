import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;

public class NewPlayer {
    private LinkedHashMap<String, List<CandleDTO>> candleMap = new LinkedHashMap<>();

    // 현재 시점, 즉 몇번째 row인지
    private int currentRow = 0;
    private double buyingPrice;
    private String buyingCoin = "";
    private double MONEY = 10_000;

    private double minusLine = 0.97;
    private double plusLine = 1.01;
    int rowSize = 0;
    static String filePath = "C:\\Users\\Hojun\\Desktop\\CoinSimulation\\";

    public static void main(String[] args) throws IOException {
        Simulator simulator = new Simulator();

        FileInputStream fis = new FileInputStream(filePath + "test.xlsx");
        simulator.setExelDataToCandleMap(fis);
        while (simulator.currentRow <= simulator.rowSize) { // 현재 행(시간) 이 전체 행보다 작을때 반복.
            if (!simulator.buySomething()) { // 구매한 코인이 없으면 구매 코인 찾기
                simulator.findTargetByNext();
            } else {
                simulator.sellCoinByNext();
            }
            simulator.currentRow += 1;
        }
        System.out.println(new BigDecimal(simulator.MONEY));
        fis.close();
    }
}
