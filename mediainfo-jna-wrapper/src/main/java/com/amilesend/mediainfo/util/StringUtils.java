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

import java.util.Objects;

/** Simple string utility methods. */
@UtilityClass
public class StringUtils {
    public static final String EMPTY = "";

    /**
     * Determines if the given char sequence is blank.
     *
     * @param cs the char sequence to evaluate
     * @return {@code true} if blank; else, {@code false}
     */
    public static boolean isBlank(final CharSequence cs) {
        if (Objects.isNull(cs) || cs.length() == 0) {
            return true;
        }

        for (int i = 0; i < cs.length(); ++i) {
            final char c = cs.charAt(i);
            if (!Character.isWhitespace(c)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Determines if the given char sequence is not blank.
     *
     * @param cs the char sequence to evaluate
     * @return {@code true} if not blank; else, {@code false}
     */
    public static boolean isNotBlank(final CharSequence cs) {
        return !isBlank(cs);
    }
}
