import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.override.core.models.HyperEntity;
import org.override.core.models.HyperRoute;
import org.override.core.models.HyperStatus;
import org.override.models.AuthenticationModel;
import org.override.models.ExampleModel;
import org.override.utils.SecurityUtil;

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
    static String STUDENT_ID = "studentId";
    static String INCLUDE_COURSE = "course";
    static String INCLUDE_SUBJECT = "includeSubject";
    static String INCLUDE_SPECIALITY = "includeSpeciality";

    static String keyString = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC1d9DD+ZvVVNzBpZD915fR6wuX4H1fFcsG306OtpJy/r9cr7zaSb7Vh2gY88m1SzBi0tURRg/C6nY0O0cJEUc1MvXeNSDaAPLrJpthK5O8yImYap+3ipCHB6zwZcAjWQwba6JBQhVd0qffytmsTEvalVHsM/R9fn96URd6XCjbCQIDAQAB";
    static String email = "string";
    public String USER_ID = "1";

    private Socket socket = null;
    ObjectOutputStream out = null;
    ObjectInputStream in = null;
    BufferedReader stdIn = null;
    List<String> routes = List.of(
            HyperRoute.LOGIN,
            HyperRoute.GET_EXAMPLE_SGU_ACADEMIC_RESULT,
            HyperRoute.GET_LEARNING_PROCESS,
            HyperRoute.GET_TERM_RESULT,
            HyperRoute.GET_RANKING
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
                        put("client_message", finalLine);
                        put(STUDENT_ID, finalLine);
                        put(INCLUDE_SUBJECT, "TRUE");
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
                        headers.put("Authorization", "%s:%s".formatted(USER_ID, ivString));
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
                    String body = response.body;
                    if (!request.route.equals(HyperRoute.LOGIN) && response.status.equals(HyperStatus.OK))
                        body = SecurityUtil.decrypt(
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