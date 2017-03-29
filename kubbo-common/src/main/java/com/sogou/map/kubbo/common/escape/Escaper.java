package com.sogou.map.kubbo.common.escape;


public interface Escaper {

  /**
   * Returns the escaped form of a given literal string.
   *
   * <p>Note that this method may treat input characters differently depending on the specific
   * escaper implementation.
   *
   * <ul>
   * <li>{@link UnicodeEscaper} handles <a href="http://en.wikipedia.org/wiki/UTF-16">UTF-16</a>
   * correctly, including surrogate character pairs. If the input is badly formed the escaper should
   * throw {@link IllegalArgumentException}.
   * <li>{@link CharEscaper} handles Java characters independently and does not verify the input for
   * well formed characters. A {@code CharEscaper} should not be used in situations where input is
   * not guaranteed to be restricted to the Basic Multilingual Plane (BMP).
   * </ul>
   *
   * @param string the literal string to be escaped
   * @return the escaped form of {@code string}
   * @throws NullPointerException if {@code string} is null
   * @throws IllegalArgumentException if {@code string} contains badly formed UTF-16 or cannot be
   *     escaped for any other reason
   */
  String escape(String string);

}
