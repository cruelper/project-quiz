package ru.nuykin.quizrestapi.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.nuykin.quizrestapi.dto.CategoryDto;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class GameStartRequestDto {
    private Integer questionCount;
    private Integer minDifficulty;
    private Integer maxDifficulty;
    private List<CategoryDto> categories;
}
