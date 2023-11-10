package hr.fer.oprpp1.custom.scripting.exec;
/*null, Integer, Double, String
    *1)if null -> Integer(0)
    *2)if String is decimal -> Double
    * else -> Integer , dont care if it throws NumberFormatException
    *3) if Double -> Double , else -> Integer
 */


public class ValueWrapper {
    private Object value;

    public ValueWrapper(Object value){
        this.value = value;
    }

    public Object getValue(){
        return value;
    }

    public void setValue(Object value){
        this.value = value;
    }


    public void add(Object incValue){
        IOperator operator = (first, second) -> (first + second);
        value = calculate(stringToNumber(value), stringToNumber(incValue), operator);
    }

    public void subtract(Object decValue){
        IOperator operator = (first, second) -> (first - second);
        value = calculate(stringToNumber(value), stringToNumber(decValue), operator);
    }
    public void multiply(Object mulValue){
        IOperator operator = (first, second) -> (first * second);
        value = calculate(stringToNumber(value), stringToNumber(mulValue), operator);
    }
    public void divide(Object divValue){
        IOperator operator = (first, second) -> (first / second);
        value = calculate(stringToNumber(value), stringToNumber(divValue), operator);
    }

    private Number stringToNumber(Object object){
        if(object == null)
            return 0;
        String string = object.toString();
        try {
            if (string.contains(".") || string.contains("e") || string.contains("E"))
                return Double.parseDouble(string);

            return Integer.parseInt(string);
        }catch (NumberFormatException e){
            throw new RuntimeException("Given string is not a number");
        }
    }
    private Number calculate(Number first, Number second, IOperator operator){

        if(first instanceof Double || second instanceof Double)
            return (Double) operator.apply(first.doubleValue(), second.doubleValue());

        Number result =  operator.apply(first.doubleValue(), second.doubleValue());
        return (Integer) result.intValue();
    }


    public int numCompare(Object withValue) {
        IOperator operator = (first, second) -> (double) first.compareTo(second);
        return (Integer) calculate(stringToNumber(value.toString()), stringToNumber(withValue.toString()), operator);
    }

    private interface IOperator {
        public Double apply(Double first, Double second);
    }

    public static void main(String[] args) {
        ValueWrapper v1 = new ValueWrapper("1.2");
        ValueWrapper v2 = new ValueWrapper(Integer.valueOf(1));
        v1.add(v2.getValue());
        System.out.println(v1.getValue());

        v1 = new ValueWrapper(null);
        v2 = new ValueWrapper(null);
        v1.add(v2.getValue()); // v1 now stores Integer(0); v2 still stores null.
        System.out.println(v1.getValue());
        ValueWrapper v3 = new ValueWrapper("1.2E1");
        ValueWrapper v4 = new ValueWrapper(Integer.valueOf(1));
        v3.add(v4.getValue()); // v3 now stores Double(13); v4 still stores Integer(1)
        System.out.println(v3.getValue());
        ValueWrapper v5 = new ValueWrapper("12");
        ValueWrapper v6 = new ValueWrapper(Integer.valueOf(1));
        v5.add(v6.getValue()); // v5 now stores Integer(13); v6 still stores Integer(1).
        System.out.println(v5.getValue());
        ValueWrapper v7 = new ValueWrapper("Ankica");
        ValueWrapper v8 = new ValueWrapper(Integer.valueOf(1));
        v7.add(v8.getValue()); // throws RuntimeException
    }
}
