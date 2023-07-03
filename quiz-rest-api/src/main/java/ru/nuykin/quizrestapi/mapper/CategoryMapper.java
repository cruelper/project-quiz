package ru.nuykin.quizrestapi.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.nuykin.quizrestapi.dto.CategoryDto;
import ru.nuykin.quizrestapi.model.Category;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryDto fromModelToDto(Category category);

    @InheritInverseConfiguration
    Category fromDtoToModel(CategoryDto categoryDto);

    @Mapping(target = "id", ignore = true)
    Category updateEntity(Category oldCategory, @MappingTarget Category newCategory);
}
