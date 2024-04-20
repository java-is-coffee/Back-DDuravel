package javaiscoffee.polaroad.login.emailAuthentication;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Properties;

@Service
public class GmailService {
    @Autowired
    private Gmail gmail;

    @Autowired
    private GmailTokenService gmailTokenService; // 토큰 서비스 추가

    public void sendEmail(String to, String from, String subject, String bodyText) throws MessagingException, IOException {
        try {
            sendMessage(to, from, subject, bodyText);
        } catch (IOException e) {
            // 토큰 갱신 시도
            Credential updatedCredential = gmailTokenService.refreshCredentials("user");
            if (updatedCredential != null) {
                // 갱신된 Credential로 Gmail 클라이언트 재설정
                updateGmailClient(updatedCredential);
                // 이메일 재전송
                sendMessage(to, from, subject, bodyText);
            } else {
                throw new IOException("Failed to refresh token and resend email", e);
            }
        }
    }

    private void sendMessage(String to, String from, String subject, String bodyText) throws MessagingException, IOException {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);
        MimeMessage email = new MimeMessage(session);
        email.setFrom(new InternetAddress(from));
        email.addRecipient(jakarta.mail.Message.RecipientType.TO, new InternetAddress(to));
        email.setSubject(subject);

        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setContent(bodyText, "text/html; charset=utf-8");
        MimeMultipart multipart = new MimeMultipart();
        multipart.addBodyPart(mimeBodyPart);
        email.setContent(multipart);

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        email.writeTo(buffer);
        byte[] bytes = buffer.toByteArray();
        String encodedEmail = Base64.getUrlEncoder().encodeToString(bytes);

        Message message = new Message();
        message.setRaw(encodedEmail);
        gmail.users().messages().send("me", message).execute();
    }

    private void updateGmailClient(Credential credential) {
        gmail = new Gmail.Builder(gmail.getRequestFactory().getTransport(), gmail.getJsonFactory(), credential)
                .setApplicationName("polaroad")
                .build();
    }
}
