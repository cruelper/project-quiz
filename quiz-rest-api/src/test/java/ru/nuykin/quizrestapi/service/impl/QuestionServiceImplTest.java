package ru.nuykin.quizrestapi.service.impl;

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
import ru.nuykin.quizrestapi.mapper.QuestionMapper;
import ru.nuykin.quizrestapi.model.Question;
import ru.nuykin.quizrestapi.repository.db.QuestionRepository;

import java.util.List;

import static org.mockito.Mockito.*;

class QuestionServiceImplTest {
    @Mock
    QuestionRepository questionRepository;
    @Mock
    QuestionMapper questionMapper;
    @InjectMocks
    QuestionServiceImpl questionServiceImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindAll_shouldReturnModelsWhenExists() {
        List<Question> questions = List.of(
                Mockito.mock(Question.class),
                Mockito.mock(Question.class)
        );
        when(questionRepository.findAll()).thenReturn(Flux.fromIterable(questions));
        Flux<Question> result = questionServiceImpl.findAll();
        StepVerifier.create(result)
                .expectNextSequence(questions)
                .verifyComplete();
    }
    @Test
    void testFindById_shouldReturnModelWhenExist() {
        Question question = Mockito.mock(Question.class);
        when(questionRepository.findById(question.getId())).thenReturn(Mono.just(question));
        Mono<Question> result = questionServiceImpl.findById(question.getId());
        StepVerifier.create(result)
                .expectNext(question)
                .verifyComplete();
    }
    @Test
    void testFindById_shouldThrowNotFoundExceptionWhenNotExist() {
        Question question = Mockito.mock(Question.class);
        when(questionRepository.findById(question.getId())).thenReturn(Mono.empty());
        Mono<Question> result = questionServiceImpl.findById(question.getId());
        StepVerifier.create(result)
                .expectError(NotFoundException.class)
                .verify();
    }

    @Test
    void testFindRandom_shouldReturnModelWhenExist() {
        Question question1 = Mockito.mock(Question.class);
        Question question2 = Mockito.mock(Question.class);
        when(questionRepository.getRandomQuestionByCategoryAndDifficulty(anyInt(), anyInt(), anyList()))
                .thenReturn(Mono.just(question1))
                .thenReturn(Mono.just(question2));

        Mono<Question> result1 = questionServiceImpl.findRandom(anyInt(), anyInt(), anyList());
        StepVerifier.create(result1)
                .expectNext(question1)
                .verifyComplete();

        Mono<Question> result2 = questionServiceImpl.findRandom(anyInt(), anyInt(), anyList());
        StepVerifier.create(result2)
                .expectNext(question2)
                .verifyComplete();
    }
    @Test
    void testFindRandom_shouldThrowNotFoundExceptionWhenNotExist() {
        when(questionRepository.getRandomQuestionByCategoryAndDifficulty(anyInt(), anyInt(), anyList()))
                .thenReturn(Mono.empty());

        Mono<Question> result = questionServiceImpl.findRandom(anyInt(), anyInt(), anyList());
        StepVerifier.create(result)
                .expectError(NotFoundException.class)
                .verify();
    }

    @Test
    void testSave_shouldSaveSuccess() {
        Question question = Mockito.mock(Question.class);
        when(questionRepository.save(question)).thenReturn(Mono.just(question));
        Mono<Question> result = questionServiceImpl.save(question);

        StepVerifier.create(result)
                .expectNext(question)
                .verifyComplete();
        Mockito.verify(questionRepository, Mockito.times(1)).save(question);
    }

    @Test
    void testUpdate_shouldSuccessUpdateWhenModelExist() {
        Question oldQuestion = Mockito.mock(Question.class);
        Question updatedQuestionAtRequest = Mockito.mock(Question.class);
        Question updatedQuestion = Mockito.mock(Question.class);
        updatedQuestion.setId(oldQuestion.getId());

        when(questionRepository.findById(oldQuestion.getId())).thenReturn(Mono.just(oldQuestion));
        when(questionRepository.save(updatedQuestion)).thenReturn(Mono.just(updatedQuestion));
        when(questionMapper.updateEntity(updatedQuestionAtRequest, oldQuestion)).thenReturn(updatedQuestion);

        Mono<Question> result = questionServiceImpl.update(oldQuestion.getId(), updatedQuestionAtRequest);
        StepVerifier.create(result)
                .expectNext(updatedQuestion)
                .verifyComplete();

        verify(questionRepository, times(1)).findById(oldQuestion.getId());
        verify(questionRepository, times(1)).save(updatedQuestion);
    }
    @Test
    void testUpdate_shouldThrowNotFoundWhenModelNotExist() {
        Question question = Mockito.mock(Question.class);
        when(questionRepository.findById(question.getId())).thenReturn(Mono.empty());
        Mono<Question> result = questionServiceImpl.update(question.getId(), question);
        StepVerifier.create(result)
                .expectError(NotFoundException.class)
                .verify();
    }

    @Test
    void testDeleteById_shouldSuccessUpdateWhenModelExist() {
        Question question = Mockito.mock(Question.class);
        when(questionRepository.findById(question.getId())).thenReturn(Mono.just(question));
        questionServiceImpl.deleteById(question.getId()).subscribe();

        verify(questionRepository, times(1)).findById(question.getId());
        verify(questionRepository, times(1)).deleteById(question.getId());
    }
    @Test
    void testDeleteById_shouldThrowNotFoundWhenModelNotExist() {
        Question question = Mockito.mock(Question.class);
        when(questionRepository.findById(question.getId())).thenReturn(Mono.empty());
        Mono<Void> result = questionServiceImpl.deleteById(question.getId());
        StepVerifier.create(result)
                .expectError(NotFoundException.class)
                .verify();

        verify(questionRepository, times(1)).findById(question.getId());
    }
}


