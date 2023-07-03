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
import ru.nuykin.quizrestapi.exception.NotFoundException;
import ru.nuykin.quizrestapi.model.Game;
import ru.nuykin.quizrestapi.model.GameQuestion;
import ru.nuykin.quizrestapi.repository.db.GameRepository;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import static org.mockito.Mockito.*;

class GameServiceImplTest {
    @Mock
    GameRepository gameRepository;
    @InjectMocks
    GameServiceImpl gameServiceImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testFindById_shouldReturnModelWhenExists() {
        Game game = Mockito.mock(Game.class);
        when(gameRepository.findById(game.getId())).thenReturn(Mono.just(game));
        StepVerifier.create(gameServiceImpl.findById(game.getId()))
                .expectNext(game)
                .verifyComplete();
    }
    @Test
    public void testFindById_shouldReturnEmptyWhenNotExist() {
        Game game = Mockito.mock(Game.class);
        when(gameRepository.findById(game.getId())).thenReturn(Mono.empty());
        StepVerifier.create(gameServiceImpl.findById(game.getId()))
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    public void testFindByUserId_shouldReturnModelWhenExists() {
        Random random = new Random();
        int count = random.nextInt(10);
        long targetUserId = random.nextLong();
        List<Game> targetGames = IntStream.range(0, random.nextInt(count))
                .mapToObj(i -> {
                    Game game = Mockito.mock(Game.class);
                    game.setUserId(targetUserId);
                    return game;
                })
                .toList();

        when(gameRepository.findAllByUserId(targetUserId)).thenReturn(Flux.fromIterable(targetGames));
        StepVerifier.create(gameServiceImpl.findByUserId(targetUserId))
                .expectNextSequence(targetGames)
                .verifyComplete();
    }
    @Test
    public void testFindByUserId_shouldReturnEmptyWhenNotExist() {
        Game game = Mockito.mock(Game.class);
        when(gameRepository.findAllByUserId(game.getUserId())).thenReturn(Flux.empty());
        StepVerifier.create(gameServiceImpl.findByUserId(game.getId()))
                .verifyComplete();
    }

    @Test
    public void testSave_shouldSaveSuccess() {
        Game game = Mockito.mock(Game.class);
        when(gameRepository.save(game)).thenReturn(Mono.just(game));
        StepVerifier.create(gameServiceImpl.save(game))
                .expectNext(game)
                .verifyComplete();
    }
}