package org.override.services;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.override.core.models.HyperEntity;
import org.override.core.models.HyperException;
import org.override.core.models.HyperStatus;
import org.override.models.*;
import org.override.repositories.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Log4j2
@RequiredArgsConstructor
@Data
public class RankingService {
    static String STUDENT_ID = "studentId";
    static String INCLUDE_COURSE = "course";
    static String INCLUDE_SUBJECT = "includeSubject";
    static String INCLUDE_SPECIALITY = "includeSpeciality";
    @Autowired
    TermResultService termResultService;

    @NonNull
    final StudentRepository studentRepository;

    public HyperEntity handleGetRanking(Map<String, String> headers) {
        String studentId = headers.get(STUDENT_ID);
        boolean includeCourse = headers.get(INCLUDE_COURSE) != null && Boolean.parseBoolean(headers.get(INCLUDE_COURSE));
        boolean includeSubject = headers.get(INCLUDE_SUBJECT) != null && Boolean.parseBoolean(headers.get(INCLUDE_SUBJECT));
        boolean includeSpeciality = headers.get(INCLUDE_SPECIALITY) != null && Boolean.parseBoolean(headers.get(INCLUDE_SPECIALITY));
        if (studentId == null) {
            return HyperEntity.badRequest(new HyperException(
                    HyperException.BAD_REQUEST,
                    "headers -> %s".formatted(STUDENT_ID),
                    "%s is required".formatted(STUDENT_ID)
            ));
        }
//        if (!(includeCourse || includeSubject || includeSpeciality))
//            return HyperEntity.badRequest(new HyperException(
//                    HyperException.BAD_REQUEST,
//                    "headers -> include*",
//                    "%s or %s or %s must be true".formatted(INCLUDE_COURSE, INCLUDE_SUBJECT, INCLUDE_SPECIALITY)
//            ));
        Optional<StudentModel> studentOpt = studentRepository.findById(studentId);
        if (studentOpt.isPresent()) {
            StudentModel student = studentOpt.get();
            Integer course = includeCourse ? student.course : null;
            String speciality = includeSpeciality ? student.speciality : null;
            String subject = includeSubject ? student.subject : null;

            List<StudentModel> studentLessThan = studentRepository.findNearlyLessThanAvgScore(
                    student.avgScore,
                    course,
                    speciality,
                    subject,
                    PageRequest.of(0, 5, Sort.by("avgScore").descending())
            );
            List<StudentModel> studentGreaterThan = studentRepository.findNearlyGreaterThanAvgScore(
                    student.avgScore,
                    course,
                    speciality,
                    subject,
                    PageRequest.of(0, 5, Sort.by("avgScore").ascending())
            );

            studentLessThan.sort((o1, o2) -> (int) (o1.avgScore - o2.avgScore));
            studentLessThan.add(student);
            studentLessThan.addAll(studentGreaterThan);

            return HyperEntity.ok(new PagingModel<>(studentLessThan));
        } else {
            return HyperEntity.badRequest(new HyperException(
                    HyperException.BAD_REQUEST,
                    "headers -> studentId",
                    "student not found"
            ));
        }
    }

    public void scanStudents(ScanStudentRequest scanStudentRequest) {
        StudentIdIterator studentIdIterator = new StudentIdIterator(scanStudentRequest);
        while (studentIdIterator.hasNext()) {
            try {
                HyperEntity entity = termResultService.termR(studentIdIterator.next());
                if (entity.status.equals(HyperStatus.OK)) {
                    TermResult termResult = TermResult.fromJson(entity.body);
                    TermScoreSummary termScoreSummary = null;
                    int i = 1;
                    while (termScoreSummary == null && i < termResult.termResultItems.size()) {
                        termScoreSummary = termResult.termResultItems.get(termResult.termResultItems.size() - i).termScoreSummary;
                        i++;
                    }
//                System.out.format("%s %s %s %s %s \n",
//                        termResult.studentSummary.id,
//                        termScoreSummary != null ? termScoreSummary.avgScore : null,
//                        termResult.studentSummary.id.substring(2, 4),
//                        termResult.studentSummary.subject,
//                        termResult.studentSummary.speciality
//                );
                    studentRepository.save(
                            new StudentModel(
                                    termResult.studentSummary.id,
                                    termResult.studentSummary.name,
                                    termScoreSummary != null ? termScoreSummary.avgScore : null,
                                    Integer.valueOf(termResult.studentSummary.id.substring(2, 4)),
                                    termResult.studentSummary.subject,
                                    termResult.studentSummary.speciality
                            )
                    );
                }
            } catch (Exception ignore) {
            }
        }
    }

    static class StudentIdIterator implements Iterator<String> {
        /*
        31 là hệ ĐH [21 là hệ cđ] ;
         15 là Khóa 15 ;
         22 là mã ngành,
          0076 là số thứ tự
        */
        String type = "31";
        List<Integer> subjects = Stream.of(
                41, // "Công nghệ thông tin"
                42 // "Tài chính - Kế toán"
        ).collect(Collectors.toList());
        List<Integer> coursesOrigin = Stream.of(17, 18, 19, 20, 21).collect(Collectors.toList());
        List<Integer> courses;

        Integer currentSubject;
        Integer currentCourse;
        int i = 0;
        int maxStudent = 5;

        public StudentIdIterator() {
            courses = new ArrayList<>(coursesOrigin);
            this.currentSubject = subjects.remove(0);
            this.currentCourse = courses.remove(0);
        }

        public StudentIdIterator(ScanStudentRequest scanStudentRequest) {
            this.subjects = scanStudentRequest.getSubjects();
            this.coursesOrigin = scanStudentRequest.getCourses();
            this.i = scanStudentRequest.getFrom();
            this.maxStudent = scanStudentRequest.getTo();

            courses = new ArrayList<>(coursesOrigin);
            this.currentSubject = subjects.remove(0);
            this.currentCourse = courses.remove(0);
        }

        @Override
        public boolean hasNext() {
            return !(subjects.isEmpty() && courses.isEmpty());
        }

        @Override
        public String next() {
            String index = "000%s".formatted(i);
            String id = "%s%s%s%s".formatted(
                    type, currentCourse, currentSubject, index.substring(index.length() - 4)
            );
            i++;
            /*
nếu hết người {
còn couse -> người về 0, course tiếp theo
hết couse -> người về 0, course mới, subject mới
            */
//

            if (i > maxStudent) {
                i = 0;
                if (courses.isEmpty() && subjects.isEmpty()) {
                    System.out.println("emty");
                } else if (courses.isEmpty()) {
                    courses = new ArrayList<>(coursesOrigin);
                    currentCourse = courses.remove(0);
                    currentSubject = subjects.remove(0);
                } else {
                    currentCourse = courses.remove(0);
                }
            }
            return id;
        }
    }
}