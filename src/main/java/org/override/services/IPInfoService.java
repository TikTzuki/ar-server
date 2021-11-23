package org.override.services;

import com.google.gson.Gson;
import lombok.extern.log4j.Log4j2;
import okhttp3.*;
import org.override.core.Server;
import org.override.core.models.HyperStatus;
import org.override.models.ExampleModel;
import org.override.core.models.HyperEntity;
import org.override.core.models.HyperException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@Service
@Log4j2
public class IPInfoService {
    private static final String URL = "http://ip-api.com/json";
    private static final String HELP = "\uF0A7 Tra thông tin server (chỉ dùng khi server hoạt động ở một mạng có NAT): client gửi\n" +
            "lệnh hello đến server. Server trả lại client public IP và private IP của server.\n" +
            "\uF0A7 Tra cứu IP: client gửi lệnh req x, với x là một địa chỉ IP public. Server trả lại client các\n" +
            "thông tin về IP x gồm: thành phố - quốc gia – châu lục mà IP đó thuộc về hoặc trả về\n" +
            "thông báo lỗi nếu IP không đúng format/IP private.";
    final Server server;
    final Gson gson = new Gson();

    public IPInfoService(Server server) {
        this.server = server;
    }

    public HyperEntity handleLookupIpInfo(Map<String, String> headers) {
        String clientMessage = headers.get("client_message");
        if (clientMessage == null) {
            return HyperEntity.badRequest(
                    new HyperException(HyperException.BAD_REQUEST, null, "field required in headers: client_message")
            );
        }
        String info = lookUpIpInfo(clientMessage);
        log.info(info);
        return HyperEntity.ok(new ExampleModel(info));
    }

    private String lookUpIpInfo(String message) {
        String ip = null;
        if (message.matches("^req .+"))
            ip = message.replaceAll("req ", "");
        else if (message.equalsIgnoreCase("hello"))
            return String.format("{\"publicAddress\" : \"%s\", \"privateAddress\" : \"%s\" }", server.getInetAddress(), server.getLocalSocketAddress());
        else
            return HELP;

        OkHttpClient client = new OkHttpClient.Builder().build();
        Request request = new Request.Builder()
                .url(URL + "/" + ip)
                .build();

        Call call = client.newCall(request);
        try {
            return call.execute().body().string();
        } catch (IOException e) {
            return e.getMessage();
        }
    }

}
