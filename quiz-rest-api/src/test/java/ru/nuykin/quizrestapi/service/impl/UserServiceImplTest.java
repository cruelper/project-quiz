package ru.nuykin.quizrestapi.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.nuykin.quizrestapi.exception.ConflictException;
import ru.nuykin.quizrestapi.exception.NotFoundException;
import ru.nuykin.quizrestapi.model.User;
import ru.nuykin.quizrestapi.repository.db.UserRepository;

import static org.mockito.Mockito.*;

class UserServiceImplTest {
    @Mock
    UserRepository userRepository;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    Logger log;
    @InjectMocks
    UserServiceImpl userServiceImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterUser_shouldSuccessSave() {
        User inputUser = User.builder()
                .username("testUser")
                .password("testPassword")
                .build();
        User savedUser = inputUser.toBuilder()
                .password("encodedPassword")
                .build();

        when(userRepository.findByUsername(inputUser.getUsername())).thenReturn(Mono.empty());
        when(passwordEncoder.encode(inputUser.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(savedUser));
        Mono<User> result = userServiceImpl.registerUser(inputUser);

        StepVerifier.create(result)
                .expectNext(savedUser)
                .verifyComplete();
    }

    @Test
    void testRegisterUser_shouldThrowConflictExceptionWhenUsernameIsUsedAnotherUser() {
        User user = User.builder()
                .id(1L)
                .username("testUser")
                .password("testPassword")
                .build();
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Mono.just(user));
        Mono<User> result = userServiceImpl.registerUser(user);

        StepVerifier.create(result)
                .expectError(ConflictException.class)
                .verify();
    }

    @Test
    void testGetUserById_shouldReturnModelWhenExist() {
        User user = Mockito.mock(User.class);
        when(userRepository.findById(user.getId())).thenReturn(Mono.just(user));
        Mono<User> result = userServiceImpl.getUserById(user.getId());
        StepVerifier.create(result)
                .expectNext(user)
                .verifyComplete();
    }
    @Test
    void testGetUserById_shouldThrowNotFoundExceptionWhenUserNotExist() {
        when(userRepository.findById(1L)).thenReturn(Mono.empty());
        Mono<User> result = userServiceImpl.getUserById(1L);

        StepVerifier.create(result)
                .expectError(NotFoundException.class)
                .verify();
    }

    @Test
    void testGetUserByUsername_shouldReturnModelWhenExist() {
        User user = Mockito.mock(User.class);
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Mono.just(user));
        Mono<User> result = userServiceImpl.getUserByUsername(user.getUsername());
        StepVerifier.create(result)
                .expectNext(user)
                .verifyComplete();
    }
    @Test
    void testGetUserByUsername_shouldThrowNotFoundExceptionWhenUserNotExist() {
        when(userRepository.findByUsername("username")).thenReturn(Mono.empty());
        Mono<User> result = userServiceImpl.getUserByUsername("username");

        StepVerifier.create(result)
                .expectError(NotFoundException.class)
                .verify();
    }
}
