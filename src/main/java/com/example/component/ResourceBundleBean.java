package com.example.component;

import com.example.view.LocaleModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;

@Component(value = "msg")
public class ResourceBundleBean extends AbstractMap<String, String> {

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

    @Override
    public Set<Entry<String, String>> entrySet() {
        return Collections.emptySet();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass() || !super.equals(o)) {
            return false;
        }
        ResourceBundleBean that = (ResourceBundleBean) o;
        return Objects.equals(messageSource, that.messageSource) &&
                Objects.equals(localeModel, that.localeModel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), messageSource, localeModel);
    }
}
