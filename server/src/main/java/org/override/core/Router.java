package org.override.core;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.extern.log4j.Log4j2;
import org.override.models.HyperEntity;
import org.override.models.ExampleModel;
import org.override.models.HyperRoute;
import org.override.services.*;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    EvalService evalService;

    public Router(EstimatingPiService estimatingPiService, DictionaryService dictionaryService, IPInfoService ipInfoService, PersonalInfoService personalInfoService, SGUAcademicResult sguAcademicResult) {
        this.estimatingPiService = estimatingPiService;
        this.dictionaryService = dictionaryService;
        this.ipInfoService = ipInfoService;
        this.personalInfoService = personalInfoService;
        this.sguAcademicResult = sguAcademicResult;
    }

    @Override
    public void handleRequest() throws IOException, ClassNotFoundException {
        String rawRequest;
        HyperEntity<Object> response = HyperEntity.notFound("");

        while ((rawRequest = (String) in.readObject()) != null) {
            log.debug(rawRequest);
            Gson gson = new Gson();
            JsonObject request = gson.fromJson(rawRequest, JsonObject.class);
            Map<String, String> headers = gson.fromJson(rawRequest, HyperEntity.class).headers;
            switch (request.get("route").getAsString()) {
                case HyperRoute.GET_EXAMPLE_ESTIMATING_PI -> {
                    ExampleModel ex = new Gson().fromJson(request.get("body"), ExampleModel.class);
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
