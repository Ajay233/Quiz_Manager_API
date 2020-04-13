package com.apiTest.IntegrationTests.contoller;

import com.apiTest.User.model.User;
import com.apiTest.User.repository.UserRepository;
import com.apiTest.authentication.model.UserPrincipal;
import com.apiTest.authentication.model.VerificationToken;
import com.apiTest.authentication.repository.VerificationTokenRepository;
import com.apiTest.config.GmailConfig;
import com.apiTest.config.MailConfig;
import com.apiTest.service.GmailService;
import com.apiTest.service.QuizUserDetailsService;
import com.apiTest.util.JwtUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;
import java.util.UUID;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthenticationControllerTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private QuizUserDetailsService quizUserDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private MailConfig mailConfig;

    @Mock
    private GmailConfig gmailConfig;

    @Mock
    private GmailService gmailService;

    @Autowired
    private MockMvc mockMvc;

    private User user;
    private User user2;
    private String jwt;
    private HttpHeaders httpHeaders = new HttpHeaders();
    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();


    @BeforeEach
    public void setupDatabase(){
        user = new User("Joe", "Blogs", "joeBlogs@test.com", encoder.encode("testPassword"));
        user2 = new User("Peter", "Parker", "spidey@test.com", encoder.encode("test2password"));
        user.setVerified("true");
        userRepository.save(user);
        userRepository.save(user2);
        UserPrincipal userPrincipal = new UserPrincipal(user);
        UserDetails userDetails = userPrincipal;
        jwt = jwtUtil.generateToken(userDetails);
        httpHeaders.add("Content-Type", "application/json");
        httpHeaders.add("Authorization", "Bearer " + jwt);
    }

    @AfterEach
    public void resetDatabase(){
        List<User> users = userRepository.findAll();
        users.stream().forEach((user) -> userRepository.delete(user));

        List<VerificationToken> tokens = verificationTokenRepository.findAll();
        tokens.stream().forEach((token) -> verificationTokenRepository.delete(token));
    }

    @Test
    void signUpNewUser() throws Exception {
        String forename = "Wade";
        String surname = "Wilson";
        String email = "deadpool@test.com";
        String password = "deadpoolsPassword";

        String body = "{\"forename\":\"" + forename + "\"," + "\"surname\":\"" + surname + "\"," + "\"email\":\"" + email + "\"," + "\"password\":\"" + password + "\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/signUp")
                .header("Content-Type", "application/json")
                .content(body))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Welcome to the quiz app"));
        Assertions.assertEquals(userRepository.findByEmail("deadpool@test.com").getForename(), forename);
    }

    @Test
    void signUpExistingUser() throws Exception {
        String forename = "Wade";
        String surname = "Wilson";
        String email = "JoeBlogs@test.com"; //email already exists
        String password = "deadpoolsPassword";

        String body = "{\"forename\":\"" + forename + "\"," + "\"surname\":\"" + surname + "\"," + "\"email\":\"" + email + "\"," + "\"password\":\"" + password + "\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/signUp")
                .header("Content-Type", "application/json")
                .content(body))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("That email already has an account"));
    }

    @Test
    void verifyUser() throws Exception {
        VerificationToken verificationToken = new VerificationToken(userRepository.findByEmail(user2.getEmail()).getId());
//        verificationToken.setExpiryDate(verificationToken.calcExpiryTime(1440));
        verificationTokenRepository.save(verificationToken);

        String body = "{\"userId\":\"" + 0 + "\"," + "\"token\":\"" + verificationToken.getToken() + "\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/verify")
                .headers(httpHeaders)
                .content(body))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("verified"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.user.forename").value("Peter"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.user.surname").value("Parker"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.user.email").value("spidey@test.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.user.permission").value("USER"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.user.verified").value("true"));
    }

    @Test
    void resendToken() throws Exception {
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(userRepository.findByEmail(user2.getEmail()).getId());
//        verificationToken.setExpiryDate(verificationToken.calcExpiryTime(1440));
        verificationTokenRepository.save(verificationToken);

        String body = "{\"userId\":\"" + 0 + "\"," + "\"token\":\"" + verificationToken.getToken() + "\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/resendToken")
                .headers(httpHeaders)
                .content(body))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("RE-ISSUED"));
    }

    @Test
    void authenticateUser() throws Exception {
        String username = "joeBlogs@test.com";
        String password = "testPassword";

        String body = "{\"email\":\"" + username + "\"," + "\"password\":\"" + password + "\"}";

        mockMvc.perform(
                MockMvcRequestBuilders.post("/auth/login")
                        .header("Content-Type", "application/json")
                        .content(body))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.jwt").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.user.forename").value("Joe"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.user.surname").value("Blogs"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.user.email").value("joeBlogs@test.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.user.password").value(""))
                .andExpect(MockMvcResultMatchers.jsonPath("$.user.permission").value("USER"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.user.verified").value("true"));
    }

}
