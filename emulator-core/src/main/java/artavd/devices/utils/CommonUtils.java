package artavd.devices.utils;

import java.util.Arrays;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

// TODO: AA: Move to common library
public class CommonUtils {

    @SafeVarargs
    public static <T> Set<T> asSet(T... items) {
        return Arrays.stream(items).collect(toSet());
    }

    @SafeVarargs
    public static <T> T[] asArray(T... items) {
        return items;
    }
}
