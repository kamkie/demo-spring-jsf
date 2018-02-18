package com.example.tests;

import com.example.DemoApplication;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class BootAppTest {

    @Test
    void main() throws Exception {
        System.setProperty("spring.devtools.restart.enabled", "false");
        DemoApplication.main(new String[]{
                "--server.port=-1", "--spring.profiles.active=test"
        });
    }

    @AfterEach
    void closeContext() {
        DemoApplication.getApplicationContext().close();
    }

}
