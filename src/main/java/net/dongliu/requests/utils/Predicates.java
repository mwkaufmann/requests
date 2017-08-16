package net.dongliu.requests.utils;

/**
 * For internal use only
 */
public class Predicates {
    public static int bigThanZero(int value) {
        if (value <= 0) {
            throw new IllegalArgumentException("Param should be bigger than 0");
        }
        return value;
    }

}
