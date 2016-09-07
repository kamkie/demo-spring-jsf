package com.example.tests;

import com.example.DemoApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.boot.SpringApplication;

import static org.mockito.Mockito.times;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;


@RunWith(PowerMockRunner.class)
@PrepareForTest(SpringApplication.class)
public class BootAppTest {

    @Test
    public void main() throws Exception {
        mockStatic(SpringApplication.class);
        when(SpringApplication.run(DemoApplication.class)).thenReturn(null);

        DemoApplication.main(new String[0]);
        PowerMockito.verifyStatic(times(1));
        DemoApplication.main(new String[0]);
        PowerMockito.verifyStatic(times(1));
    }

}
