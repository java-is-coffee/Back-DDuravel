package javaiscoffee.polaroad.login.emailAuthentication;

import org.springframework.stereotype.Component;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

// 난수를 만드는 클래스
@Component
public class CertificationGenerator {

    // 인증 번호를 생성하는 메서드
    public String createCertificationNumber(int min, int max) throws NoSuchAlgorithmException {
        String result;

        int num = SecureRandom.getInstanceStrong().nextInt(max - min + 1) + min;
        result = String.valueOf(num);


        return result;
    }
}
