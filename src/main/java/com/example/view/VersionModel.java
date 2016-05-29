package com.example.view;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class VersionModel {

    @Value("${spring.application.name}")
    private String name;
    @Value("${info.version}")
    private String version;
}
