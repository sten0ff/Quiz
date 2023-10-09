package com.example.quizapp.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class AnswerDto {
    private String text;
    private boolean correct;
}
