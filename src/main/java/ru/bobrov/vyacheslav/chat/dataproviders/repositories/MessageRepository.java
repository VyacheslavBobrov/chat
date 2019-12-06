package ru.bobrov.vyacheslav.chat.dataproviders.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.bobrov.vyacheslav.chat.dataproviders.entities.Message;

import java.util.UUID;

@Repository
public interface MessageRepository extends PagingAndSortingRepository<Message, UUID> {
    @Query("SELECT message FROM Message message WHERE message.chat.chatId=:chatId ORDER BY message.created DESC")
    Page<Message> findAllByChatIdOrderByCreatedDesc(@Param("chatId") UUID chatId, Pageable page);
}
