package org.override.repositories;

import org.override.models.StudentModel;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<StudentModel, String> {
    @Query(value = "SELECT s FROM StudentModel s WHERE s.avgScore < :avgScore")
    List<StudentModel> findNearlyScore(@Param("avgScore") Integer avgScore, Pageable pageable);
}
