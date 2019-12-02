package ru.bobrov.vyacheslav.chat.dataproviders.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.bobrov.vyacheslav.chat.dataproviders.entities.User;
import ru.bobrov.vyacheslav.chat.dataproviders.entities.UserStatus;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserRepository extends PagingAndSortingRepository<User, UUID> {
    List<User> findAllByLogin(String login);

    Page<User> findAllByStatus(UserStatus status, Pageable pageable);

    @Query(
            value = "SELECT u.* \n" +
                    "FROM users u \n" +
                    "LEFT JOIN users_chats uc ON u.user_id=uc.user_id \n" +
                    "WHERE NOT uc.chat_id=:chatId AND u.status=:#{#userStatus.name()}",
            nativeQuery = true
    )
    List<User> findAllByUserOutOfChatAndStatus(@Param("chatId") UUID chatId, @Param("userStatus") UserStatus userStatus);
}
