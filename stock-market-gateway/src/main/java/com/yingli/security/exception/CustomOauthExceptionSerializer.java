package com.yingli.security.exception;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;

/**
 * CustomOauthException的序列化实现
 */
public class CustomOauthExceptionSerializer extends StdSerializer<CustomOauthException> {
    public CustomOauthExceptionSerializer() {
        super(CustomOauthException.class);
    }

    @Override
    public void serialize(CustomOauthException e, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField(AuthExceptionConsts.ERROR, String.valueOf(e.getHttpErrorCode()));
        jsonGenerator.writeStringField(AuthExceptionConsts.MESSAGE, e.getMessage());
        jsonGenerator.writeStringField(AuthExceptionConsts.PATH, request.getServletPath());
        jsonGenerator.writeStringField(AuthExceptionConsts.TIMESTAMP, String.valueOf(new Date().getTime()));
        if (e.getAdditionalInformation() != null) {
            e.getAdditionalInformation().forEach((k, v) -> {
                        try {
                            jsonGenerator.writeStringField(k, v);
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
            );
        }
        jsonGenerator.writeEndObject();
    }
}
