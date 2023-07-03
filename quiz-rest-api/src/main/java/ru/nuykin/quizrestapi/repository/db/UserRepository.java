package ru.nuykin.quizrestapi.repository.db;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import ru.nuykin.quizrestapi.model.User;

@Repository
public interface UserRepository extends R2dbcRepository<User, Long> {
    Mono<User> findByUsername(String username);
}
