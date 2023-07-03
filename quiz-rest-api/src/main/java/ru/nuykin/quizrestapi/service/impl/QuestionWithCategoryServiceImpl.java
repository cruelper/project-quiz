package ru.nuykin.quizrestapi.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.nuykin.quizrestapi.exception.NotFoundException;
import ru.nuykin.quizrestapi.model.Question;
import ru.nuykin.quizrestapi.model.QuestionWithCategory;
import ru.nuykin.quizrestapi.service.CategoryService;
import ru.nuykin.quizrestapi.service.QuestionService;
import ru.nuykin.quizrestapi.service.QuestionWithCategoryService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionWithCategoryServiceImpl implements QuestionWithCategoryService {
    private final QuestionService questionService;
    private final CategoryService categoryService;

    @Override
    public Flux<QuestionWithCategory> findAll() {
        return questionService.findAll()
                .flatMap(quizQuestion -> categoryService.findById(quizQuestion.getCategoryId()).map(
                        quizCategory -> QuestionWithCategory.builder()
                                .id(quizQuestion.getId())
                                .question(quizQuestion.getQuestion())
                                .category(quizCategory)
                                .difficulty(quizQuestion.getDifficulty())
                                .answer(quizQuestion.getAnswer())
                                .build()
                ));
    }

    @Override
    public Mono<QuestionWithCategory> findById(long id) {
        return questionService.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException(String.valueOf(id))))
                .flatMap(quizQuestion -> categoryService.findById(quizQuestion.getCategoryId()).map(
                        quizCategory -> QuestionWithCategory.builder()
                                .id(quizQuestion.getId())
                                .question(quizQuestion.getQuestion())
                                .category(quizCategory)
                                .difficulty(quizQuestion.getDifficulty())
                                .answer(quizQuestion.getAnswer())
                                .build()
                ));
    }

    @Override
    public Mono<QuestionWithCategory> findRandom(int minDifficulty, int maxDifficulty, List<Integer> categories) {
        return questionService.findRandom(minDifficulty, maxDifficulty, categories)
                .flatMap(quizQuestion -> categoryService.findById(quizQuestion.getCategoryId()).map(
                        quizCategory -> QuestionWithCategory.builder()
                                .id(quizQuestion.getId())
                                .question(quizQuestion.getQuestion())
                                .category(quizCategory)
                                .difficulty(quizQuestion.getDifficulty())
                                .answer(quizQuestion.getAnswer())
                                .build()
                ));
    }

    @Override
    public Mono<QuestionWithCategory> save(QuestionWithCategory questionWithCategory) {
        return categoryService.findById(questionWithCategory.getCategory().getId())
                .switchIfEmpty(Mono.error(new NotFoundException(String.valueOf(questionWithCategory.getCategory().getId()))))
                .flatMap(category -> questionService.save(Question.builder()
                                .question(questionWithCategory.getQuestion())
                                .answer(questionWithCategory.getAnswer())
                                .categoryId(questionWithCategory.getCategory().getId())
                                .difficulty(questionWithCategory.getDifficulty())
                                .build()
                        ).map(quizQuestion -> {
                            questionWithCategory.setId(quizQuestion.getId());
                            return questionWithCategory;
                        })
                );
    }

    @Override
    public Mono<QuestionWithCategory> update(long id, QuestionWithCategory questionWithCategory) {
        return categoryService.findById(questionWithCategory.getCategory().getId())
                .switchIfEmpty(Mono.error(new NotFoundException(String.valueOf(questionWithCategory.getCategory().getId()))))
                .flatMap(category -> questionService.save(Question.builder()
                                .question(questionWithCategory.getQuestion())
                                .answer(questionWithCategory.getAnswer())
                                .categoryId(questionWithCategory.getCategory().getId())
                                .difficulty(questionWithCategory.getDifficulty())
                                .build()
                        ).map(quizQuestion -> {
                            questionWithCategory.setId(quizQuestion.getId());
                            return questionWithCategory;
                        })
                );
    }

    @Override
    public Mono<Void> deleteById(long id) {
        return questionService.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException(String.valueOf(id))))
                .flatMap(question -> questionService.deleteById(id));
    }
}
