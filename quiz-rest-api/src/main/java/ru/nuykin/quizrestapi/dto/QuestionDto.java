package ru.nuykin.quizrestapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@RedisHash("QuizQuestionWithAnswersDto")
public class QuestionDto implements Serializable {
    private Long id;
    private String question;
    private CategoryDto category;

    private Integer difficulty;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Integer value;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String answer;

    public void setDifficulty(Integer difficulty) {
        this.difficulty = difficulty;
        this.value = difficulty;
    }

    public void setValue(Integer value) {
        this.difficulty = value;
        this.value = value;
    }
}
