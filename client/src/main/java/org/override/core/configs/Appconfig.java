package org.override.configs;

import lombok.Value;
import org.override.core.SocketService;

public class Appconfig {
    public Integer port = 8000;
    public String host = "localhost";

    public static Appconfig INSTANCE;

    public static Appconfig getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Appconfig();
        }
        return INSTANCE;
    }
}
