package ru.nuykin.quizrestapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CategoryDto {
    private Integer id;

    private String name;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String title;

    public void setName(String name) {
        this.name = name;
        this.title = name;
    }

    public void setTitle(String title) {
        this.name = title;
        this.title = title;
    }
}
