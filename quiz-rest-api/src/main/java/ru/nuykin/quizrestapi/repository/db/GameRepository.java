package ru.nuykin.quizrestapi.repository.db;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import ru.nuykin.quizrestapi.model.Game;

@Repository
public interface GameRepository extends R2dbcRepository<Game, Long> {
    public Flux<Game> findAllByUserId(long userId);
}
