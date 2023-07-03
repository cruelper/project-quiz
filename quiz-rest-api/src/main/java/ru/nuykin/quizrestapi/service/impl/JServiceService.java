package ru.nuykin.quizrestapi.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.nuykin.quizrestapi.dto.QuestionDto;
import ru.nuykin.quizrestapi.mapper.QuestionWithCategoryMapper;
import ru.nuykin.quizrestapi.repository.client.JServiceQuestionRepository;
import ru.nuykin.quizrestapi.repository.cache.RedisQuestionRepository;

@Service
@RequiredArgsConstructor
public class JServiceService {
    private final JServiceQuestionRepository jServiceQuestionRepository;
    private final QuestionWithCategoryMapper questionWithCategoryMapper;
    private final RedisQuestionRepository redisQuestionRepository;

    public Flux<QuestionDto> findRandom(int count) {
        return jServiceQuestionRepository.getRandomQuestion(count)
                .flatMap(questionDto -> {
                    redisQuestionRepository.save(questionWithCategoryMapper.fromDtoToModel(questionDto)).subscribe();
                    return Mono.just(questionDto);
                });
    }
}
