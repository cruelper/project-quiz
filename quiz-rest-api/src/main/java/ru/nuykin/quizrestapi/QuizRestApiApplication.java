package ru.nuykin.quizrestapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.web.reactive.config.EnableWebFlux;
import reactivefeign.spring.config.EnableReactiveFeignClients;

@EnableR2dbcRepositories
@EnableAspectJAutoProxy
@EnableWebFlux
@EnableReactiveFeignClients
@SpringBootApplication
public class QuizRestApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(QuizRestApiApplication.class, args);
    }

}
