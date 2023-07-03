package ru.nuykin.quizrestapi.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.nuykin.quizrestapi.model.QuestionWithCategory;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface QuestionWithCategoryService {
    public Flux<QuestionWithCategory> findAll();
    public Mono<QuestionWithCategory> findById(long id);
    public Mono<QuestionWithCategory> findRandom(int minDifficulty, int maxDifficulty, List<Integer> categories);
    public Mono<QuestionWithCategory> save(QuestionWithCategory questionWithCategory) throws ExecutionException, InterruptedException;
    public Mono<QuestionWithCategory> update(long id, QuestionWithCategory questionWithCategory) throws ExecutionException, InterruptedException;
    public Mono<Void> deleteById(long id);
}
