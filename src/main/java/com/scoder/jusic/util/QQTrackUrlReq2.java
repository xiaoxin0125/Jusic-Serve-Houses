package com.scoder.jusic.util;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.NoSuchAlgorithmException;

/**
 * @author JumpAlang
 * @create 2024-05-30 22:34
 */
@Component
public class QQTrackUrlReq2 {
    private String[] ua_list = {"Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/112.0.0.0 Safari/537.36 Edg/112.0.1722.39",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36 Edg/114.0.1788.0",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36 Edg/114.0.1788.0  uacq",
            "Mozilla/5.0 (Windows NT 10.0; WOW64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/113.0.5666.197 Safari/537.36",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/113.0.0.0 Safari/537.36 uacq",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/113.0.0.0 Safari/537.36"
    };
    public String signRequest(JSONObject data, Boolean cache) throws UnirestException, NoSuchAlgorithmException {
        if(cache == null) {
            cache = false;
        }
        String dataStr = "{\"req_0\":{\"module\":\"vkey.GetVkeyServer\",\"method\":\"CgiGetVkey\",\"param\":{\"filename\":[\"M800003VLsik0ztbIb.mp3\"],\"guid\":\"114514\",\"songmid\":[\"00128N3r2SYKMF\"],\"songtype\":[0],\"uin\":\"1040927107\",\"loginflag\":1,\"platform\":\"20\"}},\"comm\":{\"qq\":\"1040927107\",\"authst\":\"Q_H_L_63k3NGyHi21SOdUwCb_y9xpA4OocbRy5u_nUaBY8VspkbVsIhGfoSICwKjF355lAtSYtNNEqXpQ\",\"ct\":\"26\",\"cv\":\"2010101\",\"v\":\"2010101\"}}";//data.toString();
        String s = QMWSign.sign(dataStr);
        String url = "https://u.y.qq.com/cgi-bin/musics.fcg?format=json&sign=" + s;

        return Unirest.post(url)
                    .header("User-Agent",ua_list[RandomUtil.randomInt(ua_list.length)])
//                .header("Content-Type", "application/json")
//                .header("cache", cache ? "max-age=" + (86400 * 30) : "no-cache")
                .body(dataStr).asString().getBody();

    }

    public String getTrackUrl(String songId,String strMediaMid,String quality) throws NoSuchAlgorithmException {
//        JSONObject userInfo = variable.useCookiePool
//                ? new JSONObject(random.choice(config.readConfig("module.cookiepool.tx")))
//                : new JSONObject(config.readConfig("module.tx.user"));

        JSONObject requestBody = new JSONObject();
        JSONObject req0 = new JSONObject();
        req0.put("module", "vkey.GetVkeyServer");
        req0.put("method", "CgiGetVkey");
        JSONObject param = new JSONObject();
        param.put("filename", new JSONArray().add(QQUtils.fileInfo.get(quality).h + strMediaMid + QQUtils.fileInfo.get(quality).e));
        param.put("guid", "114514");//guid
        param.put("songmid", new JSONArray().add(songId));
        param.put("songtype", new JSONArray().add(0));
        param.put("uin", "1040927107");
        param.put("loginflag", 1);
        param.put("platform", "20");
        req0.put("param", param);
        requestBody.put("req_0", req0);
        JSONObject comm = new JSONObject();
        comm.put("qq", "1040927107");
        comm.put("authst", "Q_H_L_63k3NGyHi21SOdUwCb_y9xpA4OocbRy5u_nUaBY8VspkbVsIhGfoSICwKjF355lAtSYtNNEqXpQ");
        comm.put("ct", "26");
        comm.put("cv", "2010101");
        comm.put("v", "2010101");
        requestBody.put("comm", comm);

        String resp =  signRequest(requestBody,false);
        JSONObject body = JSONObject.parseObject(resp);
        JSONObject data = body.getJSONObject("req_0").getJSONObject("data").getJSONArray("midurlinfo").getJSONObject(0);
        String url = data.getString("purl");

        if (StringUtils.isEmpty(url)) {
            return null;
        }
        return url;
    }

    public static void main(String[] args) throws NoSuchAlgorithmException {
        QQTrackUrlReq2 qqTrackUrlReq2 = new QQTrackUrlReq2();
        String url = qqTrackUrlReq2.getTrackUrl("718479","003KtYhg4frNXC","320k");
        System.out.println(url);
    }
}
