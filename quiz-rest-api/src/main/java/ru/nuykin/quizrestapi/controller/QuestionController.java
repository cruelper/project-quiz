package ru.nuykin.quizrestapi.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.nuykin.quizrestapi.dto.QuestionDto;
import ru.nuykin.quizrestapi.service.impl.JServiceService;
import ru.nuykin.quizrestapi.mapper.QuestionWithCategoryMapper;
import ru.nuykin.quizrestapi.service.QuestionWithCategoryService;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping(value = "/question-with-answers")
@RequiredArgsConstructor
public class QuestionController {
    private final QuestionWithCategoryService questionWithCategoryService;
    private final QuestionWithCategoryMapper questionWithCategoryMapper;
    private final JServiceService jServiceService;

    @GetMapping("/")
    @ResponseStatus(HttpStatus.OK)
    public Flux<QuestionDto> getAll() {
        return questionWithCategoryService.findAll().map(questionWithCategoryMapper::fromModelToDto);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<QuestionDto> findById(@PathVariable Integer id) {
        return questionWithCategoryService.findById(id)
                .map(questionWithCategoryMapper::fromModelToDto);
    }

    @GetMapping("/random")
    @ResponseStatus(HttpStatus.OK)
    public Mono<QuestionDto> findRandom(
            @RequestParam(required = true, name = "min_difficulty") Integer minDifficulty,
            @RequestParam(required = true, name = "max_difficulty") Integer maxDifficulty,
            @RequestParam(required = true, name = "categories") List<Integer> categories
    ) {
        if ((int)(Math.random()*100) % 2 == 0) {
            return questionWithCategoryService.findRandom(minDifficulty, maxDifficulty, categories)
                    .map(questionWithCategoryMapper::fromModelToDto);
        }
        return jServiceService.findRandom(1).last();
    }

    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<QuestionDto> create(@RequestBody QuestionDto questionDto) throws ExecutionException, InterruptedException {
        return questionWithCategoryService.save(
                questionWithCategoryMapper.fromDtoToModel(questionDto)
        ).map(questionWithCategoryMapper::fromModelToDto);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<QuestionDto> update(@PathVariable int id, @RequestBody QuestionDto questionDto) throws ExecutionException, InterruptedException {
        return questionWithCategoryService.update(
                id, questionWithCategoryMapper.fromDtoToModel(questionDto)
        ).map(questionWithCategoryMapper::fromModelToDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Void> delete(@PathVariable Integer id) {
        return questionWithCategoryService.deleteById(id);
    }

}
