package org.override.services;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.override.core.models.HyperEntity;
import org.override.core.models.HyperException;
import org.override.models.ExampleModel;
import org.override.models.PagingModel;
import org.override.models.StudentModel;
import org.override.repositories.StudentRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Log4j2
@RequiredArgsConstructor
@Data
public class RankingService {
    static String STUDENT_ID = "studentID";
    static String INCLUDE_COURSE = "course";
    static String INCLUDE_SUBJECT = "includeSubject";
    static String INCLUDE_SPECIALITY = "includeSpeciality";

    @NonNull
    final StudentRepository studentRepository;

    public HyperEntity handleGetRanking(Map<String, String> headers) {
        String studentId = headers.get(STUDENT_ID);
        boolean includeCourse = headers.get(INCLUDE_COURSE) != null && Boolean.parseBoolean(headers.get(INCLUDE_COURSE));
        boolean includeSubject = headers.get(INCLUDE_SUBJECT) != null && Boolean.parseBoolean(headers.get(INCLUDE_SUBJECT));
        boolean includeSpeciality = headers.get(INCLUDE_SPECIALITY) != null && Boolean.parseBoolean(headers.get(INCLUDE_SPECIALITY));
        if (!(includeCourse && includeSubject && includeSpeciality))
            return HyperEntity.badRequest(new HyperException(
                    HyperException.BAD_REQUEST,
                    "headers -> include*",
                    "%s or %s or %s must be true".formatted(INCLUDE_COURSE, INCLUDE_SUBJECT, INCLUDE_SPECIALITY)
            ));
        Optional<StudentModel> studentOpt = studentRepository.findById(studentId);
        if (studentOpt.isPresent()) {
            List<StudentModel> students = studentRepository.findNearlyScore(
                    5,
                    PageRequest.of(0, 3, Sort.by("avgScore").descending())
            );
            log.info(students);
            return HyperEntity.ok(new PagingModel<>(students));
        } else {
            return HyperEntity.badRequest(new HyperException(
                    HyperException.BAD_REQUEST,
                    "headers -> studentId",
                    "student not found"
            ));
        }
    }

    public static void main(String[] args) {
        Type collectionType = new TypeToken<List<ExampleModel>>() {
        }.getType();
    }
}
