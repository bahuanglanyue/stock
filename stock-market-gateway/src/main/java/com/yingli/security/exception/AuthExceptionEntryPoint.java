package com.yingli.security.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yingli.framework.entity.ResultBean;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 自定义AuthExceptionEntryPoint用于tokan校验失败返回信息
 */
public class AuthExceptionEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
        /*Map<String, Object> map = new HashMap<>();
        map.put(AuthExceptionConsts.ERROR, String.valueOf(HttpServletResponse.SC_UNAUTHORIZED));
        map.put(AuthExceptionConsts.MESSAGE, e.getMessage());
        map.put(AuthExceptionConsts.PATH, request.getServletPath());
        map.put(AuthExceptionConsts.TIMESTAMP, String.valueOf(new java.util.Date().getTime()));*/
        response.setContentType("application/json; charset=utf-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), new ResultBean(ResultBean.FAIL, e.getMessage()));
    }
}
