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
