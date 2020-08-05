package com.apiTest.TestData;

import com.apiTest.Quiz.model.Answer;
import com.apiTest.Quiz.repository.AnswersRepository;
import com.apiTest.Quiz.repository.QuestionRepository;
import com.apiTest.Quiz.repository.QuizRepository;
import com.apiTest.lookup.repository.LookupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@Component
public class DatabaseLoader implements CommandLineRunner {

    @Autowired
    QuizRepository quizRepository;

    @Autowired
    QuestionRepository questionRepository;

    @Autowired
    AnswersRepository answersRepository;

    @Autowired
    LookupRepository lookupRepository;


    public Boolean canUseCorrect(Long questionId){
        List<Answer> answers = answersRepository.findByQuestionId(questionId);
        return answers.stream().allMatch(answer -> answer.getCorrectAnswer() == false);
    }

    public Boolean randomPick(Long questionId, int upperBound){
        if(canUseCorrect(questionId)){
            Random rand = new Random();
            int num = rand.nextInt(upperBound);
            if(num == 1){
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public void run(String... args) throws Exception {

//        if(quizRepository.findAll().isEmpty()) {
//            List<Lookup> categories = lookupRepository.findByType("Quiz Category");
//            categories.stream().forEach(category -> {
//                for (int i = 0; i < 20; i++) {
//                    String name = "Test Quiz" + i;
//                    Quiz quiz = quizRepository.save(new Quiz(name, "Quiz added as test data", category.getName()));
//                    for (int x = 0; x < 10; x++) {
//                        String questionDescripton = "Test question";
//                        Question question = questionRepository.save(new Question(quiz.getId(), x, questionDescripton));
//                        for (int y = 0; y < 4; y++) {
//                            String answerDescription = "Test answer " + y;
//                            answersRepository.save(new Answer(question.getId(), y, answerDescription, randomPick(question.getId(), y)));
//                        }
//                    }
//                }
//            });
//        }
    }
}
