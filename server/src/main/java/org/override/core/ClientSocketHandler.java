package org.override.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public abstract class ClientSocketHandler implements Runnable {
    private static final Logger logger = LogManager.getLogger(ClientSocketHandler.class);
    protected Socket clientSocket;
    protected ObjectOutputStream out;
    protected ObjectInputStream in;

    public abstract void handleRequest() throws IOException, ClassNotFoundException;

    @Override
    public void run() {
        logger.info("Client connected: " + clientSocket.getInetAddress().getHostAddress());
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
            logger.info("Close client socket: " + clientSocket.getInetAddress().getHostAddress());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
