package lesson1;

import java.util.HashMap;
import java.util.Map;

public class IntegerCache {
    private static Map<Integer, Integer> integerCache = new HashMap<>();

    private IntegerCache() {}

    public static Integer getFromCache(Integer integer) {
        integerCache.putIfAbsent(integer, integer);
        return integerCache.get(integer);
    }
}
