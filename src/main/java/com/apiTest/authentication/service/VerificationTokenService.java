package com.apiTest.authentication.service;

import com.apiTest.User.model.User;
import com.apiTest.User.repository.UserRepository;
import com.apiTest.authentication.model.VerificationToken;
import com.apiTest.authentication.repository.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;

@Service
public class VerificationTokenService {

    @Autowired
    VerificationTokenRepository tokenRepository;

    @Autowired
    UserRepository userRepository;

    public VerificationToken createToken(User savedUser){
        VerificationToken verificationToken = new VerificationToken(savedUser.getId());
        verificationToken.setExpiryDate(verificationToken.calcExpiryTime(1)); // override the default time

        // save the token to the token table
        tokenRepository.save(verificationToken);
        return verificationToken;
    }

    public Boolean tokenVerified(VerificationToken verificationToken){
        final Calendar cal = Calendar.getInstance();
        return (verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0;
    }

    public VerificationToken replaceToken(VerificationToken token){
        // Verify token
        VerificationToken verificationToken = tokenRepository.findByToken(token.getToken());
        User user = userRepository.findById(verificationToken.getUserId()).get();

        // Delete the old token
        tokenRepository.delete(verificationToken);

        return createToken(user);
    }

    public VerificationToken replaceToken(User user){
        // If a token exists delete it (as we won't have the token, we have to find it using the user id)
        if(tokenRepository.findByUserId(user.getId()) != null){
            VerificationToken tokenToDelete = tokenRepository.findByUserId(user.getId());
            tokenRepository.delete(tokenToDelete);
        }
        return createToken(user);
    }

}
