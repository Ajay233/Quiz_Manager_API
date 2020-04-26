package com.apiTest.IntegrationTests.contoller;

import com.apiTest.Quiz.model.Answer;
import com.apiTest.Quiz.model.Question;
import com.apiTest.Quiz.repository.AnswersRepository;
import com.apiTest.Quiz.repository.QuestionRepository;
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
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
public class AnswerControllerTest {

    @Autowired
    AnswersRepository answersRepository;

    @Autowired
    QuestionRepository questionRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    MockMvc mockMvc;

    private Question question1;
    private Question question2;
    private Question question3;
    private Answer answer1;
    private Answer answer2;
    private Answer answer3;
    private Answer answer4;
    private User user;
    private String jwt;
    private HttpHeaders httpHeaders = new HttpHeaders();
    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();



    @BeforeEach
    public void setupDatabase(){
        question1 = new Question((long) 1, 1, "test question number 1 for quiz1");
        question2 = new Question((long) 1, 2, "test question number 2 for quiz1");
        question3 = new Question((long) 2, 1, "test question number 1 for quiz2");
        questionRepository.save(question1);
        questionRepository.save(question2);
        questionRepository.save(question3);

        answer1 = new Answer((long) 1, 1, "Answer1 for questionId 1", false);
        answer2 = new Answer((long) 1, 2, "Answer2 for questionId 1", true);
        answer3 = new Answer((long) 1, 3, "Answer3 for questionId 1", false);
        answer4 = new Answer((long) 2, 1, "Answer1 for questionId 2", false);
        answersRepository.save(answer1);
        answersRepository.save(answer2);
        answersRepository.save(answer3);
        answersRepository.save(answer4);

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
        answersRepository.truncateTable();
        questionRepository.truncateTable();
        userRepository.truncateTable();
    }

    @Test
    public void createAnswersTest() throws Exception {
        Answer answer5 = new Answer((long) 2, 2, "Answer2 for questionId 2", false);
        Answer answer6 = new Answer((long) 2, 3, "Answer3 for questionId 2", true);

        String body = "[{\"questionId\":\"" + answer5.getQuestionId() + "\"," + "\"answerNumber\":\"" + answer5.getAnswerNumber() +
                "\"," + "\"description\":\"" + answer5.getDescription() + "\"," + "\"correctAnswer\":\"" + answer5.getCorrectAnswer() +
                "\"}," + "{\"questionId\":\"" + answer6.getQuestionId() + "\"," + "\"answerNumber\":\"" + answer6.getAnswerNumber() +
                "\"," + "\"description\":\"" + answer6.getDescription() + "\"," + "\"correctAnswer\":\"" + answer6.getCorrectAnswer() +
                "\"}]";

        mockMvc.perform(MockMvcRequestBuilders.post("/answer/create")
                .headers(httpHeaders)
                .content(body))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("CREATED"));

        Assertions.assertTrue(answersRepository.existsById((long) 5));
        Assertions.assertTrue(answersRepository.existsById((long) 6));
    }

    @Test
    public void getAnswersByQuestionIdTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/answer/findByQuestionId")
                .headers(httpHeaders)
                .content("1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].description").value("Answer1 for questionId 1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[1].description").value("Answer2 for questionId 1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[2].description").value("Answer3 for questionId 1"));
    }

}
