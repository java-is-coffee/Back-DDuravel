package javaiscoffee.polaroad.login;

import io.swagger.v3.oas.annotations.media.Schema;
import javaiscoffee.polaroad.validator.EmailCheck;
import lombok.Data;

@Data
@Schema(description = "비밀번호 재설정할 때 정보를 받는 requestDto")
public class ResetPasswordRequestDto {
    @Schema(description = "사용자 이름", example = "박자바")
    private String name;
    @EmailCheck
    @Schema(description = "사용자 이메일", example = "aaa@naver.com")
    private String email;
}
