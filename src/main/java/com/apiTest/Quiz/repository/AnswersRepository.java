package com.apiTest.Quiz.repository;

import com.apiTest.Quiz.model.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface AnswersRepository extends JpaRepository<Answer, Long> {

    @Transactional
    @Modifying
    @Query(value = "truncate table answers", nativeQuery = true)
    void truncateTable();

    List<Answer> findByQuestionId(Long questionId);

}
