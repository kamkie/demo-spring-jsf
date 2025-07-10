package com.example.viewmodel;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Component;

@Getter
@Component
public class VersionModel {

    @Value("${spring.application.name}")
    private String name;
    private String version;

    public VersionModel(BuildProperties buildProperties) {
        this.version = buildProperties.getVersion();
    }
}
