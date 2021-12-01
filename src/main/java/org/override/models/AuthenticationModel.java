package org.override.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.override.core.models.HyperBody;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationModel implements HyperBody {
    public String email;
    public String password;

    @Override
    public String toJson() {
        return gson.toJson(this);
    }

    public static AuthenticationModel fromJson(String json) {
        return gson.fromJson(json, AuthenticationModel.class);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserCreate implements HyperBody {
        public String password;
        public String email;
        public String name;

        @Override
        public String toJson() {
            return gson.toJson(this);
        }
    }


}
