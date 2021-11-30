package org.override.models;

import lombok.*;
import org.override.core.models.HyperBody;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
public class PagingModel<T> implements HyperBody {
    @NonNull
    List<T> items;
    Integer pageNum;
    Integer pageSize;
    Integer totalItem;

    @Override
    public String toJson() {
        return gson.toJson(this);
    }
}
