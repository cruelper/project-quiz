package ru.nuykin.quizrestapi.repository.db;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import ru.nuykin.quizrestapi.model.Category;

@Repository
public interface CategoryRepository extends R2dbcRepository<Category, Integer> {
}
