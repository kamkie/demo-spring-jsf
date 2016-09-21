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

    Page<Message> findPageByKeyContainingAndLangContainingAndTextContaining(String key, String lang, String text, Pageable pageable);

    @Query("select m from Message m where (:lang is null OR (LOWER(m.lang) like LOWER(CONCAT('%', :lang, '%')))) " +
            "and (:key is null OR (LOWER(m.key) like LOWER(CONCAT('%', :key, '%'))))")
    List<Message> findPage(@Param("key") Object key, @Param("lang") Object lang);

    @Query("select m from Message m " +
            "where (:#{#filters['lang']} is null OR (LOWER(m.lang) like LOWER(CONCAT('%', :#{#filters['lang']}, '%')))) " +
            "and (:#{#filters['key']} is null OR (LOWER(m.key) like LOWER(CONCAT('%', :#{#filters['key']}, '%')))) " +
            "and (:#{#filters['text']} is null OR (LOWER(m.text) like LOWER(CONCAT('%', :#{#filters['text']}, '%')))) ")
    Page<Message> findPage(@Param("filters") Map<String, Object> filters, Pageable pageable);

    default Page<Message> findPageWithFilters(Map<String, Object> filters,
                                              Pageable pageable) {
        String key = Optional.ofNullable(filters.get("key")).map(Object::toString).orElse("");
        String lang = Optional.ofNullable(filters.get("lang")).map(Object::toString).orElse("");
        String text = Optional.ofNullable(filters.get("text")).map(Object::toString).orElse("");
        return findPageByKeyContainingAndLangContainingAndTextContaining(key, lang, text, pageable);
    }

}
