package org.override.services;

import lombok.extern.log4j.Log4j2;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.override.core.models.HyperStatus;
import org.override.models.ExampleModel;
import org.override.core.models.HyperEntity;
import org.override.core.models.HyperException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@Log4j2
public class PersonalInfoService {
    private String URL = "https://masothue.com/Search/?q=%s&type=auto&token=KlEOUzJ9n4&force-search=1";

    public HyperEntity handleLookupPersonalInfo(Map<String, String> headers) {
        String clientMessage = headers.get("client_message");
        if (clientMessage == null) {
            return HyperEntity.badRequest(
                    new HyperException(HyperException.BAD_REQUEST, null, "field required client_message")
            );
        }
        String info = lookUpPersonalInfo(clientMessage);
        log.info(info);
        return HyperEntity.ok(new ExampleModel(info));
    }

    private String lookUpPersonalInfo(String message) {
        try {
            Document document = Jsoup.connect(String.format(URL, message)).get();
            log.info(document.toString());
            List<Element> tables = document.getElementsByClass("table-taxinfo");
            if (tables.size() == 0) {
                return "NOT FOUND";
            }
            Elements rows = tables.get(0).select("tr");

            Element addressRow = rows.get(2);
            String address = addressRow.select("td").get(1).text();

            Element nameRow = rows.get(3);
            String name = nameRow.select("td").get(1).text();

            return String.format("{ \"name\" : \"%s\" , \"address\" : \" %s \" }", name, address);
        } catch (IOException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }
}
