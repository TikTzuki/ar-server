package org.override.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TermScoreSummary {
    /* Điểm trung bình học kỳ hệ 10/100 */
    public Double avgTermScore;
    /* Điểm trung bình học kỳ hệ 4: */
    public Double avgGPATermScore;
    //    Điểm trung bình tích lũy:
    public Double avgScore;
    //    Điểm trung bình tích lũy (hệ 4):
    public Double avgGPAScore;
    //    Số tín chỉ đạt:
    public Integer creditsTermCount;
    //    Số tín chỉ tích lũy:
    public Integer creditsCount;
}
