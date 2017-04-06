package com.sogou.map.kubbo.common.escape;


public interface Escaper {

  /**
   * Returns the escaped form of a given literal string.
   *
   * <p>Note that this method may treat input characters differently depending on the specific
   * escaper implementation.
   *
   *
   * @param string the literal string to be escaped
   * @return the escaped form of {@code string}
   * @throws NullPointerException if {@code string} is null
   * @throws IllegalArgumentException if {@code string} contains badly formed UTF-16 or cannot be
   *     escaped for any other reason
   */
  String escape(String string);

}
