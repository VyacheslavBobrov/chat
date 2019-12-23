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
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = PRIVATE)
@Entity
@Table(name = "users")
@EqualsAndHashCode(exclude = {"chats", "userFiles"})
@ToString(exclude = {"chats", "userFiles"})
public class User implements EntityWithTimeInfo {
    @Id
    UUID userId;

    @NotNull
    @Column(name = "user_name")
    String name;

    @NotNull
    @Column(unique = true, name = "user_login")
    String login;

    @NotNull
    @Column(name = "user_password")
    String password;

    @NotNull
    @Column
    @Enumerated(EnumType.STRING)
    UserStatus status;

    @NotNull
    @Column(name = "user_role")
    @Enumerated(EnumType.STRING)
    UserRole role;

    @NotNull
    @Column
    Timestamp created;

    @NotNull
    @Column
    Timestamp updated;

    @ManyToMany(
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL
    )
    @JoinTable(
            name = "users_chats",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "chat_id")
    )
    Set<Chat> chats;

    @OneToMany(
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL
    )
    @JoinColumn(name = "user_id")
    Set<UserFile> userFiles;
}
