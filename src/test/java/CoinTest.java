import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

public class CoinTest {

    private final int MINUTES_OF_DAY = 1440;


    @Test
    void make24dtoTest(){
        Coin coin = new Coin();
        int days = 3;
        List<CandleDTO> candleDTOS = coin.make24hoursDtos("KRW-BTC", days);
        assertThat(candleDTOS.size()).isEqualTo(MINUTES_OF_DAY * days);
    }
}
