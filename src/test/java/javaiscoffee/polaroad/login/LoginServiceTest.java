package javaiscoffee.polaroad.login;

import javaiscoffee.polaroad.exception.BadRequestException;
import javaiscoffee.polaroad.exception.NotFoundException;
import javaiscoffee.polaroad.member.Member;
import javaiscoffee.polaroad.security.TokenDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {"JWT_SECRET_KEY=3123755132fdfds4daas4551af789d59f36977df5093be12c2314515135ddasg1f5k12hdfhjk412bh531uiadfi14b14bwebs52"})
@Transactional
class LoginServiceTest {
    @Autowired
    private LoginService loginService;

    @Test
    public void testRegister() {
        RegisterDto registerDto1 = new RegisterDto();
        registerDto1.setEmail("aaa@naver.com");
        registerDto1.setName("박자바");
        registerDto1.setNickname("자바커피");
        registerDto1.setPassword("a123123!");

        Member member1 = loginService.register(registerDto1);

        assertThat(member1.getEmail()).isEqualTo("aaa@naver.com");
        assertThat(member1).isNotNull();

        Member member2 = loginService.register(registerDto1);
        assertThat(member2).isNull();

        RegisterDto registerDto2 = new RegisterDto();
        registerDto2.setEmail("aaa@naver.com");
        registerDto2.setName("김자바");
        registerDto2.setNickname("커피자바");
        registerDto2.setPassword("abc123!!");

        Member member3 = loginService.register(registerDto2);
        assertThat(member3).isNull();
    }

    @Test
    public void testLogin() {
        RegisterDto registerDto = new RegisterDto();
        registerDto.setEmail("aaa@naver.com");
        registerDto.setName("박자바");
        registerDto.setNickname("자바커피");
        registerDto.setPassword("a123123!");

        Member member = loginService.register(registerDto);

        LoginDto loginDto1 = new LoginDto();
        loginDto1.setEmail(member.getEmail());
        loginDto1.setPassword(registerDto.getPassword());

        TokenDto tokenDto1 = loginService.login(loginDto1);
        assertThat(tokenDto1).isNotNull();

        LoginDto loginDto2 = new LoginDto();
        loginDto2.setEmail("bbb@naver.com");
        loginDto2.setPassword("abc123");

        Assertions.assertThatThrownBy(() -> loginService.login(loginDto2)).isInstanceOf(NotFoundException.class);
    }
    
//    @Test
    public void testResetPassword() {
        RegisterDto registerDto = new RegisterDto();
        registerDto.setEmail("aaa@naver.com");
        registerDto.setName("박자바");
        registerDto.setNickname("자바커피");
        registerDto.setPassword("a123123!");

        Member member = loginService.register(registerDto);

        ResetPasswordRequestDto resetPasswordRequestDto1 = new ResetPasswordRequestDto();
        resetPasswordRequestDto1.setName(member.getName());
        resetPasswordRequestDto1.setEmail(member.getEmail());

        loginService.resetPassword(resetPasswordRequestDto1);

        assertThat(registerDto.getPassword()).isEqualTo("a123123!");
        assertThat(registerDto.getPassword()).isNotEqualTo(member.getPassword());

        ResetPasswordRequestDto resetPasswordRequestDto2 = new ResetPasswordRequestDto();
        resetPasswordRequestDto2.setName(member.getName());
        resetPasswordRequestDto2.setEmail("bbb@naver.com");

        Assertions.assertThatThrownBy(() -> loginService.resetPassword(resetPasswordRequestDto2)).isInstanceOf(NotFoundException.class);

        ResetPasswordRequestDto resetPasswordRequestDto3 = new ResetPasswordRequestDto();
        resetPasswordRequestDto3.setName("안녕");
        resetPasswordRequestDto3.setEmail(member.getEmail());
        
        Assertions.assertThatThrownBy(() -> loginService.resetPassword(resetPasswordRequestDto3)).isInstanceOf(BadRequestException.class);
    }
}