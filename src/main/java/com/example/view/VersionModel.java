package com.example.view;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.faces.bean.ManagedBean;

@Getter
@Component
@ManagedBean
public class VersionModel {

    @Value("${spring.application.name}")
    private String name;
    @Value("${info.version}")
    private String version;
}
