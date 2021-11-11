package org.override.core;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.extern.log4j.Log4j2;
import org.override.models.HyperEntity;
import org.override.models.ExampleModel;
import org.override.models.HyperRoute;
import org.override.services.DictionaryService;
import org.override.services.EstimatingPiService;
import org.override.services.IPInfoService;
import org.override.services.PersonalInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
@Log4j2
public class Router extends ClientSocketHandler {
    final
    EstimatingPiService estimatingPiService;
    final
    DictionaryService dictionaryService;
    final
    IPInfoService ipInfoService;
    final
    PersonalInfoService personalInfoService;

    public Router(EstimatingPiService estimatingPiService, DictionaryService dictionaryService, IPInfoService ipInfoService, PersonalInfoService personalInfoService) {
        this.estimatingPiService = estimatingPiService;
        this.dictionaryService = dictionaryService;
        this.ipInfoService = ipInfoService;
        this.personalInfoService = personalInfoService;
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
                case HyperRoute.GET_EXAMPLE_ESTIMATING_PI:
                    ExampleModel ex = new Gson().fromJson(request.get("body"), ExampleModel.class);
                    response = estimatingPiService.handleEstimatingPi(headers, ex);
                    break;
                case HyperRoute.GET_EXAMPLE_DICTIONARY:
                    response = dictionaryService.handleLookUpDictionary(headers);
                    break;
                case HyperRoute.GET_EXAMPLE_LOOK_IP_INFO:
                    response = ipInfoService.handleLookupIpInfo(headers);
                    break;
                case HyperRoute.GET_EXAMPLE_PERSONAL_INFO:
                    response = personalInfoService.handleLookupPersonalInfo(headers);
                    break;
                default:
                    break;
            }

            out.writeObject(gson.toJson(response));
            out.flush();
        }
    }
}
