package ru.bobrov.vyacheslav.chat.dataproviders.entities;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.Set;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = PRIVATE)
@Entity
@Table(name = "chats")
@EqualsAndHashCode(exclude = {"users", "messages"})
@ToString(exclude = {"users", "messages"})
public class Chat implements EntityWithTimeInfo {
    @Id
    UUID chatId;

    @NotNull
    @Column(name = "chat_name")
    String title;

    @NotNull
    @Column()
    @Enumerated(EnumType.STRING)
    ChatStatus status;

    @NotNull
    @Column
    Timestamp created;

    @NotNull
    @Column
    Timestamp updated;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "creator_id")
    User creator;

    @ManyToMany(
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL

    )
    @JoinTable(
            name = "users_chats",
            joinColumns = @JoinColumn(name = "chat_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    Set<User> users;

    @OneToMany(
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL
    )
    @JoinColumn(name = "chat_id")
    Set<Message> messages;
}
