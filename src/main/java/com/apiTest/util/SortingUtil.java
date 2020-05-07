package com.apiTest.util;

import com.apiTest.Quiz.model.Question;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SortingUtil {

    public void QuestionSelectSort(List<Question> questions, int highestIndex){

        // 1 iteration for each element
        for(int i = 0; i < highestIndex-1; i++){        // 0
            System.out.println("Outer " + i);
            int minimumValue = questions.get(i).getQuestionNumber();  // 0
            int indexOfMinVal = i;   // 0
            for(int g = 0; g < questions.size(); g++){
                System.out.println(questions.get(g).getDescription());
            }
            // [12, 6, 8, 22, 3, 55, 4, 11, 1, 17]
            // [1, 6, 8, 22, 3, 55, 4, 11, 12, 17]
            System.out.println(questions);
            for(int x = i; x < highestIndex-1; x++){        // 2
                System.out.println("Inner");
                System.out.println("min " + minimumValue);
                System.out.println(" compared with " + questions.get(x+1).getQuestionNumber());

                // Need to be able to skip elements e.g.

                if(minimumValue > questions.get(x+1).getQuestionNumber()){   //
                    minimumValue = questions.get(x+1).getQuestionNumber();   // 2
                    indexOfMinVal = x + 1;    // 1
                }

            }

            //The swap
            Question questionToSwapIn = questions.get(indexOfMinVal);
            Question questionToSwapOut = questions.get(i);  // 1
            questions.set(i, questionToSwapIn);
            questions.set(indexOfMinVal, questionToSwapOut);
        }
    }

}
