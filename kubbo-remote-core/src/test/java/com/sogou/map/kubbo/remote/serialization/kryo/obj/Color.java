/**
 * 
 */
package com.sogou.map.kubbo.remote.serialization.kryo.obj;

import java.io.Serializable;

/**
 * @author liufuliang
 *
 */

public class Color implements Serializable{
    public static final Color WHITE     = new Color(255, 255, 255);
    public static final Color BLACK 	= new Color(0, 0, 0);
    public static final Color RED       = new Color(255, 0, 0);

    int value;
    public Color(){
    	
    }
    public Color(int r, int g, int b) {
        this(r, g, b, 255);
    }
    public Color(int r, int g, int b, int a) {
        value = ((a & 0xFF) << 24) |
                ((r & 0xFF) << 16) |
                ((g & 0xFF) << 8)  |
                ((b & 0xFF) << 0);
    }
    public Color(int rgb) {
        value = 0xff000000 | rgb;
    }

    public Color(int rgba, boolean hasalpha) {
        if (hasalpha) {
            value = rgba;
        } else {
            value = 0xff000000 | rgba;
        }
    }
    public int getValue(){
    	return value;
    }
    public int getRed() {
    	return (getRGB() >> 16) & 0xFF;
    }

    public int getGreen() {
    	return (getRGB() >> 8) & 0xFF;
    }

    public int getBlue() {
    	return (getRGB() >> 0) & 0xFF;
    }

    public int getAlpha() {
        return (getRGB() >> 24) & 0xff;
    }

    public int getRGB() {
    	return value;
    }
}
