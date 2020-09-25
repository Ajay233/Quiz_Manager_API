package com.apiTest.IntegrationTests.contoller;

import com.apiTest.Quiz.model.Answer;
import com.apiTest.Quiz.model.Question;
import com.apiTest.Quiz.model.Quiz;
import com.apiTest.Quiz.repository.AnswersRepository;
import com.apiTest.Quiz.repository.QuestionRepository;
import com.apiTest.Quiz.repository.QuizRepository;
import com.apiTest.User.model.User;
import com.apiTest.User.repository.UserRepository;
import com.apiTest.authentication.model.UserPrincipal;
import com.apiTest.lookup.model.Lookup;
import com.apiTest.lookup.repository.LookupRepository;
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
public class QuizControllerTest {

    @Autowired
    QuizRepository quizRepository;

    @Autowired
    QuestionRepository questionRepository;

    @Autowired
    AnswersRepository answersRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    LookupRepository lookupRepository;

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    private MockMvc mockMvc;

    private Lookup lookupVal1;
    private Lookup lookupVal2;
    private Quiz quiz1;
    private Quiz quiz2;
    private Quiz quiz3;
    private User user;
    private String jwt;
    private HttpHeaders httpHeaders = new HttpHeaders();
    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();


    @BeforeEach
    public void setUpDatabase(){
        lookupVal1 = new Lookup("Test", "Quiz Category");
        lookupVal2 = new Lookup("NotTest", "Quiz Category");
        lookupRepository.save(lookupVal1);
        lookupRepository.save(lookupVal2);

        quiz1 = new Quiz("quiz1", "Test of quiz1", "Test");
        quiz2 = new Quiz("quiz2", "Test of quiz2", "NotTest");
        quiz3 = new Quiz("quiz3", "Test of quiz3", "Test");
        Question question = new Question((long) 2, 1, "test");
        Answer answer1 = new Answer((long) 1, "A", "test1", false);
        Answer answer2 = new Answer((long) 1, "B", "test2", true);
        quizRepository.save(quiz1);
        quizRepository.save(quiz2);
        quizRepository.save(quiz3);

        questionRepository.save(question);
        answersRepository.save(answer1);
        answersRepository.save(answer2);

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
        quizRepository.truncateTable();
        questionRepository.truncateTable();
        answersRepository.truncateTable();
        userRepository.truncateTable();
        lookupRepository.truncateTable();
    }

    @Test
    public void createQuizTest() throws Exception {
        Quiz quiz4 = new Quiz("quiz4", "Test of quiz4", "TestCat2");
//        String body = "{\"name\":\"" + quiz4.getName() + "\"," + "\"description\":\"" + quiz4.getDescription() +
//                "\"," + "\"category\":\"" + quiz4.getCategory() + "\"," + "\"status\":\"" + quiz4.getStatus() + "\"}";
        quiz4.setId((long) 4);

        mockMvc.perform(MockMvcRequestBuilders.post("/quiz/create")
                .headers(httpHeaders)
                .param("name", "quiz4")
                .param("description", "Test of quiz4")
                .param("category", "TestCat2"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$").value(quiz4));

        Assertions.assertNotNull(quizRepository.findByName(quiz4.getName()));
    }

    @Test
    public void deleteQuizTest() throws Exception {
        Quiz quiz2FromDB = quizRepository.findByName(quiz2.getName()).get(0);
        String body = "{\"id\":\"" + quiz2FromDB.getId() + "\"," +  "\"name\":\"" + quiz2FromDB.getName() + "\"," +
                "\"description\":\"" + quiz2FromDB.getDescription() + "\"," + "\"category\":\"" +
                quiz2FromDB.getCategory() + "\"}";

        mockMvc.perform(MockMvcRequestBuilders.delete("/quiz/delete")
                .headers(httpHeaders).content(body))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("DELETED"));

        Assertions.assertTrue(quizRepository.findByName(quiz2.getName()).isEmpty());
    }

    @Test
    public void getQuizByCategoryTest() throws Exception {
        String category = "Test";

        mockMvc.perform(MockMvcRequestBuilders.get("/quiz/findByCategory")
                .headers(httpHeaders)
                .param("category", category))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[1].name").value("quiz3"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[1].description").value("Test of quiz3"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[1].category").value("Test"));
    }

    @Test
    public void getAllQuizesTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/quiz/getAll")
                .headers(httpHeaders))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].category").value("NotTest"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].quizList.[0].name").value("quiz2"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].quizList.[0].description").value("Test of quiz2"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].quizList.[0].category").value("NotTest"));
    }

    @Test
    public void getQuizByName() throws Exception {
        Quiz quiz2FromDB = quizRepository.findByName(quiz2.getName()).get(0);
        mockMvc.perform(MockMvcRequestBuilders.get("/quiz/findByName")
                .headers(httpHeaders)
                .param("name", "quiz2"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].id").value(quiz2FromDB.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].name").value(quiz2FromDB.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].description").value(quiz2FromDB.getDescription()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].category").value(quiz2FromDB.getCategory()));
    }

    @Test
    public void updateQuizDetailsTest() throws Exception {
        Long quiz2Id = quizRepository.findByName(quiz2.getName()).get(0).getId();
        Quiz updatedQuiz2 = new Quiz("updatedQuiz2", "Test of quiz2 after update", "NotTest");
        updatedQuiz2.setId(quiz2Id);

//        String body = "{\"id\":\"" + updatedQuiz2.getId() + "\"," +  "\"name\":\"" + updatedQuiz2.getName() + "\"," +
//                "\"description\":\"" + updatedQuiz2.getDescription() + "\"," + "\"category\":\"" +
//                updatedQuiz2.getCategory() + "\"," + "\"status\":\"" + updatedQuiz2.getStatus() + "\"}";

        mockMvc.perform(MockMvcRequestBuilders.put("/quiz/update")
                .headers(httpHeaders)
                .param("id", "2")
                .param("name", "updatedQuiz2")
                .param("description", "Test of quiz2 after update")
                .param("category", "NotTest"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$").value(updatedQuiz2));

        Assertions.assertEquals(quizRepository.findById(quiz2Id).get(), updatedQuiz2);
    }

    @Test
    public void updateQuizStatusTest() throws Exception {

        Quiz updatedQuiz = quizRepository.findById((long) 2).get();
        updatedQuiz.setStatus("READY");

        String body = "{\"id\":\"" + updatedQuiz.getId() + "\"," +  "\"name\":\"" + updatedQuiz.getName() + "\"," +
                "\"description\":\"" + updatedQuiz.getDescription() + "\"," + "\"category\":\"" +
                updatedQuiz.getCategory() + "\"," + "\"status\":\"" + updatedQuiz.getStatus() + "\"}";

        mockMvc.perform(MockMvcRequestBuilders.put("/quiz/updateStatus")
                .headers(httpHeaders)
                .content(body))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$").value(updatedQuiz));

        Assertions.assertEquals(quizRepository.findById((long) 2).get(), updatedQuiz);
    }

}
