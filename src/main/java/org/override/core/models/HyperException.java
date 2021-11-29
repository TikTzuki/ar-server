package org.override.core.models;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
public class HyperException implements HyperBody {
    public static final String BAD_REQUEST = "BAD_REQUEST";
    public static final String NOT_FOUND = "NOT_FOUND";
    public static final String UNAUTHORIZED = "UNAUTHORIZED";
    @NonNull
    String code;
    String loc;
    String detail;

    @Override
    public String toJson() {
        return gson.toJson(this);
    }
}
