package com.example.quizapp.dto;

import com.example.quizapp.enums.Difficulty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class QuestionDto {
    private String text;
    private Difficulty difficulty;
    private boolean multipleChoice;
}
