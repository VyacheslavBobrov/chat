package ru.bobrov.vyacheslav.chat.dataproviders.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.bobrov.vyacheslav.chat.dto.enums.MessageStatus;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = PRIVATE)
@Entity
@Table(name = "messages")
public class Message implements EntityWithTimeInfo {
    @Id
    UUID messageId;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
    @JoinColumn(name = "chat_id")
    Chat chat;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
    @JoinColumn(name = "user_id")
    User user;

    @NotNull
    @Lob
    String message;

    @NotNull
    @Column
    @Enumerated(EnumType.STRING)
    MessageStatus status;

    @NotNull
    @Column
    Timestamp created;

    @NotNull
    @Column
    Timestamp updated;
}
