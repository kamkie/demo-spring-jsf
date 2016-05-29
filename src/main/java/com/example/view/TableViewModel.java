package com.example.view;

import com.example.entity.Message;
import lombok.Data;
import org.primefaces.model.LazyDataModel;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.io.Serializable;
import java.util.Map;

@Data
@Component
@SessionScope
public class TableViewModel implements Serializable {
    private static final long serialVersionUID = -8703578331855685793L;

    private LazyDataModel<Message> messages;
    private Map<String, Object> filters;

}
