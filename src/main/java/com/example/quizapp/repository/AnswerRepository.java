package com.example.quizapp.repository;

import com.example.quizapp.models.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRepository extends JpaRepository<Answer, Long>{
}
