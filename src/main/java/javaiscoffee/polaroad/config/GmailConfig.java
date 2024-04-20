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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Configuration
//@Profile("mail")
public class GmailConfig {
    private static final String APPLICATION_NAME = "polaroad";
    private static final String TOKENS_DIRECTORY_PATH = "/workspace/tokens"; // 토큰 저장 경로
    @Value("${GOOGLE_APPLICATION_CREDENTIALS}")
    private String credentialsFilePath; // 인증 정보 파일 경로
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    @Bean
    public HttpTransport httpTransport() throws GeneralSecurityException, IOException {
        return GoogleNetHttpTransport.newTrustedTransport();
    }

    @Bean
    public GoogleClientSecrets clientSecrets() throws IOException {
        InputStream in = new FileInputStream(credentialsFilePath);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + credentialsFilePath);
        }
        return GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
    }

    @Bean
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

    @Bean
    public Gmail gmailService(HttpTransport httpTransport, GoogleAuthorizationCodeFlow flow) throws IOException {
        Credential credential = flow.loadCredential("user");
        if (credential == null) {
            // Need to prompt for authorization (typically done via a web browser)
            String authorizationUrl = buildAuthorizationUrl(flow);
            // Log or display the authorization URL so the user can access it
            System.out.println("Authorize this app by visiting this url: " + authorizationUrl);
        }
        return new Gmail.Builder(httpTransport, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    private String buildAuthorizationUrl(GoogleAuthorizationCodeFlow flow) {
        GoogleAuthorizationCodeRequestUrl url = flow.newAuthorizationUrl();
        return url.setRedirectUri("https://k218cb89f724ba.user-app.krampoline.com/oauth2callback").build();
    }
}
