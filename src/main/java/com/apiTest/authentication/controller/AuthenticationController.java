package com.apiTest.authentication.controller;

import com.apiTest.User.model.User;
import com.apiTest.User.repository.UserRepository;
import com.apiTest.authentication.model.AuthenticationRequest;
import com.apiTest.authentication.model.AuthenticationResponse;
import com.apiTest.authentication.model.VerificationResponse;
import com.apiTest.authentication.model.VerificationToken;
import com.apiTest.authentication.repository.VerificationTokenRepository;
import com.apiTest.service.MailService;
import com.apiTest.service.QuizUserDetailsService;
import com.apiTest.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Calendar;

@RestController
public class AuthenticationController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private QuizUserDetailsService quizUserDetailsService;

    @Autowired
    private JwtUtil jwtTokenUtil;

    @Autowired
    private MailService mailService;

    // SIGN UP
    @RequestMapping(value = "/auth/signUp", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> signUpNewUser(@RequestBody User user){

        //ADD - validation to check the email is in a valid format, if not return an error response
        System.out.println("User exists? " + userRepository.findByEmail(user.getEmail()));
        if(userRepository.findByEmail(user.getEmail()) == null){
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            // Do sign up
            User newUser = new User(user.getForename(), user.getSurname(), user.getEmail(), encoder.encode(user.getPassword()));
            userRepository.save(newUser);

            // create the token and verificationToken object (trialing doing this in the verification token class)
//            String token = UUID.randomUUID().toString();
            VerificationToken verificationToken = new VerificationToken(userRepository.findByEmail(user.getEmail()).getId());
            verificationToken.setExpiryDate(verificationToken.calcExpiryTime(1)); // override the default time

            // save the token to the token table
            verificationTokenRepository.save(verificationToken);

            mailService.sendWelcomeEmail(newUser, verificationToken);

            return ResponseEntity.ok("Welcome to the quiz app");
        } else {
            return ResponseEntity.badRequest().body("That email already has an account");
        }
    }

    //VERIFY USER'S EMAIL IS REAL
    @RequestMapping(value = "/auth/verify", method = RequestMethod.POST)
    public ResponseEntity<?> verifyUser(@RequestBody VerificationToken token){
        VerificationToken verificationToken;

        // Make sure the token exists
        verificationToken = verificationTokenRepository.findByToken(token.getToken());
        if(verificationToken == null){
            return new ResponseEntity<String>("TOKEN_UNMATCHED", HttpStatus.NOT_FOUND);
        }

        final Calendar cal = Calendar.getInstance();
        if((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0){
            // front end will need to render a page that says token expired and give a link to
            // request a new one which links back to resend token endpoint
            // or send another email and tell the user to use that instead
            return new ResponseEntity<String>("TOKEN_EXPIRED", HttpStatus.BAD_REQUEST);
        }
        Long id = verificationToken.getUserId();
        User user = userRepository.findById(id).get();
        user.setVerified(true);
        userRepository.save(user);
        verificationTokenRepository.deleteById(verificationToken.getId());

        VerificationResponse verificationResponse = new VerificationResponse(user, "verified", HttpStatus.OK);
        return new ResponseEntity<VerificationResponse>(verificationResponse, HttpStatus.OK);
    }

    //RESEND ACCOUNT VERIFICATION TOKEN
    @RequestMapping(value = "/auth/resendToken", method = RequestMethod.POST)
    public ResponseEntity<?> resendToken(@RequestBody VerificationToken token){

        if(verificationTokenRepository.findByToken(token.getToken()) == null){
            return new ResponseEntity<String>("TOKEN_UNMATCHED", HttpStatus.NOT_FOUND);
        }

        VerificationToken verificationToken = verificationTokenRepository.findByToken(token.getToken());
        User user = userRepository.findById(verificationToken.getUserId()).get();

        // Delete the old token
        verificationTokenRepository.delete(verificationToken);

        //Create a new token (trialing doing this in the verification token class)
//        String newToken = UUID.randomUUID().toString();

        // save the token to a verificationToken object and then save to the token table
        VerificationToken replacementToken = new VerificationToken(userRepository.findByEmail(user.getEmail()).getId());
        System.out.println(replacementToken.getExpiryDate());
        verificationTokenRepository.save(replacementToken);

        mailService.resendToken(user, replacementToken);

        return ResponseEntity.ok("RE-ISSUED");
    }

    // LOGIN
    @RequestMapping(value = "/auth/login", method = RequestMethod.POST)
    public ResponseEntity<?> authenticateUser(@RequestBody AuthenticationRequest authenticationRequest) throws Exception {
        if (userRepository.findByEmail(authenticationRequest.getEmail()) != null) {
//            if (userRepository.findByEmail(authenticationRequest.getEmail()).getVerified().equals(true)) {

                try {
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    authenticationRequest.getEmail(),
                                    authenticationRequest.getPassword()
                            )
                    );
                    final UserDetails userDetails = quizUserDetailsService.loadUserByUsername(authenticationRequest.getEmail());
                    final User user = userRepository.findByEmail(authenticationRequest.getEmail());
                    user.setPassword(""); // Remove the encoded password so it's not stored on the front end
                    final String jwt = jwtTokenUtil.generateToken(userDetails);
                    return ResponseEntity.ok(new AuthenticationResponse(user, jwt)); // Need to improve on this so I can send more (look into ResponseEntity)
                } catch (BadCredentialsException e) {
//            throw new Exception("Incorrect username or password", e);

                    return ResponseEntity.badRequest().body("Incorrect username or password");
                }
//            } else {
//                return new ResponseEntity<String>("NOT VERIFIED", HttpStatus.UNAUTHORIZED);
//            }
        } else {
            return new ResponseEntity<String>("No account exists for that username", HttpStatus.BAD_REQUEST);
        }
    }

}
