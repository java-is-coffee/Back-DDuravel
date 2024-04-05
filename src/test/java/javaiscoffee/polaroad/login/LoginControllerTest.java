package javaiscoffee.polaroad.login;

import com.fasterxml.jackson.databind.ObjectMapper;
import javaiscoffee.polaroad.security.RefreshTokenDto;
import javaiscoffee.polaroad.wrapper.RequestWrapperDto;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {"JWT_SECRET_KEY=3123755132fdfds4daas4551af789d59f36977df5093be12c2314515135ddasg1f5k12hdfhjk412bh531uiadfi14b14bwebs52"})
@AutoConfigureMockMvc
class LoginControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testRegister() throws Exception {
        RegisterDto registerDto1 = new RegisterDto();
        registerDto1.setEmail("aaa@naver.com");
        registerDto1.setName("박자바");
        registerDto1.setNickname("자바커피");
        registerDto1.setPassword("a123123!");

        RequestWrapperDto<RegisterDto> requestRegisterDto1 = new RequestWrapperDto<>();
        requestRegisterDto1.setData(registerDto1);

        MockHttpServletRequestBuilder registerBuilder1 = post("/api/member/register")
                .content(objectMapper.writeValueAsString(requestRegisterDto1))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(registerBuilder1)
                .andExpect(status().isOk())
                .andReturn();

        mockMvc.perform(registerBuilder1)
                .andExpect(status().isBadRequest())
                .andReturn();

        RegisterDto registerDto2 = new RegisterDto();
        registerDto2.setEmail("aaa@naver.com");
        registerDto2.setName("김자바");
        registerDto2.setNickname("커피자바");
        registerDto2.setPassword("abc123!!");

        RequestWrapperDto<RegisterDto> requestRegisterDto2 = new RequestWrapperDto<>();
        requestRegisterDto2.setData(registerDto2);

        MockHttpServletRequestBuilder registerBuilder2 = post("/api/member/register")
                .content(objectMapper.writeValueAsString(requestRegisterDto2))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(registerBuilder2)
                .andExpect(status().isBadRequest())
                .andReturn();

        RegisterDto registerDto3 = new RegisterDto();
        registerDto3.setEmail("bbb@naver.com");
        registerDto3.setName("김자바");
        registerDto3.setNickname("커피자바");
        registerDto3.setPassword("abc");

        RequestWrapperDto<RegisterDto> requestRegisterDto3 = new RequestWrapperDto<>();
        requestRegisterDto3.setData(registerDto3);

        MockHttpServletRequestBuilder registerBuilder3 = post("/api/member/register")
                .content(objectMapper.writeValueAsString(requestRegisterDto3))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(registerBuilder3)
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    public void testLogin() throws Exception {
        RegisterDto registerDto = new RegisterDto();
        registerDto.setEmail("aaa@naver.com");
        registerDto.setName("박자바");
        registerDto.setNickname("자바커피");
        registerDto.setPassword("a123123!");

        RequestWrapperDto<RegisterDto> requestRegisterDto = new RequestWrapperDto<>();
        requestRegisterDto.setData(registerDto);

        MockHttpServletRequestBuilder registerBuilder = post("/api/member/register")
                .content(objectMapper.writeValueAsString(requestRegisterDto))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(registerBuilder)
                .andExpect(status().isOk())
                .andReturn();

        LoginDto loginDto1 = new LoginDto();
        loginDto1.setEmail("aaa@naver.com");
        loginDto1.setPassword("a123123!");

        RequestWrapperDto<LoginDto> requestLoginDto1 = new RequestWrapperDto<>();
        requestLoginDto1.setData(loginDto1);

        MockHttpServletRequestBuilder loginBuilder1 = post("/api/member/login")
                .content(objectMapper.writeValueAsString(requestLoginDto1))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(loginBuilder1)
                .andExpect(status().isOk())
                .andReturn();

        LoginDto loginDto2 = new LoginDto();
        loginDto2.setEmail("aaa@naver.com");
        loginDto2.setPassword("abc123!!");

        RequestWrapperDto<LoginDto> requestLoginDto2 = new RequestWrapperDto<>();
        requestLoginDto2.setData(loginDto2);

        MockHttpServletRequestBuilder loginBuilder2 = post("/api/member/login")
                .content(objectMapper.writeValueAsString(requestLoginDto2))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(loginBuilder2)
                .andExpect(status().isNotFound())
                .andReturn();

        LoginDto loginDto3 = new LoginDto();
        loginDto3.setEmail("bbb@naver.com");
        loginDto3.setPassword("a123123!");

        RequestWrapperDto<LoginDto> requestLoginDto3 = new RequestWrapperDto<>();
        requestLoginDto3.setData(loginDto3);

        MockHttpServletRequestBuilder loginBuilder3 = post("/api/member/login")
                .content(objectMapper.writeValueAsString(requestLoginDto3))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(loginBuilder3)
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    public void testRefreshAccessToken() throws Exception {
        RegisterDto registerDto = new RegisterDto();
        registerDto.setEmail("aaa@naver.com");
        registerDto.setName("박자바");
        registerDto.setNickname("자바커피");
        registerDto.setPassword("a123123!");

        RequestWrapperDto<RegisterDto> requestRegisterDto = new RequestWrapperDto<>();
        requestRegisterDto.setData(registerDto);

        MockHttpServletRequestBuilder registerBuilder = post("/api/member/register")
                .content(objectMapper.writeValueAsString(requestRegisterDto))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(registerBuilder)
                .andExpect(status().isOk())
                .andReturn();

        LoginDto loginDto = new LoginDto();
        loginDto.setEmail("aaa@naver.com");
        loginDto.setPassword("a123123!");

        RequestWrapperDto<LoginDto> requestLoginDto = new RequestWrapperDto<>();
        requestLoginDto.setData(loginDto);

        MockHttpServletRequestBuilder loginBuilder = post("/api/member/login")
                .content(objectMapper.writeValueAsString(requestLoginDto))
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mockMvc.perform(loginBuilder)
                .andExpect(status().isOk())
                .andReturn();

        String jsonString = mvcResult.getResponse().getContentAsString();
        JSONObject jsonObject = new JSONObject(jsonString);
        String refreshToken = jsonObject.getString("refreshToken");

        RefreshTokenDto.Data data1 = new RefreshTokenDto.Data();
        data1.setRefreshToken(refreshToken);

        RefreshTokenDto refreshTokenDto1 = new RefreshTokenDto();
        refreshTokenDto1.setData(data1);

        MockHttpServletRequestBuilder refreshTokenBuilder1 = post("/api/member/refresh")
                .content(objectMapper.writeValueAsString(refreshTokenDto1))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(refreshTokenBuilder1)
                .andExpect(status().isOk())
                .andReturn();

        RefreshTokenDto.Data data2 = new RefreshTokenDto.Data();
        data2.setRefreshToken("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhYWFAbmF2ZXIuY29tIiwiYXV0aCI6IlJPTEVfVVNFUiIsIm1lbWJlcklkIjoxLCJleHAiOjE3MTIzMTM2NDZ9.R7mMGi4nP6rUbUDjfuG55INgDNVsPfg0r3vle7alS5c");

        RefreshTokenDto refreshTokenDto2 = new RefreshTokenDto();
        refreshTokenDto2.setData(data2);

        MockHttpServletRequestBuilder refreshTokenBuilder2 = post("/api/member/refresh")
                .content(objectMapper.writeValueAsString(refreshTokenDto2))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(refreshTokenBuilder2)
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

    @Test
    public void testResetPassword() throws Exception {
        RegisterDto registerDto = new RegisterDto();
        registerDto.setEmail("aaa@naver.com");
        registerDto.setName("박자바");
        registerDto.setNickname("자바커피");
        registerDto.setPassword("a123123!");

        RequestWrapperDto<RegisterDto> requestRegisterDto = new RequestWrapperDto<>();
        requestRegisterDto.setData(registerDto);

        MockHttpServletRequestBuilder registerBuilder = post("/api/member/register")
                .content(objectMapper.writeValueAsString(requestRegisterDto))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(registerBuilder)
                .andExpect(status().isOk())
                .andReturn();

        LoginDto loginDto = new LoginDto();
        loginDto.setEmail("aaa@naver.com");
        loginDto.setPassword("a123123!");

        RequestWrapperDto<LoginDto> requestLoginDto = new RequestWrapperDto<>();
        requestLoginDto.setData(loginDto);

        MockHttpServletRequestBuilder loginBuilder = post("/api/member/login")
                .content(objectMapper.writeValueAsString(requestLoginDto))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(loginBuilder)
                .andExpect(status().isOk())
                .andReturn();

        ResetPasswordRequestDto resetPasswordDto1 = new ResetPasswordRequestDto();
        resetPasswordDto1.setName("박자바");
        resetPasswordDto1.setEmail("aaa@naver.com");

        RequestWrapperDto<ResetPasswordRequestDto> resetPasswordRequestDto1 = new RequestWrapperDto<>();
        resetPasswordRequestDto1.setData(resetPasswordDto1);

        MockHttpServletRequestBuilder resetPasswordBuilder1 = post("/api/member/login/reset-password")
                .content(objectMapper.writeValueAsString(resetPasswordRequestDto1))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(resetPasswordBuilder1)
                .andExpect(status().isOk())
                .andReturn();

        ResetPasswordRequestDto resetPasswordDto2 = new ResetPasswordRequestDto();
        resetPasswordDto2.setName("김자바");
        resetPasswordDto2.setEmail("aaa@naver.com");

        RequestWrapperDto<ResetPasswordRequestDto> resetPasswordRequestDto2 = new RequestWrapperDto<>();
        resetPasswordRequestDto2.setData(resetPasswordDto2);

        MockHttpServletRequestBuilder resetPasswordBuilder2 = post("/api/member/login/reset-password")
                .content(objectMapper.writeValueAsString(resetPasswordRequestDto2))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(resetPasswordBuilder2)
                .andExpect(status().isBadRequest())
                .andReturn();

        ResetPasswordRequestDto resetPasswordDto3 = new ResetPasswordRequestDto();
        resetPasswordDto3.setName("박자바");
        resetPasswordDto3.setEmail("bbb@naver.com");

        RequestWrapperDto<ResetPasswordRequestDto> resetPasswordRequestDto3 = new RequestWrapperDto<>();
        resetPasswordRequestDto3.setData(resetPasswordDto3);

        MockHttpServletRequestBuilder resetPasswordBuilder3 = post("/api/member/login/reset-password")
                .content(objectMapper.writeValueAsString(resetPasswordRequestDto3))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(resetPasswordBuilder3)
                .andExpect(status().isNotFound())
                .andReturn();
    }
}