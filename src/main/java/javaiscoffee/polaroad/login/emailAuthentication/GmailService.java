package javaiscoffee.polaroad.login.emailAuthentication;

import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.services.gmail.Gmail;

import java.io.*;

import com.google.api.services.gmail.model.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.nio.charset.StandardCharsets;
import java.util.Properties;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMultipart;
import java.util.Base64;

import com.google.api.services.gmail.GmailScopes;
import org.hibernate.cfg.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import java.util.Collections;

@Service
public class GmailService {
    @Autowired
    private Gmail gmail;

    /**
     * 이메일을 생성하고 보내는 메서드
     *
     * @param to 수신자 이메일 주소
     * @param from 발신자 이메일 주소
     * @param subject 이메일 제목
     * @param bodyText 이메일 본문
     * @throws MessagingException 메시지 생성 오류
     * @throws IOException 메시지 전송 실패
     */
    public void sendEmail(String to, String from, String subject, String bodyText) throws MessagingException, IOException {
        // 메일 세션 설정
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        // MIME 메시지 생성
        MimeMessage email = new MimeMessage(session);
        email.setFrom(new InternetAddress(from));
        email.addRecipient(jakarta.mail.Message.RecipientType.TO, new InternetAddress(to));
        email.setSubject(subject);

        // 메일 본문 설정 (텍스트/HTML)
        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setContent(bodyText, "text/html; charset=utf-8");

        MimeMultipart multipart = new MimeMultipart();
        multipart.addBodyPart(mimeBodyPart);
        email.setContent(multipart);

        // 메시지를 Base64 인코딩 및 전송
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        email.writeTo(buffer);
        byte[] bytes = buffer.toByteArray();
        String encodedEmail = Base64.getUrlEncoder().encodeToString(bytes);

        Message message = new Message();
        message.setRaw(encodedEmail);

        // 메일 발송
        gmail.users().messages().send("me", message).execute();
    }
}
