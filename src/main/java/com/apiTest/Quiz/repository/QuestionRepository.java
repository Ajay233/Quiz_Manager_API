package com.apiTest.Quiz.repository;

import com.apiTest.Quiz.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    List<Question>findByQuizId(Long quizId);

    @Transactional
    @Modifying
    @Query(value = "truncate table questions", nativeQuery = true)
    void truncateTable();

}
