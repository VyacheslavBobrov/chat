package ru.bobrov.vyacheslav.chat.controllers.integration;

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
import ru.bobrov.vyacheslav.chat.dataproviders.entities.User;
import ru.bobrov.vyacheslav.chat.dataproviders.repositories.UserRepository;
import ru.bobrov.vyacheslav.chat.services.authentication.JwtUserDetailsService;
import ru.bobrov.vyacheslav.chat.services.utils.JwtTokenUtil;

import javax.annotation.PostConstruct;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

import static java.lang.String.format;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.ORIGIN;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.bobrov.vyacheslav.chat.dataproviders.entities.UserRole.ADMIN;
import static ru.bobrov.vyacheslav.chat.dataproviders.entities.UserRole.USER;
import static ru.bobrov.vyacheslav.chat.dataproviders.entities.UserStatus.ACTIVE;
import static ru.bobrov.vyacheslav.chat.dataproviders.entities.UserStatus.DISABLED;
import static ru.bobrov.vyacheslav.chat.services.Constants.TOKEN_PREFIX;

@SpringBootTest(classes = ChatApplication.class)
@AutoConfigureMockMvc
@WebAppConfiguration
@TestPropertySource(locations = "classpath:application.yml")
public class UserControllerTest {
    private static final User TEST_ADMIN = User.builder()
            .userId(UUID.randomUUID())
            .login("test_admin")
            .password("adminadmin")
            .name("Шмуль Сидорович Аксельбант")
            .role(ADMIN)
            .status(ACTIVE)
            .created(Timestamp.valueOf(LocalDateTime.now()))
            .updated(Timestamp.valueOf(LocalDateTime.now()))
            .build();
    private static final User TEST_USER = User.builder()
            .userId(UUID.randomUUID())
            .login("user")
            .password("useruser")
            .name("Григорий Хачатурянович Айншлютц")
            .role(USER)
            .status(ACTIVE)
            .created(Timestamp.valueOf(LocalDateTime.now()))
            .updated(Timestamp.valueOf(LocalDateTime.now()))
            .build();

    private static final String API_PATH = "/api/v1/user";
    private static final String ORIGIN_VAL = "localhost:8080";
    @Autowired
    UserRepository userRepository;
    @Autowired
    JwtTokenUtil jwtTokenUtil;
    @Autowired
    JwtUserDetailsService jwtUserDetailsService;
    @Autowired
    private MockMvc mvc;
    private String authorizationHeader;

    @PostConstruct
    public void setUp() {
        userRepository.save(TEST_ADMIN);
        String adminToken = jwtTokenUtil.generateToken(jwtUserDetailsService.loadUserByUsername(TEST_ADMIN.getLogin()));
        authorizationHeader = format("%s %s", TOKEN_PREFIX, adminToken);
    }

    @BeforeEach
    public void setUpTest() {
        userRepository.save(TEST_USER);
    }

    @Test
    public void givenUser_getUserByUUID_returnUser() throws Exception {
        mvc.perform(
                get(API_PATH + "/" + TEST_USER.getUserId())
                        .header(AUTHORIZATION, authorizationHeader)
                        .header(ORIGIN, ORIGIN_VAL)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(TEST_USER.getUserId().toString()))
                .andExpect(jsonPath("$.name").value(TEST_USER.getName()))
                .andExpect(jsonPath("$.login").value(TEST_USER.getLogin()))
                .andExpect(jsonPath("$.status").value(TEST_USER.getStatus().name()))
                .andExpect(jsonPath("$.created").value(TEST_USER.getCreated().toString()))
                .andExpect(jsonPath("$.updated").value(TEST_USER.getUpdated().toString()));
    }

    @Test
    public void giveUser_postUpdateUser_returnUpdated() throws Exception {
        mvc.perform(
                post(API_PATH + "/" + TEST_USER.getUserId())
                        .header(AUTHORIZATION, authorizationHeader)
                        .header(ORIGIN, ORIGIN_VAL)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .param("name", "changed_user_name")
                        .param("login", "changed user login")
                        .param("password", "changed_user_password")
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(TEST_USER.getUserId().toString()))
                .andExpect(jsonPath("$.name").value("changed_user_name"))
                .andExpect(jsonPath("$.login").value("changed user login"))
                .andExpect(jsonPath("$.status").value(TEST_USER.getStatus().name()))
                .andExpect(jsonPath("$.created").value(TEST_USER.getCreated().toString()));
    }

    @Test
    public void giveActiveUser_blockUser_returnBlocked() throws Exception {
        mvc.perform(
                post(format("%s/%s/block", API_PATH, TEST_USER.getUserId()))
                        .header(AUTHORIZATION, authorizationHeader)
                        .header(ORIGIN, ORIGIN_VAL)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(TEST_USER.getUserId().toString()))
                .andExpect(jsonPath("$.status").value(DISABLED.name()));
    }

    @Test
    public void giveActiveUser_unblockUser_returnUnblocked() throws Exception {
        mvc.perform(
                post(format("%s/%s/unblock", API_PATH, TEST_USER.getUserId()))
                        .header(AUTHORIZATION, authorizationHeader)
                        .header(ORIGIN, ORIGIN_VAL)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(TEST_USER.getUserId().toString()))
                .andExpect(jsonPath("$.status").value(ACTIVE.name()));
    }
}
