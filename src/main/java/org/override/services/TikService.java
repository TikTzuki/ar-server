package org.override.services;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.extern.log4j.Log4j2;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.override.core.models.HyperBody;
import org.override.core.models.HyperEntity;
import org.override.core.models.HyperException;
import org.override.core.models.HyperStatus;
import org.override.models.ExampleModel;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Log4j2
@Service
public class TikService {
    String BASE_URL = "https://tiki.vn/api/personalish/v1/blocks/listings?limit=48&include=advertisement&aggregations=1&trackity_id=6290eea6-cefb-54c0-518f-c84ca428e4b1&category=%s";

    public HyperEntity handleCallTikiService(Map<String, String> headers) {
        String url = headers.get("client_message");
        if (url == null) {
            return HyperEntity.badRequest(
                    new HyperException(HyperException.BAD_REQUEST, null, "field required in headers: client_message")
            );
        }
        String body = callTikiService(url);
        return HyperEntity.ok(new ExampleModel(body));
    }

    public String callTikiService(String url) {
        String[] parts = url.split("/");
        String cId = parts[parts.length - 1];
        if (!cId.matches("c\\d+")) {
            return "NOT FOUND";
        }
        cId.replace("c", "");
        try {

            String doc = Jsoup.connect(String.format(BASE_URL, cId)).get().toString();
            JsonObject res = new Gson().fromJson(doc, JsonObject.class);
            JsonArray data = res.get("data").getAsJsonArray();
            Data products = new Data();
            data.forEach(e -> products.data.add(new Gson().fromJson(e, Product.class)));
            return products.toJson();
        } catch (IOException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    static class Data implements HyperBody {
        List<Product> data;

        @Override
        public String toJson() {
            return gson.toJson(this);
        }
    }

    static class Product implements HyperBody {
        public Integer price;
        public String name;

        @Override
        public String toJson() {
            return gson.toJson(this);
        }
    }
}
