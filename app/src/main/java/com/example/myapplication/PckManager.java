package com.example.myapplication;


/// 3 + 3  ///

import android.util.Log;

public class PckManager {
    static int pckIterator = 0;

    public static byte[] buildMovePck(int left, int right){
        byte[] pck;
        String strLeft =  Integer.toString(left);
        String strRight =  Integer.toString(right);
        strLeft = fillByZero(strLeft,3);
        strRight = fillByZero(strRight,3);
        String iterator = fillByZero(Integer.toString(pckIterator),3);
        pckIterator++;
        if (pckIterator>999)pckIterator=0;
        pck = (strLeft + strRight + iterator).getBytes();

        return pck;
    };

    private static String fillByZero(String str, int fixed_length){
        Log.i("MagaerBefor",str);

        String tmp = str;
        while (tmp.length()<fixed_length)
            tmp = "0" + tmp;
        Log.i("MagaerAfter", tmp);

        return tmp;
    }
}
