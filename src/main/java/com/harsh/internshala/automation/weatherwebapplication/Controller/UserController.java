package com.harsh.internshala.automation.weatherwebapplication.Controller;
import com.harsh.internshala.automation.weatherwebapplication.Model.LoginUserRequest;
import com.harsh.internshala.automation.weatherwebapplication.Model.User;
import com.harsh.internshala.automation.weatherwebapplication.Service.EmailService;
import com.harsh.internshala.automation.weatherwebapplication.Service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;
    private final EmailService emailService;

    @Autowired
    public UserController(UserService userService, EmailService emailService) {
        this.userService = userService;
        this.emailService = emailService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody User user) {
        try {
            userService.registerUser(user);
            return ResponseEntity.ok("User Created SuccessFully");
        } catch (IllegalArgumentException e) {
           throw e;
        }
    }
    @GetMapping("/verify")
    public ResponseEntity<String> verifyUser(@RequestParam String token){
        return userService.verifyUser(token);
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@Valid @RequestBody LoginUserRequest loginUserRequest){
        try {
            boolean success = userService.loginUser(loginUserRequest);
            if (success){
                return ResponseEntity.ok("Login successful"); // login
            }
        }catch (Exception e){
            throw e;
        }
        return null;
    }
    @GetMapping("/check-authentication")
    public ResponseEntity<String> checkUserAuthentication(@RequestParam String email) {
        try {
            Optional<User> userOpt = userService.findByEmail(email); // Get Optional<User>

            if (userOpt.isPresent() && userOpt.get().isVerified()) {
                return ResponseEntity.ok("User is authenticated and email is verified.");
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email not verified or user not found.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while checking authentication.");
        }
    }

}
