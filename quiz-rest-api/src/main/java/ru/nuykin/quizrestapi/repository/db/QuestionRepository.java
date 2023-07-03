package ru.nuykin.quizrestapi.repository.db;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import ru.nuykin.quizrestapi.model.Question;

import java.util.List;

@Repository
public interface QuestionRepository extends R2dbcRepository<Question, Long> {
//    @Query(value = "SELECT * FROM quiz_question ORDER BY RANDOM() LIMIT 1")
//    Mono<Question> getRandomQuestion();

    @Query(value =
            "SELECT * " +
              "FROM quiz_question q " +
             "WHERE q.difficulty >= :minDifficulty " +
               "AND q.difficulty <= :maxDifficulty " +
               "AND q.category_id IN (:categories) " +
          "ORDER BY RANDOM() " +
             "LIMIT 1"
    )
    Mono<Question> getRandomQuestionByCategoryAndDifficulty(int minDifficulty, int maxDifficulty, List<Integer> categories);
}
