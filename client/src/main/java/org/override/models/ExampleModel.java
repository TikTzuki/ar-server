package org.override.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.override.core.models.HyperBody;

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
