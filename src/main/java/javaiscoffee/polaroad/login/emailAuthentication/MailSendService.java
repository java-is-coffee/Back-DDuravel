package javaiscoffee.polaroad.login.emailAuthentication;

import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.MimeMessage;
import javaiscoffee.polaroad.exception.BadRequestException;
import javaiscoffee.polaroad.member.MemberRepository;
import javaiscoffee.polaroad.redis.RedisService;
import javaiscoffee.polaroad.response.ResponseMessages;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;

/**
 * todo: aws에 배포해서 메일인증 활성화하기
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class MailSendService {
    private final JavaMailSender mailSender;
    private final CertificationGenerator generator;
    private final RedisService redisService;
    private final MemberRepository memberRepository;
    private static final String MAIL_TITLE_CERTIFICATION = "PolaRoad 인증 번호 발송 메일입니다.";
    @Value("${spring.mail.username}")
    private String MAIL_USERNAME;
    @Value("${spring.mail.password}")
    private String MAIL_PASSWORD;
    private final String AWS_URL = "https://polaroad.shop";

    public void sendEmailForCertification(String email) throws NoSuchAlgorithmException, MessagingException, IOException {

        //이메일 인증 요청이 30초 미만으로 존재하는 경우 예외 처리
        if(redisService.checkVerificationTime(email, 30, 30)) {
            throw new BadRequestException(ResponseMessages.BAD_REQUEST.getMessage());
        }

        if(memberRepository.existsByEmail(email)) throw new BadRequestException(ResponseMessages.REGISTER_DUPLICATED.getMessage());

        // 이메일 인증을 위한 랜덤 인증 번호 생성 => 사용자가 인증 링크를 클릭할 때 확인하는 용도로 사용
        String certificationNumber = generator.createCertificationNumber(10000000,99999999);

        log.info("이메일 = {}, 인증번호 = {}",email,certificationNumber);

        String requestURL = AWS_URL+"/api/email/certification?email="+email+"&certificationNumber=" + certificationNumber;

        // 레디스에 인증번호 저장
        redisService.saveEmailVerificationCode(email,certificationNumber,30);

        log.info("이메일 인증번호 저장 완료");

        // 사용자에게 위에서 생성한 이메일 내용 전송
        sendMail(email, requestURL);
    }
    //키 값 오류로 막히면 이메일 안 보내게 수정할 것

    /**
     * 이메일을 보내는 메서드 구현
     * JavaMailSender를 사용하여 MimeMessage 객체 생성 => 이메일을 나타내는 객체로, 이메일의 헤더, 본문, 첨부 파일 등을 포함할 수 있다.
     */
    public void sendMail(String email, String requestURL) throws MessagingException, IOException {
        long startTime = System.currentTimeMillis();
        log.info("api 테스트시작");
        URL url2 = new URL("https://polaroad.shop/api/test");
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("krmp-proxy.9rum.cc", 3128));
        HttpURLConnection conn2 = (HttpURLConnection) url2.openConnection(proxy);
        log.info("api 테스트 프록시 준비 완료");
        conn2.setRequestMethod("GET");
        conn2.setRequestProperty("accept", "application/json;charset=UTF-8");
        conn2.setDoOutput(true); // Request body를 보낼 수 있게 설정
        log.info("api 테스트 준비 완료");
        try (DataOutputStream wr = new DataOutputStream(conn2.getOutputStream())) {
            wr.writeBytes("");
            wr.flush();
        }
        log.info("api 테스트 끝");
        try {
            URL url = new URL(requestURL);
            log.info("메일 전송 요청 시작: {} (URL: {})", email, requestURL);
            
            Proxy proxy2 = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("krmp-proxy.9rum.cc", 3128));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection(proxy2);

            // 크램폴린 배포용 프록시 설정 제거 및 로컬 테스트용 설정 활성화
            // HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");


            log.info("요청 데이터 전송 완료: {}", email);

            int responseCode = conn.getResponseCode();
            long responseTime = System.currentTimeMillis();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                log.info("메일 전송 완료: {} (응답 시간: {}ms)", email, (responseTime - startTime));
            } else {
                log.error("메일 전송 실패: {} (HTTP 응답 코드: {}, 응답 시간: {}ms)", email, responseCode, (responseTime - startTime));
                throw new BadRequestException("메일 전송 실패: HTTP 응답 코드: " + responseCode);
            }
        } catch (Exception e) {
            log.error("메일 전송 중 오류 발생: {}", email, e);
            throw new BadRequestException(ResponseMessages.BAD_REQUEST.getMessage());
        }
    }
}
