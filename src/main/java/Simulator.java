

import java.io.*;
import java.security.NoSuchAlgorithmException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Simulator {

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        Coin coin = new Coin();
        Exel exel = new Exel();
        List<String> coinNames= coin.getNames();
        Map<String, List<CandleDTO>> map = new HashMap<>();
        for(String name : coinNames){
            System.out.println("------------"+name+"-------------");
            List<CandleDTO> list =coin.make24hoursDtos(name);
            map.put(name,list);
        }
        Iterator<String> iterator = map.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            exel.writeExcelFile("test.xlsx", key, map.get(key));
        }
    }
}