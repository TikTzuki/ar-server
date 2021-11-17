package org.override.services;

import lombok.extern.log4j.Log4j2;
import org.override.models.ExampleModel;
import org.override.core.models.HyperEntity;
import org.override.core.models.HyperException;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

/**
 * Bài 1:
 * Viết chương trình tra từ điển Anh-Việt/Việt-Anh, hoạt động theo mô hình client-server, sử
 * dụng TCP socket với các yêu cầu chức năng tương tự bài 3 của tuần 02.
 */
@Service
@Log4j2
public class DictionaryService {
    public HyperEntity handleLookUpDictionary(Map<String, String> headers) {
        String clientMessage = headers.get("client_message");
        if (clientMessage == null) {
            return HyperEntity.badRequest(
                    new HyperException(HyperException.BAD_REQUEST, null, "field required in headers: client_message")
            );
        }
        String word = lookUpDictionary(clientMessage);
        if (word == null)
            return HyperEntity.notFound(
                    new HyperException(HyperException.NOT_FOUND, null, "word not found")
            );
        else
            return HyperEntity.ok(new ExampleModel(word));
    }

    public String lookUpDictionary(String word) {
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader("dictionary.txt"));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] pair = line.split(";");
                log.debug(pair);
                if (pair[0].equalsIgnoreCase(word))
                    return pair[1];
                if (pair[1].equalsIgnoreCase(word))
                    return pair[0];
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
