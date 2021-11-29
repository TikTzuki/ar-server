package org.override.core;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import lombok.extern.log4j.Log4j2;
import org.override.core.models.HyperEntity;
import org.override.core.models.HyperStatus;
import org.override.models.ExampleModel;
import org.override.core.models.HyperRoute;
import org.override.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
public class Router extends ClientSocketHandler {
    final SGUAcademicResult sguAcademicResult;
    final UserService userService;

    public Router(SGUAcademicResult sguAcademicResult, UserService userService) {
        this.sguAcademicResult = sguAcademicResult;
        this.userService = userService;
    }

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
                case HyperRoute.GET_EXAMPLE_SGU_ACADEMIC_RESULT -> response = sguAcademicResult.handleLookupSGUAcademicResult(headers);
                case HyperRoute.LOGIN -> response = userService.handleLogin(request);
                default -> {
                }
            }

            out.writeObject(gson.toJson(userService.encryptResponse(request, response)));
            out.flush();
        }
    }

}
