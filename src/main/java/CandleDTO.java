import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CandleDTO {
    private String market;
    private String timeUTC;
    private String timeKST;
    private double openingPrince;
    private double highPrice;
    private double lowPrice;
    private double tradePrice;
    private String timestamp;
    private String accumulateTradePrice;
    private String accumulateTradeVolume;
    private String unit;

    @Override
    public String toString() {
        return "CandleDTO{" +
                "market='" + market + '\'' +
                ", timeUTC='" + timeUTC + '\'' +
                ", timeKST='" + timeKST + '\'' +
                ", openingPrince=" + openingPrince +
                ", highPrice=" + highPrice +
                ", lowPrice=" + lowPrice +
                ", tradePrice=" + tradePrice +
                ", timestamp='" + timestamp + '\'' +
                ", accumulateTradePrice='" + accumulateTradePrice + '\'' +
                ", accumulateTradeVolume='" + accumulateTradeVolume + '\'' +
                ", unit='" + unit + '\'' +
                '}';
    }
}
