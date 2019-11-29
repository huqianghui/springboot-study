package com.hu.study.chapter43.http;

import org.springframework.format.Formatter;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

@Component
public class RosefinchDateFormatter implements Formatter<Date> {

    @Override
    public Date parse(String text, Locale locale) throws ParseException {
        return JsonBaseUtils.parseDate(text);
    }

    @Override
    public String print(Date date, Locale locale) {
        return JsonBaseUtils.formatDate(date);
    }
}
