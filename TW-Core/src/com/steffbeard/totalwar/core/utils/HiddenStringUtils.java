package com.steffbeard.totalwar.core.utils;

import java.nio.charset.Charset;
import org.bukkit.ChatColor;

public class HiddenStringUtils
{
    private static final String SEQUENCE_HEADER;
    private static final String SEQUENCE_FOOTER;
    
    static {
        SEQUENCE_HEADER = new StringBuilder().append(ChatColor.RED).append(ChatColor.MAGIC).append(ChatColor.YELLOW).toString();
        SEQUENCE_FOOTER = new StringBuilder().append(ChatColor.BLACK).append(ChatColor.GOLD).append(ChatColor.LIGHT_PURPLE).toString();
    }
    
    public static String encodeString(final String hiddenString) {
        return quote(stringToColors(hiddenString));
    }
    
    public static boolean hasHiddenString(final String input) {
        return input != null && (input.indexOf(HiddenStringUtils.SEQUENCE_HEADER) > -1 && input.indexOf(HiddenStringUtils.SEQUENCE_FOOTER) > -1);
    }
    
    public static String extractHiddenString(final String input) {
        return colorsToString(extract(input));
    }
    
    public static String replaceHiddenString(final String input, final String hiddenString) {
        if (input == null) {
            return null;
        }
        final int start = input.indexOf(HiddenStringUtils.SEQUENCE_HEADER);
        final int end = input.indexOf(HiddenStringUtils.SEQUENCE_FOOTER);
        if (start < 0 || end < 0) {
            return null;
        }
        return String.valueOf(input.substring(0, start + HiddenStringUtils.SEQUENCE_HEADER.length())) + stringToColors(hiddenString) + input.substring(end, input.length());
    }
    
    private static String quote(final String input) {
        if (input == null) {
            return null;
        }
        return String.valueOf(HiddenStringUtils.SEQUENCE_HEADER) + input + HiddenStringUtils.SEQUENCE_FOOTER;
    }
    
    private static String extract(final String input) {
        if (input == null) {
            return null;
        }
        final int start = input.indexOf(HiddenStringUtils.SEQUENCE_HEADER);
        final int end = input.indexOf(HiddenStringUtils.SEQUENCE_FOOTER);
        if (start < 0 || end < 0) {
            return null;
        }
        return input.substring(start + HiddenStringUtils.SEQUENCE_HEADER.length(), end);
    }
    
    private static String stringToColors(final String normal) {
        if (normal == null) {
            return null;
        }
        final byte[] bytes = normal.getBytes(Charset.forName("UTF-8"));
        final char[] chars = new char[bytes.length * 4];
        for (int i = 0; i < bytes.length; ++i) {
            final char[] hex = byteToHex(bytes[i]);
            chars[i * 4] = '§';
            chars[i * 4 + 1] = hex[0];
            chars[i * 4 + 2] = '§';
            chars[i * 4 + 3] = hex[1];
        }
        return new String(chars);
    }
    
    private static String colorsToString(String colors) {
        if (colors == null) {
            return null;
        }
        colors = colors.toLowerCase().replace("§", "");
        if (colors.length() % 2 != 0) {
            colors = colors.substring(0, colors.length() / 2 * 2);
        }
        final char[] chars = colors.toCharArray();
        final byte[] bytes = new byte[chars.length / 2];
        for (int i = 0; i < chars.length; i += 2) {
            bytes[i / 2] = hexToByte(chars[i], chars[i + 1]);
        }
        return new String(bytes, Charset.forName("UTF-8"));
    }
    
    private static int hexToUnsignedInt(final char c) {
        if (c >= '0' && c <= '9') {
            return c - '0';
        }
        if (c >= 'a' && c <= 'f') {
            return c - 'W';
        }
        throw new IllegalArgumentException("Invalid hex char: out of range");
    }
    
    private static char unsignedIntToHex(final int i) {
        if (i >= 0 && i <= 9) {
            return (char)(i + 48);
        }
        if (i >= 10 && i <= 15) {
            return (char)(i + 87);
        }
        throw new IllegalArgumentException("Invalid hex int: out of range");
    }
    
    private static byte hexToByte(final char hex1, final char hex0) {
        return (byte)((hexToUnsignedInt(hex1) << 4 | hexToUnsignedInt(hex0)) - 128);
    }
    
    private static char[] byteToHex(final byte b) {
        final int unsignedByte = b + 128;
        return new char[] { unsignedIntToHex(unsignedByte >> 4 & 0xF), unsignedIntToHex(unsignedByte & 0xF) };
    }
}