package artavd.devices.utils;

import rx.Observable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Predicate;
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

    public static <T> T waitFor(Observable<T> observable, Predicate<T> predicate,
                                   long timeout, TimeUnit timeUnit) throws TimeoutException {
        try {
            return observable
                    .filter(predicate::test)
                    .timeout(timeout, timeUnit)
                    .toBlocking()
                    .first();
        } catch (Exception ex) {
            Throwable cause = ex.getCause();
            if (cause instanceof TimeoutException) {
                throw new TimeoutException(String.format("Timeout expired: %s %s", timeout, timeUnit));
            }

            throw ex;
        }
    }
}
