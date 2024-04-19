package javaiscoffee.polaroad.album;

import com.fasterxml.jackson.databind.ObjectMapper;
import javaiscoffee.polaroad.login.LoginDto;
import javaiscoffee.polaroad.login.LoginService;
import javaiscoffee.polaroad.login.RegisterDto;
import javaiscoffee.polaroad.member.Member;
import javaiscoffee.polaroad.member.MemberRepository;
import javaiscoffee.polaroad.wrapper.RequestWrapperDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest(properties = {"JWT_SECRET_KEY=3123755132fdfds4daas4551af789d59f36977df5093be12c2314515135ddasg1f5k12hdfhjk412bh531uiadfi14b14bwebs52"})
@AutoConfigureMockMvc
@Transactional
public class AlbumControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private LoginService loginService;
    @Autowired
    private MemberRepository memberRepository;
    @MockBean
    private AlbumService albumService;

    private Member testMember;
    private RequestBuilder post;

    @BeforeEach
    void setup() {
        RegisterDto registerDto = new RegisterDto();
        registerDto.setEmail("aaa@naver.com");
        registerDto.setName("박자바");
        registerDto.setNickname("자바커피");
        registerDto.setPassword("a123123!");
        loginService.register(registerDto);
        LoginDto loginDto = new LoginDto();
        loginDto.setEmail("aaa@naver.com");
        loginDto.setPassword("a123123!");
        loginService.login(loginDto);
        Member member = Member.builder()
                .name("박자바")
                .nickname("자바커피")
                .email("aaa@naver.com")
                .password("a123123!")
                .build();
        memberRepository.save(member);

        this.testMember = member;
    }

//    @Test
    @DisplayName("앨범 생성 성공 테스트")
    public void createAlbum() throws Exception{
        AlbumDto albumDto = new AlbumDto();
        ResponseAlbumDto responseAlbumDto = new ResponseAlbumDto();
        responseAlbumDto.setAlbumId(1L);
        responseAlbumDto.setAlbumCardInfoList(anyList());
        responseAlbumDto.setDescription("설명");
//        responseAlbumDto.setMemberId(testMember.getMemberId());
//        responseAlbumDto.setUpdatedTime(LocalDateTime.now());

        given(albumService.createAlbum(albumDto, 1L)).willReturn(responseAlbumDto);

//        mockMvc.perform(post("/create")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(new RequestWrapperDto<>()))
//                .with(user("박자바").password("a123123!").roles("USER"))
//                .andExpect(status));

    }

}
