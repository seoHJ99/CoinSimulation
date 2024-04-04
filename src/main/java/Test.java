import java.time.LocalDateTime;
import java.util.*;

/**
 * 조건
 * 1. 확장 가능성
 * 2. 조건 여러개 추가 가능하게끔 설계
 * 3. 시간의 흐름
 * 4. 모든 코인을 순차적으로 계속 돌기
 * <p>
 * 흐름:
 * 1. 먼저 시간이 흐름
 * 2. 그 이후에 모든 코인을 순회함
 * 3. 해당 코인이 10 프로 이상 올랐는지 확인. 조건1
 * 4. 조건1에 만족하는 코인이 조건 2에 만족하는지 확인
 * 5. 다시 순회
 * <p>
 * 문제:
 * 1. 시간의 흐름.
 * 1. 조건1에 만족하는 코인을 찾았을때, 만족하는 시간별로 순차적으로 코인을 순회해야함.
 * 2. 조건이 하나만 존재할때, 해당 코인을 순회해야 함. 따라서 조건이 더 존재하는지 확인해야 함?
 * <p>
 * 구조 :
 * 1. 프로그램 실행 클래스
 * 2. 조건1에 해당하는 모든 코인(날짜 포함)의 데이터를 가져옴
 * 3. 조건2에 해당하는 코인을 다시 거름
 * 4. 조건들에 해당하는 코인을 시작 시간부터 끝 시간까지 시뮬 돌림
 * <p>
 * 메서드
 * 1. 조건 찾기 메서드
 * 매개변수 : 조건 타입(일, 분 등), 상승률, 하락율, 날짜
 * 리턴값 : 코인 이름, 시간. 끝 시간
 * 2. 상승룰 퍼센트 구하기 메서드
 * 매개변수 : candle 리스트
 * 리턴값 : 퍼센트 리스트
 */


public abstract class Test {


    public Map<String, List<CandleDTO>> addDayCondition(Double raisePercentage, Double losePercentage) {
        Map<String, List<CandleDTO>> result = new HashMap<>();
        Map<String, List<CandleDTO>> allData = getAllDayData();
        for (String key : allData.keySet()) {
            for (CandleDTO data : allData.get(key)) {

                checkPercentage(raisePercentage, losePercentage, data, result);
            }
        }
        return result;
    }

    public Map<String, List<CandleDTO>> addMinuteCondition(Double raisePercentage, Double losePercentage,
                                                           LocalDateTime startTime, Integer duration) {
        Map<String, List<CandleDTO>> result = new HashMap<>();
        Map<String, List<CandleDTO>> allData = getAllMinuteData();

        for (String key : allData.keySet()) {
            for (CandleDTO data : allData.get(key)) {
                boolean timeCheck = checkTime(result, data, startTime, duration);
                if (timeCheck == false) {
                    break;
                }
            }
        }

        allData = result;
        for (String key : allData.keySet()) {
            for (CandleDTO data : allData.get(key)) {
                result = checkPercentage(raisePercentage, losePercentage, data);
            }
        }
        return result;
    }


    abstract Map<String, List<CandleDTO>> getAllMinuteData();

    private boolean checkTime(Map<String, List<CandleDTO>> result, CandleDTO data, LocalDateTime startTime, Integer duration) {
        if (startTime != null) {

            LocalDateTime dataTime = LocalDateTime.parse(data.getTimeKST());
            if (dataTime.isAfter(startTime)) {
                addData(result, data);
            }

            LocalDateTime endTime = startTime.plusMinutes(duration);
            if (endTime.isAfter(LocalDateTime.parse(data.getTimeKST()))) {
                return false;
            }
        } else {
            addData(result, data);
        }
        return true;
    }

    private void checkPercentage(Double raisePercentage, Double losePercentage, CandleDTO data, Map<String, List<CandleDTO>> result) {
        if (raisePercentage != null && data.getHighRaisePercentage() >= raisePercentage) {
            addData(result, data);
        }
        if (losePercentage != null && data.getLowRaisePercentage() < raisePercentage) {
            deleteData(result, data);
        }
    }

    private Map<String, List<CandleDTO>> checkPercentage(Double raisePercentage, Double losePercentage, CandleDTO data) {
        Map<String, List<CandleDTO>> result = new HashMap<>();
        if (raisePercentage != null && data.getHighRaisePercentage() >= raisePercentage) {
            addData(result, data);
        }
        if (losePercentage != null && data.getLowRaisePercentage() < raisePercentage) {
            deleteData(result, data);
        }
        return result;
    }

    abstract Map<String, List<CandleDTO>> getAllDayData();


    private void addData(Map<String, List<CandleDTO>> result, CandleDTO data) {
        List<CandleDTO> resultValue = result.get(data.getMarket());
        if (resultValue == null) {
            resultValue = new ArrayList<>();
        }
        resultValue.add(data);
    }

    private void deleteData(Map<String, List<CandleDTO>> result, CandleDTO data) {
        if (result.containsKey(data.getMarket())) {
            result.get(data.getMarket()).remove(data);
        }
    }
}
