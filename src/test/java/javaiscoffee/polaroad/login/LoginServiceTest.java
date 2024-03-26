package javaiscoffee.polaroad.login;

import jakarta.servlet.http.HttpServletResponse;
import javaiscoffee.polaroad.member.Member;
import javaiscoffee.polaroad.security.TokenDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class LoginServiceTest {
    @Mock private LoginService loginService;

    @Test
    public void testRegister() {
        RegisterDto registerDto = new RegisterDto();
        registerDto.setEmail("aaa@naver.com");
        registerDto.setName("박자바");
        registerDto.setNickname("자바커피");
        registerDto.setPassword("a123123!");

        // 모의 객체가 호출될 때의 행동을 정의합니다.
        when(loginService.register(any(RegisterDto.class))).thenReturn(new Member());

        // When
        Member registeredMember = loginService.register(registerDto);

        // then
        assertThat(registeredMember).isNotNull();
    }

    @Test
    public void testLogin() {
        LoginDto loginDto = new LoginDto();
        loginDto.setEmail("aaa@naver.com");
        loginDto.setPassword("a123123!");

        MockHttpServletResponse response = new MockHttpServletResponse();

        when(loginService.login(any(LoginDto.class),any(HttpServletResponse.class)))
                .thenReturn(new TokenDto("Bearer","accessToken","refreshToken"));

        //when
        TokenDto tokenDto = loginService.login(loginDto, response);

        //then
        assertThat(tokenDto).isNotNull();
    }
}