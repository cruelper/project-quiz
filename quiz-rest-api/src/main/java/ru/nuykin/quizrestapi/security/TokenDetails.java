package ru.nuykin.quizrestapi.security;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class TokenDetails {
    private Long userId;
    private String token;
    private LocalDateTime issuedAt;
    private LocalDateTime expiresAt;
}
