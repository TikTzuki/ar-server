package org.override.services;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j2;
import org.override.models.DataEntity;
import org.override.models.Route;

import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.*;

@AllArgsConstructor
@Log4j2
@NoArgsConstructor
public class SocketService {
    private static SocketService INSTANCE;

    public static SocketService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SocketService();
        }
        return INSTANCE;
    }

    private Socket socket = null;
    ObjectOutputStream out = null;
    ObjectInputStream in = null;
    BufferedReader stdIn = null;

    public SocketService(String address, int port) throws ClassNotFoundException {
        try {
            socket = new Socket(address, port);
            socket.setSoTimeout(30_000);
            log.info("Connected");
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            stdIn = new BufferedReader(new InputStreamReader(System.in));
            String line = "";
            while (!line.equalsIgnoreCase("bye")) {
                line = stdIn.readLine();
                Map<String, Object> headers = new HashMap<>() {{
                    put("route", Route.GET_EXAMPLE_ESTIMATING_PI);
                }};
                DataEntity<Long> request = new DataEntity(Long.valueOf(line), headers, null);
                out.writeObject(request);

                DataEntity<Long> response = (DataEntity<Long>) in.readObject();
                System.out.println(response);
            }
            in.close();
            out.close();
            socket.close();
        } catch (UnknownHostException | SocketException e) {
            log.info("Can't connect to server");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendRequest(DataEntity<Object> data) throws ClassNotFoundException {
    }

    public static void main(String[] args) throws ClassNotFoundException {
        new SocketService("localhost", 8000);
    }
}
