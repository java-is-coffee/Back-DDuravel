package javaiscoffee.polaroad.login;

import javaiscoffee.polaroad.validator.EmailCheck;
import lombok.Data;

@Data
public class ResetPasswordRequestDto {
    private String name;
    @EmailCheck
    private String email;
}
