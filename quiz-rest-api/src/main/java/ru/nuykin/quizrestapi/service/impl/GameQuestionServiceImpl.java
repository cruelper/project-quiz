package ru.nuykin.quizrestapi.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.nuykin.quizrestapi.exception.NotFoundException;
import ru.nuykin.quizrestapi.model.GameQuestion;
import ru.nuykin.quizrestapi.repository.db.GameQuestionRepository;
import ru.nuykin.quizrestapi.service.GameQuestionService;

@Service
@RequiredArgsConstructor
public class GameQuestionServiceImpl implements GameQuestionService {
    private final GameQuestionRepository gameQuestionRepository;

    @Override
    public Mono<GameQuestion> findByGameIdAndQuestionNumber(long gameId, int questionNumber) {
        return gameQuestionRepository.findAllByGameIdAndNumber(
                gameId, questionNumber, Sort.by(Sort.Direction.ASC, "number")
        ).switchIfEmpty(Mono.error(new NotFoundException(gameId + "/" + questionNumber))).last();
    }

    @Override
    public Flux<GameQuestion> findAllByGameId(long gameId) {
        return gameQuestionRepository.findAllByGameId(
                gameId, Sort.by(Sort.Direction.ASC, "number")
        );
    }

    @Override
    public Mono<GameQuestion> save(GameQuestion gameQuestion) {
        return gameQuestionRepository.save(gameQuestion);
    }
}
