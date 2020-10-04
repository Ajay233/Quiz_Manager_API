package com.apiTest.Quiz.repository;

import com.apiTest.Quiz.model.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
    List<Quiz> findByName(String name);
    List<Quiz> findByCategory(String category);

    @Transactional
    @Modifying
    @Query(value = "truncate table quizzes", nativeQuery = true)
    void truncateTable();

}
