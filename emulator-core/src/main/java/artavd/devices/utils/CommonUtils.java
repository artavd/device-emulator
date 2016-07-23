package artavd.devices.utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Stream;

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

    public static <T> Stream<T> asStream(T[] array) {
        return array == null
                ? Stream.empty()
                : Arrays.stream(array);
    }

    public static <T> Stream<T> asStream(Collection<T> collection) {
        return collection == null
                ? Stream.empty()
                : collection.stream();
    }
}
