package com.albaExpress.api.alba.service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class TimeService {
    // 서울 시간 정보를 가져오는 API URL
    private static final String TIME_API_URL = "http://worldtimeapi.org/api/timezone/Asia/Seoul";

    public LocalDateTime getSeoulTime() throws IOException {
        URL url = new URL(TIME_API_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.connect();

        if (conn.getResponseCode() != 200) {
            throw new RuntimeException("Failed to connect to time API");
        }

        Scanner sc = new Scanner(url.openStream());
        String inline = "";
        while (sc.hasNext()) {
            inline += sc.nextLine();
        }
        sc.close();

        JSONObject data = new JSONObject(inline);
        String dateTimeStr = data.getString("datetime");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX");
        return LocalDateTime.parse(dateTimeStr, formatter);
    }
}
