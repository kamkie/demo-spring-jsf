package com.example.component;

import com.example.annotation.TimedMethod;
import com.example.entity.Message;
import com.example.repository.MessagesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.annotation.Primary;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

@Primary
@TimedMethod
@RequiredArgsConstructor
@Component("messageSource")
public class DbMessageSource implements MessageSource {

    private final MessagesRepository messagesRepository;

    @Override
    @Nullable
    public String getMessage(String code, @Nullable Object[] args, @Nullable String defaultMessage, @Nullable Locale locale) {
        String iso3Language = Optional.ofNullable(locale).orElse(Locale.ENGLISH).getISO3Language();
        return messagesRepository.findByKeyAndLang(code, iso3Language)
                .map(Message::getText)
                .or(() -> Optional.ofNullable(defaultMessage))
                .map(msg -> String.format(msg, args))
                .orElse(null);
    }

    @Override
    public String getMessage(String code, @Nullable Object[] args, @Nullable Locale locale) {
        return Optional.ofNullable(getMessage(code, args, null, locale))
                .orElse(code);
    }

    @Override
    public String getMessage(MessageSourceResolvable resolvable, @Nullable Locale locale) {
        var codes = resolvable.getCodes();
        var arguments = resolvable.getArguments();
        var defaultMessage = resolvable.getDefaultMessage();

        return Optional.ofNullable(codes)
                .flatMap(c -> tryFormatMessage(arguments, c, locale))
                .orElseGet(() -> formatDefaultMessage(codes, arguments, defaultMessage));
    }

    private String formatDefaultMessage(@Nullable String[] codes, @Nullable Object[] args, @Nullable String defaultMessage) {
        return Optional.ofNullable(defaultMessage)
                .map(message -> String.format(message, args))
                .orElseGet(() -> Arrays.toString(codes));
    }

    private Optional<String> tryFormatMessage(@Nullable Object[] args, String[] codes, Locale locale) {
        return Arrays.stream(codes)
                .map(code -> getMessage(code, args, null, locale))
                .filter(Objects::nonNull)
                .findFirst();
    }

}
