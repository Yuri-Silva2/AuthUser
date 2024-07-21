package org.yuri.userauth.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yuri.userauth.domain.dto.AuthenticationDTO;
import org.yuri.userauth.domain.dto.AuthenticationResponseDTO;
import org.yuri.userauth.domain.dto.RegisterDTO;
import org.yuri.userauth.domain.user.User;
import org.yuri.userauth.exception.InvalidPasswordException;
import org.yuri.userauth.infrastructure.security.jwt.JWTService;
import org.yuri.userauth.service.UserService;

import java.util.List;

@RestController
@RequestMapping("auth")
public class AuthenticationController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;

    @Autowired
    private JWTService jwtService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid AuthenticationDTO data){
        try {
            User user = new User(data.login(), data.password(), null);
            UserDetails userDetails = userService.authenticate(user);

            String token = jwtService.generateToken((User) userDetails);

            return ResponseEntity.ok(new AuthenticationResponseDTO(token));

        } catch (UsernameNotFoundException | InvalidPasswordException e){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<String> register(@RequestBody @Valid RegisterDTO data){
        if (userService.existsByUsername(data.login()))
            return ResponseEntity.badRequest().body("Username already exists.");

        String encryptedPassword = passwordEncoder.encode(data.password());

        User user = new User(data.login(), encryptedPassword, data.role());
        userService.register(user);

        return ResponseEntity.ok("User registered successfully.");
    }

    @GetMapping("/list")
    public List<User> list() {
        return userService.findAll();
    }
}
