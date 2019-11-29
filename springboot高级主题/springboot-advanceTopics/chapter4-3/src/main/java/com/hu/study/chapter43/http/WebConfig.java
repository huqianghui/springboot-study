package com.hu.study.chapter43.http;

import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.util.ClassUtils;
import org.springframework.validation.MessageCodesResolver;
import org.springframework.validation.Validator;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import java.util.List;

@Configuration
@EnableWebMvc
public class WebConfig extends WebMvcConfigurationSupport implements WebMvcConfigurer {


    private static final boolean jackson2Present =
            ClassUtils.isPresent("com.fasterxml.jackson.databind.ObjectMapper", WebMvcConfigurationSupport.class.getClassLoader()) &&
                    ClassUtils.isPresent("com.fasterxml.jackson.core.JsonGenerator", WebMvcConfigurationSupport.class.getClassLoader());

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    CharacterEncodingFilter characterEncodingFilter() {
        CharacterEncodingFilter filter = new CharacterEncodingFilter();
        filter.setEncoding("UTF-8");
        filter.setForceEncoding(true);
        return filter;
    }

    @Bean
    public RosefinchJackson2JsonMessageConverter customJackson2HttpMessageConverter() {
        RosefinchJackson2JsonMessageConverter jsonConverter = new RosefinchJackson2JsonMessageConverter();
        return jsonConverter;
    }


    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(customJackson2HttpMessageConverter());
        addDefaultHttpMessageConverters(converters);
    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {

    }

    @Override
    public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {

    }

    @Override
    public void extendHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {

    }

    @Override
    public Validator getValidator() {
        return null;
    }

    @Override
    public MessageCodesResolver getMessageCodesResolver() {
        return null;
    }

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {

    }

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {

    }

    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {

    }

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {

    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addFormatter(new RosefinchDateFormatter());
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {

    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {

    }

    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {

    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        RosefinchHandlerMethodArgumentResolverComposite handlerMethodArgumentResolverComposite = new RosefinchHandlerMethodArgumentResolverComposite();
        RosefinchRequestResponseBodyMethodProcessor requestResponseBodyMethodProcessor = new RosefinchRequestResponseBodyMethodProcessor(getMessageConverters());
        requestResponseBodyMethodProcessor.setBeanFactory(new DefaultListableBeanFactory());
        handlerMethodArgumentResolverComposite.addResolver(requestResponseBodyMethodProcessor);
        requestResponseBodyMethodProcessor.setMessageConverters(getMessageConverters());
        handlerMethodArgumentResolverComposite.addResolvers(requestResponseBodyMethodProcessor.getDefaultArgumentResolvers());
        argumentResolvers.add(0, handlerMethodArgumentResolverComposite);
    }

    @Override
    public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> returnValueHandlers) {
        RosefinchHandlerMethodReturnValueHandlerComposite handlerMethodReturnValueHandlerComposite = new RosefinchHandlerMethodReturnValueHandlerComposite();
        RosefinchRequestResponseBodyMethodProcessor requestResponseBodyMethodProcessor = new RosefinchRequestResponseBodyMethodProcessor(getMessageConverters());
        handlerMethodReturnValueHandlerComposite.addHandler(requestResponseBodyMethodProcessor);
        requestResponseBodyMethodProcessor.setMessageConverters(getMessageConverters());
        handlerMethodReturnValueHandlerComposite.addHandlers(requestResponseBodyMethodProcessor.getDefaultReturnValueHandlers());
        returnValueHandlers.add(0, handlerMethodReturnValueHandlerComposite);
    }

    @Bean
    public RequestMappingHandlerAdapter requestMappingHandlerAdapter() {
        RequestMappingHandlerAdapter adapter = super.requestMappingHandlerAdapter();
        adapter.setReturnValueHandlers(getReturnValueHandlers());
        adapter.setArgumentResolvers(getArgumentResolvers());
        return adapter;
    }

}