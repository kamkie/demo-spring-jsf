package com.example.tests;

import com.example.DemoApplication;
import org.junit.jupiter.api.Test;

class BootAppTest {

    @Test
    void main() throws Exception {
        DemoApplication.main(new String[]{
                "--server.port=-1", "--spring.profiles.active=test"
        });
    }

}
