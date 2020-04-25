package com.apiTest.IntegrationTests.repository;

import com.apiTest.User.model.User;
import com.apiTest.User.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private User user1;
    private User user2;

    @BeforeEach
    public void setUpDatabase() {
        user1 = new User("Joe", "Blogs", "joeBlogs@test.com", encoder.encode("testPassword"));
        user2 = new User("Peter", "Parker", "spidey@test.com", encoder.encode("test2password"));
        userRepository.save(user1);
        userRepository.save(user2);
    }

    @AfterEach
    public void resetDatabase() {
        userRepository.truncateTable();
    }

    @Test
    void canFindByEmail() throws Exception {
        Assertions.assertEquals(userRepository.findByEmail("joeBlogs@test.com"), user1);
        Assertions.assertEquals(userRepository.findByEmail("spidey@test.com"), user2);
    }

}
