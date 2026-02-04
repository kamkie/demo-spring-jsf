package com.example.viewmodel;

import com.example.entity.Message;
import com.example.repository.MessagesRepository;
import com.example.utils.ContextUtils;
import lombok.extern.slf4j.Slf4j;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public class MessageLazyDataModel extends LazyDataModel<Message> {

    private static final long serialVersionUID = -8803578331856683793L;

    @Override
    public int count(Map<String, FilterMeta> filterBy) {
        MessagesRepository repository = getMessagesRepository();

        Map<String, Object> filters = filterBy.entrySet().stream()
                .filter(e -> Objects.nonNull(e.getValue().getFilterValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getFilterValue()));
        return repository.countPageWithFilters(filters);
    }

    @Override
    public List<Message> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
        log.info("----------- load messages ------------------");
        Optional<SortMeta> optionalSortMeta = sortBy.values().stream().findFirst();
        SortOrder sortOrder = optionalSortMeta
                .map(SortMeta::getOrder)
                .orElse(SortOrder.ASCENDING);
        Sort.Direction direction = getDirection(sortOrder);
        String sortField = optionalSortMeta.map(SortMeta::getField)
                .orElse(null);
        PageRequest pageRequest = getPageRequest(first, pageSize, sortField, direction);
        MessagesRepository repository = getMessagesRepository();

        Map<String, Object> filters = filterBy.entrySet().stream()
                .filter(e -> Objects.nonNull(e.getValue().getFilterValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getFilterValue()));
        Page<Message> page = repository.findPageWithFilters(filters, pageRequest);

        setRowCount((int) page.getTotalElements());
        return page.getContent();
    }

    private MessagesRepository getMessagesRepository() {
        return ContextUtils.getApplicationContext().getBean(MessagesRepository.class);
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
