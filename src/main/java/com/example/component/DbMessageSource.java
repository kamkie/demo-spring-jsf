package com.example.component;

import com.example.annotation.Timed;
import com.example.entity.Message;
import com.example.repository.MessagesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Optional;

@Timed
@Primary
@Component("messageSource")
public class DbMessageSource implements MessageSource {

    private final MessagesRepository messagesRepository;

    @Autowired
    public DbMessageSource(MessagesRepository messagesRepository) {
        this.messagesRepository = messagesRepository;
    }

    @Override
    public String getMessage(String code, Object[] args, String defaultMessage, Locale locale) {
        final Optional<Message> message = messagesRepository.findByKeyAndLang(code, locale.getISO3Language());
        final String localizedMessage = message.map(Message::getText)
                .orElse(Optional.ofNullable(defaultMessage).orElse(code));

        return String.format(localizedMessage, args);
    }

    @Override
    public String getMessage(String code, Object[] args, Locale locale) {
        return getMessage(code, args, null, locale);
    }

    @Override
    public String getMessage(MessageSourceResolvable resolvable, Locale locale) {
        final Object[] args = resolvable.getArguments();
        final String defaultMessage = resolvable.getDefaultMessage();
        for (String code : resolvable.getCodes()) {
            final Optional<Message> message = messagesRepository.findByKeyAndLang(code, locale.getISO3Language());
            if (message.isPresent()) {
                return String.format(message.get().getText(), args);
            }
        }
        return String.format(defaultMessage, args);
    }

}
