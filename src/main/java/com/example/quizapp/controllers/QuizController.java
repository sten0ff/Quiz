package com.example.quizapp.controllers;

import com.example.quizapp.dto.AnswerDto;
import com.example.quizapp.dto.QuestionDto;
import com.example.quizapp.enums.Difficulty;
import com.example.quizapp.models.Answer;
import com.example.quizapp.models.Question;
import com.example.quizapp.models.Quiz;
import com.example.quizapp.repository.AnswerRepository;
import com.example.quizapp.repository.QuestionRepository;
import com.example.quizapp.repository.QuizRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Controller
@RequiredArgsConstructor
public class QuizController {
    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;

    @GetMapping("/")
    public String quiz(Model model) {
        model.addAttribute("available_quizzes", quizRepository.findAll());
        return "homepage";
    }

    @GetMapping("/quiz/{id}")
    public String quizInfo(@PathVariable Long id, Model model) {
        Quiz quiz = quizRepository.findById(id).orElse(new Quiz());
        model.addAttribute("quiz", quiz);
        model.addAttribute("difficulties", Arrays.stream(Difficulty.values()).toList());
        return "quiz-info";
    }

    @GetMapping("/quiz/create")
    public String createQuiz() {
        return "create-quiz";
    }

    @PostMapping("/quiz/create")
    public String createQuiz(@ModelAttribute Quiz quiz) {
        quizRepository.save(quiz);
        return "redirect:/";
    }

    @GetMapping("/quiz/remove/{id}")
    public String deleteQuiz(@PathVariable Long id) {
        quizRepository.deleteById(id);
        return "redirect:/";
    }

    @GetMapping("/question/{id}/{quizId}")
    public String questionInfo(@PathVariable Long id, @PathVariable Long quizId, Model model) {
        model.addAttribute("questions", questionRepository.getReferenceById(id));
        model.addAttribute("quiz", quizRepository.getReferenceById(quizId));
        model.addAttribute("difficulties", Arrays.stream(Difficulty.values()).toList());
        return "quiz-info";
    }


    @GetMapping("/question/create")
    public String createQuestion() {
        return "quiz-info";
    }

    @PostMapping("/question/create/{id}")
    public String createQuestion(@ModelAttribute QuestionDto questionDto, @PathVariable Long id, Model model) {
        Quiz quiz = quizRepository.findById(id).orElse(new Quiz());
        Question question = Question.builder().answers(new ArrayList<>()).text(questionDto.getText()).difficulty(questionDto.getDifficulty()).multipleChoice(questionDto.isMultipleChoice()).build();
        question.setQuiz(quiz);
        questionRepository.save(question);
        model.addAttribute("quiz", quiz);
        model.addAttribute("difficulties", Arrays.stream(Difficulty.values()).toList());
        return "quiz-info";
    }

    @PostMapping("/quiz/change-question/{questionId}")
    public String changeQuestionData(@ModelAttribute QuestionDto question, @PathVariable Long questionId) {
        Question question1 = questionRepository.getReferenceById(questionId);
        question1.setText(question.getText());
        questionRepository.save(question1);
        return "redirect:/quiz/" + question1.getQuiz().getId();
    }

    @GetMapping("/question/remove/{id}")
    public String deleteQuestion(@PathVariable Long id) {
        Question question = questionRepository.getReferenceById(id);
        answerRepository.deleteAll(question.getAnswers());
        questionRepository.deleteById(id);
        return "redirect:/";
    }

    @GetMapping("/answer/{id}/{questionId}")
    public String answerInfo(@PathVariable Long id, @PathVariable Long questionId, Model model) {
        model.addAttribute("answers", answerRepository.getReferenceById(id));
        model.addAttribute("question", questionRepository.getReferenceById(questionId));
        return "quiz-info";
    }


    @GetMapping("/answer/create/{questionId}")
    public String createAnswer(@PathVariable Long questionId, Model model) {
        model.addAttribute("question", questionRepository.getReferenceById(questionId));
        return "question-info";
    }

    @PostMapping("/answer/create/{id}")
    public String createAnswer(@ModelAttribute AnswerDto answerDto, @PathVariable Long id, Model model) {
        Question question = questionRepository.findById(id).orElse(new Question());
        Answer answer = Answer.builder().text(answerDto.getText()).correct(answerDto.isCorrect()).build();
        answer.setQuestion(question);
        answerRepository.save(answer);
        model.addAttribute("question", question);
        return "question-info";
    }

    @GetMapping("/answer/remove/{id}")
    public String deleteAnswer(@PathVariable Long id) {
        Answer answer = answerRepository.getReferenceById(id);
        Long questionId = answer.getQuestion().getId();
        answerRepository.deleteById(id);
        return "redirect:/answer/create/" + questionId;
    }

    @GetMapping("/quiz/start/{quizId}")
    public String startQuiz(@PathVariable Long quizId, Model model) {
        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new IllegalArgumentException("Invalid quiz ID"));
        List<Question> questions = quiz.getQuestions();
        model.addAttribute("quiz", quiz);
        model.addAttribute("questions", questions);
        return "quiz-start";
    }

    @PostMapping("/quiz/submit/{quizId}")
    public String submitQuiz(@PathVariable Long quizId, HttpServletRequest request, Model model) {
        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new IllegalArgumentException("Invalid quiz ID"));

        int correctAnswers = 0;

        for (Question question : quiz.getQuestions()) {


            String userAnswerParameter = request.getParameter("question_" + question.getId());

            for (Answer answer : question.getAnswers()) {
                if (answer.isCorrect() && String.valueOf(answer.getId()).equals(userAnswerParameter)) {
                    correctAnswers++;
                    break;
                }
            }
        }

        model.addAttribute("correctAnswers", correctAnswers);
        model.addAttribute("totalQuestions", quiz.getQuestions().stream().filter(Question::hasCorrectAnswer).count());

        return "quiz-result";
    }

}
