package com.scoder.jusic.util;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.scoder.jusic.configuration.JusicProperties;
import com.scoder.jusic.service.ConfigService;
import kong.unirest.Unirest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author JumpAlang
 * @create 2024-05-30 22:34
 */
@Component
@Slf4j
public class QQTrackUrlReq2 {
    @Autowired
    private JusicProperties jusicProperties;
    @Autowired
    private ConfigService configService;

    private String[] ua_list = {"Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/112.0.0.0 Safari/537.36 Edg/112.0.1722.39",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36 Edg/114.0.1788.0",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36 Edg/114.0.1788.0  uacq",
            "Mozilla/5.0 (Windows NT 10.0; WOW64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/113.0.5666.197 Safari/537.36",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/113.0.0.0 Safari/537.36 uacq",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/113.0.0.0 Safari/537.36"
    };
    public String signRequest(String requestUrl,Map<String, Object> requestBody) throws NoSuchAlgorithmException {
        JSONObject data = new JSONObject(requestBody);
        String dataStr = data.toString();
        String s = QMWSign.sign(dataStr);
        String url =  requestUrl.indexOf("?") == -1?(requestUrl+"?sign="+s):(requestUrl+"&sign="+s);

        return Unirest.post(url)
                    .header("User-Agent",ua_list[RandomUtil.randomInt(ua_list.length)])
                .header("Content-Type", "application/json")
                .body(dataStr).asString().getBody();

    }

    public String getTrackUrl(String songId,String strMediaMid,String quality) throws NoSuchAlgorithmException {
        return getTrackUrl(songId,strMediaMid,quality,jusicProperties.getQqUin(),jusicProperties.getQqMusicKey(),jusicProperties.getQqMusicGuid());
    }
    public String getTrackUrl(String songId,String strMediaMid,String quality,String uin,String qqmusicKey,String guid) throws NoSuchAlgorithmException {
        Map<String, Object> requestBodyMap = new LinkedHashMap<>();

        Map<String, Object> req0Map = new LinkedHashMap<>();
        req0Map.put("module", "vkey.GetVkeyServer");
        req0Map.put("method", "CgiGetVkey");

        Map<String, Object> paramMap = new LinkedHashMap<>();
        JSONArray filenameArray = new JSONArray();
        filenameArray.add(QQUtils.fileInfo.get(quality).h + strMediaMid + QQUtils.fileInfo.get(quality).e);
        paramMap.put("filename", filenameArray);
        paramMap.put("guid", guid);
        JSONArray songmidArray = new JSONArray();
        songmidArray.add(songId);
        paramMap.put("songmid", songmidArray);
        JSONArray songtypeArray = new JSONArray();
        songtypeArray.add(0);
        paramMap.put("songtype", songtypeArray);
        paramMap.put("uin", uin);
        paramMap.put("loginflag", 1);
        paramMap.put("platform", "20");

        req0Map.put("param", new JSONObject(paramMap));
        requestBodyMap.put("req_0", new JSONObject(req0Map));

        Map<String, Object> commMap = new LinkedHashMap<>();
        commMap.put("qq", uin);
        commMap.put("authst", qqmusicKey);
        commMap.put("ct", "26");
        commMap.put("cv", "2010101");
        commMap.put("v", "2010101");

        requestBodyMap.put("comm", new JSONObject(commMap));
        JSONObject requestBody = new JSONObject(requestBodyMap);

        String resp =  signRequest("https://u.y.qq.com/cgi-bin/musics.fcg?format=json",requestBody);
        JSONObject body = JSONObject.parseObject(resp);
        String url;
        try{
            JSONObject data = body.getJSONObject("req_0").getJSONObject("data").getJSONArray("midurlinfo").getJSONObject(0);
            url = data.getString("purl");
        }catch(Exception e){
            return null;
        }

        if (StringUtils.isEmpty(url)) {
            return null;
        }
        return QQUtils.getCdnAddr(null)+url;
    }

    public void refresh(String uin,String qqmusicKey) throws NoSuchAlgorithmException {
        String url;
        Map<String, Object> requestBodyMap = new LinkedHashMap<>();
        if (qqmusicKey.startsWith("W_X")) {
            Map<String, Object> commMap = new LinkedHashMap<>();
            commMap.put("fPersonality","0");
            commMap.put("tmeLoginType", "1");
            commMap.put("tmeLoginMethod", "1");
            commMap.put("qq", "");
            commMap.put("authst", "");
            commMap.put("ct", "11");
            commMap.put("cv", "12080008");
            commMap.put("v", "12080008");
            commMap.put("tmeAppID", "qqmusic");
            requestBodyMap.put("comm",new JSONObject(commMap));

            Map<String, Object> req1Map = new LinkedHashMap<>();
            req1Map.put("module", "music.login.LoginServer");
            req1Map.put("method", "Login");
            Map<String, Object> paramMap = new LinkedHashMap<>();
            paramMap.put("code", "");
            paramMap.put("openid", "");
            paramMap.put("refresh_token", "");
            paramMap.put("str_musicid", uin);
            paramMap.put("musickey", qqmusicKey);
            paramMap.put("unionid", "");
            paramMap.put("refresh_key", "");
            paramMap.put("loginMode", 2);
            req1Map.put("param",new JSONObject(paramMap));
            requestBodyMap.put("req_1", new JSONObject(req1Map));

            url = "https://u.y.qq.com/cgi-bin/musics.fcg";

        } else if (qqmusicKey.startsWith("Q_H_L")) {

            Map<String, Object> req1Map = new LinkedHashMap<>();
            req1Map.put("module", "QQConnectLogin.LoginServer");
            req1Map.put("method", "QQLogin");
            Map<String, Object> paramMap = new LinkedHashMap<>();
            paramMap.put("expired_in", 7776000);
            paramMap.put("musicid", Integer.valueOf(uin));
            paramMap.put("musickey",qqmusicKey);
            req1Map.put("param",new JSONObject(paramMap));
            requestBodyMap.put("req_1", new JSONObject(req1Map));
            url = "https://u6.y.qq.com/cgi-bin/musics.fcg";
        } else{
            log.error("未知的qqmusic_key格式");
            return;
        }
        refreshReq(url,requestBodyMap);
    }

    @Scheduled(fixedRateString = "${jusic.qq_refresh_interval}")
    public void refreshAuto(){
        Integer failCount = 0;

        while (failCount < jusicProperties.getRetryCount()) {
            try{
                refresh(jusicProperties.getQqUin(),jusicProperties.getQqMusicKey());
                return;
            }catch (Exception e){
                failCount++;
                log.error(e.getMessage(),e);
            }
        }

    }

    public void refreshReq(String url,Map<String, Object> requestBody) throws NoSuchAlgorithmException {
        String resp = signRequest(url,requestBody);
        JSONObject responseBody = JSONObject.parseObject(resp);
        if (responseBody.getJSONObject("req_1").getInteger("code") != 0) {
            log.error("刷新登录失败, code: " + responseBody.getJSONObject("req_1").getInteger("code") + "\n响应体: " + responseBody);
        } else {
            configService.setQqMusicCookie(responseBody.getJSONObject("req_1").getJSONObject("data").getString("musicid"),responseBody.getJSONObject("req_1").getJSONObject("data").getString("musickey"));
        }
    }
    public static void main(String[] args) throws NoSuchAlgorithmException {
        QQTrackUrlReq2 qqTrackUrlReq2 = new QQTrackUrlReq2();
        String uin = "1040927107";
        String guid = "114514";
        String qqmusicKey = "Q_H_L_63k3NWYfBRq1KV0rBZ4ySa7vYK6XS5803Q23v35s4abc0rela9BzP6jBXv5Fgk9hsk4FLhwXX4w";
        String url = qqTrackUrlReq2.getTrackUrl("0039MnYb0qxYhV","002202B43Cq4V4","flac",uin,qqmusicKey,guid);
        System.out.println(url);
        String url2 = qqTrackUrlReq2.getTrackUrl("002OdswE2QgnoL","003fMltF4f90Mm","flac",uin,qqmusicKey,guid);
        System.out.println(url2);
        String url3 = qqTrackUrlReq2.getTrackUrl("002E3MtF0IAMMY","0029Lt3K2XVP75","flac",uin,qqmusicKey,guid);
        System.out.println(url3);
        qqTrackUrlReq2.refresh(uin,qqmusicKey);
    }
}
