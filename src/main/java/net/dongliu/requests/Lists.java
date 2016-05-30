package net.dongliu.requests;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

/**
 * For internal use
 *
 * @author Liu Dong
 */
class Lists {
    static <T, R> List<R> convert(Collection<T> list, Function<T, R> function) {
        List<R> newList = new ArrayList<>(list.size());
        for (T t : list) {
            newList.add(function.apply(t));
        }
        return newList;
    }
}
