package com.example.tests;

import com.example.component.DbMessageSource;
import com.example.entity.Message;
import com.example.repository.MessagesRepository;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.FieldError;

import java.util.Locale;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@SuppressWarnings("PMD.AvoidDuplicateLiterals")
class DbMessageSourceTest {

    @Test
    void testMessageEmpty() {
        MessagesRepository repository = mock(MessagesRepository.class);
        DbMessageSource dbMessageSource = new DbMessageSource(repository);

        String[] codes = {};
        String defaultMessage = "some message default";
        String message = dbMessageSource.getMessage(new DefaultMessageSourceResolvable(codes, defaultMessage), Locale.ENGLISH);

        assertThat(message).isEqualTo(defaultMessage);
    }

    @Test
    void testMessage() {
        MessagesRepository repository = mock(MessagesRepository.class);
        DbMessageSource dbMessageSource = new DbMessageSource(repository);

        String[] codes = {};
        String defaultMessage = "some message default";
        String message = dbMessageSource.getMessage(new DefaultMessageSourceResolvable(codes, defaultMessage), Locale.FRANCE);

        assertThat(message).isEqualTo(defaultMessage);
    }

    @Test
    void testMessage2() {
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
    void testMessage3() {
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
    @SuppressFBWarnings("NP_NONNULL_PARAM_VIOLATION")
    void testMessage4() {
        MessagesRepository repository = mock(MessagesRepository.class);
        String key = "key";
        given(repository.findByKeyAndLang(key, "fra")).willReturn(Optional.empty());

        DbMessageSource dbMessageSource = new DbMessageSource(repository);

        String[] codes = {key};
        MessageSourceResolvable resolvable = new DefaultMessageSourceResolvable(codes, (String) null);
        String resolvedMessage = dbMessageSource.getMessage(resolvable, Locale.FRANCE);

        assertThat(resolvedMessage).isEqualTo("[key]");
    }

    @Test
    void testMessage5() {
        MessagesRepository repository = mock(MessagesRepository.class);
        String key = "key";
        given(repository.findByKeyAndLang(key, "fra")).willReturn(Optional.empty());

        DbMessageSource dbMessageSource = new DbMessageSource(repository);

        FieldError resolvable = new FieldError("some object", "some field", "some message");
        String resolvedMessage = dbMessageSource.getMessage(resolvable, Locale.FRANCE);

        assertThat(resolvedMessage).isEqualTo("some message");
    }

}
