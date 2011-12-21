package de.wehner.mediamagpie.common.util;

import java.util.Collection;

public class CollectionUtil {

    /**
     * Returns {@code true} if the given collection is {@code null} or empty.
     * 
     * @param c
     *            the collection to check.
     * @return {@code true} if the given collection is {@code null} or empty, otherwise {@code false}.
     */
    public static boolean isEmpty(Collection<?> c) {
        return c == null || c.isEmpty();
    }
}
