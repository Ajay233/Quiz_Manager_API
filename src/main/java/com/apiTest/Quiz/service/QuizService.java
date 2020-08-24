package com.apiTest.Quiz.service;

import com.apiTest.Quiz.model.Answer;
import com.apiTest.Quiz.model.Question;
import com.apiTest.Quiz.model.Quiz;
import com.apiTest.Quiz.model.QuizListItem;
import com.apiTest.Quiz.repository.AnswersRepository;
import com.apiTest.Quiz.repository.QuestionRepository;
import com.apiTest.Quiz.repository.QuizRepository;
import com.apiTest.lookup.model.Lookup;
import com.apiTest.lookup.repository.LookupRepository;
import com.apiTest.util.SortingUtil;
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
    AnswersRepository answersRepository;

    @Autowired
    LookupRepository lookupRepository;

    @Autowired
    SortingUtil sortingUtil;

    public void deleteQuizAndAssociations(Quiz quiz){
        List<Question> questions = questionRepository.findByQuizId(quiz.getId());
        if(!questions.isEmpty()){
            questionsService.deleteQuestionsAndAnswers(questions);
        }
        quizRepository.delete(quiz);
    }

    public ArrayList<QuizListItem> getAllQuizesOrderedByCategory() {
        List<Lookup> categories = lookupRepository.findByType("Quiz Category");
        sortingUtil.LookupSelectSort(categories, categories.size());
        System.out.println(categories);
        ArrayList<QuizListItem> quizList = new ArrayList<>();
        categories.stream().forEach(category -> {
            if (!quizRepository.findByCategory(category.getName()).isEmpty()) {
                System.out.println("Found...processing");
                List<Quiz> quizzes = quizRepository.findByCategory(category.getName());
                QuizListItem item = new QuizListItem(category.getName(), quizzes);
                quizList.add(item);
            } else {
                ArrayList<Quiz> quizzes = new ArrayList<>();
                QuizListItem item = new QuizListItem(category.getName(), quizzes);
                quizList.add(item);
            }
        });
        return quizList;
    }

    private Boolean contains(List<Answer> answers, Boolean val){
        return answers.stream().anyMatch(answer -> answer.getCorrectAnswer().equals(val));
    }


    public Boolean quizReady(Long quizId) {
        List<Question> questions = questionRepository.findByQuizId(quizId);
        if(!questions.isEmpty()) {
            return questions.stream().allMatch(question -> {
                List<Answer> answers = answersRepository.findByQuestionId(question.getId());
                if (answers.size() >= 2) {
                    return contains(answers, true) && contains(answers, false);
                } else {
                    return false;
                }
            });
        } else {
            return false;
        }
    }

}
