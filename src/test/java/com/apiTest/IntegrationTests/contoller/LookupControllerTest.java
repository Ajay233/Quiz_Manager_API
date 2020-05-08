package com.apiTest.IntegrationTests.contoller;

import com.apiTest.User.model.User;
import com.apiTest.User.repository.UserRepository;
import com.apiTest.authentication.model.UserPrincipal;
import com.apiTest.lookup.model.Lookup;
import com.apiTest.lookup.repository.LookupRepository;
import com.apiTest.util.JwtUtil;
import org.junit.jupiter.api.AfterEach;
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
public class LookupControllerTest {

    @Autowired
    LookupRepository lookupRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    MockMvc mockMvc;


    private Lookup lookupVal1 = new Lookup("Films", "Quiz Category");
    private Lookup lookupVal2 = new Lookup("Games", "Quiz Category");
    private Lookup lookupVal3 = new Lookup("TV", "Quiz Category");
    private User user;
    private String jwt;
    private HttpHeaders httpHeaders = new HttpHeaders();
    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @BeforeEach
    public void setupDatabase(){
        lookupRepository.save(lookupVal1);
        lookupRepository.save(lookupVal2);
        lookupRepository.save(lookupVal3);

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
        userRepository.truncateTable();
        lookupRepository.truncateTable();
    }

    @Test
    public void getListOfQuizCategoriesTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/lookup/quizCategories")
                .headers(httpHeaders))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0]").value(lookupVal1.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[1]").value(lookupVal2.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[2]").value(lookupVal3.getName()));
    }

}
