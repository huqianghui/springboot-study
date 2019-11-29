package com.hu.study.chapter43.http;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;
import org.springframework.http.*;
import org.springframework.http.converter.GenericHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.accept.PathExtensionContentNegotiationStrategy;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.annotation.*;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.mvc.method.annotation.*;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.lang.reflect.Type;
import java.util.*;

public class RosefinchRequestResponseBodyMethodProcessor extends RequestResponseBodyMethodProcessor {

    /* Extensions associated with the built-in message converters */
    private static final Set<String> WHITELISTED_EXTENSIONS = new HashSet<String>(Arrays.asList(
            "txt", "text", "yml", "properties", "csv",
            "json", "xml", "atom", "rss",
            "png", "jpe", "jpeg", "jpg", "gif", "wbmp", "bmp"));

    private static final Set<String> WHITELISTED_MEDIA_BASE_TYPES = new HashSet<String>(
            Arrays.asList("audio", "image", "video"));

    private static final UrlPathHelper DECODING_URL_PATH_HELPER = new UrlPathHelper();

    private static final UrlPathHelper RAW_URL_PATH_HELPER = new UrlPathHelper();

    private static final Object NO_VALUE = new Object();

    private static final Set<HttpMethod> SUPPORTED_METHODS =
            EnumSet.of(HttpMethod.POST, HttpMethod.PUT, HttpMethod.PATCH);

    private final ContentNegotiationManager contentNegotiationManager;

    private final PathExtensionContentNegotiationStrategy pathStrategy;

    List<HttpMessageConverter<?>> messageConverters;

    private final RequestResponseBodyAdviceChain advice;

    private RequestResponseBodyAdviceChain getCustomAdvice() {
        return this.advice;
    }

    private List requestResponseBodyAdvice = new ArrayList() {{
        add(new JsonViewRequestBodyAdvice());
    }};

    private ConfigurableBeanFactory beanFactory;

    public List<HttpMessageConverter<?>> getMessageConverters() {
        return this.messageConverters;
    }

    public void setMessageConverters(List<HttpMessageConverter<?>> messageConverters) {
        this.messageConverters = messageConverters;
    }


    private final Set<String> safeExtensions = new HashSet<String>();

    private JsonViewResponseBodyAdvice responseBodyAdvice = new JsonViewResponseBodyAdvice();


    private static PathExtensionContentNegotiationStrategy initPathStrategy(ContentNegotiationManager manager) {
        Class<PathExtensionContentNegotiationStrategy> clazz = PathExtensionContentNegotiationStrategy.class;
        PathExtensionContentNegotiationStrategy strategy = manager.getStrategy(clazz);
        return (strategy != null ? strategy : new PathExtensionContentNegotiationStrategy());
    }

    /**
     * Return the more specific of the acceptable and the producible media types
     * with the q-value of the former.
     */
    private MediaType getMostSpecificMediaType(MediaType acceptType, MediaType produceType) {
        MediaType produceTypeToUse = produceType.copyQualityValue(acceptType);
        return (MediaType.SPECIFICITY_COMPARATOR.compare(acceptType, produceTypeToUse) <= 0 ? acceptType : produceTypeToUse);
    }

    static {
        RAW_URL_PATH_HELPER.setRemoveSemicolonContent(false);
        RAW_URL_PATH_HELPER.setUrlDecode(false);
    }

    /**
     * Basic constructor with converters only. Suitable for resolving
     * {@code @RequestBody}. For handling {@code @ResponseBody} consider also
     * providing a {@code ContentNegotiationManager}.
     */
    public RosefinchRequestResponseBodyMethodProcessor(List<HttpMessageConverter<?>> converters) {
        super(converters);
        this.contentNegotiationManager = new ContentNegotiationManager();
        this.pathStrategy = initPathStrategy(this.contentNegotiationManager);
        this.safeExtensions.addAll(this.contentNegotiationManager.getAllFileExtensions());
        this.advice = new RequestResponseBodyAdviceChain(requestResponseBodyAdvice);
    }


    private boolean isNativeMessageConvertIgnoreUrl(String requestUrl) {

        return false;

    }

    @Override
    public <T> void writeWithMessageConverters(T value, MethodParameter returnType,
                                               ServletServerHttpRequest inputMessage, ServletServerHttpResponse outputMessage)
            throws IOException, HttpMediaTypeNotAcceptableException, HttpMessageNotWritableException {
        super.writeWithMessageConverters(value, returnType, inputMessage, outputMessage);
    }

    /**
     * Return the configured {@link RequestBodyAdvice} and
     * {@link RequestBodyAdvice} where each instance may be wrapped as a
     * {@link org.springframework.web.method.ControllerAdviceBean ControllerAdviceBean}.
     */
    protected JsonViewResponseBodyAdvice getJsonViewResponseBodyAdvice() {
        return this.responseBodyAdvice;
    }

    /**
     * Check if the path has a file extension and whether the extension is
     * either {@link #WHITELISTED_EXTENSIONS whitelisted} or explicitly
     * {@link ContentNegotiationManager#getAllFileExtensions() registered}.
     * If not, and the status is in the 2xx range, a 'Content-Disposition'
     * header with a safe attachment file name ("f.txt") is added to prevent
     * RFD exploits.
     */
    private void addContentDispositionHeader(ServletServerHttpRequest request, ServletServerHttpResponse response) {
        HttpHeaders headers = response.getHeaders();
        if (headers.containsKey(HttpHeaders.CONTENT_DISPOSITION)) {
            return;
        }

        try {
            int status = response.getServletResponse().getStatus();
            if (status < 200 || status > 299) {
                return;
            }
        } catch (Throwable ex) {
            // ignore
        }

        HttpServletRequest servletRequest = request.getServletRequest();
        String requestUri = RAW_URL_PATH_HELPER.getOriginatingRequestUri(servletRequest);

        int index = requestUri.lastIndexOf('/') + 1;
        String filename = requestUri.substring(index);
        String pathParams = "";

        index = filename.indexOf(';');
        if (index != -1) {
            pathParams = filename.substring(index);
            filename = filename.substring(0, index);
        }

        filename = DECODING_URL_PATH_HELPER.decodeRequestString(servletRequest, filename);
        String ext = StringUtils.getFilenameExtension(filename);

        pathParams = DECODING_URL_PATH_HELPER.decodeRequestString(servletRequest, pathParams);
        String extInPathParams = StringUtils.getFilenameExtension(pathParams);

        if (!safeExtension(servletRequest, ext) || !safeExtension(servletRequest, extInPathParams)) {
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline;filename=f.txt");
        }
    }


    @SuppressWarnings("unchecked")
    private boolean safeExtension(HttpServletRequest request, String extension) {
        if (!StringUtils.hasText(extension)) {
            return true;
        }
        extension = extension.toLowerCase(Locale.ENGLISH);
        if (this.safeExtensions.contains(extension)) {
            return true;
        }
        String pattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        if (pattern != null && pattern.endsWith("." + extension)) {
            return true;
        }
        if (extension.equals("html")) {
            String name = HandlerMapping.PRODUCIBLE_MEDIA_TYPES_ATTRIBUTE;
            Set<MediaType> mediaTypes = (Set<MediaType>) request.getAttribute(name);
            if (!CollectionUtils.isEmpty(mediaTypes) && mediaTypes.contains(MediaType.TEXT_HTML)) {
                return true;
            }
        }
        return safeMediaTypesForExtension(extension);
    }

    private boolean safeMediaTypesForExtension(String extension) {
        List<MediaType> mediaTypes = null;
        try {
            mediaTypes = this.pathStrategy.resolveMediaTypeKey(null, extension);
        } catch (HttpMediaTypeNotAcceptableException ex) {
            // Ignore
        }
        if (CollectionUtils.isEmpty(mediaTypes)) {
            return false;
        }
        for (MediaType mediaType : mediaTypes) {
            if (!safeMediaType(mediaType)) {
                return false;
            }
        }
        return true;
    }


    /**
     * Return the generic type of the {@code returnType} (or of the nested type
     * if it is an {@link HttpEntity}).
     */
    private Type getGenericType(MethodParameter returnType) {
        if (HttpEntity.class.isAssignableFrom(returnType.getParameterType())) {
            return ResolvableType.forType(returnType.getGenericParameterType()).getGeneric(0).getType();
        } else {
            return returnType.getGenericParameterType();
        }
    }

    private boolean safeMediaType(MediaType mediaType) {
        return (WHITELISTED_MEDIA_BASE_TYPES.contains(mediaType.getType()) ||
                mediaType.getSubtype().endsWith("+xml"));
    }


    private List<MediaType> getAcceptableMediaTypes(HttpServletRequest request) throws HttpMediaTypeNotAcceptableException {
        List<MediaType> mediaTypes = this.contentNegotiationManager.resolveMediaTypes(new ServletWebRequest(request));
        return (mediaTypes.isEmpty() ? Collections.singletonList(MediaType.ALL) : mediaTypes);
    }


    /**
     * Return the list of return value handlers to use including built-in and
     */
    public List<HandlerMethodReturnValueHandler> getDefaultReturnValueHandlers() {
        List<HandlerMethodReturnValueHandler> handlers = new ArrayList<HandlerMethodReturnValueHandler>();

        // Single-purpose return value types
        handlers.add(new ModelAndViewMethodReturnValueHandler());
        handlers.add(new ModelMethodProcessor());
        handlers.add(new ViewMethodReturnValueHandler());
        handlers.add(new ResponseBodyEmitterReturnValueHandler(getMessageConverters()));
        handlers.add(new StreamingResponseBodyReturnValueHandler());
        handlers.add(new HttpEntityMethodProcessor(getMessageConverters(),
                this.contentNegotiationManager, this.requestResponseBodyAdvice));
        handlers.add(new HttpHeadersReturnValueHandler());
        handlers.add(new CallableMethodReturnValueHandler());
        handlers.add(new DeferredResultMethodReturnValueHandler());

        // Annotation-based return value types
        handlers.add(new ModelAttributeMethodProcessor(false));
        handlers.add(new RequestResponseBodyMethodProcessor(getMessageConverters(),
                this.contentNegotiationManager, this.requestResponseBodyAdvice));

        // Multi-purpose return value types
        handlers.add(new ViewNameMethodReturnValueHandler());
        handlers.add(new MapMethodProcessor());
        return handlers;
    }

    @Override
    public <T> Object readWithMessageConverters(HttpInputMessage inputMessage, MethodParameter parameter,
                                                Type targetType) throws IOException, HttpMediaTypeNotSupportedException, HttpMessageNotReadableException {
        return super.readWithMessageConverters(inputMessage, parameter, targetType);
    }

    private <T> Object readWithDefaultConverters(String uri, Class<?> contextClass, Type targetType, Class<T> targetClass, MediaType contentType,
                                                 HttpInputMessage inputMessage, MethodParameter parameter) throws IOException {
        Object body = NO_VALUE;
        for (HttpMessageConverter<?> converter : this.messageConverters) {
            if (isNativeMessageConvertIgnoreUrl(uri)) {
                continue;
            }
            body = readMessage(contextClass, converter, targetType, targetClass, contentType, inputMessage, parameter);
            if(body != NO_VALUE) {
                return body;
            }
        }
        return body;
    }

    private <T> Object readMessage(Class<?> contextClass, HttpMessageConverter<?> converter, Type targetType, Class<T> targetClass, MediaType contentType,
                                   HttpInputMessage inputMessage, MethodParameter parameter) throws IOException {
        Object body = NO_VALUE;
        Class<HttpMessageConverter<?>> converterType = (Class<HttpMessageConverter<?>>) converter.getClass();
        if (converter instanceof GenericHttpMessageConverter) {
            GenericHttpMessageConverter<?> genericConverter = (GenericHttpMessageConverter<?>) converter;
            if (genericConverter.canRead(targetType, contextClass, contentType)) {
                if (inputMessage.getBody() != null) {
                    inputMessage = getCustomAdvice().beforeBodyRead(inputMessage, parameter, targetType, converterType);
                    body = genericConverter.read(targetType, contextClass, inputMessage);
                    body = getCustomAdvice().afterBodyRead(body, inputMessage, parameter, targetType, converterType);
                }
                else {
                    body = getCustomAdvice().handleEmptyBody(null, inputMessage, parameter, targetType, converterType);
                }
            }
        }
        else if (targetClass != null) {
            if (converter.canRead(targetClass, contentType)) {
                if (inputMessage.getBody() != null) {
                    inputMessage = getCustomAdvice().beforeBodyRead(inputMessage, parameter, targetType, converterType);
                    body = ((HttpMessageConverter<T>) converter).read(targetClass, inputMessage);
                    body = getCustomAdvice().afterBodyRead(body, inputMessage, parameter, targetType, converterType);
                }
                else {
                    body = getCustomAdvice().handleEmptyBody(null, inputMessage, parameter, targetType, converterType);
                }
            }
        }
        return body;
    }

    public List<HandlerMethodArgumentResolver> getDefaultArgumentResolvers() {
        List<HandlerMethodArgumentResolver> resolvers = new ArrayList<HandlerMethodArgumentResolver>();

        // Annotation-based argument resolution
        resolvers.add(new RequestParamMethodArgumentResolver(getBeanFactory(), false));
        resolvers.add(new RequestParamMapMethodArgumentResolver());
        resolvers.add(new PathVariableMethodArgumentResolver());
        resolvers.add(new PathVariableMapMethodArgumentResolver());
        resolvers.add(new MatrixVariableMethodArgumentResolver());
        resolvers.add(new MatrixVariableMapMethodArgumentResolver());
        resolvers.add(new ServletModelAttributeMethodProcessor(false));
        resolvers.add(new RequestResponseBodyMethodProcessor(getMessageConverters(), this.requestResponseBodyAdvice));
        resolvers.add(new RequestPartMethodArgumentResolver(getMessageConverters(), this.requestResponseBodyAdvice));
        resolvers.add(new RequestHeaderMethodArgumentResolver(getBeanFactory()));
        resolvers.add(new RequestHeaderMapMethodArgumentResolver());
        resolvers.add(new ServletCookieValueMethodArgumentResolver(getBeanFactory()));
        resolvers.add(new ExpressionValueMethodArgumentResolver(getBeanFactory()));
        resolvers.add(new SessionAttributeMethodArgumentResolver());
        resolvers.add(new RequestAttributeMethodArgumentResolver());

        // Type-based argument resolution
        resolvers.add(new ServletRequestMethodArgumentResolver());
        resolvers.add(new ServletResponseMethodArgumentResolver());
        resolvers.add(new HttpEntityMethodProcessor(getMessageConverters(), this.requestResponseBodyAdvice));
        resolvers.add(new RedirectAttributesMethodArgumentResolver());
        resolvers.add(new ModelMethodProcessor());
        resolvers.add(new MapMethodProcessor());
        resolvers.add(new ErrorsMethodArgumentResolver());
        resolvers.add(new SessionStatusMethodArgumentResolver());
        resolvers.add(new UriComponentsBuilderMethodArgumentResolver());

        // Catch-all
        resolvers.add(new RequestParamMethodArgumentResolver(getBeanFactory(), true));
        resolvers.add(new ServletModelAttributeMethodProcessor(true));

        return resolvers;
    }

    public ConfigurableBeanFactory getBeanFactory() {
        return this.beanFactory;
    }

    public void setBeanFactory(BeanFactory beanFactory) {
        if (beanFactory instanceof ConfigurableBeanFactory) {
            this.beanFactory = (ConfigurableBeanFactory) beanFactory;
        }
    }

    private static class EmptyBodyCheckingHttpInputMessage implements HttpInputMessage {

        private final HttpHeaders headers;

        private final InputStream body;

        private final HttpMethod method;


        public EmptyBodyCheckingHttpInputMessage(HttpInputMessage inputMessage) throws IOException {
            this.headers = inputMessage.getHeaders();
            InputStream inputStream = inputMessage.getBody();
            if (inputStream == null) {
                this.body = null;
            }
            else if (inputStream.markSupported()) {
                inputStream.mark(1);
                this.body = (inputStream.read() != -1 ? inputStream : null);
                inputStream.reset();
            }
            else {
                PushbackInputStream pushbackInputStream = new PushbackInputStream(inputStream);
                int b = pushbackInputStream.read();
                if (b == -1) {
                    this.body = null;
                }
                else {
                    this.body = pushbackInputStream;
                    pushbackInputStream.unread(b);
                }
            }
            this.method = ((HttpRequest) inputMessage).getMethod();
        }

        @Override
        public HttpHeaders getHeaders() {
            return this.headers;
        }

        @Override
        public InputStream getBody() throws IOException {
            return this.body;
        }

        public HttpMethod getMethod() {
            return this.method;
        }
    }
}
