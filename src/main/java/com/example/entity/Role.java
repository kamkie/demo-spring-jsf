package com.example.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "roles")
public class Role implements Serializable {
    private static final long serialVersionUID = -8703578331855685793L;

    @Id
    @GeneratedValue
    private Long id;

    @Column
    private String name;

}
