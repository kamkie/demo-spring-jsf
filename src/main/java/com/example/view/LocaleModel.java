package com.example.view;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

@Component
public class LocaleModel {

    private final LocaleResolver localeResolver;

    @Autowired
    public LocaleModel(LocaleResolver localeResolver) {
        this.localeResolver = localeResolver;
    }

    public Locale getLocale() {
        return localeResolver.resolveLocale(getHttpServletRequest());
    }

    public void setLocale(Locale locale) {
        localeResolver.setLocale(getHttpServletRequest(), null, locale);
    }

    public String getLocaleCode() {
        return getLocale().getLanguage();
    }

    public void setLocaleCode(String locale) {
        setLocale(Locale.forLanguageTag(locale));
    }

    private HttpServletRequest getHttpServletRequest() {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (servletRequestAttributes != null) {
            return servletRequestAttributes.getRequest();
        }
        throw new IllegalArgumentException();
    }

}
