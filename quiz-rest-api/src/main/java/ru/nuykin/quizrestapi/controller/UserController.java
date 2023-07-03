package ru.nuykin.quizrestapi.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ru.nuykin.quizrestapi.dto.UserDto;
import ru.nuykin.quizrestapi.dto.request.AuthRequestDto;
import ru.nuykin.quizrestapi.dto.response.AuthResponseDto;
import ru.nuykin.quizrestapi.mapper.UserMapper;
import ru.nuykin.quizrestapi.model.User;
import ru.nuykin.quizrestapi.security.SecurityService;
import ru.nuykin.quizrestapi.security.UserPrincipal;
import ru.nuykin.quizrestapi.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class UserController {
    private final SecurityService securityService;
    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping("/register")
    public Mono<UserDto> register(@RequestBody UserDto dto) {
        User user = userMapper.fromDtoToModel(dto);
        return userService.registerUser(user)
                .map(userMapper::fromModelToDto);
    }

    @PostMapping("/login")
    public Mono<AuthResponseDto> login(@RequestBody AuthRequestDto dto) {
        return securityService.authenticate(dto.getUsername(), dto.getPassword())
                .flatMap(tokenDetails -> Mono.just(
                        AuthResponseDto.builder()
                                .userId(tokenDetails.getUserId())
                                .token(tokenDetails.getToken())
                                .issuedAt(tokenDetails.getIssuedAt())
                                .expiresAt(tokenDetails.getExpiresAt())
                                .build()
                ));
    }

    @GetMapping("/info")
    public Mono<UserDto> getUserInfo(Authentication authentication) {
        UserPrincipal customPrincipal = (UserPrincipal) authentication.getPrincipal();

        return userService.getUserById(customPrincipal.getId())
                .map(userMapper::fromModelToDto);
    }
}
