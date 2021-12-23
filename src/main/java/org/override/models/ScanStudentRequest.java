package org.override.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ScanStudentRequest {
    List<Integer> subjects = Stream.of(
            41, // "Công nghệ thông tin"
            42 // "Tài chính - Kế toán"
    ).collect(Collectors.toList());
    List<Integer> courses = Stream.of(17, 18, 19, 20, 21).collect(Collectors.toList());
    Integer from = 1;
    Integer to = 100;
}
