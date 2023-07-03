package ru.nuykin.quizrestapi.repository.client;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Flux;
import ru.nuykin.quizrestapi.dto.QuestionDto;

@ReactiveFeignClient(url = "http://jservice.io/api/", name = "jservice")
@Component
public interface JServiceQuestionRepository {
    @GetMapping("/random")
    Flux<QuestionDto> getRandomQuestion(@RequestParam(value = "count") Integer count);
}
