package javaiscoffee.polaroad.login.emailAuthentication;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class OAuth2Controller {

    private final GoogleAuthorizationCodeFlow googleFlow;

    @Autowired
    public OAuth2Controller(GoogleAuthorizationCodeFlow googleFlow) {
        this.googleFlow = googleFlow;
    }

    @GetMapping("/oauth2callback")
    public ResponseEntity<String> handleGoogleCallback(@RequestParam("code") String code) {
        try {
            // 인증 코드를 사용하여 토큰 요청
            TokenResponse response = googleFlow.newTokenRequest(code)
                    .setRedirectUri("https://k218cb89f724ba.user-app.krampoline.com/oauth2callback")
                    .execute();

            // Credential 객체 생성 및 저장
            Credential credential = googleFlow.createAndStoreCredential(response, "user");

            // 토큰 정보와 함께 성공 메시지 반환
            return ResponseEntity.ok("Token saved: " + credential.getAccessToken());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Failed to retrieve access token: " + e.getMessage());
        }
    }
}
