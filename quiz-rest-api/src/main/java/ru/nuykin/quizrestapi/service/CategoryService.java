package ru.nuykin.quizrestapi.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.nuykin.quizrestapi.model.Category;

public interface CategoryService {
    public Flux<Category> findAll();

    public Mono<Category> findById(int id);

    public Mono<Category> save(Category category);

    public Mono<Category> update(int id, Category category);

    public Mono<Void> deleteById(int id);
}
