package javaiscoffee.polaroad.member;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "비밀번호 리셋 RequestDto")
public class PasswordResetRequestDto {
    @Schema(description = "리셋하려는 비밀번호", example = "a123123!")
    private String password;
}
