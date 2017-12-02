package com.example.view;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

@Component
@RequestScope
public class LocaleModel {

    private final LocaleResolver localeResolver;
    private final HttpServletRequest httpServletRequest;

    @Autowired
    public LocaleModel(LocaleResolver localeResolver, HttpServletRequest httpServletRequest) {
        this.localeResolver = localeResolver;
        this.httpServletRequest = httpServletRequest;
    }

    public Locale getLocale() {
        return localeResolver.resolveLocale(httpServletRequest);
    }

    public void setLocale(Locale locale) {
        localeResolver.setLocale(httpServletRequest, null, locale);
    }

    public String getLocaleCode() {
        return getLocale().getLanguage();
    }

    public void setLocaleCode(String locale) {
        setLocale(Locale.forLanguageTag(locale));
    }

}
