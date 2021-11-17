package org.override.core;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.override.core.configs.Appconfig;
import org.override.core.models.HyperBody;
import org.override.core.models.HyperEntity;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Map;

@Log4j2
@AllArgsConstructor
@NoArgsConstructor
public class SocketService {
    Appconfig configs = Appconfig.getInstance();
    private static final Gson gson = new Gson();

    public HyperEntity sendRequest(HyperEntity request) throws IOException {
        try {
            Socket socket = new Socket(configs.host, configs.port);
            socket.setSoTimeout(30_000);

            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            out.writeObject(gson.toJson(request));
            HyperEntity response = gson.fromJson((String) in.readObject(), HyperEntity.class);

            out.close();
            in.close();
            socket.close();
            return response;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public HyperEntity sendRequest(String route, Map<String, String> headers, HyperBody body) throws IOException {
        HyperEntity request = HyperEntity.request(route, headers, body);
        return sendRequest(request);
    }

    private static SocketService INSTANCE;

    public static SocketService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SocketService();
        }
        return INSTANCE;
    }
}
