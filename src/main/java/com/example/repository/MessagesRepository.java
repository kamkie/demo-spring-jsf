package com.example.repository;

import com.example.entity.Message;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(isolation = Isolation.READ_UNCOMMITTED, readOnly = true)
public interface MessagesRepository extends JpaRepository<Message, Long> {

    @Cacheable(cacheNames = "i18n")
    Optional<Message> findByKeyAndLang(String key, String lang);

}
