package com.apiTest.UnitTests;

import com.apiTest.authentication.model.VerificationToken;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Calendar;

public class VerificationTokenTest {


    @Test
    void calculatesExpiryTime() {
        Long userId = Long.valueOf(1);
        VerificationToken token = new VerificationToken(userId);
        Assertions.assertEquals(token.calcExpiryTime(1440).compareTo(Calendar.getInstance().getTime()), 1);
    }

    @Test
    void verificationToken() {
        String regex = "[a-zA-Z\\d]{8}-[a-zA-Z\\d]{4}-[a-zA-Z\\d]{4}-[a-zA-Z\\d]{4}-[a-zA-Z\\d]{12}";
        Long userId = Long.valueOf(1);
        VerificationToken token = new VerificationToken(userId);
        Assertions.assertEquals(token.getUserId(), userId);
        Assertions.assertTrue(token.getToken().matches(regex));
        Assertions.assertEquals(token.getToken().length(), 36);
    }

}
