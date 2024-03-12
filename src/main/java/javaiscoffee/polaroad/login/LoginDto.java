package javaiscoffee.polaroad.login;

import io.swagger.v3.oas.annotations.media.Schema;
import javaiscoffee.polaroad.validator.EmailCheck;
import javaiscoffee.polaroad.validator.PasswordCheck;
import lombok.Data;

@Data
@Schema(description = "로그인 입력값을 받는 requestDto")
public class LoginDto {
    @EmailCheck
    @Schema(description = "사용자 이메일이자 아이디로 사용", example = "aaa@naver.com")
    private String email;
    @PasswordCheck
    @Schema(description = "사용자 비밀번호" +
            "\n- 8~20자 사이" +
            "\n- 소문자, 숫자, 특수문자 최소 1글자씩 있어야함", example = "Abcdefgh1!")
    private String password;
}