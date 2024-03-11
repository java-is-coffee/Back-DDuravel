package javaiscoffee.polaroad.login;

import javaiscoffee.polaroad.validator.EmailCheck;
import javaiscoffee.polaroad.validator.NicknameCheck;
import javaiscoffee.polaroad.validator.PasswordCheck;
import lombok.Data;

@Data
public class RegisterDto {
    @EmailCheck
    private String email;
//    private String certificationNumber;
    private String name;
    @NicknameCheck
    private String nickname;
    @PasswordCheck
    private String password;
}