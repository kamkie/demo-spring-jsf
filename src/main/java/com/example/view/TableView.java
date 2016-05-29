package com.example.view;

import com.example.entity.Message;
import com.example.repository.MessagesRepository;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@RequestScope
public class TableView {

    private final TableViewModel tableViewModel;

    @Getter
    @Setter
    private DataTable table;

    @Autowired
    public TableView(TableViewModel tableViewModel) {
        this.tableViewModel = tableViewModel;
    }

    private LazyDataModel<Message> initMessages() {
        log.info("----------- init messages ------------------");
        LazyDataModel<Message> messages = new MessagesLazyDataModel();
        tableViewModel.setMessages(messages);
        return messages;
    }

    public LazyDataModel<Message> getMessages() {
        return Optional.ofNullable(tableViewModel.getMessages()).orElseGet(this::initMessages);
    }

    @Slf4j
    private static class MessagesLazyDataModel extends LazyDataModel<Message> {
        private static final long serialVersionUID = -8703578331856683793L;

        @Override
        public List<Message> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
            log.info("----------- load messages ------------------");
            Sort.Direction direction = getDirection(sortOrder);
            PageRequest pageRequest = getPageRequest(first, pageSize, sortField, direction);
            MessagesRepository repository = getMessagesRepository();

            Page<Message> page = repository.findAll(filters, pageRequest);

            setRowCount(Long.valueOf(page.getTotalElements()).intValue());
            return page.getContent();
        }

        private MessagesRepository getMessagesRepository() {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            ServletContext servletContext = request.getServletContext();
            return WebApplicationContextUtils.getWebApplicationContext(servletContext).getBean(MessagesRepository.class);

//            FacesContext facesContext = FacesContext.getCurrentInstance();
//            Application facesContextApplication = facesContext.getApplication();
//            return facesContextApplication.evaluateExpressionGet(facesContext, "#{messagesRepository}", MessagesRepository.class);
        }

        private PageRequest getPageRequest(int first, int pageSize, String sortField, Sort.Direction direction) {
            if (sortField != null) {
                return new PageRequest(first / pageSize, pageSize, direction, sortField);
            }
            return new PageRequest(first / pageSize, pageSize);
        }

        private Sort.Direction getDirection(SortOrder sortOrder) {
            if (sortOrder == SortOrder.DESCENDING) {
                return Sort.Direction.DESC;
            }
            return Sort.Direction.ASC;
        }
    }
}
