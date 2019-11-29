package com.hu.study.chapter43.http;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractGenericHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by qianghui.hu on 8/9/2017.
 */
public class RosefinchJackson2JsonMessageConverter extends AbstractGenericHttpMessageConverter<Object> {

    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    /**
     * Construct a new {@code GsonHttpMessageConverter}.
     */
    public RosefinchJackson2JsonMessageConverter() {
        super(MediaType.APPLICATION_JSON, new MediaType("application", "*+json"));
        this.setDefaultCharset(DEFAULT_CHARSET);
    }

    public Charset getDefaultCharset() {
        return DEFAULT_CHARSET;
    }

    @Override
    public boolean canRead(Class<?> clazz, MediaType mediaType) {
        return canRead(mediaType);
    }

    @Override
    public boolean canWrite(Class<?> clazz, MediaType mediaType) {
        return canWrite(mediaType);
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        // should not be called, since we override canRead/Write instead
        throw new UnsupportedOperationException();
    }

    @Override
    protected Object readInternal(Class<?> clazz, HttpInputMessage inputMessage)
            throws IOException, HttpMessageNotReadableException {
        throw new RuntimeException("Not Support!");
    }

    public Object read(Type type, Class<?> contextClass, HttpInputMessage inputMessage)
            throws IOException, HttpMessageNotReadableException {

        Charset charset = getCharset(inputMessage.getHeaders());

        JavaType javaType = getJavaType(type, contextClass);

        String json = IOUtils.toString(inputMessage.getBody(), charset);

        if(javaType.getRawClass().getName().equalsIgnoreCase("java.lang.String") ){
            return json;
        }

        Object obj;
        if (javaType.getRawClass() == List.class) {
            obj = JsonBaseUtils.fromJSONAsList(json,
                    javaType.getContentType().getRawClass());
        } else if (javaType.getRawClass() == Set.class) {
            obj = JsonBaseUtils.fromJSONAsSet(json,
                    javaType.getContentType().getRawClass());
        } else if (javaType.getRawClass() == Map.class) {
            obj = JsonBaseUtils.fromJSONAsMap(
                    json,
                    javaType.getRawClass(),
                    javaType.getKeyType().getRawClass(),
                    javaType.getContentType().getRawClass(), javaType);
        } else {
            obj = JsonBaseUtils.fromJSON(json, javaType.getRawClass());
        }
        return obj;
    }

    private Charset getCharset(HttpHeaders headers) {
        return DEFAULT_CHARSET;
    }

    @Override
    protected void writeInternal(Object o, Type type, HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException {

        Charset charset = getCharset(outputMessage.getHeaders());
        OutputStreamWriter writer = new OutputStreamWriter(outputMessage.getBody(), charset);
        try {
            String json = JsonBaseUtils.toJSON(o);
            if (json == null) {
                json = "";
            }
            writer.write(json);
            writer.close();
        } catch (Exception ex) {
            throw new HttpMessageNotWritableException("Could not write JSON: " + ex.getMessage(), ex);
        }
    }

    protected JavaType getJavaType(Type type, Class<?> contextClass) {
        return TypeFactory.defaultInstance().constructType(type, contextClass);
    }


}
