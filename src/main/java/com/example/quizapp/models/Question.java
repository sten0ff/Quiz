package com.example.quizapp.models;

import com.example.quizapp.enums.Difficulty;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String text;
    @Enumerated(EnumType.STRING)
    private Difficulty difficulty;
    private boolean multipleChoice;
    @ManyToOne
    @JoinColumn(name = "quiz_id")
    @JsonManagedReference
    private Quiz quiz;
    @OneToMany(mappedBy = "question")
    @JsonManagedReference
    @ToString.Exclude
    private List<Answer> answers;

    public boolean isNotAllowMultiplyChoiceAndHasCorrectAnswer(){
        return !multipleChoice && this.hasCorrectAnswer();
    }

    public boolean hasCorrectAnswer(){
        return this.answers.stream().anyMatch(Answer::isCorrect);
    }
}
