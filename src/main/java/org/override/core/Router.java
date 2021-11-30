package org.override.core;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.override.core.models.HyperEntity;
import org.override.core.models.HyperRoute;
import org.override.core.models.HyperStatus;
import org.override.services.LearningProcessService;
import org.override.services.RankingService;
import org.override.services.TermResultService;
import org.override.services.UserService;
import org.springframework.stereotype.Component;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;

@Component
@Log4j2
@AllArgsConstructor
public class Router extends ClientSocketHandler {
    final UserService userService;
    final LearningProcessService learningProcessService;
    final TermResultService termResultService;
    final RankingService rankingService;

    @Override
    public void handleRequest() throws IOException, ClassNotFoundException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeySpecException, InvalidKeyException {
        String rawRequest;
        HyperEntity response = HyperEntity.notFound(null);
        Gson gson = new Gson();
        HyperEntity request;
        while ((rawRequest = (String) in.readObject()) != null) {
            log.info(rawRequest);
            try {
                request = userService.auth(gson.fromJson(rawRequest, HyperEntity.class));
                if (HyperStatus.UNAUTHORIZED.equals(request.status)) {
                    out.writeObject(gson.toJson(request));
                    out.flush();
                    continue;
                }
            } catch (JsonSyntaxException ignore) {
                out.writeObject(gson.toJson(response));
                out.flush();
                continue;
            }

            Map<String, String> headers = request.headers;
            switch (request.route) {
                case HyperRoute.GET_TERM_RESULT -> response = termResultService.handleRequest(headers);
                case HyperRoute.GET_LEARNING_PROCESS -> response = learningProcessService.handleGetLearningProcess(headers);
                case HyperRoute.GET_RANKING -> response = rankingService.handleGetRanking(headers);
                case HyperRoute.LOGIN -> response = userService.handleLogin(request);
                default -> {
                }
            }

            out.writeObject(gson.toJson(userService.encryptResponse(request, response)));
            out.flush();
        }
    }

}
