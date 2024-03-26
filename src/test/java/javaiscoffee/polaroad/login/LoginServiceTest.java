package javaiscoffee.polaroad.login;

import jakarta.servlet.http.HttpServletResponse;
import javaiscoffee.polaroad.member.Member;
import javaiscoffee.polaroad.security.TokenDto;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class LoginServiceTest {

    @MockBean
    private LoginService loginService;

    @Test
    public void testRegister() {
        // Given
        RegisterDto registerDto = new RegisterDto();
        registerDto.setEmail("aaa@naver.com");
        registerDto.setName("박자바");
        registerDto.setNickname("자바커피");
        registerDto.setPassword("a123123!");
        Member expectedMember = new Member();
        expectedMember.setEmail("aaa@naver.com");
        expectedMember.setName("박자바");
        expectedMember.setNickname("자바커피");

        // When
        when(loginService.register(any(RegisterDto.class))).thenReturn(expectedMember);
        Member registeredMember = loginService.register(registerDto);

        // Then
        assertThat(registeredMember).isNotNull();
        assertThat(registeredMember.getEmail()).isEqualTo(registerDto.getEmail());
        assertThat(registeredMember.getName()).isEqualTo(registerDto.getName());
        assertThat(registeredMember.getNickname()).isEqualTo(registerDto.getNickname());
    }

    @Test
    public void testLogin() {
        // Given
        LoginDto loginDto = new LoginDto();
        loginDto.setEmail("aaa@naver.com");
        loginDto.setPassword("a123123!");
        MockHttpServletResponse response = new MockHttpServletResponse();
        TokenDto expectedToken = new TokenDto("Bearer", "accessToken", "refreshToken");

        // When
        when(loginService.login(any(LoginDto.class), any(HttpServletResponse.class))).thenReturn(expectedToken);
        TokenDto actualToken = loginService.login(loginDto, response);

        // Then
        assertThat(actualToken).isNotNull();
        assertThat(actualToken.getAccessToken()).isEqualTo(expectedToken.getAccessToken());
        assertThat(actualToken.getRefreshToken()).isEqualTo(expectedToken.getRefreshToken());
    }
}
