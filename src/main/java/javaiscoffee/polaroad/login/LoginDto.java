package javaiscoffee.polaroad.login;

import io.swagger.v3.oas.annotations.media.Schema;
import javaiscoffee.polaroad.validator.EmailCheck;
import javaiscoffee.polaroad.validator.PasswordCheck;
import lombok.Data;

@Data
@Schema(description = "로그인 입력값을 받는 requestDto")
public class LoginDto {
    @EmailCheck
    @Schema(description = "## 사용자 이메일", example = "aaa@naver.com")
    private String email;
    @PasswordCheck
    @Schema(description = "## 사용자 비밀번호", example = "AaBb1234%")
    private String password;
}