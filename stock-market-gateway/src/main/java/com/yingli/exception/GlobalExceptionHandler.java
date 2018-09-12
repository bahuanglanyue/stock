package com.yingli.exception;

import com.yingli.framework.entity.ResultBean;
import com.yingli.framework.exception.MessageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * 全局错误处理
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler
    @ResponseBody
    public ResultBean handleException (Exception exception) {
        if (exception instanceof MessageException) {
            return ResultBean.fail(exception.getMessage());
        } else {
            StringWriter sw = new StringWriter();
            exception.printStackTrace(new PrintWriter(sw, true));
            String errorPrintStackTrace = sw.getBuffer().toString();
            //exception.printStackTrace();
            LOGGER.error("未知异常:{}", errorPrintStackTrace);
            return ResultBean.fail("Error，请稍后重试！");
        }
    }
}
