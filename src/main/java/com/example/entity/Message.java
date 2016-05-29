package com.example.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "messages")
public class Message implements Serializable {
    private static final long serialVersionUID = 6720661546911326511L;

    @Id
    @GeneratedValue
    private Long id;

    @Column
    private String lang;

    @Column
    private String key;

    @Column
    private String text;
}
