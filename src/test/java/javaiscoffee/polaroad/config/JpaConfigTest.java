package javaiscoffee.polaroad.config;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.HttpTransport;
import com.google.api.services.gmail.Gmail;
import jakarta.persistence.EntityManager;
import javaiscoffee.polaroad.login.emailAuthentication.GmailService;
import javaiscoffee.polaroad.member.JpaMemberRepository;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;


@TestConfiguration
public class JpaConfigTest {
    private final EntityManager em;
    public JpaConfigTest(EntityManager em) {this.em = em;}

    @Bean
    public JpaMemberRepository jpaMemberRepository(EntityManager em) {
        return new JpaMemberRepository(em);
    }

    @MockBean
    public Gmail gmail;

    @MockBean
    public GoogleClientSecrets clientSecrets;

    @MockBean
    public HttpTransport httpTransport;

    @MockBean
    public GoogleAuthorizationCodeFlow googleFlow;

    @MockBean
    public GmailService gmailService;

}