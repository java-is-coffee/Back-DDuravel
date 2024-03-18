package javaiscoffee.polaroad.login.oauth.kakao;

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
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class KakaoService {

    public String getAccessTokenFromKakao(String ApiKey, String code) {
        String requestURL = "https://kauth.kakao.com/oauth/token?grant_type=authorization_code&client_id="+ApiKey+"&code=" + code;
        try {
            URL url = new URL(requestURL);
            //크램폴린 프록시 설정
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("krmp-proxy.9rum.cc", 3128));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection(proxy);
//            로컬테스트용 설정
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }

            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> jsonMap = objectMapper.readValue(result, new TypeReference<Map<String, Object>>() {
            });

            log.info("카카오 Response Body = {}",result);

            String accessToken = (String) jsonMap.get("access_token");
            String refreshToken = (String) jsonMap.get("refresh_token");
            String scope = (String) jsonMap.get("scope");

            log.info("카카오 accessToken = {}",accessToken);
            log.info("카카오 refreshToken = {}",refreshToken);
            log.info("카카오 scope = {}",scope);

            return accessToken;

        } catch (IOException e) {
            log.error("카카오 로그인 accessToken 받기 실패");
            throw new BadRequestException(ResponseMessages.ERROR.getMessage());
        }
    }

    public HashMap<String, Object> getUerInfo(String accessToken) {
        // 클라이언트 요청 정보
        HashMap<String, Object> userInfo = new HashMap<String, Object>();
        try {
            //카카오 정보 요청
            String reqeustURL = "https://kapi.kakao.com/v2/user/me";
            URL url = new URL(reqeustURL);
            //배포용 프록시 설정
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("krmp-proxy.9rum.cc", 3128));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection(proxy);
//            로컬 테스트용
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Bearer " + accessToken);

            int responseCode = conn.getResponseCode();
            log.info("카카오로부터 responseCode = {}",responseCode);

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }

            log.info("카카오로부터 얻은 Body 정보 = {}",result);

            // jackson objectMapper 생성
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> jsonMap = objectMapper.readValue(result, new TypeReference<Map<String, Object>>() {
            });

            //사용자 정보 추출
            Map<String, Object> properties = (Map<String, Object>) jsonMap.get("properties");
            Map<String, Object> kakaoAccount = (Map<String, Object>) jsonMap.get("kakao_account");

            Long id = (Long) jsonMap.get("id");
            String nickname = properties.get("nickname").toString();
            String profileImage = properties.get("profile_image").toString();
            String email = kakaoAccount.get("email").toString();

            userInfo.put("id", id);
            userInfo.put("nickname", nickname);
            userInfo.put("profile", profileImage);
            userInfo.put("email", email);
            userInfo.put("socialLogin", SocialLogin.KAKAO);

            return userInfo;

        } catch (IOException e) {
            log.error("카카오 서버로부터 정보얻기 실패");
            throw new BadRequestException(ResponseMessages.ERROR.getMessage());
        }
    }
}
