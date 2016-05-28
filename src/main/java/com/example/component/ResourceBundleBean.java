package com.example.component;

import com.example.view.LocaleModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component(value = "msg")
public class ResourceBundleBean extends HashMap<String, String> {

    private final MessageSource messageSource;
    private final LocaleModel localeModel;

    @Autowired
    public ResourceBundleBean(LocaleModel localeModel, MessageSource messageSource) {
        this.localeModel = localeModel;
        this.messageSource = messageSource;
    }

    @Override
    public String get(Object key) {
        return messageSource.getMessage(key.toString(), null, localeModel.getLocale());
    }
}
