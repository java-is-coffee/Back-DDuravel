package javaiscoffee.polaroad.login.emailAuthentication;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import javaiscoffee.polaroad.exception.BadRequestException;
import javaiscoffee.polaroad.member.MemberRepository;
import javaiscoffee.polaroad.redis.RedisService;
import javaiscoffee.polaroad.response.ResponseMessages;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;


@Slf4j
@Service
@RequiredArgsConstructor
public class MailSendService {
    private final JavaMailSender mailSender;
    private final CertificationGenerator generator;
    private final RedisService redisService;
    private final MemberRepository memberRepository;
    private static final String MAIL_TITLE_CERTIFICATION = "PolaRoad 인증 번호 발송 메일입니다.";

    public void sendEmailForCertification(String email) throws NoSuchAlgorithmException, MessagingException {

        //이메일 인증 요청이 30초 미만으로 존재하는 경우 예외 처리
        if(redisService.checkVerificationTime(email, 30, 30)) {
            throw new BadRequestException(ResponseMessages.BAD_REQUEST.getMessage());
        }

        if(memberRepository.existsByEmail(email)) throw new BadRequestException(ResponseMessages.REGISTER_DUPLICATED.getMessage());

        // 이메일 인증을 위한 랜덤 인증 번호 생성 => 사용자가 인증 링크를 클릭할 때 확인하는 용도로 사용
        String certificationNumber = generator.createCertificationNumber(10000000,99999999);

        log.info("이메일 = {}, 인증번호 = {}",email,certificationNumber);

        // String.format() 사용해서 인증 번호를 포함한 본문 생성.
        String content = String.format("%s의 이메일 인증을 위해 발송된 메일입니다.%n인증 번호는   :   %s%n인증 번호를 입력칸에 입력해주세요.%n 인증 번호는 30분 후 만료됩니다.",email,certificationNumber);

        // 레디스에 인증번호 저장
        redisService.saveEmailVerificationCode(email,certificationNumber,30);

        log.info("이메일 인증번호 저장 완료");

        // 사용자에게 위에서 생성한 이메일 내용 전송
        sendMail(email, content);
    }
    //키 값 오류로 막히면 이메일 안 보내게 수정할 것

    /**
     * 이메일을 보내는 메서드 구현
     * JavaMailSender를 사용하여 MimeMessage 객체 생성 => 이메일을 나타내는 객체로, 이메일의 헤더, 본문, 첨부 파일 등을 포함할 수 있다.
     */
    public void sendMail(String email, String content) throws jakarta.mail.MessagingException {
        MimeMessage mimeMailMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMailMessage);
        helper.setTo(email);    // 이메일 수신자
        helper.setSubject(MAIL_TITLE_CERTIFICATION);    // 이메일 제목
        helper.setText(content);    // 이메일 본문 내용
        mailSender.send(mimeMailMessage);   // JavaMailSender를 이용하여 이메일 전송. send()를 호출해서 이메일을 전송하면, 이메일이 수신자에게 발송된다.
    }
}
