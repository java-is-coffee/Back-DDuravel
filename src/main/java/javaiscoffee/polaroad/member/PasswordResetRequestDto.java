package javaiscoffee.polaroad.member;

import io.swagger.v3.oas.annotations.media.Schema;
import javaiscoffee.polaroad.validator.EmailCheck;
import javaiscoffee.polaroad.validator.PasswordCheck;
import lombok.Data;

@Data
@Schema(description = "비밀번호 리셋 RequestDto")
public class PasswordResetRequestDto {
    @PasswordCheck
    @Schema(description = "리셋하려는 비밀번호", example = "a123123!")
    private String password;
}
