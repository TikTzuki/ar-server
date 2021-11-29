import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.override.models.AuthenticationModel;
import org.override.models.ExampleModel;
import org.override.core.models.HyperEntity;
import org.override.core.models.HyperRoute;
import org.override.utils.SecurityUtil;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@AllArgsConstructor
@Log4j2
@NoArgsConstructor
public class Client {
    static String keyString = "CC5i89cNrJdFlyBp2Uln5Q\u003d\u003d";
    private Socket socket = null;
    ObjectOutputStream out = null;
    ObjectInputStream in = null;
    BufferedReader stdIn = null;
    List<String> routes = List.of(
            HyperRoute.LOGIN,
            HyperRoute.GET_EXAMPLE_SGU_ACADEMIC_RESULT
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
            while (true) {
                try {
                    log.info("SELECT ONE:");
//                GET ROUTE
                    for (int i = 0; i < routes.size(); i++) {
                        System.out.format("%s: %s \n", i, routes.get(i));
                    }

                    String routeOpt = stdIn.readLine();
                    if (routeOpt.equalsIgnoreCase("bye"))
                        break;
                    String route = routes.get(Integer.parseInt(routeOpt));
//               GET MESSAGE
                    log.info("YOUR MESSAGE");
                    line = stdIn.readLine();
//                HEADERS
                    String finalLine = line;
                    Map<String, String> headers = new HashMap<>() {{
                        put("mssv", finalLine);
                        put("client_message", finalLine);
                    }};
//               REQUEST
                    HyperEntity request;
                    if (HyperRoute.LOGIN.equals(route))
                        request = new HyperEntity(
                                route,
                                gson.toJson(new AuthenticationModel("string", "string")),
                                headers, null
                        );
                    else {
                        String ivString = SecurityUtil.generateIv();
                        headers.put("Authorization", "4:%s".formatted(ivString));
                        request = new HyperEntity(
                                route,
                                SecurityUtil.encrypt(
                                        new ExampleModel("example model").toJson(),
                                        keyString,
                                        ivString
                                ),
                                headers, null
                        );
                    }
                    String requestJson = gson.toJson(request);
                    out.writeObject(requestJson);
                    String rawResponse = (String) in.readObject();
                    JsonObject response = gson.fromJson(rawResponse, JsonObject.class);
                    System.out.format("Server response: \n%s\n\n", response.get("body").getAsString());
                } catch (NumberFormatException e) {
                    log.error("You must enter a number");
                } catch (ArrayIndexOutOfBoundsException ignore) {
                } catch (InvalidAlgorithmParameterException | NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException | BadPaddingException | InvalidKeyException e) {
                    e.printStackTrace();
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

    public void sendRequest(HyperEntity data) throws ClassNotFoundException {
    }

    public static void main(String[] args) throws Exception {
        new Client("localhost", 8000);
    }
}