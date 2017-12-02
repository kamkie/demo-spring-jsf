package com.example.component;

import com.example.annotation.TimedMethod;
import com.example.entity.Message;
import com.example.repository.MessagesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.annotation.Primary;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

@Primary
@TimedMethod
@Component("messageSource")
public class DbMessageSource implements MessageSource {

    private final MessagesRepository messagesRepository;

    @Autowired
    public DbMessageSource(MessagesRepository messagesRepository) {
        this.messagesRepository = messagesRepository;
    }

    @Override
    public String getMessage(String code, @Nullable Object[] args, @Nullable String defaultMessage, Locale locale) {
        return messagesRepository.findByKeyAndLang(code, locale.getISO3Language())
                .map(Message::getText)
                .or(() -> Optional.ofNullable(defaultMessage))
                .map(msg -> String.format(msg, args))
                .orElse(null);
    }

    @Override
    public String getMessage(String code, @Nullable Object[] args, Locale locale) {
        return Optional.ofNullable(getMessage(code, args, null, locale))
                .orElse(code);
    }

    @Override
    public String getMessage(MessageSourceResolvable resolvable, Locale locale) {
        String[] codes = resolvable.getCodes();
        Object[] arguments = resolvable.getArguments();
        String defaultMessage = resolvable.getDefaultMessage();

        if (codes != null) {
            for (String code : codes) {
                String message = getMessage(code, arguments, null, locale);
                if (message != null) {
                    return message;
                }
            }
        }

        if (defaultMessage != null) {
            return String.format(defaultMessage, arguments);
        }

        return Arrays.toString(codes);
    }

}
