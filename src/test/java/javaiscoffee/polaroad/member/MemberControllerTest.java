package javaiscoffee.polaroad.member;

import com.fasterxml.jackson.databind.ObjectMapper;
import javaiscoffee.polaroad.login.LoginDto;
import javaiscoffee.polaroad.login.RegisterDto;
import javaiscoffee.polaroad.wrapper.RequestWrapperDto;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {"JWT_SECRET_KEY=3123755132fdfds4daas4551af789d59f36977df5093be12c2314515135ddasg1f5k12hdfhjk412bh531uiadfi14b14bwebs52"})
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class MemberControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MemberRepository memberRepository;

    @Test
    public void testGetMyProfile() throws Exception {
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
        String accessToken = jsonObject.getString("accessToken");

        MockHttpServletRequestBuilder customUserDetailBuilder1 = get("/api/member/my")
                .header("Authorization", "Bearer " + accessToken);

        mockMvc.perform(customUserDetailBuilder1)
                .andExpect(status().isOk())
                .andReturn();

        MockHttpServletRequestBuilder customUserDetailBuilder2 = get("/api/member/my")
                .header("Authorization", "Bearer " + "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhYWFAbmF2ZXIuY29tIiwiYXV0aCI6IlJPTEVfVVNFUiIsIm1lbWJlcklkIjoxLCJleHAiOjE3MTIzMTM2NDZ9.R7mMGi4nP6rUbUDjfuG55INgDNVsPfg0r3vle7alS5c");

        mockMvc.perform(customUserDetailBuilder2)
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

    @Test
    public void testEditMyProfile() throws Exception {
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
        String accessToken = jsonObject.getString("accessToken");

        Member member = memberRepository.findByEmail("aaa@naver.com").get();

        MemberInformationRequestDto memberInformationRequestDto1 = new MemberInformationRequestDto();
        memberInformationRequestDto1.setMemberId(member.getMemberId());
        memberInformationRequestDto1.setEmail(member.getEmail());
        memberInformationRequestDto1.setName("김자바");
        memberInformationRequestDto1.setNickname("커피자바");
        memberInformationRequestDto1.setProfileImage("https://lh3.googleusercontent.com/a/ACg8ocJupG7o_Y0DlY4Fg8uUmlElHoG2o7l4PF3UhBSSelYnAZ9YAw=s96-c");

        RequestWrapperDto<MemberInformationRequestDto> memberInformationRequestDtoWrapper1 = new RequestWrapperDto<>();
        memberInformationRequestDtoWrapper1.setData(memberInformationRequestDto1);

        MockHttpServletRequestBuilder customUserDetailBuilder1 = patch("/api/member/my/edit")
                .header("Authorization", "Bearer " + accessToken)
                .content(objectMapper.writeValueAsString(memberInformationRequestDtoWrapper1))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(customUserDetailBuilder1)
                .andExpect(status().isOk())
                .andReturn();

        MockHttpServletRequestBuilder customUserDetailBuilder2 = patch("/api/member/my/edit")
                .header("Authorization", "Bearer " + "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhYWFAbmF2ZXIuY29tIiwiYXV0aCI6IlJPTEVfVVNFUiIsIm1lbWJlcklkIjoxLCJleHAiOjE3MTIzMTM2NDZ9.R7mMGi4nP6rUbUDjfuG55INgDNVsPfg0r3vle7alS5c")
                .content(objectMapper.writeValueAsString(memberInformationRequestDtoWrapper1))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(customUserDetailBuilder2)
                .andExpect(status().isUnauthorized())
                .andReturn();

        MemberInformationRequestDto memberInformationRequestDto2 = new MemberInformationRequestDto();
        memberInformationRequestDto2.setMemberId(2L);
        memberInformationRequestDto2.setEmail("bbb@naver.com");
        memberInformationRequestDto2.setName("김자바");
        memberInformationRequestDto2.setNickname("커피자바");
        memberInformationRequestDto2.setProfileImage("https://lh3.googleusercontent.com/a/ACg8ocJupG7o_Y0DlY4Fg8uUmlElHoG2o7l4PF3UhBSSelYnAZ9YAw=s96-c");

        RequestWrapperDto<MemberInformationRequestDto> memberInformationRequestDtoWrapper2 = new RequestWrapperDto<>();
        memberInformationRequestDtoWrapper2.setData(memberInformationRequestDto2);

        MockHttpServletRequestBuilder customUserDetailBuilder3 = patch("/api/member/my/edit")
                .header("Authorization", "Bearer " + accessToken)
                .content(objectMapper.writeValueAsString(memberInformationRequestDtoWrapper2))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(customUserDetailBuilder3)
                .andExpect(status().isBadRequest())
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

        MvcResult mvcResult = mockMvc.perform(loginBuilder)
                .andExpect(status().isOk())
                .andReturn();

        String jsonString = mvcResult.getResponse().getContentAsString();
        JSONObject jsonObject = new JSONObject(jsonString);
        String accessToken = jsonObject.getString("accessToken");

        PasswordResetRequestDto passwordResetRequestDto1 = new PasswordResetRequestDto();
        passwordResetRequestDto1.setPassword("abc123!!");

        RequestWrapperDto<PasswordResetRequestDto> PasswordResetRequestDtoWrapper1 = new RequestWrapperDto<>();
        PasswordResetRequestDtoWrapper1.setData(passwordResetRequestDto1);

        MockHttpServletRequestBuilder customUserDetailBuilder1 = patch("/api/member/my/edit/reset-password")
                .header("Authorization", "Bearer " + accessToken)
                .content(objectMapper.writeValueAsString(PasswordResetRequestDtoWrapper1))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(customUserDetailBuilder1)
                .andExpect(status().isOk())
                .andReturn();

        MockHttpServletRequestBuilder customUserDetailBuilder2 = patch("/api/member/my/edit/reset-password")
                .header("Authorization", "Bearer " + "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhYWFAbmF2ZXIuY29tIiwiYXV0aCI6IlJPTEVfVVNFUiIsIm1lbWJlcklkIjoxLCJleHAiOjE3MTIzMTM2NDZ9.R7mMGi4nP6rUbUDjfuG55INgDNVsPfg0r3vle7alS5c")
                .content(objectMapper.writeValueAsString(PasswordResetRequestDtoWrapper1))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(customUserDetailBuilder2)
                .andExpect(status().isUnauthorized())
                .andReturn();

        PasswordResetRequestDto passwordResetRequestDto2 = new PasswordResetRequestDto();
        passwordResetRequestDto2.setPassword("abc");

        RequestWrapperDto<PasswordResetRequestDto> PasswordResetRequestDtoWrapper2 = new RequestWrapperDto<>();
        PasswordResetRequestDtoWrapper2.setData(passwordResetRequestDto2);

        MockHttpServletRequestBuilder customUserDetailBuilder3 = patch("/api/member/my/edit/reset-password")
                .header("Authorization", "Bearer " + accessToken)
                .content(objectMapper.writeValueAsString(PasswordResetRequestDtoWrapper2))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(customUserDetailBuilder3)
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    public void testClickFollow() throws Exception {
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
        String accessToken = jsonObject.getString("accessToken");

        RegisterDto registerDto2 = new RegisterDto();
        registerDto2.setEmail("bbb@naver.com");
        registerDto2.setName("김자바");
        registerDto2.setNickname("커피자바");
        registerDto2.setPassword("abc123!!");

        RequestWrapperDto<RegisterDto> requestRegisterDto2 = new RequestWrapperDto<>();
        requestRegisterDto2.setData(registerDto2);

        MockHttpServletRequestBuilder registerBuilder2 = post("/api/member/register")
                .content(objectMapper.writeValueAsString(requestRegisterDto2))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(registerBuilder2)
                .andExpect(status().isOk())
                .andReturn();

        Member member = memberRepository.findByEmail("bbb@naver.com").get();

        Long followedMemberId1 = member.getMemberId();

        MockHttpServletRequestBuilder customUserDetailBuilder1 = post("/api/member/my/follow/{followedMemberId}", followedMemberId1)
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(customUserDetailBuilder1)
                .andExpect(status().isOk())
                .andReturn();

        MockHttpServletRequestBuilder customUserDetailBuilder2 = post("/api/member/my/follow/{followedMemberId}", followedMemberId1)
                .header("Authorization", "Bearer " + "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhYWFAbmF2ZXIuY29tIiwiYXV0aCI6IlJPTEVfVVNFUiIsIm1lbWJlcklkIjoxLCJleHAiOjE3MTIzMTM2NDZ9.R7mMGi4nP6rUbUDjfuG55INgDNVsPfg0r3vle7alS5c")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(customUserDetailBuilder2)
                .andExpect(status().isUnauthorized())
                .andReturn();

        Long followedMemberId2 = member.getMemberId() + 1;

        MockHttpServletRequestBuilder customUserDetailBuilder3 = post("/api/member/my/follow/{followedMemberId}", followedMemberId2)
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(customUserDetailBuilder3)
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    public void testDeleteAccount() throws Exception {
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
        String accessToken = jsonObject.getString("accessToken");

        MockHttpServletRequestBuilder customUserDetailBuilder1 = delete("/api/member/my/delete-account")
                .header("Authorization", "Bearer " + accessToken);

        mockMvc.perform(customUserDetailBuilder1)
                .andExpect(status().isOk())
                .andReturn();

        MockHttpServletRequestBuilder customUserDetailBuilder2 = delete("/api/member/my/delete-account")
                .header("Authorization", "Bearer " + "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhYWFAbmF2ZXIuY29tIiwiYXV0aCI6IlJPTEVfVVNFUiIsIm1lbWJlcklkIjoxLCJleHAiOjE3MTIzMTM2NDZ9.R7mMGi4nP6rUbUDjfuG55INgDNVsPfg0r3vle7alS5c");

        mockMvc.perform(customUserDetailBuilder2)
                .andExpect(status().isUnauthorized())
                .andReturn();
    }
}