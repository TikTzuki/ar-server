package org.override.models;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
public class HyperException {
    @NonNull
    String code;
    String loc;
    String detail;
}
