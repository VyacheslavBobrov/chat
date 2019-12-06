package ru.bobrov.vyacheslav.chat.dataproviders.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import ru.bobrov.vyacheslav.chat.dataproviders.entities.Chat;
import ru.bobrov.vyacheslav.chat.dataproviders.entities.Message;

import java.util.UUID;

@Repository
public interface MessageRepository extends PagingAndSortingRepository<Message, UUID> {
    Page<Message> findAllByChatOrderByCreatedDesc(Chat chat, Pageable page);
}
