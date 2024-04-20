package javaiscoffee.polaroad.login.emailAuthentication;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.StoredCredential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.http.GenericUrl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class GmailTokenService {

    @Autowired
    private GoogleAuthorizationCodeFlow googleFlow;

    private static final String REDIRECT_URI = "https://k218cb89f724ba.user-app.krampoline.com/oauth2callback";

    public Credential refreshCredentials(String userId) throws IOException {
        Credential oldCredential = googleFlow.loadCredential(userId);
        if (oldCredential != null && (oldCredential.getExpiresInSeconds() == null || oldCredential.getExpiresInSeconds() <= 60)) {
            if (oldCredential.refreshToken()) {
                // StoredCredential 객체 생성
                StoredCredential storedCredential = new StoredCredential();
                storedCredential.setAccessToken(oldCredential.getAccessToken());
                storedCredential.setRefreshToken(oldCredential.getRefreshToken());
                storedCredential.setExpirationTimeMilliseconds(oldCredential.getExpirationTimeMilliseconds());

                // 데이터 스토어에 StoredCredential 저장
                googleFlow.getCredentialDataStore().set(userId, storedCredential);

                // Credential 객체 다시 생성
                Credential newCredential = new Credential.Builder(oldCredential.getMethod())
                        .setTransport(oldCredential.getTransport())
                        .setJsonFactory(oldCredential.getJsonFactory())
                        .setTokenServerUrl(new GenericUrl(oldCredential.getTokenServerEncodedUrl()))
                        .setClientAuthentication(oldCredential.getClientAuthentication())
                        .setRequestInitializer(oldCredential.getRequestInitializer())
                        .build();

                newCredential.setAccessToken(oldCredential.getAccessToken());
                newCredential.setRefreshToken(oldCredential.getRefreshToken());
                newCredential.setExpirationTimeMilliseconds(oldCredential.getExpirationTimeMilliseconds());

                return newCredential;
            }
        }
        return oldCredential;
    }

    public String refreshAccessToken(String refreshToken) throws IOException {
        // 이 메서드는 실제 구현에 따라 다르게 처리될 수 있습니다.
        TokenResponse response = new TokenResponse();
        response.setRefreshToken(refreshToken);

        return googleFlow.newTokenRequest(refreshToken)
                .setRedirectUri(REDIRECT_URI)
                .execute()
                .getAccessToken();
    }
}

