package com.apiTest.lookup.repository;

import com.apiTest.lookup.model.Lookup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface LookupRepository extends JpaRepository<Lookup, Long> {

    @Transactional
    @Modifying
    @Query(value = "truncate table lookup", nativeQuery = true)
    void truncateTable();

    List<Lookup> findByType(String type);

}
