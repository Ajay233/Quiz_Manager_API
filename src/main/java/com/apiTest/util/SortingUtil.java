package com.apiTest.util;

import com.apiTest.Quiz.model.Question;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SortingUtil {

    public void QuestionSelectSort(List<Question> questions, int highestIndex){

        for(int i = 0; i < highestIndex-1; i++){

            int minimumValue = questions.get(i).getQuestionNumber();
            int indexOfMinVal = i;

            for(int g = 0; g < questions.size(); g++){
                System.out.println(questions.get(g).getDescription());
            }

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

}
