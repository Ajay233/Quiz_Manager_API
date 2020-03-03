package com.apiTest.contoller;

import com.apiTest.config.MailConfig;
import com.apiTest.model.*;
import com.apiTest.repository.UserRepository;
import com.apiTest.repository.VerificationTokenRepository;
import com.apiTest.service.MailService;
import com.apiTest.service.QuizUserDetailsService;
import com.apiTest.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailSendException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Calendar;
import java.util.List;
import java.util.UUID;

@RestController
public class UserController {

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
    private MailConfig mailConfig;

    @Autowired
    ApplicationEventPublisher eventPublisher;


    @CrossOrigin(origins = "http://localhost:3000") // <-- Temp, needs to be removed once config file created
    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    @CrossOrigin(origins = "http://localhost:3000") // <-- Temp, needs to be removed once config file created
    @RequestMapping(value = "/users/updateForename", method = RequestMethod.PUT)
    public ResponseEntity<?> updateForename(@RequestBody User user){
        User tempUser = userRepository.findByEmail(user.getEmail());
        tempUser.setForename(user.getForename());
        userRepository.save(tempUser);
        return ResponseEntity.ok("Forename changed");
    }

    @CrossOrigin(origins = "http://localhost:3000") // <-- Temp, needs to be removed once config file created
    @RequestMapping(value = "/users/deleteAccount", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteAccount(@RequestBody User user){
        User tempUser = userRepository.findByEmail(user.getEmail());
        userRepository.deleteById(tempUser.getId());
        return ResponseEntity.ok("Sorry to see you go " + tempUser.getForename() + "!! Your account has now been deleted");
    }

    @CrossOrigin(origins = "http://localhost:3000") // <-- Temp, needs to be removed once config file created
    @RequestMapping(value = "/users/auth/signUp", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> signUpNewUser(@RequestBody User user){
        System.out.println(user);
        System.out.println(userRepository.findByEmail(user.getEmail()));

        if(user.getEmail().equals("")){
            return ResponseEntity.badRequest().body("No email supplied");
        }

        if(userRepository.findByEmail(user.getEmail()) == null){
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            // Do sign up
            User newUser = new User(user.getForename(), user.getSurname(), user.getEmail(), encoder.encode(user.getPassword()));
            userRepository.save(newUser);

            // create the token and verificationToken object
            String token = UUID.randomUUID().toString();
            VerificationToken verificationToken = new VerificationToken(userRepository.findByEmail(user.getEmail()).getId(), token);
            verificationToken.setExpiryDate(verificationToken.calcExpiryTime(1440));

            // save the token to the token table
            verificationTokenRepository.save(verificationToken);

            // create the message that will go in the email.  will need to include the token
            String message = "Hi " + newUser.getForename() + "\r\n\r\n" + "In order to complete the registration process please click on the link below to verify your account:" + "\r\n\r\n" + "http://localhost:8080/users/auth/verify?token=" + token;

            // Start a new thread so the user can be informed that their account has been successfully created
            // Send the token as a link in an email to the user
            new Thread(() -> {
                try {
                    MailService mailService = new MailService();
                    mailService.composeAndSendEmail(message,
                            "ajaymungurwork@outlook.com",
                            "ajaymungur@hotmail.com",
                            "Complete your registration",
                            mailConfig
                    );
                } catch (MailSendException e) {
                    e.printStackTrace();
                }
            }).start();
            return ResponseEntity.ok("Welcome to the quiz app");
        } else {
            return ResponseEntity.badRequest().body("That email already has an account");
        }
    }

    @CrossOrigin(origins = "http://localhost:3000") // <-- Temp, needs to be removed once config file created
    @RequestMapping(value = "/users/auth/verify", method = RequestMethod.POST)
    public ResponseEntity<?> verifyUser(@RequestBody VerificationToken token){
        System.out.println(token.getToken());
        VerificationToken verificationToken;

        // Make sure the token exists
        verificationToken = verificationTokenRepository.findByToken(token.getToken());
        System.out.println(verificationToken);
        if(verificationToken == null){
            return new ResponseEntity<VerificationToken>(verificationToken, HttpStatus.BAD_REQUEST);
        }

        final Calendar cal = Calendar.getInstance();
        if((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0){
            // front end will need to render a page that says token expired and gives a link to
            // request a new one which links back to resend token endpoint
            // or send another email and tell the user to use that instead
            return new ResponseEntity<VerificationToken>(verificationToken, HttpStatus.BAD_REQUEST);
        }
        System.out.println("Verify endpoint hit");
        Long id = verificationToken.getUserId();
        User user = userRepository.findById(id).get();
        user.setVerified("true");
        userRepository.save(user);
        verificationTokenRepository.deleteById(verificationToken.getId());

        VerificationResponse verificationResponse = new VerificationResponse(user, "verified", HttpStatus.OK);
        return new ResponseEntity<VerificationResponse>(verificationResponse, HttpStatus.OK);
    }

    @CrossOrigin(origins = "http://localhost:3000") // <-- Temp, needs to be removed once config file created
    @RequestMapping(value = "/users/auth/resendToken", method = RequestMethod.GET)
    public ResponseEntity<?> resendToken(@RequestBody VerificationToken token){
        User user = userRepository.findById(token.getUserId()).get();

        // Delete the old token
        verificationTokenRepository.delete(token);

        //Create a new token
        String newToken = UUID.randomUUID().toString();

        // save the token to a verificationToken object and then save to the token table
        VerificationToken verificationToken = new VerificationToken(userRepository.findByEmail(user.getEmail()).getId(), newToken);
        verificationToken.setExpiryDate(verificationToken.calcExpiryTime(1440));
        System.out.println(verificationToken.getExpiryDate());
        verificationTokenRepository.save(verificationToken);

        // create the message that will go in the email
        String message = "Hi " + user.getForename() + "\r\n\r\n" + "In order to complete the registration process please click on the link below to verify your account:" + "\r\n\r\n" + "http://localhost:8080/users/auth/verify?token=" + token;

        // send email
        new Thread(() -> {
            try {
                MailService mailService = new MailService();
                mailService.composeAndSendEmail(message,
                        "ajaymungurwork@outlook.com",
                        "ajaymungur@hotmail.com",
                        "Complete your registration",
                        mailConfig
                );
            } catch(MailSendException e){
                e.printStackTrace();
            }
        }).start();
        return ResponseEntity.ok("re-issued");
    }

    @CrossOrigin(origins = "http://localhost:3000") // <-- Temp, needs to be removed once config file created
    @RequestMapping(value = "/users/auth/login", method = RequestMethod.POST)
    public ResponseEntity<?> authenticateUser(@RequestBody AuthenticationRequest authenticationRequest) throws Exception {

        String verified = userRepository.findByEmail(authenticationRequest.getEmail()).getVerified();

        // Need to add in a check to see if a user is verified, if not return
        // a response to confirm they need to verify before they can login

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authenticationRequest.getEmail(),
                            authenticationRequest.getPassword()
                    )
            );
            final UserDetails userDetails = quizUserDetailsService.loadUserByUsername(authenticationRequest.getEmail());
            final User user = userRepository.findByEmail(authenticationRequest.getEmail());
            final String jwt = jwtTokenUtil.generateToken(userDetails);
            return ResponseEntity.ok(new AuthenticationResponse(user, jwt)); // Need to improve on this so I can send more (look into ResponseEntity)
        } catch (BadCredentialsException e) {
//            throw new Exception("Incorrect username or password", e);
            return ResponseEntity.badRequest().body("Incorrect username or password");
        }

    }


}
