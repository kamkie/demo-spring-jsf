package com.example.view;

import com.example.entity.Message;
import lombok.Data;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.io.Serializable;
import java.util.List;

@Data
@Component
@SessionScope
public class TableViewModel implements Serializable {
    private static final long serialVersionUID = -8703578331855685793L;

    private List<Message> messages;
}
