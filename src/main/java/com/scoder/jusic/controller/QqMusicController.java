package com.scoder.jusic.controller;

import com.scoder.jusic.common.message.Response;
import com.scoder.jusic.service.ConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author JumpAlang
 * @create 2024-06-01 18:59
 */
@Controller
@Slf4j
public class QqMusicController {
    @Autowired
    private ConfigService configService;
    @RequestMapping("/qqmusic/setCookie")
    @ResponseBody
    public Response setCookie(String uin, String qqMusicKey) {
        configService.setQqMusicCookie(uin,qqMusicKey);
        return Response.success();
    }
}
