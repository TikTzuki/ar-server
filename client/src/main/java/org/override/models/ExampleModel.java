package org.override.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class ExampleModel implements HyperBody {
    String name;
    @NonNull
    String message;

    @Override
    public String toJson() {
        return gson.toJson(this);
    }

}
