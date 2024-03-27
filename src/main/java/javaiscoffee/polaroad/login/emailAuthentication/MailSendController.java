package javaiscoffee.polaroad.login.emailAuthentication;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import javaiscoffee.polaroad.response.ResponseMessages;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/email")
public class MailSendController {
    private final MailSendService mailSendService;


    @Parameter(name = "certificationNumber", description = "설정할 임시 패스워드", required = true, example = "12345678")
    @PostMapping("/certification")
    public ResponseEntity<String> sendCertification(String email, @RequestParam("certificationNumber") String certificationNumber) {
        mailSendService.sendCertificationEmail(email, certificationNumber);
        return ResponseEntity.ok(ResponseMessages.SUCCESS.getMessage());
    }

    @Operation(description = "비밀번호 재설정 메일 전송")
    @Parameter(name = "tempPassword", description = "설정할 임시 패스워드", required = true, example = "a123123!")
    @PostMapping("/password-reset")
    public ResponseEntity<String> sendPasswordReset(String email, @RequestParam("tempPassword") String tempPassword) {
        mailSendService.sendPasswordResetEmail(email,tempPassword);
        return ResponseEntity.ok(ResponseMessages.SUCCESS.getMessage());
    }
}
