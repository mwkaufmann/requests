package net.dongliu.requests.utils;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * List utils functions
 *
 * @author Liu Dong
 */
public class Lists {

    /**
     * If list is null, return immutable empty list; else return self
     */
    public static <T> List<T> nullToEmpty(@Nullable List<T> list) {
        return list == null ? Lists.of() : list;
    }

    /**
     * Transform list
     */
    public static <T, R> List<R> map(Collection<T> list, Function<? super T, ? extends R> function) {
        List<R> newList = new ArrayList<>(list.size());
        for (T value : list) {
            newList.add(function.apply(value));
        }
        return newList;
    }

    /**
     * Get new list with items accepted by predicate
     */
    public static <T> List<T> filter(List<T> list, Predicate<? super T> predicate) {
        List<T> newList = new ArrayList<>(Math.min(16, list.size()));
        for (T value : list) {
            if (predicate.test(value)) {
                newList.add(value);
            }
        }
        return newList;
    }


    /**
     * Fold left
     */
    public static <T, R> R fold(List<T> list, R initialValue, BiFunction<R, ? super T, R> function) {
        R value = initialValue;
        for (T item : list) {
            value = function.apply(value, item);
        }
        return value;
    }

    /**
     * Fold right
     */
    public static <T, R> R foldRight(List<T> list, R initialValue, BiFunction<R, ? super T, R> function) {
        ListIterator<T> iterator = list.listIterator(list.size());
        R value = initialValue;
        while (iterator.hasPrevious()) {
            T item = iterator.previous();
            value = function.apply(value, item);
        }
        return value;
    }

    /**
     * Reduce left
     *
     * @return empty optional if list is empty
     */
    public static <T> Optional<T> reduce(List<T> list, BinaryOperator<T> function) {
        if (list.isEmpty()) {
            return Optional.empty();
        }
        T value = list.get(0);
        int idx = 0;
        for (T item : list) {
            if (idx++ > 0) {
                value = function.apply(value, item);
            }
        }
        return Optional.of(value);
    }

    /**
     * Reduce right
     *
     * @return empty optional if list is empty
     */
    public static <T> Optional<T> reduceRight(List<T> list, BinaryOperator<T> function) {
        if (list.isEmpty()) {
            return Optional.empty();
        }
        ListIterator<T> iterator = list.listIterator(list.size());
        T value = iterator.previous();
        while (iterator.hasPrevious()) {
            T item = iterator.previous();
            value = function.apply(value, item);
        }
        return Optional.of(value);
    }

    /**
     * Create new, reversed list
     */
    public static <T> List<T> reverse(List<T> list) {
        List<T> newList = new ArrayList<>(list.size());
        int idx = newList.size();
        for (T value : list) {
            newList.set(--idx, value);
        }
        return newList;
    }

    /**
     * Create immutable view of this List.
     */
    public static <T> List<T> wrapImmutable(List<T> list) {
        return Collections.unmodifiableList(list);
    }

    /**
     * Create a immutable list with content
     */
    public static <T> List<T> copyImmutable(Collection<T> list) {
        return Collections.unmodifiableList(new ArrayList<>(list));
    }

    /**
     * Create mutable list
     */
    public static <T> List<T> create() {
        return new ArrayList<>();
    }

    /**
     * Create mutable list
     */
    public static <T> List<T> create(T value) {
        List<T> list = new ArrayList<>(1);
        list.add(value);
        return list;
    }

    /**
     * Create mutable list
     */
    public static <T> List<T> create(T value1, T value2) {
        List<T> list = new ArrayList<>(2);
        list.add(value1);
        list.add(value2);
        return list;
    }

    /**
     * Create mutable list
     */
    public static <T> List<T> create(T value1, T value2, T value3) {
        List<T> list = new ArrayList<>(3);
        list.add(value1);
        list.add(value2);
        list.add(value3);
        return list;
    }

    /**
     * Create mutable list
     */
    public static <T> List<T> create(T value1, T value2, T value3, T value4) {
        List<T> list = new ArrayList<>(4);
        list.add(value1);
        list.add(value2);
        list.add(value3);
        list.add(value4);
        return list;
    }

    /**
     * Create mutable list
     */
    public static <T> List<T> create(T value1, T value2, T value3, T value4, T value5) {
        List<T> list = new ArrayList<>(5);
        list.add(value1);
        list.add(value2);
        list.add(value3);
        list.add(value4);
        list.add(value5);
        return list;
    }

    /**
     * Create mutable list
     */
    public static <T> List<T> create(T value1, T value2, T value3, T value4, T value5, T value6) {
        List<T> list = new ArrayList<>(6);
        list.add(value1);
        list.add(value2);
        list.add(value3);
        list.add(value4);
        list.add(value5);
        list.add(value6);
        return list;
    }

    /**
     * Create mutable list
     */
    public static <T> List<T> create(T value1, T value2, T value3, T value4, T value5, T value6, T value7) {
        List<T> list = new ArrayList<>(7);
        list.add(value1);
        list.add(value2);
        list.add(value3);
        list.add(value4);
        list.add(value5);
        list.add(value6);
        list.add(value7);
        return list;
    }

    /**
     * create mutable list
     */
    @SafeVarargs
    public static <T> List<T> create(T... values) {
        List<T> list = new ArrayList<>(values.length);
        Collections.addAll(list, values);
        return list;
    }


    /**
     * Create immutable list
     */
    public static <T> List<T> of() {
        return Collections.emptyList();
    }

    /**
     * Create immutable list
     */
    public static <T> List<T> of(T value) {
        return Collections.singletonList(value);
    }

    /**
     * create immutable list
     */
    @SafeVarargs
    public static <T> List<T> of(T... values) {
        return Collections.unmodifiableList(Arrays.asList(values));
    }

    /**
     * Split list into pieces by fix size
     *
     * @param subSize should be larger than 0
     */
    public static <T> List<List<T>> split(List<T> list, int subSize) {
        int len = list.size();
        int count = (len - 1) / subSize + 1;
        List<List<T>> result = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            int start = i * subSize;
            List<T> subList = list.subList(start, Math.min(start + subSize, len));
            result.add(subList);
        }
        return result;
    }

    /**
     * Merge list
     */
    public static <T> List<T> concat(List<? extends T> list1, List<? extends T> list2) {
        List<T> list = new ArrayList<>(list1.size() + list2.size());
        list.addAll(list1);
        list.addAll(list2);
        return list;
    }

    /**
     * Merge list
     */
    @SafeVarargs
    public static <T> List<T> concat(List<? extends T>... lists) {
        return concat(Lists.of(lists));
    }

    /**
     * Merge list
     */
    public static <T> List<T> concat(Collection<? extends List<? extends T>> lists) {
        int size = 0;
        for (List<? extends T> list : lists) {
            size += list.size();
        }
        List<T> newList = new ArrayList<>(size);
        for (List<? extends T> list : lists) {
            newList.addAll(list);
        }
        return newList;
    }

    /**
     * Find first matched element.
     *
     * @return null if not exists
     */
    @Nullable
    public static <T> T findFirst(List<T> values, Predicate<T> predicate) {
        for (T value : values) {
            if (predicate.test(value)) {
                return value;
            }
        }
        return null;
    }


    /**
     * Find last matched element.
     *
     * @return null if not exists
     */
    @Nullable
    public static <T> T findLast(List<T> values, Predicate<T> predicate) {
        ListIterator<T> iterator = values.listIterator(values.size());
        while (iterator.hasPrevious()) {
            T value = iterator.previous();
            if (predicate.test(value)) {
                return value;
            }
        }
        return null;
    }

    /**
     * Get the first value of list
     *
     * @return null if not exists
     */
    @Nullable
    public static <T> T first(List<T> values) {
        if (values.isEmpty()) {
            return null;
        }
        return values.get(0);
    }

    /**
     * Get the last value of list
     *
     * @return null if not exists
     */
    @Nullable
    public static <T> T last(List<T> values) {
        if (values.isEmpty()) {
            return null;
        }
        return values.listIterator(values.size()).previous();
    }
}
