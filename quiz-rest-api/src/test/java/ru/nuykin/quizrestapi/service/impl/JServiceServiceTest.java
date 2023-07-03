package ru.nuykin.quizrestapi.service.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.nuykin.quizrestapi.dto.QuestionDto;
import ru.nuykin.quizrestapi.mapper.QuestionWithCategoryMapper;
import ru.nuykin.quizrestapi.model.Question;
import ru.nuykin.quizrestapi.model.QuestionWithCategory;
import ru.nuykin.quizrestapi.repository.cache.RedisQuestionRepository;
import ru.nuykin.quizrestapi.repository.client.JServiceQuestionRepository;

import static org.mockito.Mockito.*;

class JServiceServiceTest {
    @Mock
    JServiceQuestionRepository jServiceQuestionRepository;
    @Mock
    QuestionWithCategoryMapper questionWithCategoryMapper;
    @Mock
    RedisQuestionRepository redisQuestionRepository;
    @InjectMocks
    JServiceService jServiceService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testFindRandom() {
        QuestionDto questionDto = Mockito.mock(QuestionDto.class);
        QuestionWithCategory question = Mockito.mock(QuestionWithCategory.class);
        Mockito.when(jServiceQuestionRepository.getRandomQuestion(anyInt())).thenReturn(Flux.just(questionDto));
        Mockito.when(questionWithCategoryMapper.fromDtoToModel(questionDto)).thenReturn(question);
        Mockito.when(redisQuestionRepository.save(question)).thenReturn(Mono.just(true));
        StepVerifier.create(jServiceService.findRandom(1))
                .expectNext(questionDto)
                .verifyComplete();
        Mockito.verify(redisQuestionRepository, Mockito.times(1)).save(question);
    }
}
