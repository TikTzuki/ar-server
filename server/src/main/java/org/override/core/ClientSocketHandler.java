package org.override.core;

import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

@Log4j2
public abstract class ClientSocketHandler implements Runnable {
    protected Socket clientSocket;
    protected ObjectOutputStream out;
    protected ObjectInputStream in;

    public abstract void handleRequest() throws IOException, ClassNotFoundException;

    @Override
    public void run() {
        log.info("Client connected: " + clientSocket.getInetAddress().getHostAddress());
        try {
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            in = new ObjectInputStream(clientSocket.getInputStream());
            this.handleRequest();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            closeClientSocket();
        }
    }

    public void run(Socket socket) {
        this.clientSocket = socket;
        this.run();
    }

    public void closeClientSocket() {
        try {
            in.close();
            out.close();
            clientSocket.close();
            log.info("Close client socket: " + clientSocket.getInetAddress().getHostAddress());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
