package com.commons;

/**
 * creator: sunc
 * date: 2017/4/18
 * description:
 */
public class RandomUtils {

    private static final Integer stringLength = 32;

    public static String getRandomString() {
        String[] chars = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
                "a", "b", "c", "d", "e", "f", "g", "h", "i", "g", "k", "l",
                "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x",
                "y", "z", "A", "B", "C", "D", "E", "F", "G", "H", "I", "G",
                "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
                "W", "X", "Y", "Z", "0"};
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < stringLength; i++) {
            int index = new Float(Math.random() * (chars.length - 1)).intValue();
            str.append(chars[index]);
        }
        return str.toString();
    }

}
