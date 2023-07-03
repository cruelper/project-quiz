package ru.nuykin.quizrestapi.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.nuykin.quizrestapi.exception.ConflictException;
import ru.nuykin.quizrestapi.exception.NotFoundException;
import ru.nuykin.quizrestapi.model.User;
import ru.nuykin.quizrestapi.model.UserRole;
import ru.nuykin.quizrestapi.repository.db.UserRepository;
import ru.nuykin.quizrestapi.service.UserService;

import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Mono<User> registerUser(User user) {
        return userRepository.findByUsername(user.getUsername())
                .switchIfEmpty(Mono.defer(() -> userRepository.save(
                        user.toBuilder()
                                .password(passwordEncoder.encode(user.getPassword()))
                                .role(UserRole.USER)
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .build()
                ).doOnSuccess(u -> log.info("IN registerUser - user: {} created", u))))
                .or(
                        Mono.error(new ConflictException("Username is used by another user"))
                );
    }

    public Mono<User> getUserById(Long id) {
        return userRepository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException(String.valueOf(id))));
    }

    public Mono<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .switchIfEmpty(Mono.error(new NotFoundException(String.valueOf(username))));
    }
}
