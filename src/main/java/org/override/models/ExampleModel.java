package org.override.models;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.hibernate.criterion.Example;
import org.override.core.models.HyperBody;
import org.override.core.models.HyperEntity;
import org.override.core.models.HyperException;
import org.override.core.models.HyperStatus;

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
