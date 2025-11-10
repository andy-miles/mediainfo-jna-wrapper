/*
 * The MIT License
 * Copyright © 2024-2025 Andy Miles
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
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
