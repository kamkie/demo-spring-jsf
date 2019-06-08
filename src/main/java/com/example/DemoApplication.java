package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.lang.NonNull;

@SpringBootApplication
public class DemoApplication implements ApplicationContextAware {

    private static ConfigurableApplicationContext applicationContext;

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    public static ConfigurableApplicationContext getApplicationContext() {
        return DemoApplication.applicationContext;
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) {
        setStaticApplicationContext((ConfigurableApplicationContext) applicationContext);
    }

    private static void setStaticApplicationContext(ConfigurableApplicationContext applicationContext) {
        DemoApplication.applicationContext = applicationContext;
    }
}
