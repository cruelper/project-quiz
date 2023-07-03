package ru.nuykin.quizrestapi.repository.db;

import org.springframework.data.domain.Sort;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import ru.nuykin.quizrestapi.model.GameQuestion;

@Repository
public interface GameQuestionRepository extends R2dbcRepository<GameQuestion, Long> {
    public Flux<GameQuestion> findAllByGameIdAndNumber(Long gameId, Integer number, Sort sort);

    public Flux<GameQuestion> findAllByGameId(Long gameId, Sort sort);
}
