package ru.nuykin.quizrestapi.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.nuykin.quizrestapi.dto.QuestionDto;
import ru.nuykin.quizrestapi.model.Question;

@Mapper(componentModel = "spring")
public interface QuestionMapper {
    QuestionDto fromModelToDto(Question question);

    @InheritInverseConfiguration
    Question fromDtoToModel(QuestionDto questionDto);

    @Mapping(target = "id", ignore = true)
    Question updateEntity(Question oldQuestion, @MappingTarget Question newQuestion);
}

