package com.apiTest.IntegrationTests.contoller;

import com.apiTest.User.model.User;
import com.apiTest.User.repository.UserRepository;
import com.apiTest.authentication.model.UserPrincipal;
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

    @Mock
    private GmailConfig gmailConfig;

    @Mock
    private GmailService gmailService;

//    This will be needed if I implement calling services when events fire
//    @Autowired
//    ApplicationEventPublisher eventPublisher;

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
        user.setVerified(true);
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
        userRepository.truncateTable();
        verificationTokenRepository.truncateMyTable();
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
        String id = "1";
        String forename = "Joey";
        String surname = "Blogger";
        String email = "joeBlogs@test.com";
        String newEmail = "joeBlogs@test.com";

        String body = "{\"id\":\"" + id + "\"," + "\"forename\":\"" + forename + "\"," + "\"surname\":\"" + surname + "\"," + "\"email\":\"" + email + "\"," + "\"newEmail\":\"" + newEmail + "\"}";

        mockMvc.perform(MockMvcRequestBuilders.put("/users/update").headers(httpHeaders).content(body))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.content().string("UPDATED"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Assertions.assertEquals(userRepository.findByEmail("joeBlogs@test.com").getForename(), forename);
        Assertions.assertEquals(userRepository.findByEmail("joeBlogs@test.com").getSurname(), surname);
    }

    @Test
    void updatePassword() throws Exception {
        String id = "1";
        String email = "joeBlogs@test.com";
        String password = "testPassword";
        String newPassword = "newTestPassword";
        String retypedPassword = "newTestPassword";

        String body = "{\"id\":\"" + id + "\"," + "\"email\":\"" + email + "\"," + "\"password\":\"" + password +
                "\"," + "\"newPassword\":\"" + newPassword + "\"," + "\"retypedPassword\":\"" + retypedPassword + "\"}";
        String authBody = "{\"email\":\"" + email + "\"," + "\"password\":\"" + newPassword + "\"}";

        // Update password
        mockMvc.perform(MockMvcRequestBuilders.put("/users/updatePassword")
                .headers(httpHeaders)
                .content(body))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("UPDATED"));

        // Then attempt login with the new password
        mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                .header("Content-Type", "application/json")
                .content(authBody))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void updatePasswordWithIncorrectId() throws Exception {
        String id = "99";
        String email = "joeBloggers@test.com";
        String password = "testPassword";
        String newPassword = "newTestPassword";
        String retypedPassword = "newTestPassword";

        String body = "{\"id\":\"" + id + "\"," + "\"email\":\"" + email + "\"," + "\"password\":\"" + password +
                "\"," + "\"newPassword\":\"" + newPassword + "\"," + "\"retypedPassword\":\"" + retypedPassword + "\"}";

        mockMvc.perform(MockMvcRequestBuilders.put("/users/updatePassword")
                .headers(httpHeaders)
                .content(body))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("NO MATCH"));
    }

    @Test
    void updatePasswordWithIncorrectPassword() throws Exception {
        String id = "1";
        String email = "joeBlogs@test.com";
        String password = "Password";
        String newPassword = "newTestPassword";
        String retypedPassword = "newTestPassword";

        String body = "{\"id\":\"" + id + "\"," + "\"email\":\"" + email + "\"," + "\"password\":\"" +
                password + "\"," + "\"newPassword\":\"" + newPassword + "\"," + "\"retypedPassword\":\""
                + retypedPassword + "\"}";

        mockMvc.perform(MockMvcRequestBuilders.put("/users/updatePassword")
                .headers(httpHeaders)
                .content(body))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("PASSWORD INCORRECT"));
    }

    @Test
    void passwordMismatchTest() throws Exception {
        String id = "1";
        String email = "joeBlogs@test.com";
        String password = "Password";
        String newPassword = "newTestPassword";
        String retypedPassword = "differentNewTestPassword";

        String body = "{\"id\":\"" + id + "\"," + "\"email\":\"" + email + "\"," + "\"password\":\"" +
                password + "\"," + "\"newPassword\":\"" + newPassword + "\"," + "\"retypedPassword\":\""
                + retypedPassword + "\"}";

        mockMvc.perform(MockMvcRequestBuilders.put("/users/updatePassword")
                .headers(httpHeaders)
                .content(body))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("PASSWORD MISMATCH"));
    }

    @Test
    void deleteAccount() throws Exception {
        String id = "2";
        String body = "{\"id\":\"" + id + "\"}";

        mockMvc.perform(MockMvcRequestBuilders.delete("/users/deleteAccount")
                .headers(httpHeaders)
                .content(body)).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("DELETED"));

        Assertions.assertFalse(userRepository.existsById((long) 2));
    }

    @Test
    void getUserByEmailTest() throws Exception{
        String email = "joeBlogs@test.com";
        user.setId(1);
        user.setPassword("N/A");

        mockMvc.perform(MockMvcRequestBuilders.get("/users/findByEmail")
                .headers(httpHeaders)
                .param("email", email))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").value(user));
    }

    @Test
    void getUserByIdTest() throws Exception{
        String id = "1";
        user.setId(1);
        user.setPassword("N/A");

        mockMvc.perform(MockMvcRequestBuilders.get("/users/findById")
                .headers(httpHeaders)
                .param("id", id))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").value(user));
    }

    @Test
    void updateUserPermission() throws Exception {

        String body = "{\"id\":\"" + "1" + "\"," + "\"permission\":\"" + "ADMIN" + "\"}";

        mockMvc.perform(MockMvcRequestBuilders.put("/users/updatePermission")
                .headers(httpHeaders).content(body))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("UPDATED"));

        Assertions.assertEquals(userRepository.findByEmail(user.getEmail()).getPermission(), "ADMIN");

    }

    @Test
    public void updatePermissionRequestTest() throws Exception{
        User savedUser1 = userRepository.findById((long) 1).get();
        String requestedPermision = "ADMIN";
        String body = "{\"id\":\"" + savedUser1.getId() + "\"," + "\"forename\":\"" + savedUser1.getForename() +
                "\"," + "\"surname\":\"" + savedUser1.getSurname() + "\"," + "\"email\":\"" + savedUser1.getEmail() +
                "\"," + "\"email\":\"" + savedUser1.getEmail() + "\"," + "\"permission\":\"" + requestedPermision + "\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/users/updatePermissionRequest")
                .headers(httpHeaders)
                .content(body))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Request sent to Admin"));
    }

}