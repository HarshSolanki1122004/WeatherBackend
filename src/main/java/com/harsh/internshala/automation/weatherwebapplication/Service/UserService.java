package com.harsh.internshala.automation.weatherwebapplication.Service;
import com.harsh.internshala.automation.weatherwebapplication.Exceptions.*;
import com.harsh.internshala.automation.weatherwebapplication.Model.LoginUserRequest;
import com.harsh.internshala.automation.weatherwebapplication.Model.User;
import com.harsh.internshala.automation.weatherwebapplication.Repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final EmailService emailService;
    @Value("${main.domain}")
    private String domainName;

    @Autowired
    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder,EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    public User registerUser(User user) {
        // Check if the email already exists
        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser.isPresent()) {
            throw new UserAlreadyExistsException("Email is already registered try with another email or Login");
        }

        // Encrypt the password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Generate verification token
        String token = UUID.randomUUID().toString();
        user.setVerificationToken(token);

        // Save the user
        User savedUser = userRepository.save(user);

        // Send verification email
        String verificationUrl = domainName + "/api/v1/users/verify?token=" + token;
        String subject = "🔑 Verify Your Email to Activate Your Account";
        String htmlBody = "<html><body>"
                + "<h2 style='color:#4CAF50;'>Welcome to Our Service!</h2>"
                + "<p style='font-size:16px;'>Thank you for registering with us. Please <a href='" + verificationUrl + "' style='color:#2196F3;'>click here</a> to verify your email address and complete your registration.</p>"
                + "<p>If you did not sign up, please ignore this email.</p>"
                + "</body></html>";

        emailService.sendHtmlEmail(user.getEmail(), subject, htmlBody);
        return savedUser;
    }

    public ResponseEntity<String> verifyUser(String token){
        Optional<User> emailToken= userRepository.findByVerificationToken(token);
        if (emailToken.isEmpty()){
            throw new InValidVerificationToken("Invalid Token");
        }
        User user = emailToken.get();
        user.setVerified(true);
        user.setVerificationToken(null);
        userRepository.save(user);
        return ResponseEntity.ok("Email verified successfully");
    }

    public boolean loginUser(@Valid LoginUserRequest loginUserRequest){
        User user = userRepository.findByEmail(loginUserRequest.getEmail()).orElseThrow(
                ()-> new UserNotFound("User Not Found"));
        if (!user.isVerified()){
            throw new EmailNotVerified("Email is not verified");
        }
        if (!passwordEncoder.matches(loginUserRequest.getPassword(),user.getPassword())){
            throw new InValidCredin("Invalid credentials");
        }
        return true;
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email); // Returns an Optional of User
    }
}
