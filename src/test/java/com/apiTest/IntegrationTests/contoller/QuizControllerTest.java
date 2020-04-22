package com.apiTest.IntegrationTests.contoller;

import com.apiTest.Quiz.model.Quiz;
import com.apiTest.Quiz.repository.QuizRepository;
import com.apiTest.User.model.User;
import com.apiTest.User.repository.UserRepository;
import com.apiTest.authentication.model.UserPrincipal;
import com.apiTest.util.JwtUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
public class QuizControllerTest {

    @Autowired
    QuizRepository quizRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    private MockMvc mockMvc;

    private Quiz quiz1;
    private Quiz quiz2;
    private Quiz quiz3;
    private User user;
    private String jwt;
    private HttpHeaders httpHeaders = new HttpHeaders();
    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();


    @BeforeEach
    public void setUpDatabase(){
        quiz1 = new Quiz("quiz1", "Test of quiz1", "Test");
        quiz2 = new Quiz("quiz2", "Test of quiz2", "NotTest");
        quiz3 = new Quiz("quiz3", "Test of quiz3", "Test");
        quizRepository.save(quiz1);
        quizRepository.save(quiz2);
        quizRepository.save(quiz3);

        user = new User("Joe", "Blogs", "joeBlogs@test.com", encoder.encode("testPassword"));
        userRepository.save(user);

        UserPrincipal userPrincipal = new UserPrincipal(user);
        UserDetails userDetails = userPrincipal;
        jwt = jwtUtil.generateToken(userDetails);
        httpHeaders.add("Content-Type", "application/json");
        httpHeaders.add("Authorization", "Bearer " + jwt);
    }

    @AfterEach
    public void resetDatabase(){
        List<Quiz> quizes = quizRepository.findAll();
        quizes.stream().forEach((quiz) -> quizRepository.delete(quiz));

        userRepository.delete(user);
    }

    @Test
    public void createQuizTest() throws Exception {
        Quiz quiz4 = new Quiz("quiz4", "Test of quiz4", "TestCat2");
        String body = "{\"name\":\"" + quiz4.getName() + "\"," + "\"description\":\"" + quiz4.getDescription() + "\"," + "\"category\":\"" + quiz4.getCategory() + "\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/quiz/create")
                .headers(httpHeaders)
                .content(body))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("CREATED"));

        Assertions.assertNotNull(quizRepository.findByName(quiz4.getName()));
    }


}
