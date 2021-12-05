package org.override.services;

import lombok.extern.log4j.Log4j2;
import org.override.core.models.HyperEntity;
import org.override.core.models.HyperException;
import org.override.core.models.HyperStatus;
import org.override.models.CreditModel;
import org.override.models.LearningProcessModel;
import org.override.models.TermResult;
import org.override.repositories.CreditRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Log4j2
public class LearningProcessService {
    String STUDEN_ID = "studentId";
    String INCLUDE_ACHIEVED = "includeAchieved";
    String INCLUDE_NOT_ACHIEVED = "includeNotAchieved";
    final CreditRepository creditRepository;
    String[] notIncludeCredits = new String[]{"BOBA1"};
    Map<String, Integer> minCreditsRequred = new HashMap<>() {{
        put("17", 147);
        put("18", 147);
        put("19", 147);
        put("20", 158);
        put("21", 158);
    }};

    @Autowired
    TermResultService termResultService;

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

        HyperEntity res = termResultService.termR(studenId);
        if (!res.status.equals(HyperStatus.OK))
            return res;

        TermResult termResult = TermResult.fromJson(res.body);
        List<String> ids = termResult.termResultItems.stream().map(i ->
                i.termScoreItems.stream().map(scoreItem ->
                        scoreItem.subjectId
                ).collect(Collectors.toList())
        ).flatMap(Collection::stream).collect(Collectors.toList());

        String course = termResult.studentSummary.id.substring(2, 4);
        List<CreditModel> credits = creditRepository.findAllNotIncludeCondition(
                course,
                ids
        );
        Integer minCredit = minCreditsRequred.get(course);
        List<String> processSubjectId = ids.stream().filter(
                id -> !Arrays.stream(notIncludeCredits).toList().contains(id)
        ).collect(Collectors.toList());
        String process = "(%s/%s)".formatted(processSubjectId.size(), minCredit);
        Double percent = ((double) processSubjectId.size() / minCredit) * 100;


        return HyperEntity.ok(new LearningProcessModel(percent, process, credits));
    }

}
