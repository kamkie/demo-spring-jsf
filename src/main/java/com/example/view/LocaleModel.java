package com.example.view;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class LocaleModel {

    public Locale getLocale() {
        return LocaleContextHolder.getLocale();
    }

}
