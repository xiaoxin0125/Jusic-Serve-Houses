package com.scoder.jusic.util;

import java.util.HashMap;
import java.util.Map;

/**
 * @author JumpAlang
 * @create 2024-05-30 23:02
 */

public class QQUtils {
    public static class FileInfo {
        public String e;
        public String h;

        public FileInfo(String e, String h) {
            this.e = e;
            this.h = h;
        }
    }

    public static final Map<String, FileInfo> fileInfo = new HashMap<String, FileInfo>() {{
        put("128k", new FileInfo(".mp3", "M500"));
        put("320k", new FileInfo(".mp3", "M800"));
        put("flac", new FileInfo(".flac", "F000"));
        put("flac24bit", new FileInfo(".flac", "RS01"));
        put("dolby", new FileInfo(".flac", "Q000"));
        put("master", new FileInfo(".flac", "AI00"));
    }};

    public static final Map<String, String> qualityMapReverse = new HashMap<String, String>() {{
        put("M500", "128k");
        put("M800", "320k");
        put("F000", "flac");
        put("RS01", "flac24bit");
        put("Q000", "dolby");
        put("AI00", "master");
    }};

    public static String getCdnAddr(String cdnAddr) {
        return (cdnAddr != null && !cdnAddr.isEmpty()) ? cdnAddr : "http://ws.stream.qqmusic.qq.com/";
    }
}

