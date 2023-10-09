package com.example.quizapp.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class QuizDto {
    private Long id;
    private String text;
}
