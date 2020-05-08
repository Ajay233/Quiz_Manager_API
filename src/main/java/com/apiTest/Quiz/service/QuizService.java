package com.apiTest.Quiz.service;

import com.apiTest.Quiz.model.Question;
import com.apiTest.Quiz.model.Quiz;
import com.apiTest.Quiz.model.QuizListItem;
import com.apiTest.Quiz.repository.QuestionRepository;
import com.apiTest.Quiz.repository.QuizRepository;
import com.apiTest.lookup.model.Lookup;
import com.apiTest.lookup.repository.LookupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuizService {

    @Autowired
    QuizRepository quizRepository;

    @Autowired
    QuestionRepository questionRepository;

    @Autowired
    QuestionsService questionsService;

    @Autowired
    LookupRepository lookupRepository;

    public void deleteQuizAndAssociations(Quiz quiz){
        List<Question> questions = questionRepository.findByQuizId(quiz.getId());
        if(!questions.isEmpty()){
            questionsService.deleteQuestionsAndAnswers(questions);
        }
        quizRepository.delete(quiz);
    }

    public ArrayList<QuizListItem> getAllQuizesOrderedByCategory() {
        List<Lookup> categories = lookupRepository.findByType("Quiz Category");
        System.out.println(categories);
        ArrayList<QuizListItem> quizList = new ArrayList<>();
        categories.stream().forEach(category -> {
            if (!quizRepository.findByCategory(category.getName()).isEmpty()) {
                System.out.println("Found...processing");
                List<Quiz> quizzes = quizRepository.findByCategory(category.getName());
                QuizListItem item = new QuizListItem(category.getName(), quizzes);
                quizList.add(item);
            } else {
                return;
            }
        });
        return quizList;
    }

}
