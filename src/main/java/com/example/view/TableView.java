package com.example.view;

import com.example.entity.Message;
import com.example.viewmodel.MessageLazyDataModel;
import lombok.extern.slf4j.Slf4j;
import org.primefaces.model.LazyDataModel;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.io.Serializable;

@Slf4j
@Component
@SessionScope
public class TableView implements Serializable {

    private static final long serialVersionUID = -8703578331855685793L;

    private LazyDataModel<Message> messages;

    public LazyDataModel<Message> getMessages() {
        if (messages == null) {
            messages = new MessageLazyDataModel();
        }

        return messages;
    }
}
