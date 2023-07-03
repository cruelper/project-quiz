package ru.nuykin.quizrestapi.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table("quiz_question")
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column("id")
    private Long id;

    @Column("text")
    private String question;

    @Column("answer")
    private String answer;

    @Column("category_id")
    private Integer categoryId;

    @Column("difficulty")
    private Integer difficulty;
}
