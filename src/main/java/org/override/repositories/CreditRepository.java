package org.override.repositories;

import org.override.models.CreditModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CreditRepository extends JpaRepository<CreditModel, String> {
    @Query("SELECT c  " +
            "FROM CreditModel c " +
            "WHERE c.courses LIKE %:course% " +
            "AND c.subjectId NOT IN (:subjectIds)"
    )
    List<CreditModel> findAllNotIncludeCondition(
            @Param("course") String course,
            @Param("subjectIds") List<String> subjectIds
    );
}
