package org.override.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.override.core.models.HyperBody;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Log4j2
public class ExampleModel implements HyperBody {
    String name;
    @NonNull
    String message;

    @Override
    public String toJson() {
        return gson.toJson(this);
    }

    public static void main(String[] args) {
       log.warn("Vu cute");
    }
}
