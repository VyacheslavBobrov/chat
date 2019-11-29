package ru.bobrov.vyacheslav.chat.dataproviders.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.bobrov.vyacheslav.chat.dataproviders.entities.Chat;

import java.util.UUID;

@Repository
public interface ChatRepository extends CrudRepository<Chat, UUID> {
}
