package com.apiTest.authentication.repository;

import com.apiTest.authentication.model.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    VerificationToken findByToken(String token);

    @Transactional
    @Modifying
    @Query(
            value = "truncate table verification_token",
            nativeQuery = true
    )
    void truncateMyTable();

}
