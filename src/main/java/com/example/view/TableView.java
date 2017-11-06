package com.example.view;

import com.example.entity.Message;
import com.example.repository.MessagesRepository;
import lombok.extern.slf4j.Slf4j;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.annotation.SessionScope;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

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

    private static class MessageLazyDataModel extends LazyDataModel<Message> {
        private static final long serialVersionUID = -8803578331856683793L;

        @Override
        public List<Message> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
            log.info("----------- load messages ------------------");
            Sort.Direction direction = getDirection(sortOrder);
            PageRequest pageRequest = getPageRequest(first, pageSize, sortField, direction);
            MessagesRepository repository = getMessagesRepository();

            Page<Message> page = repository.findPageWithFilters(filters, pageRequest);

            setRowCount((int) page.getTotalElements());
            return page.getContent();
        }

        private MessagesRepository getMessagesRepository() {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            WebApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(request.getServletContext());
            if (applicationContext != null) {
                return applicationContext.getBean(MessagesRepository.class);
            }
            throw new IllegalArgumentException();
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
