package javaiscoffee.polaroad.login;

import jakarta.servlet.http.HttpServletResponse;
import javaiscoffee.polaroad.member.Member;
import javaiscoffee.polaroad.member.SocialLogin;
import javaiscoffee.polaroad.security.TokenDto;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(properties = {"JWT_SECRET_KEY=3123755132fdfds4daas4551af789d59f36977df5093be12c2314515135ddasg1f5k12hdfhjk412bh531uiadfi14b14bwebs52"})
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

    @Test
    public void testOauthGoogleLogin() {
        HashMap<String, Object> userInfo = new HashMap<>();

        userInfo.put("id", "823742296139163914196");
        userInfo.put("name", "박자바");
        userInfo.put("picture", "https://lh3.googleusercontent.com/a/ACg8ocIUDVrYxwJiLs3303WK329pqp2QXNnJE9UFEsAaPzz6=s96-c");
        userInfo.put("email", "aaa@gmail.com");
        userInfo.put("socialLogin", SocialLogin.GOOGLE);

        when(loginService.oauthGoogleLogin(any(HashMap.class)))
                .thenReturn(new TokenDto("Bearer","accessToken","refreshToken"));

        //when
        TokenDto tokenDto = loginService.oauthGoogleLogin(userInfo);

        //then
        assertThat(tokenDto).isNotNull();
    }

    @Test
    public void testRefresh() {
        String refreshToken = "";
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(loginService.refresh(any(String.class),any(HttpServletResponse.class)))
                .thenReturn(new TokenDto("Bearer","accessToken","refreshToken"));

        //when
        TokenDto tokenDto = loginService.refresh(refreshToken, response);

        //then
        assertThat(tokenDto).isNotNull();
    }
}