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
import ru.bobrov.vyacheslav.chat.dataproviders.repositories.UserRepository;
import ru.bobrov.vyacheslav.chat.services.authentication.JwtUserDetailsService;
import ru.bobrov.vyacheslav.chat.services.utils.JwtTokenUtil;
import ru.bobrov.vyacheslav.chat.utils.DateUtils;

import javax.annotation.PostConstruct;
import java.sql.Timestamp;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.ORIGIN;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.bobrov.vyacheslav.chat.dataproviders.entities.UserStatus.ACTIVE;
import static ru.bobrov.vyacheslav.chat.dataproviders.entities.UserStatus.DISABLED;
import static ru.bobrov.vyacheslav.chat.services.Constants.TOKEN_PREFIX;
import static ru.bobrov.vyacheslav.chat.testdata.Chats.CHATS;
import static ru.bobrov.vyacheslav.chat.testdata.Chats.CHAT_IDS_MAP;
import static ru.bobrov.vyacheslav.chat.testdata.Users.*;

@SpringBootTest(classes = ChatApplication.class)
@AutoConfigureMockMvc
@WebAppConfiguration
@TestPropertySource(locations = "classpath:application.yml")
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserControllerTest {
    private static final String USERS_API_PATH = "/api/v1/user";
    private static final String CHATS_API_PATH = "/api/v1/chat";
    private static final String ORIGIN_VAL = "localhost:8080";

    @Autowired
    UserRepository userRepository;
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
        userRepository.save(TEST_USER_1);
        String userToken = jwtTokenUtil.generateToken(jwtUserDetailsService.loadUserByUsername(TEST_USER_1.getLogin()));
        userAuthorizationHeader = format("%s %s", TOKEN_PREFIX, userToken);
    }

    @Test
    public void givenUser_getUserByUUID_returnUser() throws Exception {
        mvc.perform(
                get(USERS_API_PATH + "/" + TEST_USER_1.getUserId())
                        .header(AUTHORIZATION, adminAuthorizationHeader)
                        .header(ORIGIN, ORIGIN_VAL)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(TEST_USER_1.getUserId().toString()))
                .andExpect(jsonPath("$.name").value(TEST_USER_1.getName()))
                .andExpect(jsonPath("$.login").value(TEST_USER_1.getLogin()))
                .andExpect(jsonPath("$.status").value(TEST_USER_1.getStatus().name()))
                .andExpect(jsonPath("$.role").value(TEST_USER_1.getRole().name()))
                .andExpect(jsonPath("$.created").value(TEST_USER_1.getCreated().toLocalDateTime().toString()))
                .andExpect(jsonPath("$.updated").value(TEST_USER_1.getUpdated().toLocalDateTime().toString()));
    }

    @Test
    public void giveUser_postUpdateUser_returnUpdated() throws Exception {
        MvcResult result = mvc.perform(
                post(USERS_API_PATH + "/" + TEST_USER_1.getUserId())
                        .header(AUTHORIZATION, adminAuthorizationHeader)
                        .header(ORIGIN, ORIGIN_VAL)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .param("name", "changed_user_name")
                        .param("login", "changed user login")
                        .param("password", "changed_user_password")
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(TEST_USER_1.getUserId().toString()))
                .andExpect(jsonPath("$.name").value("changed_user_name"))
                .andExpect(jsonPath("$.login").value("changed user login"))
                .andExpect(jsonPath("$.status").value(TEST_USER_1.getStatus().name()))
                .andExpect(jsonPath("$.created").value(TEST_USER_1.getCreated().toLocalDateTime().toString()))
                .andReturn();

        Object document = Configuration.defaultConfiguration().jsonProvider()
                .parse(result.getResponse().getContentAsString());
        Timestamp updated = DateUtils.parse(JsonPath.read(document, "$.updated"));
        Assertions.assertTrue(updated.after(TEST_USER_1.getUpdated()));
    }

    @Test
    public void giveActiveUser_blockUser_returnBlocked() throws Exception {
        mvc.perform(
                post(format("%s/%s/block", USERS_API_PATH, TEST_USER_1.getUserId()))
                        .header(AUTHORIZATION, adminAuthorizationHeader)
                        .header(ORIGIN, ORIGIN_VAL)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(TEST_USER_1.getUserId().toString()))
                .andExpect(jsonPath("$.status").value(DISABLED.name()));
    }

    @Test
    public void giveActiveUser_unblockUser_returnUnblocked() throws Exception {
        mvc.perform(
                post(format("%s/%s/unblock", USERS_API_PATH, TEST_USER_1.getUserId()))
                        .header(AUTHORIZATION, adminAuthorizationHeader)
                        .header(ORIGIN, ORIGIN_VAL)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(TEST_USER_1.getUserId().toString()))
                .andExpect(jsonPath("$.status").value(ACTIVE.name()));
    }

    private void saveAllUsers() {
        ALL_USERS.stream()
                .filter(user -> user != TEST_ADMIN || user != TEST_USER_1)
                .forEach(user -> userRepository.save(user));
    }

    @Test
    public void giveUsers_getAllUsers_returnAllActiveUsers() throws Exception {
        saveAllUsers();
        final Set<String> activeUserIds = ALL_USERS.stream()
                .filter(user -> user.getStatus() == ACTIVE)
                .map(user -> user.getUserId().toString())
                .collect(Collectors.toUnmodifiableSet());

        mvc.perform(
                get(USERS_API_PATH)
                        .header(AUTHORIZATION, adminAuthorizationHeader)
                        .header(ORIGIN, ORIGIN_VAL)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .param("page", "0")
                        .param("size", String.valueOf(ALL_USERS.size()))
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value("0"))
                .andExpect(jsonPath("$.pageLimit").value("1"))
                .andExpect(jsonPath("$.totalItems").value(String.valueOf(activeUserIds.size())))
                .andExpect(jsonPath("$..userId", containsInAnyOrder(activeUserIds.toArray())));
    }

    private void createChats() {
        CHATS.forEach(chat -> {
                    try {
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
                        CHAT_IDS_MAP.put(chat, UUID.fromString(JsonPath.read(document, "$.chatId")));
                    } catch (Exception e) {
                        log.error(e.getLocalizedMessage(), e);
                        fail(e.getMessage());
                    }
                }
        );
    }

    private void addUsersToChats() {
        CHATS.forEach(chat -> {
            final Set<String> userIdsToAdd = chat.getUsers().stream()
                    .filter(user -> user != chat.getCreator())
                    .map(user -> user.getUserId().toString())
                    .collect(Collectors.toUnmodifiableSet());

            try {
                mvc.perform(
                        post(format("%s/%s/users", CHATS_API_PATH, CHAT_IDS_MAP.get(chat)))
                                .header(AUTHORIZATION, chat.getCreator() == TEST_ADMIN
                                        ? adminAuthorizationHeader
                                        : userAuthorizationHeader
                                )
                                .header(ORIGIN, ORIGIN_VAL)
                                .param("chatId", chat.getCreator().getUserId().toString())
                                .param("userUUIDs", String.join(",", userIdsToAdd))
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .accept(MediaType.APPLICATION_JSON)
                )
                        .andExpect(status().isOk());
            } catch (Exception e) {
                log.error(e.getLocalizedMessage(), e);
                fail(e.getMessage());
            }
        });
    }

    private void blockChatsIfNeeded() {
        CHATS.stream().filter(chat -> chat.getStatus() == ChatStatus.DISABLED).forEach(chat -> {
            try {
                mvc.perform(
                        post(format("%s/%s/block", CHATS_API_PATH, CHAT_IDS_MAP.get(chat)))
                                .header(AUTHORIZATION, chat.getCreator() == TEST_ADMIN
                                        ? adminAuthorizationHeader
                                        : userAuthorizationHeader
                                )
                                .header(ORIGIN, ORIGIN_VAL)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .accept(MediaType.APPLICATION_JSON)
                )
                        .andExpect(status().isOk());
            } catch (Exception e) {
                log.error(e.getLocalizedMessage(), e);
                fail(e.getMessage());
            }
        });
    }

    @Test
    public void giveUserChats_getAllChatsForUser_returnChats() throws Exception {
        createChats();
        addUsersToChats();
        blockChatsIfNeeded();

        final Set<Chat> chatsForUser = CHATS.stream()
                .filter(chat -> chat.getUsers().contains(TEST_USER_1) && chat.getStatus() == ChatStatus.ACTIVE)
                .collect(Collectors.toUnmodifiableSet());

        mvc.perform(
                get(format("%s/%s/chats", USERS_API_PATH, TEST_USER_1.getUserId()))
                        .header(AUTHORIZATION, adminAuthorizationHeader)
                        .header(ORIGIN, ORIGIN_VAL)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(chatsForUser.size())))
                .andExpect(jsonPath("$.[*].chatId",
                        containsInAnyOrder(chatsForUser.stream()
                                .map(chat -> CHAT_IDS_MAP.get(chat).toString()).toArray())));
    }
}
