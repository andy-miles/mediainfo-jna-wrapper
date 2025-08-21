package com.amilesend.mediainfo.util;

import lombok.experimental.UtilityClass;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

@UtilityClass
public class Validate {
    /**
     * Throws an exception if the given {@code chars} is blank.
     *
     * @param chars the character sequence to validate
     * @param message the message to include in the exception thrown
     * @throws NullPointerException if the char sequence is null
     * @throws IllegalArgumentException if the char sequence is not blank
     */
    public static void notBlank(final CharSequence chars, final String message) {
        if (Objects.isNull(chars)) {
            throw new NullPointerException(message);
        }

        if (StringUtils.isBlank(chars)) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Throws an exception if the given {@code collection} is empty.
     *
     * @param collection the collection to validate
     * @param message the message to include in the exception thrown
     * @throws NullPointerException if the collection is null
     * @throws IllegalArgumentException if the collection is not blank
     */
    public static void notEmpty(final Collection<?> collection, final String message) {
        if (Objects.isNull(collection)) {
            throw new NullPointerException(message);
        }

        if (collection.isEmpty()) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Throws an exception if the given {@code map} is empty.
     *
     * @param map the map to validate
     * @param message the message to include in the exception thrown
     * @throws NullPointerException if the map is null
     * @throws IllegalArgumentException if the map is not blank
     */
    public static void notEmpty(final Map<?, ?> map, final String message) {
        if (Objects.isNull(map)) {
            throw new NullPointerException(message);
        }

        if (map.isEmpty()) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Throws an exception if the given expression is false.
     *
     * @param exp the boolean expression
     * @param message the message to include in the exception thrown
     * @throws IllegalArgumentException if the expression is false
     */
    public static void isTrue(final boolean exp, final String message) {
        if (!exp) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Throws an exception if the given object is {@code null}.
     *
     * @param obj the object
     * @param message the message to include in the exception thrown
     * @throws NullPointerException if the object is null
     */
    public static void notNull(final Object obj, final String message) {
        Objects.requireNonNull(obj, message);
    }
}
