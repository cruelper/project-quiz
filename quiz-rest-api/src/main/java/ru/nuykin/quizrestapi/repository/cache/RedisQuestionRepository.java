package ru.nuykin.quizrestapi.repository.cache;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.nuykin.quizrestapi.model.QuestionWithCategory;

@Repository
@RequiredArgsConstructor
public class RedisQuestionRepository {
    private final String KEY = "QuizQuestionWithAnswersDto";
    private final ReactiveRedisOperations<String, Object> reactiveRedisOperations;
    private final ObjectMapper mapper = new ObjectMapper();

    @PostConstruct
    private void init() {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public Flux<QuestionWithCategory> findAll() {
        return reactiveRedisOperations.opsForHash().values(KEY).map(map -> mapper.convertValue(map, QuestionWithCategory.class));
    }
    public Mono<QuestionWithCategory> findById(Long id) {
        return reactiveRedisOperations.opsForHash().get(KEY, id).map(map -> mapper.convertValue(map, QuestionWithCategory.class));
    }
    public Mono<Boolean> save(QuestionWithCategory question) {
        return reactiveRedisOperations.opsForHash().put(KEY, question.getId(), question);
    }
    public Mono<Long> remove(Long id) {
        return reactiveRedisOperations.opsForHash().remove(KEY, id);
    }
}
