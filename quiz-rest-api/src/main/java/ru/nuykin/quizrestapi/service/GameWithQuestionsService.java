package ru.nuykin.quizrestapi.service;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Mono;
import ru.nuykin.quizrestapi.dto.CheckQuestionAnswerDto;
import ru.nuykin.quizrestapi.dto.request.GameStartRequestDto;
import ru.nuykin.quizrestapi.dto.response.GameFinishResponseDto;
import ru.nuykin.quizrestapi.dto.response.GameStartResponseDto;
import ru.nuykin.quizrestapi.model.Question;
import ru.nuykin.quizrestapi.model.QuestionWithCategory;

import java.util.concurrent.ExecutionException;

public interface GameWithQuestionsService {
    public Mono<GameStartResponseDto> startGame(Long userId, GameStartRequestDto gameStartRequestDto);

    public Mono<QuestionWithCategory> getQuestion(Long userId, Long gameId, Integer questionNumber);

    public Mono<CheckQuestionAnswerDto> checkQuestion(
            Long userId,
            Long game_id,
            Integer question_number,
            CheckQuestionAnswerDto answer
    );

    public Mono<GameFinishResponseDto> finishGame(Long userId, Long gameId);
}
