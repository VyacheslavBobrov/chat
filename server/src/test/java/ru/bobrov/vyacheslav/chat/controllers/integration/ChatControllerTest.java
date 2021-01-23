package ru.bobrov.vyacheslav.chat.controllers.integration;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
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
import ru.bobrov.vyacheslav.chat.ChatApplication;
import ru.bobrov.vyacheslav.chat.dataproviders.entities.Chat;
import ru.bobrov.vyacheslav.chat.dataproviders.entities.Message;
import ru.bobrov.vyacheslav.chat.dataproviders.entities.User;
import ru.bobrov.vyacheslav.chat.dataproviders.repositories.ChatRepository;
import ru.bobrov.vyacheslav.chat.dataproviders.repositories.UserRepository;
import ru.bobrov.vyacheslav.chat.dto.enums.ChatStatus;
import ru.bobrov.vyacheslav.chat.dto.enums.MessageStatus;
import ru.bobrov.vyacheslav.chat.dto.enums.UserStatus;
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
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.ORIGIN;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.bobrov.vyacheslav.chat.dto.enums.ChatStatus.ACTIVE;
import static ru.bobrov.vyacheslav.chat.dto.enums.ChatStatus.DISABLED;
import static ru.bobrov.vyacheslav.chat.services.Constants.TOKEN_PREFIX;
import static ru.bobrov.vyacheslav.chat.testdata.Chats.CHAT_IDS_MAP;
import static ru.bobrov.vyacheslav.chat.testdata.Chats.TEST_FOR_ADD_USERS_CHAT;
import static ru.bobrov.vyacheslav.chat.testdata.Chats.TEST_FOR_BLOCK_CHAT;
import static ru.bobrov.vyacheslav.chat.testdata.Chats.TEST_FOR_CREATE_CHAT;
import static ru.bobrov.vyacheslav.chat.testdata.Chats.TEST_FOR_GET_CHAT;
import static ru.bobrov.vyacheslav.chat.testdata.Chats.TEST_FOR_GET_MESSAGES_CHAT;
import static ru.bobrov.vyacheslav.chat.testdata.Chats.TEST_FOR_GET_USERS_CHAT;
import static ru.bobrov.vyacheslav.chat.testdata.Chats.TEST_FOR_GET_USERS_OUT_OF_CHAT;
import static ru.bobrov.vyacheslav.chat.testdata.Chats.TEST_FOR_KICK_USER_OUT_OF_CHAT;
import static ru.bobrov.vyacheslav.chat.testdata.Chats.TEST_FOR_UNBLOCK_CHAT;
import static ru.bobrov.vyacheslav.chat.testdata.Chats.TEST_FOR_UPDATE_CHAT;
import static ru.bobrov.vyacheslav.chat.testdata.Messages.MESSAGES;
import static ru.bobrov.vyacheslav.chat.testdata.Messages.MESSAGE_UUID_MAP;
import static ru.bobrov.vyacheslav.chat.testdata.Users.ALL_USERS;
import static ru.bobrov.vyacheslav.chat.testdata.Users.TEST_ADMIN;
import static ru.bobrov.vyacheslav.chat.testdata.Users.TEST_USER_1;

@SpringBootTest(classes = ChatApplication.class)
@AutoConfigureMockMvc
@WebAppConfiguration
@TestPropertySource(locations = "classpath:application.yml")
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChatControllerTest {
    private static final String CHATS_API_PATH = "/api/v1/chat";
    private static final String MESSAGES_API_PATH = "/api/v1/message";
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
        adminAuthorizationHeader = generateAuthorizationHeader(TEST_ADMIN);
    }

    private String generateAuthorizationHeader(User user) {
        val token = jwtTokenUtil.generateToken(jwtUserDetailsService.loadUserByUsername(user.getLogin()));
        return format("%s %s", TOKEN_PREFIX, token);
    }

    @BeforeEach
    public void setUpTest() {
        userRepository.save(TEST_USER_1);
        userAuthorizationHeader = generateAuthorizationHeader(TEST_USER_1);
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
        val chatId = createChat(TEST_FOR_GET_CHAT);

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
        val chatId = createChat(TEST_FOR_UPDATE_CHAT);
        val createdTime = Timestamp.valueOf(LocalDateTime.now());

        val newTitle = "Очень удачное название чата";

        val result = mvc.perform(
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

        val document = Configuration.defaultConfiguration().jsonProvider()
                .parse(result.getResponse().getContentAsString());
        val updated = DateUtils.parse(JsonPath.read(document, "$.updated"));
        Assertions.assertTrue(updated.after(createdTime));
    }

    private UUID createChat(final Chat chat) throws Exception {
        val result = mvc.perform(
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
        val document = Configuration.defaultConfiguration().jsonProvider()
                .parse(result.getResponse().getContentAsString());
        val chatId = UUID.fromString(JsonPath.read(document, "$.chatId"));
        CHAT_IDS_MAP.put(chat, chatId);
        return chatId;
    }

    private void createAllUsers() {
        ALL_USERS.stream()
                .filter(user -> user != TEST_USER_1 && user != TEST_ADMIN)
                .forEach(user -> userRepository.save(user));
    }

    @Test
    public void giveChat_addUsersToChat_userAdded() throws Exception {
        createAllUsers();

        val userIdsToAdd = TEST_FOR_ADD_USERS_CHAT.getUsers().stream()
                .filter(user -> user != TEST_FOR_ADD_USERS_CHAT.getCreator())
                .map(user -> user.getUserId().toString())
                .collect(Collectors.toUnmodifiableSet());

        val chatId = createChat(TEST_FOR_ADD_USERS_CHAT);

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
        createAllUsers();

        val chatId = createChat(TEST_FOR_GET_USERS_CHAT);
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
        createAllUsers();

        val chatId = createChat(TEST_FOR_GET_USERS_OUT_OF_CHAT);
        addUsers(chatId, TEST_FOR_GET_USERS_OUT_OF_CHAT.getUsers());

        val usersOutOfChat = ALL_USERS.stream()
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
        createAllUsers();

        val chatId = createChat(TEST_FOR_KICK_USER_OUT_OF_CHAT);
        addUsers(chatId, TEST_FOR_KICK_USER_OUT_OF_CHAT.getUsers());

        val usersWithoutKicked = ALL_USERS.stream()
                .filter(user -> user != TEST_USER_1).collect(Collectors.toUnmodifiableSet());

        mvc.perform(
                post(format("%s/%s/kick", CHATS_API_PATH, chatId))
                        .header(AUTHORIZATION, TEST_FOR_GET_USERS_OUT_OF_CHAT.getCreator() == TEST_ADMIN
                                ? adminAuthorizationHeader
                                : userAuthorizationHeader
                        )
                        .header(ORIGIN, ORIGIN_VAL)
                        .param("userId", TEST_USER_1.getUserId().toString())
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
        val chatId = createChat(TEST_FOR_BLOCK_CHAT);

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
        val chatId = createChat(TEST_FOR_UNBLOCK_CHAT);
        val chat = chatRepository.findById(chatId).orElseThrow();
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

    private void createMessage(Message message, UUID chatId) throws Exception {
        val user = message.getUser();

        val result = mvc.perform(
                post(MESSAGES_API_PATH)
                        .header(AUTHORIZATION, user == TEST_ADMIN
                                ? adminAuthorizationHeader
                                : generateAuthorizationHeader(user)
                        )
                        .header(ORIGIN, ORIGIN_VAL)
                        .param("chatId", chatId.toString())
                        .param("userId", user.getUserId().toString())
                        .param("message", message.getMessage())
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andReturn();

        val document = Configuration.defaultConfiguration().jsonProvider()
                .parse(result.getResponse().getContentAsString());
        val messageId = UUID.fromString(JsonPath.read(document, "$.messageId"));
        MESSAGE_UUID_MAP.put(message, messageId);
    }

    private void blockMessage(UUID messageId) throws Exception {
        mvc.perform(
                post(format("%s/%s/block", MESSAGES_API_PATH, messageId))
                        .header(AUTHORIZATION, adminAuthorizationHeader)
                        .header(ORIGIN, ORIGIN_VAL)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk());
    }

    @Test
    public void giveChatWithMessages_getMessages_returnAllActiveMessages() throws Exception {
        val chatId = createChat(TEST_FOR_GET_MESSAGES_CHAT);
        addUsers(chatId, TEST_FOR_GET_MESSAGES_CHAT.getUsers());
        MESSAGES.forEach(message -> {
            try {
                createMessage(message, chatId);
            } catch (Exception e) {
                log.error(e.getLocalizedMessage(), e);
                fail(e);
            }
        });
        createAllUsers();

        val chatMessages = MESSAGES.stream()
                .filter(message -> message.getStatus() == MessageStatus.ACTIVE)
                .collect(Collectors.toUnmodifiableSet());

        MESSAGES.stream().filter(message -> message.getStatus() == MessageStatus.DISABLED).forEach(message -> {
            try {
                blockMessage(MESSAGE_UUID_MAP.get(message));
            } catch (Exception e) {
                log.error(e.getLocalizedMessage(), e);
                fail(e);
            }
        });

        val chatMessagesIds = chatMessages.stream()
                .map(message -> MESSAGE_UUID_MAP.get(message).toString())
                .collect(Collectors.toUnmodifiableSet());

        val user = TEST_FOR_GET_MESSAGES_CHAT.getCreator();
        mvc.perform(
                get(format("%s/%s/messages", CHATS_API_PATH, chatId))
                        .header(AUTHORIZATION, user == TEST_ADMIN
                                ? adminAuthorizationHeader
                                : generateAuthorizationHeader(user)
                        )
                        .header(ORIGIN, ORIGIN_VAL)
                        .param("page", "0")
                        .param("size", String.valueOf(chatMessages.size()))
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value("0"))
                .andExpect(jsonPath("$.pageLimit").value("1"))
                .andExpect(jsonPath("$.totalItems").value(String.valueOf(chatMessages.size())))
                .andExpect(jsonPath("$..messageId", containsInAnyOrder(chatMessagesIds.toArray())));
    }
}
