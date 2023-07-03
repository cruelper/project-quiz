package ru.nuykin.quizrestapi.service.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.nuykin.quizrestapi.exception.NotFoundException;
import ru.nuykin.quizrestapi.mapper.CategoryMapper;
import ru.nuykin.quizrestapi.model.Category;
import ru.nuykin.quizrestapi.repository.db.CategoryRepository;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.stream.IntStream;

@SpringBootTest
public class CategoryServiceImplTest {
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private CategoryMapper categoryMapper;
    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    public void testFindAll_shouldReturnEmptyListWhenModelsNotExists() {
        Mockito.when(categoryRepository.findAll()).thenReturn(Flux.empty());
        Flux<Category> categories = categoryService.findAll();
        StepVerifier.create(categoryService.findAll())
                .expectNextCount(0)
                .verifyComplete();
    }
    @Test
    public void testFindAll_shouldReturnNotEmptyWhenModelsExists() throws ExecutionException, InterruptedException {
        Random random = new Random();
        int count = random.nextInt(10);
        List<Category> inputCategories = IntStream.range(0, random.nextInt(count))
                .mapToObj(i -> Mockito.mock(Category.class))
                .toList();

        Mockito.when(categoryRepository.findAll()).thenReturn(Flux.fromIterable(inputCategories));
        List<Category> outputCategories = categoryService.findAll().collectList().toFuture().get();

        Assertions.assertEquals(inputCategories.size(), outputCategories.size());

        inputCategories.forEach(category -> Assertions.assertTrue(outputCategories.contains(category)));
    }

    @Test
    public void testFindById_shouldReturnModelWhenExist() throws ExecutionException, InterruptedException {
        Category inputCategory = Mockito.mock(Category.class);
        Mono<Category> categoryMono = Mono.just(inputCategory);

        Mockito.when(categoryRepository.findById(inputCategory.getId())).thenReturn(categoryMono);
        Category outputCategory = categoryService.findById(inputCategory.getId()).toFuture().get();

        Assertions.assertEquals(inputCategory.getId(), outputCategory.getId());
        Assertions.assertEquals(inputCategory.getName(), outputCategory.getName());
    }
    @Test
    public void testFindById_shouldThrowNotFoundExceptionWhenNotExist() {
        Category category = Mockito.mock(Category.class);
        Mockito.when(categoryRepository.findById(category.getId())).thenReturn(Mono.empty());

        Assertions.assertThrows(NotFoundException.class, () ->
                categoryService.findById(category.getId()).block());
    }

    @Test
    public void testSave_shouldSaveSuccess() throws ExecutionException, InterruptedException {
        Category categoryToSave = Mockito.mock(Category.class);
        Mockito.when(categoryRepository.save(categoryToSave)).thenReturn(Mono.just(categoryToSave));

        Category savedCategory = categoryService.save(categoryToSave).toFuture().get();
        Assertions.assertEquals(categoryToSave.getId(), savedCategory.getId());
        Assertions.assertEquals(categoryToSave.getName(), savedCategory.getName());
    }

    @Test
    public void testUpdate_shouldUpdateSuccess() throws ExecutionException, InterruptedException {
        Category oldCategory = Mockito.mock(Category.class);
        Category newCategory = Mockito.mock(Category.class);
        Mockito.when(categoryRepository.findById(oldCategory.getId())).thenReturn(Mono.just(oldCategory));
        Mockito.when(categoryRepository.save(newCategory)).thenReturn(Mono.just(newCategory));
        Mockito.when(categoryMapper.updateEntity(newCategory, oldCategory)).thenReturn(newCategory);

        Category updatedCategory = categoryService.update(oldCategory.getId(), newCategory).toFuture().get();
        Assertions.assertEquals(oldCategory.getId(), updatedCategory.getId());
        Assertions.assertEquals(newCategory.getName(), updatedCategory.getName());
    }
    @Test
    public void testUpdate_shouldThrowNotFoundExceptionWhenNotExist() {
        Category category = Mockito.mock(Category.class);
        Mockito.when(categoryRepository.findById(category.getId())).thenReturn(Mono.empty());

        Assertions.assertThrows(NotFoundException.class, () ->
                categoryService.update(category.getId(), category).block());
    }

    @Test
    public void deleteById_shouldReturnsVoidWhenValidId() {
        Category category = Mockito.mock(Category.class);
        Mockito.when(categoryRepository.findById(category.getId())).thenReturn(Mono.just(category));
        Mockito.when(categoryRepository.deleteById(category.getId())).thenReturn(Mono.empty());
        Mono<Void> result = categoryService.deleteById(category.getId());
        StepVerifier.create(result)
                .expectNextCount(0)
                .verifyComplete();
    }
    @Test
    public void deleteById_shouldThrowsNotFoundExceptionWhenInvalidId() {
        Category category = Mockito.mock(Category.class);
        Mockito.when(categoryRepository.findById(category.getId())).thenReturn(Mono.empty());

        Assertions.assertThrows(NotFoundException.class, () ->
                categoryService.deleteById(category.getId()).block());
    }
}