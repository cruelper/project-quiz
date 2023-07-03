package ru.nuykin.quizrestapi.service.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.nuykin.quizrestapi.exception.NotFoundException;
import ru.nuykin.quizrestapi.model.Category;
import ru.nuykin.quizrestapi.model.Question;
import ru.nuykin.quizrestapi.model.QuestionWithCategory;
import ru.nuykin.quizrestapi.service.CategoryService;
import ru.nuykin.quizrestapi.service.QuestionService;

import static org.mockito.Mockito.*;

class QuestionWithCategoryServiceImplTest {
    @Mock
    QuestionService questionService;
    @Mock
    CategoryService categoryService;
    @InjectMocks
    QuestionWithCategoryServiceImpl questionWithCategoryServiceImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindAll_shouldReturnModelsSuccess() {
        Question question1 = Mockito.mock(Question.class);
        Question question2 = Mockito.mock(Question.class);
        Category category = Mockito.mock(Category.class);

        when(questionService.findAll()).thenReturn(Flux.just(question1, question2));
        when(categoryService.findById(category.getId())).thenReturn(Mono.just(category));

        Flux<QuestionWithCategory> result = questionWithCategoryServiceImpl.findAll();
        StepVerifier.create(result)
                .expectNextMatches(qwc ->
                        qwc.getId().equals(question1.getId()) &&
                        qwc.getCategory().getId().equals(category.getId())
                )
                .expectNextMatches(qwc ->
                        qwc.getId().equals(question2.getId()) &&
                        qwc.getCategory().getId().equals(category.getId())
                )
                .verifyComplete();
    }

    @Test
    void testFindById_shouldReturnModelWhenExist() {
        Question question = Mockito.mock(Question.class);
        Category category = Mockito.mock(Category.class);
        when(questionService.findById(question.getId())).thenReturn(Mono.just(question));
        when(categoryService.findById(category.getId())).thenReturn(Mono.just(category));

        Mono<QuestionWithCategory> result = questionWithCategoryServiceImpl.findById(question.getId());
        StepVerifier.create(result)
                .expectNextMatches(qwc ->
                        qwc.getId().equals(question.getId()) &&
                                qwc.getCategory().getId().equals(category.getId())
                )
                .verifyComplete();
    }
    @Test
    void testFindById_shouldThrowNotFoundExceptionWhenNotExist() {
        Question question = Mockito.mock(Question.class);
        when(questionService.findById(question.getId())).thenReturn(Mono.empty());

        Mono<QuestionWithCategory> result = questionWithCategoryServiceImpl.findById(question.getId());
        StepVerifier.create(result)
                .expectError(NotFoundException.class)
                .verify();
    }

    @Test
    void testFindRandom_shouldReturnModelSuccess() {
        Question question = Mockito.mock(Question.class);
        Category category = Mockito.mock(Category.class);
        when(questionService.findRandom(anyInt(), anyInt(), anyList())).thenReturn(Mono.just(question));
        when(categoryService.findById(question.getCategoryId())).thenReturn(Mono.just(category));

        Mono<QuestionWithCategory> result = questionWithCategoryServiceImpl.findRandom(anyInt(), anyInt(), anyList());
        StepVerifier.create(result)
                .expectNextMatches(qwc ->
                        qwc.getId().equals(question.getId()) && qwc.getCategory().getId().equals(category.getId())
                )
                .verifyComplete();
    }

    @Test
    void testSave_shouldSaveSuccess() {
        Category category = Mockito.mock(Category.class);
        QuestionWithCategory questionWithCategory = Mockito.mock(QuestionWithCategory.class);
        questionWithCategory.setCategory(category);
        Question savedQuestion = Mockito.mock(Question.class);
        savedQuestion.setCategoryId(category.getId());

        when(questionService.save(savedQuestion)).thenReturn(Mono.just(savedQuestion));
        when(categoryService.findById(category.getId())).thenReturn(Mono.just(category));

        Mono<QuestionWithCategory> result = questionWithCategoryServiceImpl.save(questionWithCategory);
        StepVerifier.create(result)
                .expectNextMatches(qwc -> qwc.getId().equals(savedQuestion.getId()))
                .verifyComplete();
    }
    @Test
    void testSave_shouldThrowNotFoundExceptionWhenCategoryNotExist() {
        Category category = Mockito.mock(Category.class);
        QuestionWithCategory questionWithCategory = Mockito.mock(QuestionWithCategory.class);
        questionWithCategory.setCategory(category);

        when(categoryService.findById(category.getId())).thenReturn(Mono.empty());

        Mono<QuestionWithCategory> result = questionWithCategoryServiceImpl.save(questionWithCategory);
        StepVerifier.create(result)
                .expectError(NotFoundException.class)
                .verify();
    }

    @Test
    void testUpdate() {
        when(questionService.save(any())).thenReturn(null);

        Mono<QuestionWithCategory> result = questionWithCategoryServiceImpl.update(0L, new QuestionWithCategory(Long.valueOf(1), "question", new Category(Integer.valueOf(0), "name"), Integer.valueOf(0), "answer"));
        Assertions.assertEquals(null, result);
        Assertions.assertTrue(false);
    }

    @Test
    void testDeleteById_shouldSuccessUpdateWhenModelExist() {
        QuestionWithCategory questionWithCategory = Mockito.mock(QuestionWithCategory.class);
        Question question = Mockito.mock(Question.class);
        when(questionService.findById(questionWithCategory.getId())).thenReturn(Mono.just(question));
        questionWithCategoryServiceImpl.deleteById(questionWithCategory.getId()).subscribe();

        verify(questionService, times(1)).findById(questionWithCategory.getId());
        verify(questionService, times(1)).deleteById(questionWithCategory.getId());
    }
    @Test
    void testDeleteById_shouldThrowNotFoundWhenModelNotExist() {
        QuestionWithCategory questionWithCategory = Mockito.mock(QuestionWithCategory.class);
        Question question = Mockito.mock(Question.class);
        when(questionService.findById(questionWithCategory.getId())).thenReturn(Mono.empty());
        Mono<Void> result = questionWithCategoryServiceImpl.deleteById(question.getId());
        StepVerifier.create(result)
                .expectError(NotFoundException.class)
                .verify();

        verify(questionService, times(1)).findById(question.getId());
    }
}