package com.scoder.jusic.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author wanglangjie
 * @Date 2024/6/1 8:19
 * @description: qq签名
 */
public class QMWSign {
    public static String v(String b) {
        int[] p = {21, 4, 9, 26, 16, 20, 27, 30};
        StringBuilder res = new StringBuilder();
        for (int x : p) {
            res.append(b.charAt(x));
        }
        return res.toString();
    }

    public static String c(String b) {
        int[] p = {18, 11, 3, 2, 1, 7, 6, 25};
        StringBuilder res = new StringBuilder();
        for (int x : p) {
            res.append(b.charAt(x));
        }
        return res.toString();
    }

    public static int[] y(int a, Integer b, Integer c) {
        int[] e = new int[4];
        int r25 = a >> 2;
        if (b != null && c != null) {
            int r26 = a & 3;
            int r26_2 = r26 << 4;
            int r26_3 = b >> 4;
            int r26_4 = r26_2 | r26_3;
            int r27 = b & 15;
            int r27_2 = r27 << 2;
            int r27_3 = r27_2 | (c >> 6);
            int r28 = c & 63;
            e[0] = r25;
            e[1] = r26_4;
            e[2] = r27_3;
            e[3] = r28;
        } else {
            int r10 = a >> 2;
            int r11 = a & 3;
            int r11_2 = r11 << 4;
            e[0] = r10;
            e[1] = r11_2;
        }
        return e;
    }

    public static String n(int[] ls) {
        StringBuilder res = new StringBuilder();
        String b64all = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";
        for (int i = 0; i < ls.length; i += 3) {
            if (i < ls.length - 2) {
                int[] yResult = y(ls[i], ls[i + 1], ls[i + 2]);
                for (int y : yResult) {
                    res.append(b64all.charAt(y));
                }
            } else {
                int[] yResult = y(ls[i], null, null);
                for (int y : yResult) {
                    res.append(b64all.charAt(y));
                }
            }
        }
        return res.substring(0,res.length()-2);
    }

    public static int[] t(String b) {
        Map<Character, Integer> zd = new HashMap<>();
        zd.put('0', 0); zd.put('1', 1); zd.put('2', 2); zd.put('3', 3);
        zd.put('4', 4); zd.put('5', 5); zd.put('6', 6); zd.put('7', 7);
        zd.put('8', 8); zd.put('9', 9); zd.put('A', 10); zd.put('B', 11);
        zd.put('C', 12); zd.put('D', 13); zd.put('E', 14); zd.put('F', 15);

        int[] ol = {212, 45, 80, 68, 195, 163, 163, 203, 157, 220, 254, 91, 204, 79, 104, 6};
        int[] res = new int[b.length() / 2];
        int j = 0;
        for (int i = 0; i < b.length(); i += 2) {
            int one = zd.get(b.charAt(i));
            int two = zd.get(b.charAt(i + 1));
            int r = one * 16 ^ two;
            res[j] = r ^ ol[j];
            j++;
        }
        return res;
    }

    public static String sign(String params) throws NoSuchAlgorithmException {
        String md5Str = CryptoUtil2.md5(params).toUpperCase();
        String h = v(md5Str);
        String e = c(md5Str);
        int[] ls = t(md5Str);
        String m = n(ls);
        String res = "zzb" + h + m + e;
        res = res.toLowerCase().replaceAll("[\\/+]", "");
        return res;
    }


}
