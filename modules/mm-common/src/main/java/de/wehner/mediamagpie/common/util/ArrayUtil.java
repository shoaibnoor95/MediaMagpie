package de.wehner.mediamagpie.common.util;

public class ArrayUtil {
    /**
     * Returns {@code true} if the given array is {@code null} or empty.
     * 
     * @param <T>
     *            the type of elements of the array.
     * @param array
     *            the array to check.
     * @return {@code true} if the given array is {@code null} or empty, otherwise {@code false}.
     */
    public static <T> boolean isEmpty(T[] array) {
        return array == null || !hasElements(array);
    }

    private static <T> boolean hasElements(T[] array) {
        return array.length > 0;
    }

}
