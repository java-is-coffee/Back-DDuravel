package javaiscoffee.polaroad.config;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import javaiscoffee.polaroad.login.emailAuthentication.CustomTransportWithProxy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Configuration
public class GmailConfig {
    @Value("${GOOGLE_APPLICATION_JSON}")
    private String googleApplicationJson;
    private static final String APPLICATION_NAME = "polaroad";

    @Bean
    public Gmail getGmailService() throws GeneralSecurityException, IOException {
        HttpTransport httpTransport = CustomTransportWithProxy.createCustomTransportWithProxy();
        JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

        try (InputStream is = new ByteArrayInputStream(googleApplicationJson.getBytes(StandardCharsets.UTF_8))) {
            GoogleCredential credential = GoogleCredential.fromStream(is)
                    .createScoped(Collections.singleton(GmailScopes.GMAIL_SEND));

            return new Gmail.Builder(httpTransport, jsonFactory, credential)
                    .setApplicationName(APPLICATION_NAME)
                    .build();
        }
    }
}
