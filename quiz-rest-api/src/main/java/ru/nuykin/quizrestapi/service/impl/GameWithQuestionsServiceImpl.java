package ru.nuykin.quizrestapi.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.nuykin.quizrestapi.dto.CategoryDto;
import ru.nuykin.quizrestapi.dto.CheckQuestionAnswerDto;
import ru.nuykin.quizrestapi.dto.request.GameStartRequestDto;
import ru.nuykin.quizrestapi.dto.response.GameFinishResponseDto;
import ru.nuykin.quizrestapi.dto.response.GameStartResponseDto;
import ru.nuykin.quizrestapi.exception.ConflictException;
import ru.nuykin.quizrestapi.exception.NotFoundException;
import ru.nuykin.quizrestapi.model.Game;
import ru.nuykin.quizrestapi.model.GameQuestion;
import ru.nuykin.quizrestapi.model.QuestionSource;
import ru.nuykin.quizrestapi.model.QuestionWithCategory;
import ru.nuykin.quizrestapi.repository.cache.RedisQuestionRepository;
import ru.nuykin.quizrestapi.service.GameQuestionService;
import ru.nuykin.quizrestapi.service.GameService;
import ru.nuykin.quizrestapi.service.GameWithQuestionsService;
import ru.nuykin.quizrestapi.service.QuestionWithCategoryService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class GameWithQuestionsServiceImpl implements GameWithQuestionsService {
    private final GameService gameService;
    private final GameQuestionService gameQuestionService;
    private final QuestionWithCategoryService questionWithCategoryService;
    private final JServiceService jServiceService;
    private final RedisQuestionRepository redisQuestionRepository;

    @Override
    public Mono<GameStartResponseDto> startGame(Long userId, GameStartRequestDto gameStartRequestDto) {
        return gameService.save(
                Game.builder()
                        .userId(userId)
                        .startTime(LocalDateTime.now())
                        .build()
        ).map(game -> {
            IntStream.range(0, gameStartRequestDto.getQuestionCount()).forEach(i ->
                    getIdOfRandomQuestion(
                            gameStartRequestDto.getMinDifficulty(),
                            gameStartRequestDto.getMaxDifficulty(),
                            gameStartRequestDto.getCategories().stream().map(CategoryDto::getId).toList()
                    ).flatMap(
                            entry -> gameQuestionService.save(
                                    GameQuestion.builder()
                                            .gameId(game.getId())
                                            .number(i)
                                            .questionId(entry.getKey())
                                            .questionSource(entry.getValue())
                                            .isCorrectAnswer(false)
                                            .isAnswerGiven(false)
                                            .build()
                            )).subscribe()
            );
            return GameStartResponseDto.builder()
                    .questionCount(Long.valueOf(gameStartRequestDto.getQuestionCount()))
                    .gameId(game.getId())
                    .build();
        });
    }

    private Mono<Map.Entry<Long, QuestionSource>> getIdOfRandomQuestion(
            int minDifficulty, int maxDifficulty, List<Integer> categories
    ) {
        if (Math.random() > 0.5) {
            return questionWithCategoryService.findRandom(minDifficulty, maxDifficulty, categories)
                    .map(question -> Map.entry(question.getId(), QuestionSource.DB));
        }
        return jServiceService.findRandom(1).last().map(question ->
                Map.entry(question.getId(), QuestionSource.JSERVICE
                ));
    }

    @Override
    public Mono<QuestionWithCategory> getQuestion(Long userId, Long gameId, Integer questionNumber) {
        return gameService.findById(gameId)
                .switchIfEmpty(Mono.error(new NotFoundException(gameId + "/" + questionNumber)))
                .flatMap(game -> {
                    if (!game.getUserId().equals(userId)) {
                        return Mono.error(new ConflictException("This is another users game"));
                    } else {
                        return gameQuestionService.findByGameIdAndQuestionNumber(gameId, questionNumber)
                                .switchIfEmpty(Mono.error(new NotFoundException(gameId + "/" + questionNumber)))
                                .flatMap(gameQuestion -> {
                                    if (gameQuestion.getQuestionSource() == QuestionSource.DB) {
                                        return questionWithCategoryService.findById(gameQuestion.getQuestionId()).log();
                                    } else {
                                        return redisQuestionRepository.findById(gameQuestion.getQuestionId()).log();
                                    }
                                });
                    }
                });
    }

    @Override
    public Mono<CheckQuestionAnswerDto> checkQuestion(
            Long userId,
            Long gameId,
            Integer questionNumber,
            CheckQuestionAnswerDto answer
    ) {
        return gameService.findById(gameId)
                .switchIfEmpty(Mono.error(new NotFoundException(gameId + "/" + questionNumber)))
                .flatMap(game -> {
                    if (!game.getUserId().equals(userId)) {
                        return Mono.error(new ConflictException("This is another users game"));
                    }
                    if (game.getEndTime() != null) {
                        return Mono.error(new ConflictException("Game already completed"));
                    } else {
                        return gameQuestionService.findByGameIdAndQuestionNumber(gameId, questionNumber)
                                .switchIfEmpty(Mono.error(new NotFoundException(gameId + "/" + questionNumber)))
                                .flatMap(gameQuestion -> {
                                    Mono<QuestionWithCategory> questionWithCategoryMono = gameQuestion.getQuestionSource() == QuestionSource.DB ?
                                            questionWithCategoryService.findById(gameQuestion.getQuestionId()) :
                                            redisQuestionRepository.findById(gameQuestion.getQuestionId());
                                    return questionWithCategoryMono.flatMap(question -> {
                                        gameQuestion.setIsCorrectAnswer(question.getAnswer().equals(answer.getYourAnswer()));
                                        if (gameQuestion.getIsAnswerGiven()) {
                                            return Mono.error(new ConflictException("Answer already given"));
                                        }
                                        gameQuestion.setIsAnswerGiven(true);
                                        return gameQuestionService.save(gameQuestion)
                                                .map(gameQuestion1 -> CheckQuestionAnswerDto.builder()
                                                        .correctAnswer(question.getAnswer())
                                                        .yourAnswer(answer.getYourAnswer())
                                                        .isCorrect(gameQuestion.getIsCorrectAnswer())
                                                        .build());
                                    });
                                });
                    }
                });
    }

    @Override
    public Mono<GameFinishResponseDto> finishGame(Long userId, Long gameId) {
        return gameService.findById(gameId)
                .switchIfEmpty(Mono.error(new NotFoundException(String.valueOf(gameId))))
                .flatMap(game -> {
                    if (game.getEndTime() != null) {
                        return Mono.error(new ConflictException("Game already completed"));
                    }
                    if (!game.getUserId().equals(userId)) {
                        return Mono.error(new ConflictException("This is another users game"));
                    }
                    game.setEndTime(LocalDateTime.now());
                    return gameService.save(game);
                })
                .flatMap(game -> gameQuestionService.findAllByGameId(gameId)
                        .map(gameQuestion -> Map.entry(gameQuestion.getNumber(), gameQuestion.getIsCorrectAnswer()))
                        .collectList()
                        .map(isCorrectList -> GameFinishResponseDto.builder()
                                .gameId(gameId)
                                .answers(isCorrectList)
                                .build()
                        ));
    }
}
