package com.sogou.map.kubbo.common.escape;

import java.util.HashMap;
import java.util.Map;

public final class Escapers {
    private Escapers() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private final Map<Character, String> replacementMap = new HashMap<Character, String>();
        private char safeMin = Character.MIN_VALUE;
        private char safeMax = Character.MAX_VALUE;
        private String unsafeReplacement = null;

        private Builder() {
        }

        /**
         * Sets the safe range of characters for the escaper. Characters in this
         * range that have no explicit replacement are considered 'safe' and
         * remain unescaped in the output. If {@code safeMax < safeMin} then the
         * safe range is empty.
         *
         * @param safeMin
         *            the lowest 'safe' character
         * @param safeMax
         *            the highest 'safe' character
         * @return the builder instance
         */
        public Builder setSafeRange(char safeMin, char safeMax) {
            this.safeMin = safeMin;
            this.safeMax = safeMax;
            return this;
        }

        /**
         * Sets the replacement string for any characters outside the 'safe'
         * range that have no explicit replacement. If {@code unsafeReplacement}
         * is {@code null} then no replacement will occur, if it is {@code ""}
         * then the unsafe characters are removed from the output.
         *
         * @param unsafeReplacement
         *            the string to replace unsafe characters
         * @return the builder instance
         */
        public Builder setUnsafeReplacement(String unsafeReplacement) {
            this.unsafeReplacement = unsafeReplacement;
            return this;
        }

        /**
         * Adds a replacement string for the given input character. The
         * specified character will be replaced by the given string whenever it
         * occurs in the input, irrespective of whether it lies inside or
         * outside the 'safe' range.
         *
         * @param c
         *            the character to be replaced
         * @param replacement
         *            the string to replace the given character
         * @return the builder instance
         * @throws NullPointerException
         *             if {@code replacement} is null
         */
        public Builder addEscape(char c, String replacement) {
            if (replacement == null) {
                throw new IllegalArgumentException("replacement == NULL");
            }
            // This can replace an existing character (the builder is
            // re-usable).
            replacementMap.put(c, replacement);
            return this;
        }

        /**
         * Returns a new escaper based on the current state of the builder.
         */
        public Escaper build() {
            return new ArrayBasedCharEscaper(replacementMap, safeMin, safeMax) {
                private final char[] replacementChars = unsafeReplacement != null ? unsafeReplacement.toCharArray() : null;

                @Override
                protected char[] escapeUnsafe(char c) {
                    return replacementChars;
                }
            };
        }
    }
}
