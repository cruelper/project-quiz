package ru.nuykin.quizrestapi.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.nuykin.quizrestapi.dto.CategoryDto;
import ru.nuykin.quizrestapi.exception.NotFoundException;
import ru.nuykin.quizrestapi.mapper.CategoryMapper;
import ru.nuykin.quizrestapi.service.CategoryService;

@RestController
@RequestMapping(value = "/category")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;

    @GetMapping("/test")
    public Mono<Object> test() {
        return Mono.error(new NotFoundException("12"));
    }

    @GetMapping("/")
    @ResponseStatus(HttpStatus.OK)
    public Flux<CategoryDto> getAll() {
        return categoryService.findAll().map(categoryMapper::fromModelToDto);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<CategoryDto> findById(@PathVariable Integer id) {
        return categoryService.findById(id)
                .map(categoryMapper::fromModelToDto);
    }

    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<CategoryDto> create(@RequestBody CategoryDto categoryDto) {
        return categoryService.save(
                categoryMapper.fromDtoToModel(categoryDto)
        ).map(categoryMapper::fromModelToDto);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<CategoryDto> update(@PathVariable int id, @RequestBody CategoryDto categoryDto) {
        return categoryService.update(
                id, categoryMapper.fromDtoToModel(categoryDto)
        ).map(categoryMapper::fromModelToDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Void> delete(@PathVariable Integer id) {
        return categoryService.deleteById(id);
    }

}
