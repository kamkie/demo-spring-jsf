package com.example.view;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.LocaleResolver;

import javax.faces.bean.ManagedBean;
import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

@Component
@ManagedBean
public class LocaleModel {

    private final LocaleResolver localeResolver;

    @Autowired
    public LocaleModel(LocaleResolver localeResolver) {
        this.localeResolver = localeResolver;
    }

    public Locale getLocale() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest servletRequest = ((ServletRequestAttributes) requestAttributes).getRequest();
        return localeResolver.resolveLocale(servletRequest);
    }

    public void setLocale(Locale locale) {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest servletRequest = ((ServletRequestAttributes) requestAttributes).getRequest();
        localeResolver.setLocale(servletRequest, null, locale);
    }

    public String getLocaleCode() {
        return getLocale().getLanguage();
    }

    public void setLocaleCode(String locale) {
        setLocale(Locale.forLanguageTag(locale));
    }

}
