package javaiscoffee.polaroad.login;

import javaiscoffee.polaroad.validator.EmailCheck;
import javaiscoffee.polaroad.validator.PasswordCheck;
import lombok.Data;

@Data
public class LoginDto {
    @EmailCheck
    private String email;
    @PasswordCheck
    private String password;
}