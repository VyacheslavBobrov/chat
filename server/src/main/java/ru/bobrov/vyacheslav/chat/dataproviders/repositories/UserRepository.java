package ru.bobrov.vyacheslav.chat.dataproviders.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.bobrov.vyacheslav.chat.dataproviders.entities.User;
import ru.bobrov.vyacheslav.chat.dto.enums.UserStatus;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserRepository extends PagingAndSortingRepository<User, UUID> {
    List<User> findAllByLogin(String login);

    Page<User> findAllByStatus(UserStatus status, Pageable pageable);

    @Query(
            value = "SELECT  u.*\n" +
                    "FROM " +
                    "   users u\n" +
                    "WHERE\n" +
                    "   NOT u.user_id in (SELECT uc.user_id FROM users_chats uc WHERE uc.chat_id=:chatId)\n" +
                    "   AND u.status=:#{#userStatus.name()} \n" +
                    "ORDER BY \n" +
                    "   u.user_login",
            nativeQuery = true
    )
    List<User> findAllByUserOutOfChatAndStatus(@Param("chatId") UUID chatId, @Param("userStatus") UserStatus userStatus);
}
