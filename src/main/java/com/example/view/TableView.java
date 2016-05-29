package com.example.view;

import com.example.entity.Message;
import com.example.repository.MessagesRepository;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.event.data.FilterEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequestScope
public class TableView {

    private final MessagesRepository messagesRepository;
    private final TableViewModel tableViewModel;

    @Getter
    @Setter
    private DataTable table;


    @Autowired
    public TableView(MessagesRepository messagesRepository, TableViewModel tableViewModel) {
        this.messagesRepository = messagesRepository;
        this.tableViewModel = tableViewModel;
    }

    private List<Message> initMessages() {
        log.info("----------- init messages ------------------");
//        LazyDataModel<Message> messages = new LazyDataModel<Message>() {
//            @Override
//            public List<Message> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
//                log.info("----------- load messages ------------------");
//                Sort.Direction direction = Sort.Direction.fromStringOrNull(sortOrder.name());
//                Page<Message> page = messagesRepository.findAll(new PageRequest(first / pageSize, pageSize, direction, sortField));
//
//                setRowCount(Long.valueOf(page.getTotalElements()).intValue());
//
//                return page.getContent();
//            }
//        };
        List<Message> messages = messagesRepository.findAll();

        tableViewModel.setMessages(messages);
        return tableViewModel.getMessages();
    }

    public List<Message> getMessages() {
        return Optional.ofNullable(tableViewModel.getMessages()).orElseGet(this::initMessages);
    }

    public void filterListener(FilterEvent filterEvent) {
        log.info("filterListener {}", filterEvent.getFilters());
        Object lang = filterEvent.getFilters().get("lang");
        List<Message> messages = messagesRepository.findAllByLang(String.valueOf(lang));

        tableViewModel.setMessages(messages);
    }

}
