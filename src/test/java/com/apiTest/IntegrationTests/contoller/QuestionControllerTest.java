package com.apiTest.IntegrationTests.contoller;

import com.apiTest.Quiz.model.Question;
import com.apiTest.Quiz.model.Quiz;
import com.apiTest.Quiz.repository.QuestionRepository;
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
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
public class QuestionControllerTest {

    @Autowired
    QuestionRepository questionRepository;

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
    private Question question1;
    private Question question2;
    private Question question3;
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


        question1 = new Question((long) 1, 1, "test question number 1 for quiz1");
        question2 = new Question((long) 1, 2, "test question number 2 for quiz1");
        question3 = new Question((long) 2, 1, "test question number 1 for quiz2");
        questionRepository.save(question1);
        questionRepository.save(question2);
        questionRepository.save(question3);

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
        questionRepository.truncateTable();
        quizRepository.truncateTable();
        userRepository.truncateTable();
    }

    @Test
    public void createQuestion() throws Exception {
        Question question4 = new Question((long) 2, 2, "Test question number 2 for quiz2");
        Question question5 = new Question((long) 2, 3, "Test question number 3 for quiz2");
        question4.setId((long) 4);
        question5.setId((long) 5);
        String body = "[{\"quizId\":\"" + question4.getQuizId() + "\"," + "\"questionNumber\":\"" +
                question4.getQuestionNumber() + "\"," + "\"description\":\"" + question4.getDescription() +
                "\"},{\"quizId\":\"" + question5.getQuizId() + "\"," + "\"questionNumber\":\"" +
                question5.getQuestionNumber() + "\"," + "\"description\":\"" + question5.getDescription() + "\"}]";

        mockMvc.perform(MockMvcRequestBuilders.post("/question/create")
                .headers(httpHeaders)
                .content(body))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[1]").value(question5));

        Assertions.assertEquals(questionRepository.findById((long) 4).get(), question4);
        Assertions.assertEquals(questionRepository.findById((long) 5).get(), question5);

    }

    @Test
    public void getQuestionsByQuizIdTest() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.get("/question/findByQuizId")
                .headers(httpHeaders).param("quizId", "1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].description").value("test question number 1 for quiz1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[1].description").value("test question number 2 for quiz1"));
    }

    @Test
    public void updateQuestionsTest() throws Exception {
        Question updatedQuestion3 = new Question((long) 1, 3, "Test question number 3 for quiz1");
        String body = "[{\"id\":\"" + "3" + "\"," + "\"quizId\":\"" + updatedQuestion3.getQuizId() + "\"," + "\"questionNumber\":\"" + updatedQuestion3.getQuestionNumber() + "\"," + "\"description\":\"" + updatedQuestion3.getDescription() + "\"}]";

        mockMvc.perform(MockMvcRequestBuilders.put("/question/update")
                .headers(httpHeaders)
                .content(body))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("UPDATED"));
    }

    @Test
    public void deleteQuestionsTest() throws Exception {
        String body = "[{\"id\":\"" + "3" + "\"," + "\"quizId\":\"" + question3.getQuizId() + "\"," + "\"questionNumber\":\"" + question3.getQuestionNumber() + "\"," + "\"description\":\"" + question3.getDescription() + "\"}]";

        mockMvc.perform(MockMvcRequestBuilders.delete("/question/delete")
                .headers(httpHeaders)
                .content(body))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("DELETED"));

        Assertions.assertFalse(questionRepository.existsById((long) 3));
    }

}
