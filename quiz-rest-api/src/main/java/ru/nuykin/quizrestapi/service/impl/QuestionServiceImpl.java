package ru.nuykin.quizrestapi.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.nuykin.quizrestapi.exception.NotFoundException;
import ru.nuykin.quizrestapi.mapper.QuestionMapper;
import ru.nuykin.quizrestapi.model.Question;
import ru.nuykin.quizrestapi.repository.db.QuestionRepository;
import ru.nuykin.quizrestapi.service.QuestionService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    private final QuestionMapper questionMapper;

    @Override
    public Flux<Question> findAll() {
        return questionRepository.findAll();
    }

    @Override
    public Mono<Question> findById(long id) {
        return questionRepository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException(String.valueOf(id))));
    }

    @Override
    public Mono<Question> findRandom(int minDifficulty, int maxDifficulty, List<Integer> categories) {
        return questionRepository.getRandomQuestionByCategoryAndDifficulty(minDifficulty, maxDifficulty, categories)
                .switchIfEmpty(Mono.error(new NotFoundException(String.valueOf("*"))));
    }

    @Override
    public Mono<Question> save(Question question) {
        return questionRepository.save(question);
    }

    @Override
    public Mono<Question> update(long id, Question question) {
        return questionRepository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException(String.valueOf(id))))
                .flatMap(oldQuizQuestion -> questionRepository.save(
                                questionMapper.updateEntity(question, oldQuizQuestion)
                        )
                );
    }

    @Override
    public Mono<Void> deleteById(long id) {
        return questionRepository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException(String.valueOf(id))))
                .flatMap(question -> questionRepository.deleteById(id));
    }
}
