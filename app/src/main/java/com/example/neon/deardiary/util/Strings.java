package com.example.neon.deardiary.util;


public class Strings {
    public static String formatNumber(int number) {
        return number < 10 ? "0" + number : number + "";
    }
}
