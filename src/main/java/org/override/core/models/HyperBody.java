package org.override.core.models;

import com.google.gson.Gson;

public interface HyperBody {
    Gson gson = new Gson();

    String toJson();
}
