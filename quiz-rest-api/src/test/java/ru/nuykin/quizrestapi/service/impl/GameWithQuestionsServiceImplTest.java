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
import ru.nuykin.quizrestapi.dto.CheckQuestionAnswerDto;
import ru.nuykin.quizrestapi.dto.QuestionDto;
import ru.nuykin.quizrestapi.dto.request.GameStartRequestDto;
import ru.nuykin.quizrestapi.exception.ConflictException;
import ru.nuykin.quizrestapi.exception.NotFoundException;
import ru.nuykin.quizrestapi.model.*;
import ru.nuykin.quizrestapi.repository.cache.RedisQuestionRepository;
import ru.nuykin.quizrestapi.service.GameQuestionService;
import ru.nuykin.quizrestapi.service.GameService;
import ru.nuykin.quizrestapi.service.QuestionWithCategoryService;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

class GameWithQuestionsServiceImplTest {
    @InjectMocks
    private GameWithQuestionsServiceImpl gameWithQuestionsService;
    @Mock
    private GameService gameService;
    @Mock
    private GameQuestionService gameQuestionService;
    @Mock
    private QuestionWithCategoryService questionWithCategoryService;
    @Mock
    private JServiceService jServiceService;
    @Mock
    private RedisQuestionRepository redisQuestionRepository;

    private Game game;
    private GameQuestion gameQuestion1;
    private GameQuestion gameQuestion2;
    private QuestionWithCategory questionWithCategory1;
    private QuestionWithCategory questionWithCategory2;
    private QuestionDto questionDto;
    private User user;

    @BeforeEach
    void setUp() {
        user = Mockito.mock(User.class);

        game = Mockito.mock(Game.class);
        game.setUserId(user.getId());

        Question question1 = Mockito.mock(Question.class);
        Question question2 = Mockito.mock(Question.class);

        gameQuestion1 = GameQuestion.builder()
                .gameId(game.getId())
                .number(1)
                .questionId(question1.getId())
                .questionSource(QuestionSource.DB)
                .isCorrectAnswer(false)
                .isAnswerGiven(false)
                .build();

        gameQuestion2 = GameQuestion.builder()
                .gameId(game.getId())
                .number(2)
                .questionId(question2.getId())
                .questionSource(QuestionSource.JSERVICE)
                .isCorrectAnswer(false)
                .isAnswerGiven(false)
                .build();

        questionDto = QuestionDto.builder()
                .id(question2.getId())
                .question(question2.getQuestion())
                .answer(question2.getAnswer())
                .difficulty(question2.getDifficulty())
                .build();
        questionWithCategory1 = QuestionWithCategory.builder()
                .id(question1.getId())
                .build();

        questionWithCategory2 = QuestionWithCategory.builder()
                .id(question2.getId())
                .build();

        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testStartGame_shouldReturnValidResponse() {
        GameStartRequestDto gameStartRequestDto = GameStartRequestDto.builder()
                .questionCount(2)
                .build();
        when(gameService.save(any(Game.class))).thenReturn(Mono.just(game));
        when(questionWithCategoryService.findRandom(anyInt(), anyInt(), anyList())).thenReturn(Mono.just(questionWithCategory1));
        when(jServiceService.findRandom(1)).thenReturn(Flux.just(questionDto));
        when(gameQuestionService.save(any(GameQuestion.class))).thenReturn(Mono.just(gameQuestion1));
        StepVerifier.create(gameWithQuestionsService.startGame(user.getId(), gameStartRequestDto))
                .expectNextMatches(response ->
                        response.getGameId().equals(game.getId()) && response.getQuestionCount().equals(2L)
                )
                .verifyComplete();
    }

    @Test
    void testGetQuestion_shouldReturnWhenQuestionExist() {
        when(gameService.findById(game.getId())).thenReturn(Mono.just(game));
        when(gameQuestionService.findByGameIdAndQuestionNumber(gameQuestion1.getGameId(), gameQuestion1.getNumber())).thenReturn(Mono.just(gameQuestion1));
        when(questionWithCategoryService.findById(gameQuestion1.getQuestionId())).thenReturn(Mono.just(questionWithCategory1));
        StepVerifier.create(gameWithQuestionsService.getQuestion(user.getId(), game.getId(), gameQuestion1.getNumber()))
                .expectNext(questionWithCategory1)
                .verifyComplete();
    }

    @Test
    void testGetQuestion_shouldThrowConflictExceptionWhenWrongUser() {
        when(gameService.findById(game.getId())).thenReturn(Mono.just(game));
        StepVerifier.create(gameWithQuestionsService.getQuestion(user.getId() + 1, game.getId(), gameQuestion1.getNumber()))
                .expectError(ConflictException.class)
                .verify();
    }

    @Test
    void testGetQuestion_shouldThrowNotFoundExceptionWhenGameOrQuestionNotExist() {
        when(gameService.findById(game.getId())).thenReturn(Mono.empty());
        StepVerifier.create(gameWithQuestionsService.getQuestion(user.getId(), game.getId(), gameQuestion1.getNumber()))
                .expectError(NotFoundException.class)
                .verify();

        when(gameService.findById(game.getId())).thenReturn(Mono.just(game));
        when(gameQuestionService.findByGameIdAndQuestionNumber(game.getId(), gameQuestion1.getNumber())).thenReturn(Mono.empty());
        StepVerifier.create(gameWithQuestionsService.getQuestion(user.getId(), game.getId(), gameQuestion1.getNumber()))
                .expectError(NotFoundException.class)
                .verify();
    }

    // null pointer exception
    @Test
    void testCheckQuestion_shouldReturnAnswerWhenQuestionExist() {
        game.setEndTime(null);
        CheckQuestionAnswerDto answer = Mockito.mock(CheckQuestionAnswerDto.class);
        when(gameService.findById(game.getId())).thenReturn(Mono.just(game));
        when(gameQuestionService.findByGameIdAndQuestionNumber(game.getId(), gameQuestion1.getNumber())).thenReturn(Mono.just(gameQuestion1));
        when(questionWithCategoryService.findById(gameQuestion1.getQuestionId())).thenReturn(Mono.just(questionWithCategory1));
        when(gameQuestionService.save(gameQuestion1)).thenReturn(Mono.just(gameQuestion1));
        StepVerifier.create(gameWithQuestionsService.checkQuestion(user.getId(), game.getId(), gameQuestion1.getNumber(), answer))
                .expectNextMatches(response -> response.getCorrectAnswer().equals(questionWithCategory1.getAnswer()) &&
                        response.getIsCorrect() && answer.getYourAnswer().equals(questionWithCategory1.getAnswer()))
                .verifyComplete();

        when(questionWithCategoryService.findById(gameQuestion1.getQuestionId())).thenReturn(Mono.just(questionWithCategory2));
        StepVerifier.create(gameWithQuestionsService.checkQuestion(user.getId(), game.getId(), gameQuestion1.getNumber(), answer))
                .expectNextMatches(response -> response.getCorrectAnswer().equals(questionWithCategory1.getAnswer()) &&
                        !response.getIsCorrect() && !answer.getYourAnswer().equals(questionWithCategory1.getAnswer())
                )
                .verifyComplete();
    }
    // null pointer exception
    @Test
    void testCheckQuestion_shouldThrowConflictExceptionWhenWrongUser() {
        CheckQuestionAnswerDto answer = Mockito.mock(CheckQuestionAnswerDto.class);
        when(gameService.findById(game.getId())).thenReturn(Mono.just(game));
        StepVerifier.create(gameWithQuestionsService.checkQuestion(user.getId(), game.getId(), gameQuestion1.getNumber(), answer))
                .expectError(ConflictException.class)
                .verify();
    }
    @Test
    void testCheckQuestion_shouldThrowNotFoundExceptionWhenGameOrQuestionNotExist() {
        CheckQuestionAnswerDto answer = Mockito.mock(CheckQuestionAnswerDto.class);

        when(gameService.findById(game.getId())).thenReturn(Mono.empty());
        StepVerifier.create(gameWithQuestionsService.checkQuestion(user.getId(), game.getId(), gameQuestion1.getNumber(), answer))
                .expectError(NotFoundException.class)
                .verify();

        when(gameService.findById(game.getId())).thenReturn(Mono.just(game));
        when(gameQuestionService.findByGameIdAndQuestionNumber(game.getId(), gameQuestion1.getNumber())).thenReturn(Mono.empty());
        StepVerifier.create(gameWithQuestionsService.checkQuestion(user.getId(), game.getId(), gameQuestion1.getNumber(), answer))
                .expectError(NotFoundException.class)
                .verify();
    }
    // null pointer exception
    @Test
    void testCheckQuestion_shouldThrowConflictExceptionWhenAlreadyAnswered() {
        CheckQuestionAnswerDto answer = Mockito.mock(CheckQuestionAnswerDto.class);

        gameQuestion1.setIsAnswerGiven(true);
        when(gameService.findById(game.getId())).thenReturn(Mono.just(game));
        when(gameQuestionService.findByGameIdAndQuestionNumber(game.getId(), gameQuestion1.getNumber())).thenReturn(Mono.just(gameQuestion1));
        StepVerifier.create(gameWithQuestionsService.checkQuestion(user.getId(), game.getId(), gameQuestion1.getNumber(), answer))
                .expectError(ConflictException.class)
                .verify();
    }

    // null pointer exception
    @Test
    void testFinishGame_shouldReturnResponse() {
        game.setEndTime(LocalDateTime.now());
        when(gameService.findById(game.getId())).thenReturn(Mono.just(game));
        when(gameQuestionService.findAllByGameId(game.getId())).thenReturn(Flux.just(gameQuestion1));
        StepVerifier.create(gameWithQuestionsService.finishGame(user.getId(), game.getId()))
                .expectNextMatches(response -> response.getGameId().equals(game.getId()) && response.getAnswers().size() == 1)
                .verifyComplete();
    }
    @Test
    void testFinishGame_shouldThrowConflictExceptionWHenGameAlreadyCompleted() {
        game.setEndTime(LocalDateTime.now());
        when(gameService.findById(game.getId())).thenReturn(Mono.just(game));
        StepVerifier.create(gameWithQuestionsService.finishGame(user.getId(), game.getId()))
                .expectError(ConflictException.class)
                .verify();
    }
    @Test
    void testFinishGame_shouldThrowNotFoundExceptionWhenGameNotExist() {
        when(gameService.findById(game.getId())).thenReturn(Mono.empty());
        StepVerifier.create(gameWithQuestionsService.finishGame(user.getId(), game.getId()))
                .expectError(NotFoundException.class)
                .verify();
    }
    @Test
    void testFinishGame_shouldThrowConflictExceptionWhenWrongUser() {
        when(gameService.findById(game.getId())).thenReturn(Mono.just(game));
        StepVerifier.create(gameWithQuestionsService.finishGame(user.getId() + 1, game.getId()))
                .expectError(ConflictException.class)
                .verify();
    }
}


