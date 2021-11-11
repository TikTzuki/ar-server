package org.override.core;

import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.override.Application;
import org.override.configs.AppConfigs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.ServerSocket;

@Component
@Log4j2
public class Server extends ServerSocket {
    private static final Logger logger = LogManager.getLogger(Server.class);

    @Autowired
    ClientSocketHandler clientSocketHandler;

    @Autowired
    public Server(AppConfigs configs) throws IOException {
        super(configs.socketPort);
    }

    public void launch() {
        logger.info("Server started");
        while (true) {
            logger.info("Server listening");
            try {
                clientSocketHandler.run(this.accept());
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }

}
