package com.apiTest.UnitTests;

import com.apiTest.User.model.User;
import com.apiTest.authentication.model.UserPrincipal;
import com.apiTest.util.JwtUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;

@SpringBootTest
public class JwtUtilTests {

    @Autowired
    private JwtUtil jwtUtil;

    private String jwt;
    private User user;

    @BeforeEach
    public void setUpJwt(){
        user = new User("Joe", "Bloggs", "JoeBloggs@test.com", "testPassword");
        UserPrincipal userPrincipal = new UserPrincipal(user);
        UserDetails userDetails = userPrincipal;
        jwt = jwtUtil.generateToken(userDetails);
    }

    @Test
    public void extractUserNameTest(){
        String userName = jwtUtil.extractUsername(jwt);
        Assertions.assertEquals(userName, user.getEmail());
    }

    @Test
    public void extractExpirationTest(){
        Date date = jwtUtil.extractExpiration(jwt);
        Assertions.assertFalse(date.before(new Date()));
    }

}
