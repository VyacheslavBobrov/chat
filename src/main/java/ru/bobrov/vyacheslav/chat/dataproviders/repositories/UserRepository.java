package ru.bobrov.vyacheslav.chat.dataproviders.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import ru.bobrov.vyacheslav.chat.dataproviders.entities.User;
import ru.bobrov.vyacheslav.chat.dataproviders.entities.UserStatus;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserRepository extends PagingAndSortingRepository<User, UUID> {
    List<User> findAllByLogin(String login);

    Page<User> findAllByStatus(UserStatus status, Pageable pageable);

    List<User> findAllByStatus(UserStatus status);
}
