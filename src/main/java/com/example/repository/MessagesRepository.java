package com.example.repository;

import com.example.entity.Message;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Transactional(isolation = Isolation.READ_UNCOMMITTED, readOnly = true)
public interface MessagesRepository extends JpaRepository<Message, Long> {

    @Cacheable(cacheNames = "i18n")
    Optional<Message> findByKeyAndLang(String key, String lang);

    int countPageByKeyContainingAndLangContainingAndTextContaining(String key, String lang, String text);

    Page<Message> findPageByKeyContainingAndLangContainingAndTextContaining(String key, String lang, String text, Pageable pageable);

    @Query("""
            select m from Message m where (:lang is null OR (LOWER(m.lang) like LOWER(CONCAT('%', :lang, '%'))))\s
            and (:key is null OR (LOWER(m.key) like LOWER(CONCAT('%', :key, '%'))))
            """)
    List<Message> findPage(@Param("key") Object key, @Param("lang") Object lang);

    @Query("""
            select m from Message m\s
            where (:#{#filters['lang']} is null OR (LOWER(m.lang) like LOWER(CONCAT('%', :#{#filters['lang']}, '%'))))\s
            and (:#{#filters['key']} is null OR (LOWER(m.key) like LOWER(CONCAT('%', :#{#filters['key']}, '%'))))\s
            and (:#{#filters['text']} is null OR (LOWER(m.text) like LOWER(CONCAT('%', :#{#filters['text']}, '%'))))\s
            """)
    Page<Message> findPage(@Param("filters") Map<String, Object> filters, Pageable pageable);

    default Page<Message> findPageWithFilters(Map<String, Object> filters, Pageable pageable) {
        String key = getParam(filters, "key");
        String lang = getParam(filters, "lang");
        String text = getParam(filters, "text");
        return findPageByKeyContainingAndLangContainingAndTextContaining(key, lang, text, pageable);
    }

    default int countPageWithFilters(Map<String, Object> filters) {
        String key = getParam(filters, "key");
        String lang = getParam(filters, "lang");
        String text = getParam(filters, "text");
        return countPageByKeyContainingAndLangContainingAndTextContaining(key, lang, text);
    }

    private String getParam(Map<String, Object> filters, String key) {
        return Optional.ofNullable(filters.get(key)).map(Object::toString).orElse("");
    }

}
