package ru.nuykin.quizrestapi.mapper;

import org.mapstruct.*;
import ru.nuykin.quizrestapi.dto.QuestionDto;
import ru.nuykin.quizrestapi.model.QuestionWithCategory;

@Mapper(componentModel = "spring")
public interface QuestionWithCategoryMapper {
    QuestionDto fromModelToDto(QuestionWithCategory quizAnswer);

    @InheritInverseConfiguration
    QuestionWithCategory fromDtoToModel(QuestionDto questionDto);

    @Mapping(target = "id", ignore = true)
    QuestionWithCategory updateEntity(QuestionWithCategory oldQuizQuestionWithAnswer, @MappingTarget QuestionWithCategory newQuizQuestionWithAnswer);
}
