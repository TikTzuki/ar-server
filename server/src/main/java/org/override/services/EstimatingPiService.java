package org.override.services;

import lombok.extern.log4j.Log4j2;
import org.override.models.ExampleModel;
import org.override.models.HyperEntity;
import org.override.models.HyperException;
import org.override.utils.ErrorCodes;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.Map;

/**
 * Bài 3:
 *
 * <p>Viết chương trình tính số Pi theo phương pháp Monte Carlo, hoạt động theo mô hình client-
 * server, sử dụng TCP socket
 *  <ul>
 * <li> Client gửi số N đến server (N là số nguyên dương, có giá trị từ 1.000.000 trở lên).
 * <li> Server phát sinh N điểm ngẫu nhiên và tính Pi theo phương pháp Monte Carlo (tham
 * khảo https://www.geeksforgeeks.org/estimating-value-pi-using-monte-carlo/)
 * <li> Client nhận số Pi từ server và in ra màn hình kèm theo thời gian trễ (từ lúc client gửi N
 * cho đến lúc nhận Pi).
 */
@Log4j2
@Service
public class EstimatingPiService {

    public HyperEntity<Object> handleEstimatingPi(Map<String, String> headers, ExampleModel data) {
        Long number;
        try {
            number = Long.valueOf(headers.get("client_message"));
        } catch (NumberFormatException e) {
            return HyperEntity.badRequest(
                    new HyperException(ErrorCodes.BAD_REQUEST, null, "client_message must be digits")
            );
        }
        System.out.println(number);
        if (number == 0) {
            return HyperEntity.badRequest(
                    new HyperException(ErrorCodes.BAD_REQUEST, null, "field required in headers: client_message")
            );
        }
        String pi = estimatingPi(number);
        return HyperEntity.ok(pi);
    }

    public String estimatingPi(long interval) {
        int circlePoints = 0;
        int squarePoints = 0;
        double pi = 0;
        Timestamp start = new Timestamp(new Date().getTime());
        for (long i = 0L; i < interval * interval; i++) {
            int min = -1;
            int max = 1;
            double randX = (Math.random() * (max - min)) + min;
            double randY = (Math.random() * (max - min)) + min;

            double originDist = randX * randX + randY * randY;

            if (originDist <= 1)
                circlePoints++;

            squarePoints++;

            pi = (double) 4 * circlePoints / squarePoints;
        }
        Timestamp end = new Timestamp(new Date().getTime());
        long duration = end.getTime() - start.getTime();
        return String.format("{ \"duration (millisecond)\":\"%s\" , \"pi\":\"%s\"  }", duration, pi);
    }

}
