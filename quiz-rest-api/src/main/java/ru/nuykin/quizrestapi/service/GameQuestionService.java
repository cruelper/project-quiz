package ru.nuykin.quizrestapi.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.nuykin.quizrestapi.model.GameQuestion;

public interface GameQuestionService {
    public Mono<GameQuestion> findByGameIdAndQuestionNumber(long gameId, int questionNumber);

    public Flux<GameQuestion> findAllByGameId(long gameId);

    public Mono<GameQuestion> save(GameQuestion gameQuestion);

}
