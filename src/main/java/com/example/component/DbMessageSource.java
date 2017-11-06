package com.example.component;

import com.example.annotation.TimedMethod;
import com.example.entity.Message;
import com.example.repository.MessagesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.annotation.Primary;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Optional;

@TimedMethod
@Primary
@Component("messageSource")
public class DbMessageSource implements MessageSource {

    private final MessagesRepository messagesRepository;

    @Autowired
    public DbMessageSource(MessagesRepository messagesRepository) {
        this.messagesRepository = messagesRepository;
    }

    @Override
    @Cacheable(cacheNames = "messageSource", key = "#code + '|' + #defaultMessage + '|' + #locale")
    public String getMessage(String code, @Nullable Object[] args, @Nullable String defaultMessage, Locale locale) {
        final String resolvedDefaultMessage = Optional.ofNullable(defaultMessage).orElse(code);
        final String message = messagesRepository.findByKeyAndLang(code, locale.getISO3Language())
                .map(Message::getText)
                .orElse(resolvedDefaultMessage);

        return String.format(message, args);
    }

    @Override
    public String getMessage(String code, @Nullable Object[] args, Locale locale) {
        return getMessage(code, args, null, locale);
    }

    @Override
    public String getMessage(MessageSourceResolvable resolvable, Locale locale) {
        final Object[] args = resolvable.getArguments();
        String defaultMessage = resolvable.getDefaultMessage();
        String[] codes = resolvable.getCodes();

        if (codes != null) {
            for (String code : codes) {
                if (defaultMessage == null) {
                    defaultMessage = code;
                }
                final String message = getMessage(code, args, defaultMessage, locale);
                if (!code.equals(message)) {
                    return message;
                }
            }
        }

        return defaultMessage == null ? "" : defaultMessage;
    }

}
