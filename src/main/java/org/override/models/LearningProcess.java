package org.override.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.override.core.models.HyperBody;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LearningProcess implements HyperBody {
    public Double learningProcessPercent;
    public List<Credits> credits;

    @Override
    public String toJson() {
        return gson.toJson(this);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Credits {
        public String id;
        public String name;
        public Integer creditsCount;
    }
}
