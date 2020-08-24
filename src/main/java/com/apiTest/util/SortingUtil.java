package com.apiTest.util;

import com.apiTest.Quiz.model.Answer;
import com.apiTest.Quiz.model.Question;
import com.apiTest.lookup.model.Lookup;
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

    // This could be replaced with a class (maybe called something like LookupService) which implements Comparator<Lookup>
    // For the time being this has been left as is to demonstrate using a select sort
    public void LookupSelectSort(List<Lookup> categories, int highestIndex){

        for(int i = 0; i < highestIndex-1; i++){

            String smallestVal = categories.get(i).getName();
            int indexOfSmallestVal = i;

            for(int x = i; x < highestIndex-1; x++){

                if(smallestVal.compareTo(categories.get(x + 1).getName()) > 0){
                    smallestVal = categories.get(x+1).getName();
                    indexOfSmallestVal = x + 1;
                }

            }

            //The swap
            Lookup answerToSwapIn = categories.get(indexOfSmallestVal);
            Lookup answerToSwapOut = categories.get(i);
            categories.set(i, answerToSwapIn);
            categories.set(indexOfSmallestVal, answerToSwapOut);
        }

    }

}
