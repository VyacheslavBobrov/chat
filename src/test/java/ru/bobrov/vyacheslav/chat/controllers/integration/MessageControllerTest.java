package ru.bobrov.vyacheslav.chat.controllers.integration;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
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
import ru.bobrov.vyacheslav.chat.dataproviders.entities.Message;
import ru.bobrov.vyacheslav.chat.dataproviders.entities.User;
import ru.bobrov.vyacheslav.chat.dataproviders.repositories.MessageRepository;
import ru.bobrov.vyacheslav.chat.dataproviders.repositories.UserRepository;
import ru.bobrov.vyacheslav.chat.services.authentication.JwtUserDetailsService;
import ru.bobrov.vyacheslav.chat.services.utils.JwtTokenUtil;

import javax.annotation.PostConstruct;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.ORIGIN;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.bobrov.vyacheslav.chat.dataproviders.entities.MessageStatus.ACTIVE;
import static ru.bobrov.vyacheslav.chat.dataproviders.entities.MessageStatus.DISABLED;
import static ru.bobrov.vyacheslav.chat.services.Constants.TOKEN_PREFIX;
import static ru.bobrov.vyacheslav.chat.testdata.Messages.TEST_FOR_CREATE_MESSAGE;
import static ru.bobrov.vyacheslav.chat.testdata.Users.TEST_ADMIN;
import static ru.bobrov.vyacheslav.chat.testdata.Users.TEST_USER_1;

@SpringBootTest(classes = ChatApplication.class)
@AutoConfigureMockMvc
@WebAppConfiguration
@TestPropertySource(locations = "classpath:application.yml")
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MessageControllerTest {
    private static final String CHATS_API_PATH = "/api/v1/chat";
    private static final String MESSAGES_API_PATH = "/api/v1/message";
    private static final String ORIGIN_VAL = "localhost:8080";

    @Autowired
    UserRepository userRepository;
    @Autowired
    MessageRepository repository;
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
        String token = jwtTokenUtil.generateToken(jwtUserDetailsService.loadUserByUsername(user.getLogin()));
        return format("%s %s", TOKEN_PREFIX, token);
    }

    @BeforeEach
    public void setUpTest() {
        userRepository.save(TEST_USER_1);
        userAuthorizationHeader = generateAuthorizationHeader(TEST_USER_1);
    }

    private UUID createChat(Chat chat) throws Exception {
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

        mvc.perform(
                post(format("%s/%s/users", CHATS_API_PATH, chatId))
                        .header(AUTHORIZATION, chat.getCreator() == TEST_ADMIN
                                ? adminAuthorizationHeader
                                : userAuthorizationHeader
                        )
                        .header(ORIGIN, ORIGIN_VAL)
                        .param("chatId", chatId.toString())
                        .param("userUUIDs", chat.getUsers().stream()
                                .map(user -> user.getUserId().toString()).collect(Collectors.joining(",")))
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk());

        return chatId;
    }

    @Test
    public void giveChat_createMessage_returnCreatedMessage() throws Exception {
        final UUID chatId = createChat(TEST_FOR_CREATE_MESSAGE.getChat());
        final UUID userId = TEST_FOR_CREATE_MESSAGE.getUser().getUserId();
        final String message = TEST_FOR_CREATE_MESSAGE.getMessage();

        mvc.perform(
                post(MESSAGES_API_PATH)
                        .header(AUTHORIZATION, TEST_FOR_CREATE_MESSAGE.getUser() == TEST_ADMIN
                                ? adminAuthorizationHeader
                                : userAuthorizationHeader
                        )
                        .header(ORIGIN, ORIGIN_VAL)
                        .param("chatId", chatId.toString())
                        .param("userId", userId.toString())
                        .param("message", message)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.messageId").isNotEmpty())
                .andExpect(jsonPath("$.chat.chatId").value(chatId.toString()))
                .andExpect(jsonPath("$.user.userId").value(userId.toString()))
                .andExpect(jsonPath("$.message").value(message))
                .andExpect(jsonPath("$.status").value(ACTIVE.name()))
                .andExpect(jsonPath("$.created").isNumber())
                .andExpect(jsonPath("$.updated").isNumber());
    }

    private UUID createMessage(Message message, UUID chatId) throws Exception {
        final User user = message.getUser();

        MvcResult result = mvc.perform(
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

        Object document = Configuration.defaultConfiguration().jsonProvider()
                .parse(result.getResponse().getContentAsString());
        return UUID.fromString(JsonPath.read(document, "$.messageId"));
    }

    @Test
    public void giveMessage_getMessage_returnMessage() throws Exception {
        final UUID chatId = createChat(TEST_FOR_CREATE_MESSAGE.getChat());
        final UUID messageId = createMessage(TEST_FOR_CREATE_MESSAGE, chatId);
        final UUID userId = TEST_FOR_CREATE_MESSAGE.getUser().getUserId();
        final String message = TEST_FOR_CREATE_MESSAGE.getMessage();

        mvc.perform(
                get(format("%s/%s", MESSAGES_API_PATH, messageId.toString()))
                        .header(AUTHORIZATION, TEST_FOR_CREATE_MESSAGE.getUser() == TEST_ADMIN
                                ? adminAuthorizationHeader
                                : userAuthorizationHeader
                        )
                        .header(ORIGIN, ORIGIN_VAL)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.messageId").isNotEmpty())
                .andExpect(jsonPath("$.chat.chatId").value(chatId.toString()))
                .andExpect(jsonPath("$.user.userId").value(userId.toString()))
                .andExpect(jsonPath("$.message").value(message))
                .andExpect(jsonPath("$.status").value(ACTIVE.name()))
                .andExpect(jsonPath("$.created").isNumber())
                .andExpect(jsonPath("$.updated").isNumber());
    }

    @Test
    public void giveActiveMessage_setBlocked_returnBlockedMessage() throws Exception {
        final UUID chatId = createChat(TEST_FOR_CREATE_MESSAGE.getChat());
        final UUID messageId = createMessage(TEST_FOR_CREATE_MESSAGE, chatId);

        mvc.perform(
                post(format("%s/%s/block", MESSAGES_API_PATH, messageId))
                        .header(AUTHORIZATION, TEST_FOR_CREATE_MESSAGE.getUser() == TEST_ADMIN
                                ? adminAuthorizationHeader
                                : userAuthorizationHeader
                        )
                        .header(ORIGIN, ORIGIN_VAL)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.messageId").value(messageId.toString()))
                .andExpect(jsonPath("$.status").value(DISABLED.name()));
    }

    @Test
    public void giveActiveMessage_setUnblocked_returnUnblockedMessage() throws Exception {
        final UUID chatId = createChat(TEST_FOR_CREATE_MESSAGE.getChat());
        final UUID messageId = createMessage(TEST_FOR_CREATE_MESSAGE, chatId);

        mvc.perform(
                post(format("%s/%s/unblock", MESSAGES_API_PATH, messageId))
                        .header(AUTHORIZATION, TEST_FOR_CREATE_MESSAGE.getUser() == TEST_ADMIN
                                ? adminAuthorizationHeader
                                : userAuthorizationHeader
                        )
                        .header(ORIGIN, ORIGIN_VAL)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.messageId").value(messageId.toString()))
                .andExpect(jsonPath("$.status").value(ACTIVE.name()));
    }
}
