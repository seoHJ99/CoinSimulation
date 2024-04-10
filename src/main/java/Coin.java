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

    private final int MINUTES_OF_DAY = 1440;

    public List<String> getNames() {

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

    public List<CandleDTO> getMinuteCandleDTOs(String coinName, int count, String beforeTime) {
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
            JSONArray jsonObject = (JSONArray) jsonParser.parse(entityString);
            ;
            CandleDTO afterDto = null;

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
                dto.setAccumulateTradePrice(json.get("candle_acc_trade_price").toString());
                dto.setAccumulateTradeVolume(json.get("candle_acc_trade_volume").toString());

                if (dtos.size() > 1) {
                    afterDto = dtos.get(dtos.size() - 1);
                    CandleDTO lastDto = dtos.get(dtos.size() - 1);
                    lastDto.setTradeRaisePercentage(getRaisePercentage(dto.getTradePrice(), afterDto.getTradePrice()));
                    lastDto.setHighRaisePercentage(getRaisePercentage(dto.getTradePrice(), afterDto.getHighPrice()));
                    lastDto.setLowRaisePercentage(getRaisePercentage(dto.getTradePrice(), afterDto.getLowPrice()));
                }
                dtos.add(dto);
            }

            CandleDTO firstDto = dtos.get(0);
            CandleDTO secondDto = dtos.get(1);
            firstDto.setTradeRaisePercentage(getRaisePercentage(secondDto.getTradePrice(), firstDto.getTradePrice()));
            firstDto.setHighRaisePercentage(getRaisePercentage(secondDto.getTradePrice(), firstDto.getHighPrice()));
            firstDto.setLowRaisePercentage(getRaisePercentage(secondDto.getTradePrice(), firstDto.getLowPrice()));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (org.apache.hc.core5.http.ParseException | ParseException e) {
            throw new RuntimeException(e);
        }
        return dtos;
    }

    public Double getRaisePercentage(Double past, Double now) {
        double diff = past - now;
        double percentage = (double) ((-1) * Math.round(diff / past * 10000)) / 100;
        return percentage;
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
            JSONArray jsonObject = (JSONArray) jsonParser.parse(entityString);
            ;
            CandleDTO afterDto = null;
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
                dto.setAccumulateTradePrice(json.get("candle_acc_trade_price").toString());
                dto.setAccumulateTradeVolume(json.get("candle_acc_trade_volume").toString());

                if (dtos.size() > 1) {
                    afterDto = dtos.get(dtos.size() - 1);
                    CandleDTO lastDto = dtos.get(dtos.size() - 1);
                    lastDto.setTradeRaisePercentage(getRaisePercentage(dto.getTradePrice(), afterDto.getTradePrice()));
                    lastDto.setHighRaisePercentage(getRaisePercentage(dto.getTradePrice(), afterDto.getHighPrice()));
                    lastDto.setLowRaisePercentage(getRaisePercentage(dto.getTradePrice(), afterDto.getLowPrice()));
                }
                dtos.add(dto);
            }

            CandleDTO firstDto = dtos.get(0);
            CandleDTO secondDto = dtos.get(1);
            firstDto.setTradeRaisePercentage(getRaisePercentage(secondDto.getTradePrice(), firstDto.getTradePrice()));
            firstDto.setHighRaisePercentage(getRaisePercentage(secondDto.getTradePrice(), firstDto.getHighPrice()));
            firstDto.setLowRaisePercentage(getRaisePercentage(secondDto.getTradePrice(), firstDto.getLowPrice()));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (org.apache.hc.core5.http.ParseException | ParseException e) {
            throw new RuntimeException(e);
        }
        return dtos;
    }

    public List<CandleDTO> make24hoursDtos(String name, int days) {
        LocalDateTime yesterDay = LocalDateTime.now();
        String stringDate
                = yesterDay.format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
        );

        List<CandleDTO> list = getMinuteCandleDTOs(name, 200, stringDate);
        int allCount = MINUTES_OF_DAY * days;
        int count = (allCount - 200) / 200;
        int restCount = (allCount - 200) % 200;
        for (int i = 0; i < count+1; i++ ) {
            CandleDTO dto = list.get(list.size() - 1);
            String time = dto.getTimeUTC();

            if(i == count){
                list.addAll(getMinuteCandleDTOs(name, restCount, time));
            }

            if (list.size() % MINUTES_OF_DAY != 0) {
                list.addAll(getMinuteCandleDTOs(name, 200, time));
            }
        }
        return list;
    }

}
