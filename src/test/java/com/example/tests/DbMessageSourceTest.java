package com.example.tests;

import com.example.component.DbMessageSource;
import com.example.entity.Message;
import com.example.repository.MessagesRepository;
import org.junit.Test;
import org.springframework.context.support.DefaultMessageSourceResolvable;

import java.util.Locale;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public class DbMessageSourceTest {

    @Test
    public void getMessageEmpty() throws Exception {
        MessagesRepository repository = mock(MessagesRepository.class);
        DbMessageSource dbMessageSource = new DbMessageSource(repository);

        String[] codes = {};
        String defaultMessage = "some message default";
        String message = dbMessageSource.getMessage(new DefaultMessageSourceResolvable(codes, defaultMessage), Locale.ENGLISH);

        assertThat(message).isEqualTo(defaultMessage);
    }

    @Test
    public void getMessage() throws Exception {
        MessagesRepository repository = mock(MessagesRepository.class);
        DbMessageSource dbMessageSource = new DbMessageSource(repository);

        String[] codes = {};
        String defaultMessage = "some message default";
        String message = dbMessageSource.getMessage(new DefaultMessageSourceResolvable(codes, defaultMessage), Locale.FRANCE);

        assertThat(message).isEqualTo(defaultMessage);
    }

    @Test
    public void getMessage2() throws Exception {
        MessagesRepository repository = mock(MessagesRepository.class);
        String key = "key";
        String lang = "eng";
        Message message = new Message(1L, lang, key, "text");
        given(repository.findByKeyAndLang(key, lang)).willReturn(Optional.of(message));

        DbMessageSource dbMessageSource = new DbMessageSource(repository);

        String[] codes = {key};
        String defaultMessage = "some message default";
        String resolvedMessage = dbMessageSource.getMessage(new DefaultMessageSourceResolvable(codes, defaultMessage), Locale.ENGLISH);

        assertThat(resolvedMessage).isEqualTo("text");
    }

    @Test
    public void getMessage3() throws Exception {
        MessagesRepository repository = mock(MessagesRepository.class);
        String key = "key";
        String lang = "fra";
        given(repository.findByKeyAndLang(key, lang)).willReturn(Optional.empty());

        DbMessageSource dbMessageSource = new DbMessageSource(repository);

        String[] codes = {key};
        String defaultMessage = "some message default";
        String resolvedMessage = dbMessageSource.getMessage(new DefaultMessageSourceResolvable(codes, defaultMessage), Locale.FRANCE);

        assertThat(resolvedMessage).isEqualTo(defaultMessage);
    }

    @Test
    public void getMessage4() throws Exception {
        MessagesRepository repository = mock(MessagesRepository.class);
        String key = "key";
        given(repository.findByKeyAndLang(key, "fra")).willReturn(Optional.empty());

        DbMessageSource dbMessageSource = new DbMessageSource(repository);

        String[] codes = {key};
        String resolvedMessage = dbMessageSource.getMessage(new DefaultMessageSourceResolvable(codes, (String) null), Locale.FRANCE);

        assertThat(resolvedMessage).isEqualTo(key);
    }

}
