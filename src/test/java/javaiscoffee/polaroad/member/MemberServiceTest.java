package javaiscoffee.polaroad.member;

import javaiscoffee.polaroad.login.LoginService;
import javaiscoffee.polaroad.login.RegisterDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(properties = {"JWT_SECRET_KEY=3123755132fdfds4daas4551af789d59f36977df5093be12c2314515135ddasg1f5k12hdfhjk412bh531uiadfi14b14bwebs52"})
class MemberServiceTest {
    @Mock private MemberService memberService;

    @Test
    public void testGetMemberInformation() {
        String email = "aaa@naver.com";

        when(memberService.getMemberInformation(any(String.class))).thenReturn(new MemberInformationResponseDto());

        MemberInformationResponseDto memberInformationResponseDto = memberService.getMemberInformation(email);

        assertThat(memberInformationResponseDto).isNotNull();
    }

    @Test
    public void testUpdateMemberInformation() {
        MemberInformationRequestDto memberInformationRequestDto = new MemberInformationRequestDto();
        memberInformationRequestDto.setMemberId(8237422961391639141L);
        memberInformationRequestDto.setEmail("aaa@naver.com");
        memberInformationRequestDto.setName("박자바");
        memberInformationRequestDto.setNickname("자바커피");
        memberInformationRequestDto.setProfileImage("https://lh3.googleusercontent.com/a/ACg8ocIUDVrYxwJiLs3303WK329pqp2QXNnJE9UFEsAaPzz6=s96-c");

        String email = "bbb@naver.com";

        when(memberService.updateMemberInformation(any(String.class), any(MemberInformationRequestDto.class))).thenReturn(new MemberInformationResponseDto());

        MemberInformationResponseDto memberInformationResponseDto = memberService.updateMemberInformation(email, memberInformationRequestDto);

        assertThat(memberInformationResponseDto).isNotNull();
    }

    @Test
    public void testResetPassword() {
        String email = "bbb@naver.com";
        String password = "a123123!";

        when(memberService.resetPassword(any(String.class), any(String.class))).thenReturn(true);

        Boolean b = memberService.resetPassword(email, password);

        assertThat(b).isTrue();
    }
}