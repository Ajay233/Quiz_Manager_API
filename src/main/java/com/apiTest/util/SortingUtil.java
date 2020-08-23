package com.apiTest.util;

import com.apiTest.Quiz.model.Answer;
import com.apiTest.Quiz.model.Question;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SortingUtil {

    public void QuestionSelectSort(List<Question> questions, int highestIndex){

        for(int i = 0; i < highestIndex-1; i++){

            int minimumValue = questions.get(i).getQuestionNumber();
            int indexOfMinVal = i;

            for(int x = i; x < highestIndex-1; x++){

                if(minimumValue > questions.get(x+1).getQuestionNumber()){
                    minimumValue = questions.get(x+1).getQuestionNumber();
                    indexOfMinVal = x + 1;
                }

            }

            //The swap
            Question questionToSwapIn = questions.get(indexOfMinVal);
            Question questionToSwapOut = questions.get(i);
            questions.set(i, questionToSwapIn);
            questions.set(indexOfMinVal, questionToSwapOut);
        }

    }

    public void AnswerSelectSort(List<Answer> answers, int highestIndex){

        for(int i = 0; i < highestIndex-1; i++){

            String smallestVal = answers.get(i).getAnswerIndex();
            int indexOfSmallestVal = i;

            for(int x = i; x < highestIndex-1; x++){

                if(smallestVal.compareTo(answers.get(x + 1).getAnswerIndex()) > 0){
                    smallestVal = answers.get(x+1).getAnswerIndex();
                    indexOfSmallestVal = x + 1;
                }

            }

            //The swap
            Answer answerToSwapIn = answers.get(indexOfSmallestVal);
            Answer answerToSwapOut = answers.get(i);
            answers.set(i, answerToSwapIn);
            answers.set(indexOfSmallestVal, answerToSwapOut);
        }

    }

}
