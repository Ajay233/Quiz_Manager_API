package com.apiTest.contoller;

import com.apiTest.config.GmailConfig;
import com.apiTest.config.MailConfig;
import com.apiTest.model.*;
import com.apiTest.repository.UserRepository;
import com.apiTest.repository.VerificationTokenRepository;
import com.apiTest.service.GmailService;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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
    private GmailConfig gmailConfig;

    @Autowired
    ApplicationEventPublisher eventPublisher;


    //LIST ALL USERS
    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public ResponseEntity<?> getAllUsers(){
        System.out.println("users endpoint hit");
        return new ResponseEntity<List>(userRepository.findAll(), HttpStatus.OK);
    }

    //EDIT PROFILE DATA (Forename, Surname, Email  ** will need to handle email separately and do another verify **)
    @RequestMapping(value = "/users/update", method = RequestMethod.POST)
    public ResponseEntity<?> updateUserData(@RequestBody UserDTO updatedUserData){
        User user = userRepository.findById(updatedUserData.getId()).get();
        user.setForename(updatedUserData.getForename());
        user.setSurname(updatedUserData.getSurname());
        user.setEmail(updatedUserData.getNewEmail());
        userRepository.save(user);
        return ResponseEntity.ok("UPDATED");
    }

    //UPDATE PASSWORD
    @RequestMapping(value = "/users/updatePassword", method = RequestMethod.POST)
    public ResponseEntity<?> updatePassword(@RequestBody PasswordUpdate newDetails){

        User user = userRepository.findByEmail(newDetails.getEmail());
        if(user == null){
            return new ResponseEntity<String>("NO MATCH", HttpStatus.NOT_FOUND);
        }
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    newDetails.getEmail(),
                    newDetails.getOldPassword()
                )
            );
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            String updatedPassword = encoder.encode(newDetails.getNewPassword());
            user.setPassword(updatedPassword);
            userRepository.save(user);
            return ResponseEntity.ok("UPDATED");
        } catch (BadCredentialsException e) {
            return new ResponseEntity<String>("PASSWORD INCORRECT", HttpStatus.BAD_REQUEST);
        }
    }


    //DELETE ACCOUNT
    @RequestMapping(value = "/users/deleteAccount", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteAccount(@RequestBody UserDTO user){
        System.out.println(user.getId());
        userRepository.deleteById(user.getId());
        return ResponseEntity.ok("DELETED");
    }

    // SIGN UP
    @RequestMapping(value = "/users/auth/signUp", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> signUpNewUser(@RequestBody User user){
        System.out.println(user);
        System.out.println(userRepository.findByEmail(user.getEmail()));

        //ADD - validation to check the email is in a valid format, if not return an error response

        if(userRepository.findByEmail(user.getEmail()) == null){
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            // Do sign up
            User newUser = new User(user.getForename(), user.getSurname(), user.getEmail(), encoder.encode(user.getPassword()));
            userRepository.save(newUser);

            // create the token and verificationToken object
            String token = UUID.randomUUID().toString();
            VerificationToken verificationToken = new VerificationToken(userRepository.findByEmail(user.getEmail()).getId(), token);
            verificationToken.setExpiryDate(verificationToken.calcExpiryTime(1));

            // save the token to the token table
            verificationTokenRepository.save(verificationToken);

            // create the message that will go in the email.  will need to include the token
            String message = "Hi " + newUser.getForename() + "\r\n\r\n" + "In order to complete the registration process please click on the link below to verify your account:" + "\r\n\r\n" + "http://localhost:3000/verify?token=" + token;
            String messageTwo = "In order to complete the registration process please click on the link below to verify your account:" + "\r\n\r\n" + "http://localhost:3000/verify?token=" + token;

            // Start a new thread so the user can be informed that their account has been successfully created
            // Send the token as a link in an email to the user
            new Thread(() -> {
                try {
//                    MailService mailService = new MailService();
//                    mailService.composeAndSendEmail(message,
//                            "ajaymungur@gmail.com",
//                            "ajaymungur@hotmail.com",
//                            "Complete your registration",
//                            mailConfig
//                    );
                    GmailService.sendMail("ajaymungurwork@outlook.com", newUser.getForename(), messageTwo, gmailConfig);
                } catch (MailSendException e) {
                    e.printStackTrace();
                }
            }).start();
            return ResponseEntity.ok("Welcome to the quiz app");
        } else {
            return ResponseEntity.badRequest().body("That email already has an account");
        }
    }

    //VERIFY USER'S EMAIL IS REAL
    @RequestMapping(value = "/users/auth/verify", method = RequestMethod.POST)
    public ResponseEntity<?> verifyUser(@RequestBody VerificationToken token){
        System.out.println(token.getToken());
        VerificationToken verificationToken;

        // Make sure the token exists
        verificationToken = verificationTokenRepository.findByToken(token.getToken());
        System.out.println(verificationToken);
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
        System.out.println("Verify endpoint hit");
        Long id = verificationToken.getUserId();
        User user = userRepository.findById(id).get();
        user.setVerified("true");
        userRepository.save(user);
        verificationTokenRepository.deleteById(verificationToken.getId());

        VerificationResponse verificationResponse = new VerificationResponse(user, "verified", HttpStatus.OK);
        return new ResponseEntity<VerificationResponse>(verificationResponse, HttpStatus.OK);
    }


    //RESEND ACCOUNT VERIFICATION TOKEN
    @RequestMapping(value = "/users/auth/resendToken", method = RequestMethod.POST)
    public ResponseEntity<?> resendToken(@RequestBody VerificationToken token){

        if(verificationTokenRepository.findByToken(token.getToken()) == null){
            return new ResponseEntity<String>("TOKEN_UNMATCHED", HttpStatus.NOT_FOUND);
        }

        VerificationToken verificationToken = verificationTokenRepository.findByToken(token.getToken());
        User user = userRepository.findById(verificationToken.getUserId()).get();

        // Delete the old token
        verificationTokenRepository.delete(verificationToken);

        //Create a new token
        String newToken = UUID.randomUUID().toString();

        // save the token to a verificationToken object and then save to the token table
        VerificationToken replacementToken = new VerificationToken(userRepository.findByEmail(user.getEmail()).getId(), newToken);
        replacementToken.setExpiryDate(verificationToken.calcExpiryTime(1440));
        System.out.println(replacementToken.getExpiryDate());
        verificationTokenRepository.save(replacementToken);

        // create the message that will go in the email
        String message = "Hi " + user.getForename() + "\r\n\r\n" + "In order to complete the registration process please click on the link below to verify your account:" + "\r\n\r\n" + "http://localhost:3000/verify?token=" + replacementToken.getToken();
        String messageTwo = "In order to complete the registration process please click on the link below to verify your account:" + "\r\n\r\n" + "http://localhost:3000/verify?token=" + replacementToken.getToken();
        // send email
        new Thread(() -> {
            try {
//                MailService mailService = new MailService();
//                mailService.composeAndSendEmail(message,
//                        "ajaymungur@gmail.com",
//                        "ajaymungur@hotmail.com",
//                        "Complete your registration",
//                        mailConfig
//                );
                GmailService.sendMail("ajaymungurwork@outlook.com", user.getForename(), messageTwo, gmailConfig);
            } catch(MailSendException e){
                e.printStackTrace();
            }
        }).start();
        return ResponseEntity.ok("RE-ISSUED");
    }


    // LOGIN
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
            user.setPassword(""); // Remove the encoded password so it's not stored on the front end
            final String jwt = jwtTokenUtil.generateToken(userDetails);
            return ResponseEntity.ok(new AuthenticationResponse(user, jwt)); // Need to improve on this so I can send more (look into ResponseEntity)
        } catch (BadCredentialsException e) {
//            throw new Exception("Incorrect username or password", e);

            return ResponseEntity.badRequest().body("Incorrect username or password");
        }

    }

}
