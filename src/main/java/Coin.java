import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Coin {

    public static List<String> names = new ArrayList<>();

    public List<String> getNames()  {

        JSONParser jsonParser = new JSONParser();
        String serverUrl = "https://api.upbit.com";
        List<String> result = new ArrayList<>();

        try {
            CloseableHttpClient client = HttpClients.createDefault();
            HttpGet request = new HttpGet(serverUrl + "/v1/market/all?isDetails=false");

            CloseableHttpResponse response = client.execute(request);
            HttpEntity entity = response.getEntity();

            String entityString = (EntityUtils.toString(entity, "UTF-8"));
            JSONArray jsonObject = (JSONArray) jsonParser.parse(entityString);

            for (Object one : jsonObject) {
                JSONObject jsonOne = (JSONObject) one;
                String name = jsonOne.get("market").toString();
                if (name.contains("KRW")) {
                    result.add(name);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException | org.apache.hc.core5.http.ParseException e) {
            throw new RuntimeException(e);
        }
        names = result;
        return result;
    }

    public List<CandleDTO> getCandleDTOs(String coinName, int count, String beforeTime) {
        JSONParser jsonParser = new JSONParser();
        String serverUrl = "https://api.upbit.com";
        NumberFormat f = NumberFormat.getInstance();
        f.setGroupingUsed(false);
        List<CandleDTO> dtos = new ArrayList<>();

        try {
            CloseableHttpClient client = HttpClients.createDefault();
            String url = serverUrl + "/v1/candles/minutes/1?market=" + coinName + "&count=" + count;
            if (!"".equals(beforeTime) && beforeTime != null) {
                url += "&to=" + beforeTime;
            }
            HttpGet request = new HttpGet(url);
            CloseableHttpResponse response = client.execute(request);
            HttpEntity entity = response.getEntity();
            String entityString = (EntityUtils.toString(entity, "UTF-8"));
            JSONArray jsonObject = (JSONArray) jsonParser.parse(entityString);;
            for (int i = 0; i < jsonObject.size(); i++) {
                JSONObject json = (JSONObject) jsonObject.get(i);
                CandleDTO dto = new CandleDTO();
                dto.setMarket((String) json.get("market"));
                dto.setTimeUTC((String) json.get("candle_date_time_utc"));
                dto.setTimeKST((String) json.get("candle_date_time_kst"));
                dto.setOpeningPrince(Double.parseDouble(json.get("opening_price").toString()));
                dto.setHighPrice(Double.parseDouble(json.get("high_price").toString()));
                dto.setLowPrice(Double.parseDouble(json.get("low_price").toString()));
                dto.setTradePrice(Double.parseDouble(json.get("trade_price").toString()));
                dto.setAccumulateTradePrice( json.get("candle_acc_trade_price").toString());
                dto.setAccumulateTradeVolume(json.get("candle_acc_trade_volume").toString());
                dto.setUnit(json.get("unit").toString());
                dtos.add(dto);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (org.apache.hc.core5.http.ParseException | ParseException e) {
            throw new RuntimeException(e);
        }
        return dtos;
    }

    public List<CandleDTO> dayCandleDtos(String coinName, int days) {
        JSONParser jsonParser = new JSONParser();
        String serverUrl = "https://api.upbit.com";
        NumberFormat f = NumberFormat.getInstance();
        f.setGroupingUsed(false);
        List<CandleDTO> dtos = new ArrayList<>();

        try {
            CloseableHttpClient client = HttpClients.createDefault();
            String url = serverUrl + "/v1/candles/days?market=" + coinName + "&count=" + days;
            HttpGet request = new HttpGet(url);
            CloseableHttpResponse response = client.execute(request);
            HttpEntity entity = response.getEntity();
            String entityString = (EntityUtils.toString(entity, "UTF-8"));
            JSONArray jsonObject = (JSONArray) jsonParser.parse(entityString);;
            for (int i = 0; i < jsonObject.size(); i++) {
                JSONObject json = (JSONObject) jsonObject.get(i);
                CandleDTO dto = new CandleDTO();
                dto.setMarket((String) json.get("market"));
                dto.setTimeUTC((String) json.get("candle_date_time_utc"));
                dto.setTimeKST((String) json.get("candle_date_time_kst"));
                dto.setOpeningPrince(Double.parseDouble(json.get("opening_price").toString()));
                dto.setHighPrice(Double.parseDouble(json.get("high_price").toString()));
                dto.setLowPrice(Double.parseDouble(json.get("low_price").toString()));
                dto.setTradePrice(Double.parseDouble(json.get("trade_price").toString()));
                dto.setAccumulateTradePrice( json.get("candle_acc_trade_price").toString());
                dto.setAccumulateTradeVolume(json.get("candle_acc_trade_volume").toString());

                dtos.add(dto);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (org.apache.hc.core5.http.ParseException | ParseException e) {
            throw new RuntimeException(e);
        }
        return dtos;
    }

    public List<CandleDTO> make24hoursDtos(String name, int days) {
        LocalDateTime yesterDay = LocalDateTime.now();
//                .minusDays(1);
        String stringDate
                = yesterDay.format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
        );

        List<CandleDTO> list = getCandleDTOs(name, 200, stringDate);
        for(int i =0; i<days;){
            CandleDTO dto = list.get(list.size() - 1);
            String time = dto.getTimeUTC();
            if(list.size() % 1440 != 0){
                list.addAll(getCandleDTOs(name,200, time));
            }else {
                list.addAll(getCandleDTOs(name,40,time));
                i++;
            }
        }
        return list;
    }
}
