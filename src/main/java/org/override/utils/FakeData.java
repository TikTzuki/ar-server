package org.override.utils;

import org.override.models.StudentSummary;
import org.override.models.TermResult;
import org.override.models.TermScoreItem;
import org.override.models.TermScoreSummary;

import java.util.List;

public class FakeData {
    public static TermResult getTermResult() {
        return new TermResult(
                new StudentSummary(
                        "3118410488",
                        "Lê Thị Hồng Vũ",
                        "Nữ",
                        "Đồng Nai",
                        null,
                        "DCT1188( Đại học chính quy - ngành Công nghệ thông tin - K.18 - Lớp 8)",
                        "Công nghệ thông tin",
                        "asd"
                ),
                List.of(
                        new TermResult.TermResultItem(
                                1,
                                "2018-2019",
                                List.of(
                                        new TermScoreItem(
                                                "861001",
                                                "Những nguyên lí cơ bản của chủ nghĩa Mác - Lênin",
                                                5,
                                                40D,
                                                60D,
                                                7.0,
                                                7.0,
                                                7.0,
                                                7.0,
                                                "B",
                                                "B",
                                                3.0,
                                                "Đạt"
                                        )
                                ),
                                new TermScoreSummary(
                                        5.89,
                                        1.95,
                                        6.56,
                                        2.44,
                                        16,
                                        16
                                )
                        )
                )
        );
    }
}
