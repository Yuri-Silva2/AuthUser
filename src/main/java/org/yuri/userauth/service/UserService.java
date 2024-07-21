package org.yuri.userauth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.yuri.userauth.domain.user.User;
import org.yuri.userauth.exception.InvalidPasswordException;
import org.yuri.userauth.repository.UserRepository;

import java.util.List;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public boolean existsByUsername(String username) {
        return userRepository.findByLogin(username).isPresent();
    }

    public void register(User user) {
        userRepository.save(user);
    }

    public UserDetails authenticate(User user) {
        UserDetails userDetails = loadUserByUsername(user.getUsername());
        boolean passwordMatches = passwordEncoder.matches(user.getPassword(), userDetails.getPassword());

        if (!passwordMatches) throw new InvalidPasswordException();

        return userDetails;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
       return userRepository.findByLogin(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }
}
