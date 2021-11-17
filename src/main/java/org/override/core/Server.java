package org.override.core;

import lombok.extern.log4j.Log4j2;
import org.override.core.configs.AppConfigs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.ServerSocket;

@Component
@Log4j2
public class Server extends ServerSocket {

    @Autowired
    ClientSocketHandler clientSocketHandler;

    @Autowired
    public Server(AppConfigs configs) throws IOException {
        super(configs.socketPort);
    }

    public void launch() {
        log.info("Server started");
        while (true) {
            log.info("Server listening");
            try {
                clientSocketHandler.run(this.accept());
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }

}
