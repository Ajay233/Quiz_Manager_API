package com.apiTest.IntegrationTests.repository;

import com.apiTest.User.model.User;
import com.apiTest.authentication.model.VerificationToken;
import com.apiTest.authentication.repository.VerificationTokenRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.Arrays;

@SpringBootTest
public class VerificationTokenRepositoryTest {

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private User user = new User("Joe", "Blogs", "joeBlogs@test.com", encoder.encode("testPassword"));
    private User user2 = new User("Peter", "Parker", "spidey@test.com", encoder.encode("test2password"));
    private User user3 = new User("Wade", "Wilson", "deadpool@test.com", encoder.encode("test3Password"));
    private VerificationToken verificationToken = new VerificationToken(user.getId());
    private VerificationToken verificationToken2 = new VerificationToken(user2.getId());
    private VerificationToken verificationToken3 = new VerificationToken(user3.getId());
    private ArrayList<VerificationToken> verificationTokens = new ArrayList<>(Arrays.asList(verificationToken, verificationToken2, verificationToken3));


    @BeforeEach
    public void setupDatabase() {
        for(int i = 0; i < verificationTokens.size(); i++) {
            verificationTokenRepository.save(verificationTokens.get(i));
        }
    }

    @AfterEach
    public void resetDatabase(){
        verificationTokenRepository.truncateMyTable();
    }

    @Test
    void canFindByToken() throws Exception {
        Assertions.assertEquals(verificationTokenRepository.findByToken(verificationToken2.getToken()).getId(), verificationToken2.getId());
        Assertions.assertEquals(verificationTokenRepository.findByToken(verificationToken2.getToken()).getUserId(), verificationToken2.getUserId());
        Assertions.assertEquals(verificationTokenRepository.findByToken(verificationToken2.getToken()).getToken(), verificationToken2.getToken());
    }

}
