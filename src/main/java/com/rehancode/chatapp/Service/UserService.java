package com.rehancode.chatapp.Service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.rehancode.chatapp.DTO.LoginRequest;
import com.rehancode.chatapp.DTO.LoginResponse;
import com.rehancode.chatapp.DTO.RegisterRequest;
import com.rehancode.chatapp.DTO.RegisterResponse;
import com.rehancode.chatapp.Entity.User;
import com.rehancode.chatapp.Exceptions.AllFieldsRequired;
import com.rehancode.chatapp.Exceptions.InvalidCredentialsException;
import com.rehancode.chatapp.Exceptions.UsernameNotFoundException;
import com.rehancode.chatapp.JWT.JwtService;
import com.rehancode.chatapp.Repository.UserRepository;

@Service
public class UserService {

    private final JwtService jwtService;
    private final UserRepository userRepo;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);
    private final AuthenticationManager authenticationManager;


    public UserService(UserRepository userRepo,JwtService jwtService,AuthenticationManager authenticationManager) {
        this.userRepo = userRepo;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    // Convert Entity -> DTO
    public RegisterResponse convertToDto(User user) {
        return RegisterResponse.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }

    // Convert DTO -> Entity
    public User convertToEntity(RegisterRequest req) {
        return User.builder()
                .username(req.getUsername().trim())
                .email(req.getEmail().trim())
                .build();
    }
    public RegisterResponse registerUser(RegisterRequest req) {
        if (req.getUsername() == null || req.getUsername().trim().isEmpty() ||
            req.getEmail() == null || req.getEmail().trim().isEmpty() ||
            req.getPassword() == null || req.getPassword().trim().isEmpty()) {

            throw new AllFieldsRequired("All fields are required");
        }

        if (userRepo.findByEmail(req.getEmail()).isPresent()) {
            throw new AllFieldsRequired("Email already exists");
        }

        if (userRepo.findByUsername(req.getUsername()).isPresent()) {
            throw new AllFieldsRequired("Username already exists");
        }
        User user = convertToEntity(req);
        user.setPassword(passwordEncoder.encode(req.getPassword()));

        User savedUser = userRepo.save(user);

        return convertToDto(savedUser);
    }
public LoginResponse loginUser(LoginRequest req) {
    if (req.getUsername() == null || req.getUsername().trim().isEmpty() ||
        req.getPassword() == null || req.getPassword().trim().isEmpty()) {
        throw new AllFieldsRequired("Username and password are required");
    }

    try {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())
        );
    } catch (Exception ex) {
        // Catch authentication failures and throw custom exception
        throw new InvalidCredentialsException("Invalid username or password");
    }

    User user = userRepo.findByUsername(req.getUsername())
                        .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    String token = jwtService.generateToken(user.getUsername());

    return LoginResponse.builder()
            .username(user.getUsername())
            .email(user.getEmail())
            .token(token)
            .build();
}


}
