package org.override.services;

import lombok.extern.log4j.Log4j2;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.override.core.models.HyperStatus;
import org.override.models.ExampleModel;
import org.override.core.models.HyperEntity;
import org.override.core.models.HyperException;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Log4j2
public class EvalService {
    public HyperEntity handleEval(Map<String, String> headers) {
        String clientMessage = headers.get("client_message");
        if (clientMessage == null) {
            return HyperEntity.badRequest(
                    new HyperException(HyperException.BAD_REQUEST, HyperStatus.BAD_REQUEST, null, "field required in headers: client_message")
            );
        }
        String mathResult = eval(clientMessage);
        return HyperEntity.ok(new ExampleModel(mathResult));
    }

    public String eval(String clientMessage) {
        try {
            Expression expression = new ExpressionBuilder(clientMessage).build();
            return String.format("{\"result\": \"%s\"}", expression.evaluate());
        } catch (Exception e) {
            return "Wrong syntax";
        }
    }
}
