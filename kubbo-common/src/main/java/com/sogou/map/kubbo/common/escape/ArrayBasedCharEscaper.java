package com.sogou.map.kubbo.common.escape;

import java.util.Map;

/**
 * A {@link CharEscaper} that uses an array to quickly look up replacement
 * characters for a given {@code char} value. An additional safe range is
 * provided that determines whether {@code char} values without specific
 * replacements are to be considered safe and left unescaped or should be
 * escaped in a general way.
 *
 * <p>
 * A good example of usage of this class is for Java source code escaping where
 * the replacement array contains information about special ASCII characters
 * such as {@code \\t} and {@code \\n} while {@link #escapeUnsafe} is overridden
 * to handle general escaping of the form {@code \\uxxxx}.
 *
 * <p>
 * The size of the data structure used by {@link ArrayBasedCharEscaper} is
 * proportional to the highest valued character that requires escaping. For
 * example a replacement map containing the single character
 * '{@code \}{@code u1000}' will require approximately 16K of memory. If you
 * need to create multiple escaper instances that have the same character
 * replacement mapping consider using {@link ArrayBasedEscaperMap}.
 *
 */
public abstract class ArrayBasedCharEscaper extends CharEscaper {
    // The replacement array (see ArrayBasedEscaperMap).
    private final char[][] replacements;
    // The number of elements in the replacement array.
    private final int replacementsLength;
    // The first character in the safe range.
    private final char safeMin;
    // The last character in the safe range.
    private final char safeMax;

    protected ArrayBasedCharEscaper(Map<Character, String> replacementMap, char safeMin, char safeMax) {
        this(ArrayBasedEscaperMap.create(replacementMap), safeMin, safeMax);
    }

    protected ArrayBasedCharEscaper(ArrayBasedEscaperMap escaperMap, char safeMin, char safeMax) {
        if (escaperMap == null) {
            throw new IllegalArgumentException("escaperMap == NULL");
        }
        this.replacements = escaperMap.getReplacementArray();
        this.replacementsLength = replacements.length;
        if (safeMax < safeMin) {
            safeMax = Character.MIN_VALUE;
            safeMin = Character.MAX_VALUE;
        }
        this.safeMin = safeMin;
        this.safeMax = safeMax;
    }

    /*
     * This is overridden to improve performance. Rough benchmarking shows that
     * this almost doubles the speed when processing strings that do not require
     * any escaping.
     */
    @Override
    public final String escape(String s) {
        if (s == null) {
            throw new IllegalArgumentException("s == NULL");
        }
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if ((c < replacementsLength && replacements[c] != null) || c > safeMax || c < safeMin) {
                return escapeSlow(s, i);
            }
        }
        return s;
    }

    /**
     * Escapes a single character using the replacement array and safe range
     * values. If the given character does not have an explicit replacement and
     * lies outside the safe range then {@link #escapeUnsafe} is called.
     */
    @Override
    protected final char[] escape(char c) {
        if (c < replacementsLength) {
            char[] chars = replacements[c];
            if (chars != null) {
                return chars;
            }
        }
        if (c >= safeMin && c <= safeMax) {
            return null;
        }
        return escapeUnsafe(c);
    }

    /**
     * Escapes a {@code char} value that has no direct explicit value in the
     * replacement array and lies outside the stated safe range. Subclasses
     * should override this method to provide generalized escaping for
     * characters.
     *
     * <p>
     * Note that arrays returned by this method must not be modified once they
     * have been returned. However it is acceptable to return the same array
     * multiple times (even for different input characters).
     *
     * @param c
     *         the character to escape
     * @return the replacement characters, or {@code null} if no escaping was
     *         required
     */
    protected abstract char[] escapeUnsafe(char c);
}
