package com.example.myapplication;


import android.util.Log;

public class PckManager {
    static int pckIterator = 0;

    public static byte[] buildMovePck(int left, int right) { // The structure of this type pck is: 4b(left -100 to 100) + 4b(right the same as left) + 16b(number 0 to 999)
        byte[] pck;
        String strLeft = Integer.toString(left);
        String strRight = Integer.toString(right);
        strLeft = fillByZero(strLeft, 4);
        strRight = fillByZero(strRight, 4);
        String iterator = fillByZero(Integer.toString(pckIterator), 8);
        pckIterator++;
        if (pckIterator > 999) pckIterator = 0;
        pck = (strLeft + strRight + iterator).getBytes();

        return pck;
    }

    ;

    private static String fillByZero(String str, int fixed_length) {
//        Log.i("MagaerBefor", str);
        String tmp = str;
        while (tmp.length() < fixed_length)
            tmp = "0" + tmp;
//        Log.i("MagaerAfter", tmp);
        return tmp;
    }
}
