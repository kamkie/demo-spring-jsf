package com.example.view;

import com.example.entity.Message;
import com.example.repository.MessagesRepository;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.event.data.FilterEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import javax.faces.context.FacesContext;
import java.util.List;
import java.util.Map;
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
        FacesContext facesContext = FacesContext.getCurrentInstance();
//        MessagesRepository repository = facesContext.getApplication().evaluateExpressionGet(facesContext, "#{messagesRepository}", MessagesRepository.class);
//        LazyDataModel<Message> messages = new MessageLazyDataModel();
        List<Message> messages = this.messagesRepository.findAll();

        tableViewModel.setMessages(messages);
        return tableViewModel.getMessages();
    }

    public List<Message> getMessages() {
        return Optional.ofNullable(tableViewModel.getMessages()).orElseGet(this::initMessages);
    }

    public void filterListener(FilterEvent filterEvent) {
        Map<String, Object> filters = filterEvent.getFilters();
        log.info("filterListener {}", filters);
        tableViewModel.setFilters(filters);

        List<Message> messages = messagesRepository.findAll(filters, new Sort(""));

        tableViewModel.setMessages(messages);
    }

    private class MessageLazyDataModel extends LazyDataModel<Message> {
        @Override
        public List<Message> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
            log.info("----------- load messages ------------------");
            Sort.Direction direction = Sort.Direction.fromStringOrNull(sortOrder.name());
            Page<Message> page = messagesRepository.findAll(new PageRequest(first / pageSize, pageSize, direction, sortField));

            setRowCount(Long.valueOf(page.getTotalElements()).intValue());

            return page.getContent();
        }
    }
}
