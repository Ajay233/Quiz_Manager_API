package com.apiTest.User.controller;

import com.apiTest.User.model.User;
import com.apiTest.User.model.UserDTO;
import com.apiTest.User.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.List;

@RestController
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationManager authenticationManager;


// This has not yet been used.  It was part of the Baeldung tutorial and will need to be looked into
//    @Autowired
//    ApplicationEventPublisher eventPublisher;


    //LIST ALL USERS
    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public ResponseEntity<?> getAllUsers() throws ServletException, IOException {
        return new ResponseEntity<List>(userRepository.findAll(), HttpStatus.OK);
    }

    //EDIT PROFILE DATA (Forename, Surname, Email  ** will need to handle email separately and do another verify **)
    @RequestMapping(value = "/users/update", method = RequestMethod.PUT)
    public ResponseEntity<?> updateUserData(@RequestBody UserDTO updatedUserData){
        User user = userRepository.findByEmail(updatedUserData.getEmail());
        user.setForename(updatedUserData.getForename());
        user.setSurname(updatedUserData.getSurname());
        user.setEmail(updatedUserData.getNewEmail());
        userRepository.save(user);
        return ResponseEntity.ok("UPDATED");
    }

    //UPDATE PASSWORD
    @RequestMapping(value = "/users/updatePassword", method = RequestMethod.PUT)
    public ResponseEntity<?> updatePassword(@RequestBody UserDTO newDetails){

        User user = userRepository.findByEmail(newDetails.getEmail());
        if(user == null){
            return new ResponseEntity<String>("NO MATCH", HttpStatus.NOT_FOUND);
        }
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    newDetails.getEmail(),
                    newDetails.getPassword()
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
        User userToDelete = userRepository.findByEmail(user.getEmail());
        userRepository.delete(userToDelete);
        return ResponseEntity.ok("DELETED");
    }

}
