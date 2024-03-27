package javaiscoffee.polaroad.login.oauth.google;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javaiscoffee.polaroad.exception.BadRequestException;
import javaiscoffee.polaroad.member.SocialLogin;
import javaiscoffee.polaroad.response.ResponseMessages;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class GoogleService {

    public String getAccessTokenFromGoogle(String clientId, String clientSecret, String redirectUri, String code) {
        String requestURL = "https://oauth2.googleapis.com/token";
        try {
            String parameters = "client_id=" + URLEncoder.encode(clientId, "UTF-8")
                            + "&client_secret=" + URLEncoder.encode(clientSecret, "UTF-8")
                            + "&redirect_uri=" + URLEncoder.encode(redirectUri, "UTF-8")
                            + "&grant_type=authorization_code"
                            + "&code=" + URLEncoder.encode(code, "UTF-8");

            URL url = new URL(requestURL);
//            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("krmp-proxy.9rum.cc", 3128));
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection(proxy);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true); // 요청 본문에 데이터를 넣기 위해 필요
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            // 요청 본문에 파라미터 쓰기
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = parameters.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String line;
            StringBuilder result = new StringBuilder();

            while ((line = br.readLine()) != null) {
                result.append(line);
            }

            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> jsonMap = objectMapper.readValue(result.toString(), new TypeReference<Map<String, Object>>() {});
            log.info("구글 Response Body = {}", result);

            String accessToken = (String) jsonMap.get("access_token");

            log.info("구글 accessToken = {}", accessToken);

            return accessToken;

        } catch (IOException e) {
            log.error("구글 로그인 accessToken 받기 실패", e);
            throw new BadRequestException(ResponseMessages.ERROR.getMessage());
        }
    }

    public HashMap<String, Object> getUerInfo(String accessToken) {
        HashMap<String, Object> userInfo = new HashMap<>();
        try {
            String requestURL = "https://www.googleapis.com/oauth2/v2/userinfo";
            URL url = new URL(requestURL);
//            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("krmp-proxy.9rum.cc", 3128));
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection(proxy);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Bearer " + accessToken);

            int responseCode = conn.getResponseCode();
            log.info("구글로부터 responseCode = {}", responseCode);

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String line;
            StringBuilder result = new StringBuilder();

            while ((line = br.readLine()) != null) {
                result.append(line);
            }

            log.info("구글로부터 얻은 Body 정보 = {}", result);

            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> jsonMap = objectMapper.readValue(result.toString(), new TypeReference<Map<String, Object>>() {
            });

            String id = (String) jsonMap.get("id");
            String name = (String) jsonMap.get("name");
            String picture = (String) jsonMap.get("picture");
            String email = (String) jsonMap.get("email");

            userInfo.put("id", id);
            userInfo.put("name", name);
            userInfo.put("picture", picture);
            userInfo.put("email", email);
            userInfo.put("socialLogin", SocialLogin.GOOGLE);

            return userInfo;

        } catch (IOException e) {
            log.error("구글 서버로부터 정보얻기 실패");
            throw new BadRequestException(ResponseMessages.ERROR.getMessage());
        }
    }
}
