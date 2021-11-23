package org.override.services;

import lombok.extern.log4j.Log4j2;
import org.override.core.models.HyperEntity;
import org.override.core.models.HyperException;
import org.override.models.TermResult;
import org.override.utils.FakeData;
import org.springframework.stereotype.Service;

import java.util.Map;

@Log4j2
@Service
public class TermResultService {
    /**
     * header: {
     * "mssv": "3118410488"
     * }
     */
    public HyperEntity handleRequest(Map<String, String> headers) {
        String mssv = headers.get("mssv");
        if (mssv == null) {
            return HyperEntity.badRequest(
                    new HyperException(
                            HyperException.BAD_REQUEST,
                            "headers -> mssv",
                            "detail mssv at headers is required"
                    )
            );
        }
        return getTermResult(mssv);
    }

    public HyperEntity getTermResult(String mssv) {
//        TODO: dùng jsoup lấy dữ liệu
        TermResult termResult = FakeData.getTermResult();
        return HyperEntity.ok(termResult);
    }

}
