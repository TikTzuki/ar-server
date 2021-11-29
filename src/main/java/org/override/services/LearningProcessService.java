package org.override.services;

import lombok.extern.log4j.Log4j2;
import org.override.core.models.HyperEntity;
import org.override.core.models.HyperException;
import org.override.models.LearningProcessModel;
import org.override.repositories.CreditRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Log4j2
public class LearningProcessService {
    String STUDEN_ID = "studentId";
    String INCLUDE_ACHIEVED = "includeAchieved";
    String INCLUDE_NOT_ACHIEVED = "includeNotAchieved";
    final CreditRepository creditRepository;

    public LearningProcessService(CreditRepository creditRepository) {
        this.creditRepository = creditRepository;
    }

    public HyperEntity handleGetLearningProcess(Map<String, String> headers) {
        String studenId = headers.get(STUDEN_ID);
        boolean includeAchieved = Boolean.parseBoolean(headers.get(INCLUDE_ACHIEVED) != null ? headers.get(INCLUDE_ACHIEVED) : "TRUE");
        boolean includeNotAchieved = Boolean.parseBoolean(headers.get(INCLUDE_NOT_ACHIEVED));
        if (studenId == null) {
            return HyperEntity.badRequest(
                    new HyperException(
                            HyperException.BAD_REQUEST,
                            "headers -> studenId",
                            "detail studenId at header is required"
                    )
            );
        }
        return getLearningProcess(studenId, includeAchieved, includeNotAchieved);
    }

    public HyperEntity getLearningProcess(String studenId, boolean includeAchieved, boolean includeNotAchieved) {
        String process = "(90/100)";
        Double percent = 10D;
        return HyperEntity.ok(
                new LearningProcessModel(percent, process, List.of())
        );
    }

}
