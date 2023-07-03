package ru.nuykin.quizrestapi.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import ru.nuykin.quizrestapi.model.User;
import ru.nuykin.quizrestapi.service.UserService;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@RequiredArgsConstructor
@Component
public class SecurityService {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.expirationInMS}")
    private Long expirationInMS;
    @Value("${jwt.issuer}")
    private String issuer;

    public Mono<TokenDetails> authenticate(String username, String password) {
        return userService.getUserByUsername(username)
                .flatMap(user -> {
                    if (!passwordEncoder.matches(password, user.getPassword())) {
                        return Mono.error(new RuntimeException());
                    }
                    return Mono.just(generateToken(user).toBuilder()
                            .userId(user.getId())
                            .build());
                })
                .switchIfEmpty(Mono.error(new RuntimeException()));
    }

    private TokenDetails generateToken(User user) {
        Map<String, Object> claims = new HashMap<>() {{
            put("role", user.getRole());
        }};

        String customer = user.getId().toString();

        return generateToken(claims, customer);
    }

    private TokenDetails generateToken(Map<String, Object> claims, String customer) {
        LocalDateTime createdDateTime = LocalDateTime.now();
        LocalDateTime expirationDateTime = createdDateTime.plusSeconds(expirationInMS);

        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuer(issuer)
                .setSubject(customer)
                .setIssuedAt(Date.from(createdDateTime.atZone(ZoneId.systemDefault()).toInstant()))
                .setId(UUID.randomUUID().toString())
                .setExpiration(Date.from(expirationDateTime.atZone(ZoneId.systemDefault()).toInstant()))
                .signWith(SignatureAlgorithm.HS256, Base64.getEncoder().encodeToString(secret.getBytes()))
                .compact();

        return TokenDetails.builder()
                .token(token)
                .issuedAt(createdDateTime)
                .expiresAt(expirationDateTime)
                .build();
    }

}
