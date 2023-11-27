

import java.io.*;
import java.security.NoSuchAlgorithmException;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Simulator {
    static String filePath = "C:\\Users\\Hojun\\Desktop\\simulation\\";

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        Coin coin = new Coin();
        Exel exel = new Exel();
//        exel.makeExelFile("test.xlsx");
//        Simulator simulator = new Simulator();
//        simulator.makeAllCoin24hourExelData();
        FileInputStream fis = new FileInputStream(filePath + "test.xlsx");
        double money = exel.readExelFile(fis);
        System.out.println("money = " + money);
        fis.close();
    }

    public void makeAllCoin24hourExelData() throws IOException, NoSuchAlgorithmException {
        Coin coin = new Coin();
        Exel exel = new Exel();
        List<String> coinNames = coin.getNames();
        Map<String, List<CandleDTO>> map = new HashMap<>();
        for (String name : coinNames) {
            System.out.println("------------" + name + "-------------");
            List<CandleDTO> list = coin.make24hoursDtos(name, 4);
            map.put(name, list);
        }
        Iterator<String> iterator = map.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            exel.writeExcelFile("test.xlsx", key, map.get(key));
        }
    }
}