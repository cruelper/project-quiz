package ru.nuykin.quizrestapi.service;

import reactor.core.publisher.Mono;
import ru.nuykin.quizrestapi.model.User;

public interface UserService {
    public Mono<User> registerUser(User user);
    public Mono<User> getUserById(Long id);
    public Mono<User> getUserByUsername(String username);
}
