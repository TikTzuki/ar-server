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
    @Deprecated
    @Query(value = "SELECT s " +
            "FROM StudentModel s " +
            "WHERE s.avgScore<:avgScore " +
            "AND (:course is null or s.course=:course) " +
            "AND (:speciality is null or s.speciality=:speciality) " +
            "AND (:subject is null or s.subject=:subject) ")
    List<StudentModel> findNearlyLessThanAvgScore(
            @Param("avgScore") Double avgScore,
            @Param("course") Integer course,
            @Param("speciality") String speciality,
            @Param("subject") String subject,
            Pageable pageable
    );

    @Query(value = "SELECT s " +
            "FROM StudentModel s " +
            "WHERE s.avgScore > :avgScore " +
            "AND (:course is null or s.course=:course) " +
            "AND (:speciality is null or s.speciality=:speciality) " +
            "AND (:subject is null or s.subject=:subject) ")
    List<StudentModel> findNearlyGreaterThanAvgScore(
            @Param("avgScore") Double avgScore,
            @Param("course") Integer course,
            @Param("speciality") String speciality,
            @Param("subject") String subject,
            Pageable pageable
    );
}
