package com.example.extension;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

class SeleniumModeTest {

    @Test
    void defaultsToHost() {
        assertThat(SeleniumMode.resolve(null, null)).isEqualTo(SeleniumMode.HOST);
    }

    @Test
    void readsEnvironmentValue() {
        assertThat(SeleniumMode.resolve(null, "container")).isEqualTo(SeleniumMode.CONTAINER);
    }

    @Test
    void propertyOverridesEnvironment() {
        assertThat(SeleniumMode.resolve("host", "container")).isEqualTo(SeleniumMode.HOST);
    }

    @Test
    void rejectsUnknownValue() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> SeleniumMode.resolve("remote", null))
                .withMessageContaining("host")
                .withMessageContaining("container");
    }
}
