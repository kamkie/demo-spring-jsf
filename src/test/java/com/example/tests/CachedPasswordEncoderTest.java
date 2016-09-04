package com.example.tests;

import com.example.security.CachedPasswordEncoder;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CachedPasswordEncoderTest {
    @Test
    public void encodeMatches() throws Exception {
        CachedPasswordEncoder passwordEncoder = new CachedPasswordEncoder();
        String password = "password";
        String encodedPassword = passwordEncoder.encode(password);
        assertThat(passwordEncoder.matches(password, encodedPassword)).isTrue();
        assertThat(passwordEncoder.matches(password, "$2a$10$.LCG4WLBrXd0iNIcRZjfcehDMwaZcqstb4AJb8SmX2hVNSHUIa79W")).isTrue();
    }

    @Test
    public void encodeNotMatches() throws Exception {
        CachedPasswordEncoder passwordEncoder = new CachedPasswordEncoder();
        String password = "password";
        String encodedPassword = passwordEncoder.encode(password);
        assertThat(passwordEncoder.matches("password_", encodedPassword)).isFalse();
        assertThat(passwordEncoder.matches(password, "$2a$10$.LCG4WLBrXd0iNIcRZjfcehDMwaZcqstb4AJb8SmX2hVNSHUIa79k")).isFalse();
    }

}
