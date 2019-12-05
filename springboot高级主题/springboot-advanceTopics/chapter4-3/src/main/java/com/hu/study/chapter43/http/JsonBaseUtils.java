package com.hu.study.chapter43.http;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JsonBaseUtils {

    private static ObjectMapper objectMapper;

    private static final String CLASS_FULL_NAME = "com.hu.study.chapter43.http.JsonBaseUtils";

    private static Class CLASS_JSON_UTILS;

    static {
        initMapper();
    }

    private static void initMapper() {
        objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.setDateFormat(new ISO8601WithoutTimeZoneDateFormat());
        objectMapper.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
        objectMapper.configure(MapperFeature.REQUIRE_SETTERS_FOR_GETTERS, true);
        objectMapper.configure(MapperFeature.CAN_OVERRIDE_ACCESS_MODIFIERS, false);
        objectMapper.configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true);
        objectMapper.configure(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN, true);
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.UPPER_CAMEL_CASE);
        objectMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        objectMapper.registerModules(new JavaTimeModule());
    }

    public static String toJSON(Object object)
            throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, JsonProcessingException, ClassNotFoundException {
        return toJSON(object, true, false);
    }

    public static Date parseDate(String str) {
        if (StringUtils.isEmpty(str)) {
            return null;
        }
        try {
            return ISO8601WithoutTimeZoneUtils.parse(str, new ParsePosition(0));
        } catch (ParseException e) {
            throw new RuntimeException("Invalid json date format,value:" + str, e);
        }
    }

    public static String formatDate(Date date) {
        if (date == null) {
            return null;
        }
        return ISO8601WithoutTimeZoneUtils.format(date);
    }

    public static Class getJsonUtilsClass() {
        if (CLASS_JSON_UTILS != null) {
            return CLASS_JSON_UTILS;
        } else {
            try {
                CLASS_JSON_UTILS = Class.forName(CLASS_FULL_NAME);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            return CLASS_JSON_UTILS;
        }
    }

    public static String toJSON(Object object, boolean trimEmptyJson, boolean withNullValue) {
        try {
            if (object == null) {
                return null;
            }

            String jsonString;
            jsonString = objectMapper.writeValueAsString(object);
            if (trimEmptyJson && ("[]".equals(jsonString) || "{}".equals(jsonString))) {
                jsonString = null;
            }
            return jsonString;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T fromJSON(String json, Class<T> clazz) {
        try {
            if (json == null) {
                return null;
            }

            Object object = objectMapper.readValue(json, clazz);
            return (T) object;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static <T> List<T> fromJSONAsList(String json, Class<T> elementClazz) {
        return fromJSONAsList(json, List.class, elementClazz);
    }

    public static <T> List<T> fromJSONAsList(String json, Class<? extends List> listClazz, Class<T> elementClazz) {
        if (json == null) {
            return null;
        }
        try {
            JavaType type = objectMapper.getTypeFactory().constructCollectionType(listClazz, elementClazz);
            List<T> list = (List<T>) objectMapper.readValue(json, type);
            return list;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> Set<T> fromJSONAsSet(String json, Class<T> elementClazz) {
        return fromJSONAsSet(json, Set.class, elementClazz);
    }

    public static <T> Set<T> fromJSONAsSet(String json, Class<? extends Set> setClazz, Class<T> elementClazz) {
        if (json == null) {
            return null;
        }
        try {
            JavaType type = objectMapper.getTypeFactory().constructCollectionType(setClazz, elementClazz);
            Set<T> set = (Set<T>) objectMapper.readValue(json, type);
            return set;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <K, V> Map<K, V> fromJSONAsMap(String json, Class mapClazz, Class<K> keyClazz,
                                                 Class<V> valueClazz, JavaType originalType) {
        if (json == null) {
            return null;
        }
        try {
            JavaType valueType;
            JavaType keyType = objectMapper.getTypeFactory().constructType(keyClazz);
            if (List.class.isAssignableFrom(valueClazz)) {
                Class elementClazz = originalType.getContentType().getContentType().getRawClass();
                JavaType elementType = objectMapper.getTypeFactory().constructType(elementClazz);
                valueType = CollectionType.construct(valueClazz, elementType);
            } else {
                valueType = objectMapper.getTypeFactory().constructType(valueClazz);
            }
            JavaType type = MapType.construct(mapClazz, keyType, valueType);
            Map<K, V> map = objectMapper.readValue(json, type);
            return map;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
