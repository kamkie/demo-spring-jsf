package com.example.viewmodel;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Component;

@Getter
@Component
public class VersionModel {

    private final String name;
    private final String version;

    public VersionModel(@Value("${spring.application.name}") String name,
                        BuildProperties buildProperties) {
        this.name = name;
        this.version = buildProperties.getVersion();
    }
}
