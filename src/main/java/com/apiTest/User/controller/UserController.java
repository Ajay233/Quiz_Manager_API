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
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

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

    //GET USER BY EMAIL
    @RequestMapping(value = "/users/findByEmail", method = RequestMethod.GET)
    public ResponseEntity<?> getUserByEmail(@RequestParam String email){

        User user = userRepository.findByEmail(email);
        if(user != null){
            user.setPassword("N/A");
            return new ResponseEntity<User>(user, HttpStatus.OK);
        } else {
            return new ResponseEntity<String>("No user found with that email", HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/users/findById", method = RequestMethod.GET)
    public ResponseEntity<?> getUserById(@RequestParam Long id){
        try{
            User user = userRepository.findById(id).get();
            user.setPassword("N/A");
            return new ResponseEntity<User>(user, HttpStatus.OK);
        } catch(NoSuchElementException e){
            return new ResponseEntity<String>("No user found with that email", HttpStatus.NOT_FOUND);
        }
    }

    //EDIT PROFILE DATA (Forename, Surname, Email  ** will need to handle email separately and do another verify **)
    @RequestMapping(value = "/users/update", method = RequestMethod.PUT)
    public ResponseEntity<?> updateUserData(@RequestBody UserDTO updatedUserData){
        User user = userRepository.findById(updatedUserData.getId()).get();
        user.setForename(updatedUserData.getForename());
        user.setSurname(updatedUserData.getSurname());
        user.setEmail(updatedUserData.getNewEmail());
        userRepository.save(user);
        return ResponseEntity.ok("UPDATED");
    }

    //UPDATE PASSWORD
    @RequestMapping(value = "/users/updatePassword", method = RequestMethod.PUT)
    public ResponseEntity<?> updatePassword(@RequestBody UserDTO newDetails){
        if(newDetails.getRetypedPassword().equals(newDetails.getNewPassword())) {

            try {
                User user = userRepository.findById(newDetails.getId()).get();
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
            } catch(NoSuchElementException e) {
                System.out.println(e);
                return new ResponseEntity<String>("NO MATCH", HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<String>("PASSWORD MISMATCH", HttpStatus.BAD_REQUEST);
        }

    }

    @RequestMapping(value = "/users/updatePermission", method = RequestMethod.PUT)
    public ResponseEntity<?> updateUserPermission(@RequestBody UserDTO user){
        if(userRepository.existsById(user.getId())){
            User userToUpdate = userRepository.findById(user.getId()).get();
            userToUpdate.setPermission(user.getPermission());
            userRepository.save(userToUpdate);
            return ResponseEntity.ok("UPDATED");
        } else {
            return new ResponseEntity<String>("Unable to update, user not found", HttpStatus.NOT_FOUND);
        }
    }

    //DELETE ACCOUNT
    @RequestMapping(value = "/users/deleteAccount", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteAccount(@RequestBody UserDTO user){
        User userToDelete = userRepository.findById(user.getId()).get();
        userRepository.delete(userToDelete);
        return ResponseEntity.ok("DELETED");
    }

}
