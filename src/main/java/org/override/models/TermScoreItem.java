package org.override.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.override.core.models.HyperBody;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TermScoreItem implements HyperBody {
    public String subjectId;
    public String subjectName;
    public Integer creditsCount;
    public Double examPercent;
    public Double finalExamPercent;
    public Double examScore;
    public Double finalExamScore;
    public Double termScoreFirst;
    public Double termScoreSecond;
    public String gpaFirst;
    public String gpaSecond;
    public Double gpa;
    public String result;

    @Override
    public String toJson() {
        return gson.toJson(this);
    }

}
