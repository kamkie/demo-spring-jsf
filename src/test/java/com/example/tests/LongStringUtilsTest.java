package com.example.tests;

import com.example.entity.Role;
import com.example.entity.User;
import com.example.utils.LongStringUtils;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

public class LongStringUtilsTest {

    @Test
    public void formatLongString() throws Exception {
        String input = "[d6080736-4941-42d8-9e6d-291eb2807560, 75635b23-333d-42ca-a596-12744d882ba8," +
                " 749679ad-bb19-42c3-9431-f65014ba0551, 2cad3579-c97b-4532-b9dd-b58170f7e9ef, c81d9e4c-a391-466d-90a2-1b932ba0734e]";
        assertThat(LongStringUtils.formatLongString(input)).isNotEmpty()
                .hasSize(43)
                .endsWith("...>");
    }

    @Test
    public void format60CharacterString() throws Exception {
        String input = "d6080736-4941-42d8-9e6d-291eb2807560, 75635b23-333d-42ca-a59";
        assertThat(LongStringUtils.formatLongString(input)).isNotEmpty()
                .hasSize(60)
                .isEqualTo(input)
                .doesNotEndWith("...>");
    }

    @Test
    public void formatNull() throws Exception {
        assertThat(LongStringUtils.formatLongString(null)).isEqualTo("{null}");
    }

    @Test
    public void formatRole() throws Exception {
        assertThat(LongStringUtils.formatLongString(new Role(1L, ""))).isEqualTo("Role(id=1, name=)");
    }

    @Test
    public void formatUser() throws Exception {
        assertThat(LongStringUtils.formatLongString(new User("login", "password", Collections.singleton(new Role(1L, "")))))
                .isEqualTo("User(id=null, login=login, roles=[Role(id=1, name=)])");
    }

    @Test
    public void formatEmpty() throws Exception {
        assertThat(LongStringUtils.formatLongString("")).isEqualTo("{empty}");
    }

    @Test
    public void formatShortString() throws Exception {
        assertThat(LongStringUtils.formatLongString("some text")).isEqualTo("some text").doesNotEndWith("...>");
    }

    @Test
    public void cannotCreateInstance() throws Exception {
        Constructor<LongStringUtils> constructor = LongStringUtils.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        try {
            constructor.newInstance();
        } catch (Exception e) {
            assertThat(e).hasCauseInstanceOf(UnsupportedOperationException.class);
        }
    }

}
