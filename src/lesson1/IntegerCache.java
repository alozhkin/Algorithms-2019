package lesson1;

import java.util.HashMap;
import java.util.Map;

class IntegerCache {
    private static Map<Integer, Integer> integerCache = new HashMap<>();

    private IntegerCache() {}

    static Integer getFromCache(Integer integer) {
        integerCache.putIfAbsent(integer, integer);
        return integerCache.get(integer);
    }
}
