package com.yingli.feature.auth.controller;

import com.yingli.framework.entity.ResultBean;
import com.yingli.security.config.CustomDefaultTokenServices;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/auth/1.0")
@Api(description = "安全控制API", produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthController {

    @Autowired
    private CustomDefaultTokenServices tokenServices;

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    @ApiOperation(value = "登出", httpMethod = "get")
    public ResultBean<?> logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
            OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails) auth.getDetails();
            String accessToken = details.getTokenValue();
            tokenServices.revokeToken(accessToken);//清除token
            return ResultBean.ok("注销成功");
        }
        return ResultBean.fail("注销失败");
    }
}
