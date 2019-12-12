package ru.bobrov.vyacheslav.chat.controllers.integration;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.bobrov.vyacheslav.chat.ChatApplication;
import ru.bobrov.vyacheslav.chat.dataproviders.entities.Chat;
import ru.bobrov.vyacheslav.chat.dataproviders.entities.ChatStatus;
import ru.bobrov.vyacheslav.chat.dataproviders.entities.User;
import ru.bobrov.vyacheslav.chat.dataproviders.entities.UserStatus;
import ru.bobrov.vyacheslav.chat.dataproviders.repositories.ChatRepository;
import ru.bobrov.vyacheslav.chat.dataproviders.repositories.UserRepository;
import ru.bobrov.vyacheslav.chat.services.authentication.JwtUserDetailsService;
import ru.bobrov.vyacheslav.chat.services.utils.JwtTokenUtil;
import ru.bobrov.vyacheslav.chat.utils.DateUtils;

import javax.annotation.PostConstruct;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.ORIGIN;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.bobrov.vyacheslav.chat.dataproviders.entities.ChatStatus.ACTIVE;
import static ru.bobrov.vyacheslav.chat.dataproviders.entities.ChatStatus.DISABLED;
import static ru.bobrov.vyacheslav.chat.services.Constants.TOKEN_PREFIX;
import static ru.bobrov.vyacheslav.chat.testdata.Chats.*;
import static ru.bobrov.vyacheslav.chat.testdata.Users.*;

@SpringBootTest(classes = ChatApplication.class)
@AutoConfigureMockMvc
@WebAppConfiguration
@TestPropertySource(locations = "classpath:application.yml")
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChatControllerTest {
    private static final String CHATS_API_PATH = "/api/v1/chat";
    private static final String ORIGIN_VAL = "localhost:8080";

    @Autowired
    UserRepository userRepository;
    @Autowired
    ChatRepository chatRepository;
    @Autowired
    JwtTokenUtil jwtTokenUtil;
    @Autowired
    JwtUserDetailsService jwtUserDetailsService;

    @Autowired
    MockMvc mvc;
    String adminAuthorizationHeader;
    String userAuthorizationHeader;

    @PostConstruct
    public void setUp() {
        String adminToken = jwtTokenUtil.generateToken(jwtUserDetailsService.loadUserByUsername(TEST_ADMIN.getLogin()));
        adminAuthorizationHeader = format("%s %s", TOKEN_PREFIX, adminToken);
    }

    @BeforeEach
    public void setUpTest() {
        userRepository.save(TEST_USER);
        String userToken = jwtTokenUtil.generateToken(jwtUserDetailsService.loadUserByUsername(TEST_ADMIN.getLogin()));
        userAuthorizationHeader = format("%s %s", TOKEN_PREFIX, userToken);
    }

    @Test
    public void giveNoChat_createChat_returnCreatedChat() throws Exception {
        mvc.perform(
                post(CHATS_API_PATH)
                        .header(AUTHORIZATION, TEST_FOR_CREATE_CHAT.getCreator() == TEST_ADMIN
                                ? adminAuthorizationHeader
                                : userAuthorizationHeader
                        )
                        .header(ORIGIN, ORIGIN_VAL)
                        .param("userId", TEST_FOR_CREATE_CHAT.getCreator().getUserId().toString())
                        .param("title", TEST_FOR_CREATE_CHAT.getTitle())
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(TEST_FOR_CREATE_CHAT.getTitle()))
                .andExpect(jsonPath("$.creator.userId")
                        .value(TEST_FOR_CREATE_CHAT.getCreator().getUserId().toString()))
                .andExpect(jsonPath("$.status").value(ChatStatus.ACTIVE.name()));
    }

    @Test
    public void giveChat_getChat_returnChat() throws Exception {
        final UUID chatId = createChat(TEST_FOR_GET_CHAT);

        mvc.perform(
                get(format("%s/%s", CHATS_API_PATH, chatId))
                        .header(AUTHORIZATION, TEST_FOR_GET_CHAT.getCreator() == TEST_ADMIN
                                ? adminAuthorizationHeader
                                : userAuthorizationHeader
                        )
                        .header(ORIGIN, ORIGIN_VAL)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(TEST_FOR_GET_CHAT.getTitle()))
                .andExpect(jsonPath("$.creator.userId")
                        .value(TEST_FOR_GET_CHAT.getCreator().getUserId().toString()))
                .andExpect(jsonPath("$.status").value(ChatStatus.ACTIVE.name()));
    }

    @Test
    public void giveChat_updateChat_returnUpdatedChat() throws Exception {
        final UUID chatId = createChat(TEST_FOR_UPDATE_CHAT);
        final Timestamp createdTime = Timestamp.valueOf(LocalDateTime.now());

        final String newTitle = "Очень удачное название чата";

        MvcResult result = mvc.perform(
                post(format("%s/%s", CHATS_API_PATH, chatId))
                        .header(AUTHORIZATION, TEST_FOR_UPDATE_CHAT.getCreator() == TEST_ADMIN
                                ? adminAuthorizationHeader
                                : userAuthorizationHeader
                        )
                        .header(ORIGIN, ORIGIN_VAL)
                        .param("title", newTitle)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(newTitle))
                .andReturn();

        Object document = Configuration.defaultConfiguration().jsonProvider()
                .parse(result.getResponse().getContentAsString());
        Timestamp updated = DateUtils.parse(JsonPath.read(document, "$.updated"));
        Assertions.assertTrue(updated.after(createdTime));
    }

    private UUID createChat(final Chat chat) throws Exception {
        MvcResult result = mvc.perform(
                post(CHATS_API_PATH)
                        .header(AUTHORIZATION, chat.getCreator() == TEST_ADMIN
                                ? adminAuthorizationHeader
                                : userAuthorizationHeader
                        )
                        .header(ORIGIN, ORIGIN_VAL)
                        .param("userId", chat.getCreator().getUserId().toString())
                        .param("title", chat.getTitle())
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andReturn();
        Object document = Configuration.defaultConfiguration().jsonProvider()
                .parse(result.getResponse().getContentAsString());
        final UUID chatId = UUID.fromString(JsonPath.read(document, "$.chatId"));
        CHAT_IDS_MAP.put(chat, chatId);
        return chatId;
    }

    @Test
    public void giveChat_addUsersToChat_userAdded() throws Exception {
        ALL_USERS.stream()
                .filter(user -> user != TEST_USER && user != TEST_ADMIN)
                .forEach(user -> userRepository.save(user));

        final Set<String> userIdsToAdd = TEST_FOR_ADD_USERS_CHAT.getUsers().stream()
                .filter(user -> user != TEST_FOR_ADD_USERS_CHAT.getCreator())
                .map(user -> user.getUserId().toString())
                .collect(Collectors.toUnmodifiableSet());

        final UUID chatId = createChat(TEST_FOR_ADD_USERS_CHAT);

        mvc.perform(
                post(format("%s/%s/users", CHATS_API_PATH, chatId))
                        .header(AUTHORIZATION, TEST_FOR_ADD_USERS_CHAT.getCreator() == TEST_ADMIN
                                ? adminAuthorizationHeader
                                : userAuthorizationHeader
                        )
                        .header(ORIGIN, ORIGIN_VAL)
                        .param("chatId", TEST_FOR_ADD_USERS_CHAT.getCreator().getUserId().toString())
                        .param("userUUIDs", String.join(",", userIdsToAdd))
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..userId",
                        containsInAnyOrder(TEST_FOR_ADD_USERS_CHAT.getUsers().stream()
                                .map(user -> user.getUserId().toString()).toArray())));
    }

    @Test
    public void giveChatWithUsers_getChatUsers_allUsersReturned() throws Exception {
        ALL_USERS.stream()
                .filter(user -> user != TEST_USER && user != TEST_ADMIN)
                .forEach(user -> userRepository.save(user));

        final UUID chatId = createChat(TEST_FOR_GET_USERS_CHAT);
        addUsers(chatId, TEST_FOR_GET_USERS_CHAT.getUsers());
        mvc.perform(
                get(format("%s/%s/users", CHATS_API_PATH, chatId))
                        .header(AUTHORIZATION, TEST_FOR_GET_USERS_CHAT.getCreator() == TEST_ADMIN
                                ? adminAuthorizationHeader
                                : userAuthorizationHeader
                        )
                        .header(ORIGIN, ORIGIN_VAL)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*].userId",
                        containsInAnyOrder(TEST_FOR_GET_USERS_CHAT.getUsers().stream()
                                .map(user -> user.getUserId().toString()).toArray())));
    }

    @Test
    public void giveChatWithUsers_getOutOfChatUsers_allOutOfChatUsersReturned() throws Exception {
        ALL_USERS.stream()
                .filter(user -> user != TEST_USER && user != TEST_ADMIN)
                .forEach(user -> userRepository.save(user));

        final UUID chatId = createChat(TEST_FOR_GET_USERS_OUT_OF_CHAT);
        addUsers(chatId, TEST_FOR_GET_USERS_OUT_OF_CHAT.getUsers());

        final Set<User> usersOutOfChat = ALL_USERS.stream()
                .filter(user -> !TEST_FOR_GET_USERS_OUT_OF_CHAT.getUsers().contains(user)
                        && user.getStatus() == UserStatus.ACTIVE)
                .collect(Collectors.toUnmodifiableSet());

        mvc.perform(
                get(format("%s/%s/users-out", CHATS_API_PATH, chatId))
                        .header(AUTHORIZATION, TEST_FOR_GET_USERS_OUT_OF_CHAT.getCreator() == TEST_ADMIN
                                ? adminAuthorizationHeader
                                : userAuthorizationHeader
                        )
                        .header(ORIGIN, ORIGIN_VAL)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*].userId",
                        containsInAnyOrder(usersOutOfChat.stream().map(user -> user.getUserId().toString()).toArray())));
    }

    @Test
    public void giveChatWithUsers_kickUserFromChat_userKicked() throws Exception {
        ALL_USERS.stream()
                .filter(user -> user != TEST_USER && user != TEST_ADMIN)
                .forEach(user -> userRepository.save(user));

        final UUID chatId = createChat(TEST_FOR_KICK_USER_OUT_OF_CHAT);
        addUsers(chatId, TEST_FOR_KICK_USER_OUT_OF_CHAT.getUsers());

        final Set<User> usersWithoutKicked = ALL_USERS.stream()
                .filter(user -> user != TEST_USER).collect(Collectors.toUnmodifiableSet());

        mvc.perform(
                post(format("%s/%s/kick", CHATS_API_PATH, chatId))
                        .header(AUTHORIZATION, TEST_FOR_GET_USERS_OUT_OF_CHAT.getCreator() == TEST_ADMIN
                                ? adminAuthorizationHeader
                                : userAuthorizationHeader
                        )
                        .header(ORIGIN, ORIGIN_VAL)
                        .param("userId", TEST_USER.getUserId().toString())
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*].userId", containsInAnyOrder(usersWithoutKicked.stream()
                        .map(user -> user.getUserId().toString()).toArray())));
    }

    private void addUsers(UUID chatId, Set<User> users) throws Exception {
        mvc.perform(
                post(format("%s/%s/users", CHATS_API_PATH, chatId))
                        .header(AUTHORIZATION, TEST_FOR_ADD_USERS_CHAT.getCreator() == TEST_ADMIN
                                ? adminAuthorizationHeader
                                : userAuthorizationHeader
                        )
                        .header(ORIGIN, ORIGIN_VAL)
                        .param("chatId", chatId.toString())
                        .param("userUUIDs", users.stream()
                                .map(user -> user.getUserId().toString()).collect(Collectors.joining(",")))
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk());
    }

    @Test
    public void giveActiveChat_blockChat_chatBlocked() throws Exception {
        final UUID chatId = createChat(TEST_FOR_BLOCK_CHAT);

        mvc.perform(
                post(format("%s/%s/block", CHATS_API_PATH, chatId))
                        .header(AUTHORIZATION, TEST_FOR_BLOCK_CHAT.getCreator() == TEST_ADMIN
                                ? adminAuthorizationHeader
                                : userAuthorizationHeader
                        )
                        .header(ORIGIN, ORIGIN_VAL)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.chatId").value(CHAT_IDS_MAP.get(TEST_FOR_BLOCK_CHAT).toString()))
                .andExpect(jsonPath("$.status").value(DISABLED.name()));
    }

    @Test
    public void giveBlockedChat_unblockChat_chatUnblocked() throws Exception {
        final UUID chatId = createChat(TEST_FOR_UNBLOCK_CHAT);
        final Chat chat = chatRepository.findById(chatId).orElseThrow();
        chat.setStatus(DISABLED);
        chatRepository.save(chat);

        mvc.perform(
                post(format("%s/%s/unblock", CHATS_API_PATH, chatId))
                        .header(AUTHORIZATION, TEST_FOR_UNBLOCK_CHAT.getCreator() == TEST_ADMIN
                                ? adminAuthorizationHeader
                                : userAuthorizationHeader
                        )
                        .header(ORIGIN, ORIGIN_VAL)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.chatId").value(CHAT_IDS_MAP.get(TEST_FOR_UNBLOCK_CHAT).toString()))
                .andExpect(jsonPath("$.status").value(ACTIVE.name()));
    }
}
