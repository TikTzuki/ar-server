package org.override.core.models;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
public class HyperException implements HyperBody {
    public static final String BAD_REQUEST = "BAD_REQUEST";
    public static final String NOT_FOUND = "NOT_FOUND";
    @NonNull
    String code;
    @NonNull
    Integer status;
    String loc;
    String detail;

    @Override
    public String toJson() {
        return gson.toJson(this);
    }
}
