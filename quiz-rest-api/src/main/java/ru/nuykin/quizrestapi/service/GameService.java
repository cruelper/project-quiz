package ru.nuykin.quizrestapi.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.nuykin.quizrestapi.model.Category;
import ru.nuykin.quizrestapi.model.Game;

public interface GameService {
    public Mono<Game> findById(long id);

    public Flux<Game> findByUserId(long id);

    public Mono<Game> save(Game game);
}
