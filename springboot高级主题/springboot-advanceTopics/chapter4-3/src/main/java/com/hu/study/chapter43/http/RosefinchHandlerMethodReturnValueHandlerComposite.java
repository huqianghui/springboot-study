package com.hu.study.chapter43.http;

import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.AsyncHandlerMethodReturnValueHandler;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.HandlerMethodReturnValueHandlerComposite;
import org.springframework.web.method.support.ModelAndViewContainer;


public class RosefinchHandlerMethodReturnValueHandlerComposite extends HandlerMethodReturnValueHandlerComposite {

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType,
                                  ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {

        HandlerMethodReturnValueHandler handler = selectHandler(returnValue, returnType, webRequest);
        if (handler == null) {
            throw new IllegalArgumentException("Unknown return value type: " + returnType.getParameterType().getName());
        }
        handler.handleReturnValue(returnValue, returnType, mavContainer, webRequest);
    }

    private HandlerMethodReturnValueHandler selectHandler(Object value, MethodParameter returnType, NativeWebRequest webRequest) {
        boolean isAsyncValue = false;
        for (HandlerMethodReturnValueHandler handler : this.getHandlers()) {
            if ((isAsyncValue && !(handler instanceof AsyncHandlerMethodReturnValueHandler))) {
                continue;
            }
            if (handler.supportsReturnType(returnType)) {
                return handler;
            }
        }
        return null;
    }
}
