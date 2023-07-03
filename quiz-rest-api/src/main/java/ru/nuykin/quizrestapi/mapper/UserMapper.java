package ru.nuykin.quizrestapi.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import ru.nuykin.quizrestapi.dto.UserDto;
import ru.nuykin.quizrestapi.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto fromModelToDto(User user);

    @InheritInverseConfiguration
    User fromDtoToModel(UserDto userDto);
}
