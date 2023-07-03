package ru.nuykin.quizrestapi.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.nuykin.quizrestapi.model.Question;

import java.util.List;

public interface QuestionService {
    public Flux<Question> findAll();

    public Mono<Question> findById(long id);
    public Mono<Question> findRandom(int minDifficulty, int maxDifficulty, List<Integer> categories);

    public Mono<Question> save(Question question);

    public Mono<Question> update(long id, Question question);

    public Mono<Void> deleteById(long id);
}
