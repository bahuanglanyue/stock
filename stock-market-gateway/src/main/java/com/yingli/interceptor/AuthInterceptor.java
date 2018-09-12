/*
package com.yingli.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yingli.common.constant.CommConsts;
import com.yingli.framework.entity.ResultBean;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class AuthInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String auth = request.getHeader("Authorization");
        if ((auth != null) && (auth.length() > 7)) {
            String HeadStr = auth.trim().substring(0, 6);
            if (HeadStr.compareTo("Bearer") == 0) {
                auth = auth.substring(7, auth.length()).trim();
                //校验token是否有效


                return true;
            }
        }
        response.setCharacterEncoding(CommConsts.DEFAULT_ENCODING);
        response.setContentType(CommConsts.CONTENT_TYPE_APP_JSON);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        ObjectMapper mapper = new ObjectMapper();
        ResultBean resultBean = new ResultBean(ResultBean.FAIL, "invalid token");
        response.getWriter().write(mapper.writeValueAsString(resultBean));
        return false;
    }
}
*/
