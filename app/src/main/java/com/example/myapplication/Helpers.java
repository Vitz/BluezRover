package com.example.myapplication;

public class Helpers {

    public static int normalizeTo0(int val) {
        int res = (int) (((2.0 * val / 100.0) - 1.0) * 100.0);
        return res;
    }

    public static int normalizeTo50(int val) {
        int res = (int) ((((val / 100.0) + 1.0) * 100.0) / 2.0);
        return res;
    }

}

