package javaiscoffee.polaroad.login;

import io.swagger.v3.oas.annotations.media.Schema;
import javaiscoffee.polaroad.validator.EmailCheck;
import javaiscoffee.polaroad.validator.NicknameCheck;
import javaiscoffee.polaroad.validator.PasswordCheck;
import lombok.Data;

@Data
public class RegisterDto {
    @EmailCheck
    @Schema(description = "## 사용자 이메일", example = "aaa@naver.com")
    private String email;
//    private String certificationNumber;
    @Schema(description = "사용자 이름", example = "박자바")
    private String name;
    @NicknameCheck
    @Schema(description = "사용자 닉네임", example = "자바커피")
    private String nickname;
    @PasswordCheck
    @Schema(description = "## 사용자 비밀번호", example = "Abcdefgh1!")
    private String password;
}