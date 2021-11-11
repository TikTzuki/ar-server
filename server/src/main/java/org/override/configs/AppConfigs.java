package org.override.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfigs {
    @Value("${server.port}")
    public Integer port;
    @Value("${server.socket.port}")
    public Integer socketPort;
}
