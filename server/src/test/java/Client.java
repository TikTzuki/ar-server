import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.override.models.ExampleModel;
import org.override.models.HyperEntity;
import org.override.models.HyperRoute;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Log4j2
@NoArgsConstructor
public class Client {
    private static Client INSTANCE;

    public static Client getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Client();
        }
        return INSTANCE;
    }

    private Socket socket = null;
    ObjectOutputStream out = null;
    ObjectInputStream in = null;
    BufferedReader stdIn = null;
    List<String> routes = List.of(
            HyperRoute.GET_EXAMPLE_ESTIMATING_PI,
            HyperRoute.GET_EXAMPLE_DICTIONARY,
            HyperRoute.GET_EXAMPLE_LOOK_IP_INFO,
            HyperRoute.GET_EXAMPLE_PERSONAL_INFO
    );

    public Client(String address, int port) throws ClassNotFoundException {
        try {
            socket = new Socket(address, port);
            log.info("Connected");
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            stdIn = new BufferedReader(new InputStreamReader(System.in));
            String line = "";
            Gson gson = new Gson();
            while (!line.equalsIgnoreCase("bye")) {
                try {
                    log.info("SELECT ONE:");
//                GET ROUTE
                    for (int i = 0; i < routes.size(); i++) {
                        System.out.format("%s: %s \n", i, routes.get(i));
                    }
                    String route = routes.get(Integer.parseInt(stdIn.readLine()));
//               GET MESSAGE
                    log.info("YOUR MESSAGE");
                    line = stdIn.readLine();
//                HEADERS
                    String finalLine = line;
                    Map<String, String> headers = new HashMap<>() {{
                        put("client_message", finalLine);
                    }};
//               REQUEST
                    HyperEntity<ExampleModel> request = new HyperEntity<>(
                            route, new ExampleModel("tiktzuki"), headers, null
                    );
                    String requestJson = gson.toJson(request);
                    out.writeObject(requestJson);
                    String rawResponse = (String) in.readObject();
                    JsonObject response = gson.fromJson(rawResponse, JsonObject.class);
                    System.out.println("Server response: " + response.get("body").getAsString());
                } catch (NumberFormatException e) {
                    log.error("You must enter a number");
                }
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

    public void sendRequest(HyperEntity<Object> data) throws ClassNotFoundException {
    }

    public static void main(String[] args) throws ClassNotFoundException {
        new Client("localhost", 8000);
    }
}