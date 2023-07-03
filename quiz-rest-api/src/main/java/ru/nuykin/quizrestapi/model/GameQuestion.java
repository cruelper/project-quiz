package ru.nuykin.quizrestapi.model;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(name = "game_question")
public class GameQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column("id")
    private Long id;

    @Column("game_id")
    private Long gameId;

    @Column("question_number")
    private Integer number;

    @Column("question_id")
    private Long questionId;

    @Column("question_source")
    private QuestionSource questionSource;

    @Column("is_correct_answer")
    private Boolean isCorrectAnswer;

    @Column("is_answer_given")
    private Boolean isAnswerGiven;
}
