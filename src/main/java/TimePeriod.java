import java.util.function.Function;

public enum TimePeriod {
    MINUTE("minute"), HOUR("hour"), DAY("day");

    private final String valueName;

//    private Function<Integer, Integer> expression;

    TimePeriod(String valueName){
        this. valueName = valueName;
//        this.expression =expression;
    }

//    public int calculate(int value){return expression.apply(value);}
}
