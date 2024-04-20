package javaiscoffee.polaroad.config;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import javaiscoffee.polaroad.login.emailAuthentication.CustomTransportWithProxy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.Collections;

import static org.mockito.Mockito.mock;

@Configuration
public class GmailConfig {
    private static final String APPLICATION_NAME = "polaroad";
    private static final String TOKENS_DIRECTORY_PATH = "/workspace/tokens";
    @Value("${GOOGLE_APPLICATION_CREDENTIALS}")
    private String credentialsFilePath;
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    @Bean
    @Profile("local")
    public HttpTransport localHttpTransport() throws GeneralSecurityException, IOException {
        return GoogleNetHttpTransport.newTrustedTransport();
    }

    @Bean
    @Profile("prod")
    public HttpTransport prodHttpTransport() throws GeneralSecurityException, IOException {
        return CustomTransportWithProxy.createCustomTransportWithProxy();
    }

    @Bean
    @Profile("prod")
    public GoogleClientSecrets clientSecrets() throws IOException {
        InputStream in = new FileInputStream(credentialsFilePath);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + credentialsFilePath);
        }
        return GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
    }

    @Bean
    @Profile("prod")
    public GoogleAuthorizationCodeFlow googleFlow(HttpTransport httpTransport, GoogleClientSecrets clientSecrets) throws IOException {
        return new GoogleAuthorizationCodeFlow.Builder(
                httpTransport,
                JSON_FACTORY,
                clientSecrets,
                Collections.singletonList(GmailScopes.GMAIL_SEND))
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
    }

    // OAuth2 인증 URL을 생성하여 리디렉션
    public String buildAuthorizationUrl(GoogleAuthorizationCodeFlow flow) {
        GoogleAuthorizationCodeRequestUrl url = flow.newAuthorizationUrl();
        return url.setRedirectUri("https://k218cb89f724ba.user-app.krampoline.com/oauth2callback").build();
    }

    @Bean
    @Profile("prod")
    public Gmail gmail(HttpTransport httpTransport, GoogleAuthorizationCodeFlow flow) throws IOException {
        Credential credential = flow.loadCredential("user");
        if (credential == null) {
            String authorizationUrl = buildAuthorizationUrl(flow);
            // 로깅이나 리디렉션 처리
            System.out.println("Authorize this app by visiting this url: " + authorizationUrl);
        }
        return new Gmail.Builder(httpTransport, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    @Bean
    @Profile("local")  // 개발 환경에서는 Mock Gmail 객체 사용
    public Gmail mockGmail() {
        return mock(Gmail.class);
    }
    @Bean
    @Profile("local")  // 개발 환경에서는 Mock Gmail 객체 사용
    public GoogleAuthorizationCodeFlow localGoogleFlow() {
        return mock(GoogleAuthorizationCodeFlow.class);
    }
}
