package ru.nuykin.quizrestapi.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.nuykin.quizrestapi.exception.NotFoundException;
import ru.nuykin.quizrestapi.model.Category;
import ru.nuykin.quizrestapi.model.Game;
import ru.nuykin.quizrestapi.repository.db.GameRepository;
import ru.nuykin.quizrestapi.service.GameService;

@Service
@RequiredArgsConstructor
public class GameServiceImpl implements GameService {
    private final GameRepository gameRepository;

    @Override
    public Mono<Game> findById(long id) {
        return gameRepository.findById(id);
    }

    @Override
    public Flux<Game> findByUserId(long id) {
        return gameRepository.findAllByUserId(id);
    }

    @Override
    public Mono<Game> save(Game game) {
        return gameRepository.save(game);
    }
}
