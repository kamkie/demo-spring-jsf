package com.example.tests;

import com.example.DemoApplication;
import org.junit.Test;

public class BootAppTest {

    @Test
    public void main() throws Exception {
        DemoApplication.main(new String[]{
                "--server.port=-1"
        });
    }

}
