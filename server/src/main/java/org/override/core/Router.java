package org.override.core;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import lombok.extern.log4j.Log4j2;
import org.override.core.models.HyperEntity;
import org.override.models.ExampleModel;
import org.override.core.models.HyperRoute;
import org.override.services.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
@Log4j2
public class Router extends ClientSocketHandler {
    final EstimatingPiService estimatingPiService;
    final DictionaryService dictionaryService;
    final IPInfoService ipInfoService;
    final PersonalInfoService personalInfoService;
    final SGUAcademicResult sguAcademicResult;
    final EvalService evalService;

    public Router(EstimatingPiService estimatingPiService, DictionaryService dictionaryService, IPInfoService ipInfoService, PersonalInfoService personalInfoService, SGUAcademicResult sguAcademicResult, EvalService evalService) {
        this.estimatingPiService = estimatingPiService;
        this.dictionaryService = dictionaryService;
        this.ipInfoService = ipInfoService;
        this.personalInfoService = personalInfoService;
        this.sguAcademicResult = sguAcademicResult;
        this.evalService = evalService;
    }

    @Override
    public void handleRequest() throws IOException, ClassNotFoundException {
        String rawRequest;
        HyperEntity response = HyperEntity.notFound(null);
        Gson gson = new Gson();
        HyperEntity request;
        while ((rawRequest = (String) in.readObject()) != null) {
            log.info(rawRequest);
            try {
                request = gson.fromJson(rawRequest, HyperEntity.class);
            } catch (JsonSyntaxException ignore) {
                out.writeObject(gson.toJson(response));
                out.flush();
                continue;
            }

            Map<String, String> headers = request.headers;
            switch (request.route) {
                case HyperRoute.GET_EXAMPLE_ESTIMATING_PI -> {
                    ExampleModel ex = gson.fromJson(request.body, ExampleModel.class);
                    response = estimatingPiService.handleEstimatingPi(headers, ex);
                }
                case HyperRoute.GET_EXAMPLE_DICTIONARY -> response = dictionaryService.handleLookUpDictionary(headers);
                case HyperRoute.GET_EXAMPLE_LOOK_IP_INFO -> response = ipInfoService.handleLookupIpInfo(headers);
                case HyperRoute.GET_EXAMPLE_PERSONAL_INFO -> response = personalInfoService.handleLookupPersonalInfo(headers);
                case HyperRoute.GET_EXAMPLE_SGU_ACADEMIC_RESULT -> response = sguAcademicResult.handleLookupSGUAcademicResult(headers);
                case HyperRoute.GET_EXAMPLE_EVAL -> response = evalService.handleEval(headers);
                default -> {
                }
            }

            out.writeObject(gson.toJson(response));
            out.flush();
        }
    }

}
