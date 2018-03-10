package com.example.view;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.servlet.LocaleResolver;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
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

    public void onLocaleChange(AjaxBehaviorEvent event) throws IOException {
        System.out.println(event);
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        ec.redirect(((HttpServletRequest) ec.getRequest()).getRequestURI());
    }

}
