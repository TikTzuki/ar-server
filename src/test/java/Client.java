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
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@AllArgsConstructor
@Log4j2
@NoArgsConstructor
public class Client {
    static String keyString = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCHxPdpxAtQxapqTFHorahKCkvNT9Y59gmISh+3SfphMkOkoo7Y53i8vtEG5LAiwI15Y7mDgeFnYlBpTamXc3oZ9nR4xSQ8kTf1x2bHfMi5pdITvy8SWxIya9axFEzNi26AZkyC0WyCjm/+8z5MGqZSimrAliwQrnaNbSNcaYxZLQIDAQAB";
    static String email = "string";

    private Socket socket = null;
    ObjectOutputStream out = null;
    ObjectInputStream in = null;
    BufferedReader stdIn = null;
    List<String> routes = List.of(
            HyperRoute.LOGIN,
            HyperRoute.GET_EXAMPLE_SGU_ACADEMIC_RESULT,
            HyperRoute.GET_LEARNING_PROCESS,
            HyperRoute.GET_TERM_RESULT
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
                        put("studentId", finalLine);
                    }};
//               REQUEST
                    HyperEntity request;
                    String ivString = SecurityUtil.generateIv();
                    if (HyperRoute.LOGIN.equals(route))
                        request = new HyperEntity(
                                route,
                                gson.toJson(new AuthenticationModel("string", "string")),
                                headers, null
                        );
                    else {
                        headers.put("Authorization", "4:%s".formatted(ivString));
                        request = new HyperEntity(
                                route,
                                SecurityUtil.encrypt(
                                        new ExampleModel("example model").toJson(),
                                        SecurityUtil.generateKey(keyString, email),
                                        ivString
                                ),
                                headers, null
                        );
                    }
                    String requestJson = gson.toJson(request);
                    out.writeObject(requestJson);
                    String rawResponse = (String) in.readObject();
                    HyperEntity response = gson.fromJson(rawResponse, HyperEntity.class);
                    String body = SecurityUtil.decrypt(
                            response.body,
                            SecurityUtil.generateKey(keyString, email),
                            ivString
                    );
                    System.out.format("Server response: \n%s\n\n", body);
                } catch (Exception e) {
                    log.error(e.getMessage());
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

    public static void main(String[] args) throws Exception {
        new Client("localhost", 8000);
    }
}