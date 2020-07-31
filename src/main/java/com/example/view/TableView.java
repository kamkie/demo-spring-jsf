package com.example.view;

import com.example.DemoApplication;
import com.example.entity.Message;
import com.example.repository.MessagesRepository;
import lombok.extern.slf4j.Slf4j;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@SessionScope
public class TableView implements Serializable {
    @SuppressWarnings("PMD.FieldNamingConventions")
    private static final long serialVersionUID = -8703578331855685793L;

    private LazyDataModel<Message> messages;

    public LazyDataModel<Message> getMessages() {
        if (messages == null) {
            messages = new MessageLazyDataModel();
        }

        return messages;
    }

    private static class MessageLazyDataModel extends LazyDataModel<Message> {
        @SuppressWarnings("PMD.FieldNamingConventions")
        private static final long serialVersionUID = -8803578331856683793L;

        @Override
        public List<Message> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, FilterMeta> filters) {
            log.info("----------- load messages ------------------");
            Sort.Direction direction = getDirection(sortOrder);
            PageRequest pageRequest = getPageRequest(first, pageSize, sortField, direction);
            MessagesRepository repository = getMessagesRepository();

            Page<Message> page = repository.findPageWithFilters(filters, pageRequest);

            setRowCount((int) page.getTotalElements());
            return page.getContent();
        }

        private MessagesRepository getMessagesRepository() {
            return DemoApplication.getApplicationContext().getBean(MessagesRepository.class);
        }

        private PageRequest getPageRequest(int first, int pageSize, String sortField, Sort.Direction direction) {
            if (sortField != null) {
                return PageRequest.of(first / pageSize, pageSize, direction, sortField);
            }
            return PageRequest.of(first / pageSize, pageSize);
        }

        private Sort.Direction getDirection(SortOrder sortOrder) {
            if (sortOrder == SortOrder.DESCENDING) {
                return Sort.Direction.DESC;
            }
            return Sort.Direction.ASC;
        }
    }
}
