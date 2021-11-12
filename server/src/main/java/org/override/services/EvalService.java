package org.override.services;

import lombok.extern.log4j.Log4j2;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.override.models.HyperEntity;
import org.override.models.HyperException;
import org.override.utils.ErrorCodes;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Log4j2
public class EvalService {
    public HyperEntity<Object> handleEval(Map<String, String> headers) {
        String clientMessage = headers.get("client_message");
        if (clientMessage == null) {
            return HyperEntity.badRequest(
                    new HyperException(ErrorCodes.BAD_REQUEST, null, "field required in headers: client_message")
            );
        }
        String mathResult = eval(clientMessage);
        return HyperEntity.ok(mathResult);
    }

    public String eval(String clientMessage) {
        Expression expression = new ExpressionBuilder(clientMessage).build();
        return String.format("{\"result\": \"%s\"}", expression.evaluate());
    }
}
