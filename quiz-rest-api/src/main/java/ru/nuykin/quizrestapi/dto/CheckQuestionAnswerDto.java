package ru.nuykin.quizrestapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CheckQuestionAnswerDto {
    private String yourAnswer;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Boolean isCorrect;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String correctAnswer;
}
