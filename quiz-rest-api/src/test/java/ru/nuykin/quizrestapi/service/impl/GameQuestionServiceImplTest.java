package ru.nuykin.quizrestapi.service.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Sort;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.nuykin.quizrestapi.exception.NotFoundException;
import ru.nuykin.quizrestapi.model.Category;
import ru.nuykin.quizrestapi.model.GameQuestion;
import ru.nuykin.quizrestapi.model.QuestionSource;
import ru.nuykin.quizrestapi.repository.db.GameQuestionRepository;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;

class GameQuestionServiceImplTest {
    @Mock
    GameQuestionRepository gameQuestionRepository;
    @InjectMocks
    GameQuestionServiceImpl gameQuestionServiceImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindByGameIdAndQuestionNumber_shouldReturnModelWhenExist() {
        GameQuestion gameQuestion = Mockito.mock(GameQuestion.class);
        Mockito.when(gameQuestionRepository.findAllByGameIdAndNumber(
                gameQuestion.getGameId(), gameQuestion.getNumber(), Sort.by(Sort.Direction.ASC, "number")
        )).thenReturn(Flux.just(gameQuestion));
        Mono<GameQuestion> result = gameQuestionServiceImpl.findByGameIdAndQuestionNumber(
                gameQuestion.getGameId(), gameQuestion.getNumber()
        );
        StepVerifier.create(result)
                .expectNext(gameQuestion)
                .verifyComplete();
    }
    @Test
    void testFindByGameIdAndQuestionNumber_shouldThrowNotFoundExceptionWhenNotExist() {
        GameQuestion gameQuestion = Mockito.mock(GameQuestion.class);

        Mockito.when(gameQuestionRepository.findAllByGameIdAndNumber(
                gameQuestion.getGameId(), gameQuestion.getNumber(), Sort.by(Sort.Direction.ASC, "number")
        )).thenReturn(Flux.empty());
        Mono<GameQuestion> result = gameQuestionServiceImpl.findByGameIdAndQuestionNumber(
                gameQuestion.getGameId(), gameQuestion.getNumber()
        );
        StepVerifier.create(result)
                .expectError(NotFoundException.class)
                .verify();
    }

    @Test
    void testFindAllByGameId_shouldReturnNotEmptyWhenModelsExists() throws ExecutionException, InterruptedException {
        Random random = new Random();
        int count = random.nextInt(10);
        Long targetGameId = random.nextLong();
        List<GameQuestion> targetGameQuestions = IntStream.range(0, random.nextInt(count))
                .mapToObj(i -> {
                    GameQuestion gameQuestion = Mockito.mock(GameQuestion.class);
                    gameQuestion.setGameId(targetGameId);
                    gameQuestion.setNumber(i);
                    return gameQuestion;
                })
                .toList();

        Mockito.when(gameQuestionRepository.findAllByGameId(
                targetGameId, Sort.by(Sort.Direction.ASC, "number"))
        ).thenReturn(Flux.fromIterable(targetGameQuestions));

        Flux<GameQuestion> result = gameQuestionServiceImpl.findAllByGameId(targetGameId);
        StepVerifier.create(result)
                .expectNextSequence(targetGameQuestions)
                .verifyComplete();
    }
    @Test
    void testFindAllByGameId_shouldReturnEmptyListWhenModelsNotExists() {
        Random random = new Random();
        long gameId = random.nextLong();
        Mockito.when(gameQuestionRepository.findAllByGameId(gameId, Sort.by(Sort.Direction.ASC, "number")))
                .thenReturn(Flux.empty());
        Flux<GameQuestion> result = gameQuestionServiceImpl.findAllByGameId(gameId);
        StepVerifier.create(result)
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void testSave_shouldSaveSuccess() {
        GameQuestion gameQuestion = Mockito.mock(GameQuestion.class);
        Mockito.when(gameQuestionRepository.save(gameQuestion))
                .thenReturn(Mono.just(gameQuestion));

        Mono<GameQuestion> saveResult = gameQuestionServiceImpl.save(gameQuestion);
        StepVerifier.create(saveResult)
                .expectNext(gameQuestion)
                .verifyComplete();
    }
}