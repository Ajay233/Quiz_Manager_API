package com.apiTest.contoller;

import com.apiTest.config.GmailConfig;
import com.apiTest.config.MailConfig;
import com.apiTest.model.User;
import com.apiTest.model.UserPrincipal;
import com.apiTest.repository.UserRepository;
import com.apiTest.repository.VerificationTokenRepository;
import com.apiTest.service.QuizUserDetailsService;
import com.apiTest.util.JwtUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;


@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

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

    @Autowired
    private GmailConfig gmailConfig;

    @Autowired
    ApplicationEventPublisher eventPublisher;

    @Autowired
    private MockMvc mockMvc;

    private User user;
    private String jwt;
    private HttpHeaders httpHeaders = new HttpHeaders();
    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();


    @BeforeEach
    public void setupDatabase(){
        user = new User("Joe", "Blogs", "joeBlogs@test.com", encoder.encode("testPassword"));
        user.setVerified("true");
        userRepository.save(user);
        UserPrincipal userPrincipal = new UserPrincipal(user);
        UserDetails userDetails = userPrincipal;
        jwt = jwtUtil.generateToken(userDetails);
        httpHeaders.add("Content-Type", "application/json");
        httpHeaders.add("Authorization", "Bearer " + jwt);
    }

    @AfterEach
    public void resetDatabase(){
        userRepository.delete(user);
    }

    @Test
    void getAllUsers() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/users").headers(httpHeaders))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void updateUserData() throws Exception {
        String forename = "Joey";
        String surname = "Blogger";
        String email = "joeBlogs@test.com";
        String newEmail = "joeBlogs@test.com";

        String body = "{\"forename\":\"" + forename + "\"," + "\"surname\":\"" + surname + "\"," + "\"email\":\"" + email + "\"," + "\"newEmail\":\"" + newEmail + "\"}";

        mockMvc.perform(MockMvcRequestBuilders.put("/users/update").headers(httpHeaders).content(body))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.content().string("UPDATED"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Assertions.assertEquals(userRepository.findByEmail("joeBlogs@test.com").getForename(), forename);
        Assertions.assertEquals(userRepository.findByEmail("joeBlogs@test.com").getSurname(), surname);
    }

    @Test
    void updatePassword() throws Exception {
    }

    @Test
    void deleteAccount() throws Exception {
    }

    @Test
    void signUpNewUser() throws Exception {
    }

    @Test
    void verifyUser() throws Exception {
    }

    @Test
    void resendToken() throws Exception {
    }

    @Test
    void authenticateUser() throws Exception {

        String username = "joeBlogs@test.com";
        String password = "testPassword";

        String body = "{\"email\":\"" + username + "\"," + "\"password\":\"" + password + "\"}";

        mockMvc.perform(
                MockMvcRequestBuilders.post("/users/auth/login")
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