package com.yingli.feature.test.controller;

import com.yingli.framework.entity.ResultBean;
import com.yingli.framework.exception.MessageException;
import io.swagger.annotations.Api;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/noauth/test/1.0")
@Api(description = "授权相关API", produces = MediaType.APPLICATION_JSON_VALUE)
public class TestController {

    @RequestMapping(value = "/getData", method = RequestMethod.POST)
    public ResultBean<?> getData() {
        System.out.println("---------------");
        return ResultBean.ok();
    }
}
