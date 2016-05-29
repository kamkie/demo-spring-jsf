package com.example.repository;

import com.example.entity.Message;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
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

    @Query("select m from Message m where (:lang is null OR (LOWER(m.lang) like LOWER(CONCAT('%', :lang, '%')))) " +
            "and (:key is null OR (LOWER(m.key) like LOWER(CONCAT('%', :key, '%'))))")
    List<Message> findAll(@Param("key") Object key, @Param("lang") Object lang);

    @Query("select m from Message m " +
            "where (:#{#filters['lang']} is null OR (LOWER(m.lang) like LOWER(CONCAT('%', :#{#filters['lang']}, '%')))) " +
            "and (:#{#filters['key']} is null OR (LOWER(m.key) like LOWER(CONCAT('%', :#{#filters['key']}, '%')))) " +
            "and (:#{#filters['text']} is null OR (LOWER(m.text) like LOWER(CONCAT('%', :#{#filters['text']}, '%')))) ")
    List<Message> findAll(@Param("filters") Map<String, Object> filters, Sort orders);

}
