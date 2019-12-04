package ru.bobrov.vyacheslav.chat.dataproviders.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.bobrov.vyacheslav.chat.dataproviders.entities.UserFile;

import java.util.UUID;

@Repository
public interface UserFileRepository extends CrudRepository<UserFile, UUID> {
}
