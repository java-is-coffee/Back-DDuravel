package javaiscoffee.polaroad.member;

import javaiscoffee.polaroad.exception.BadRequestException;
import javaiscoffee.polaroad.exception.NotFoundException;
import javaiscoffee.polaroad.login.RegisterDto;
import javaiscoffee.polaroad.security.BaseException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberServiceUnitTest {
    @Mock
    private MemberRepository memberRepository;
    @InjectMocks
    private MemberService memberService;

    @Test
    public void testGetMemberInformation() {
        RegisterDto registerDto = new RegisterDto();
        registerDto.setEmail("aaa@naver.com");
        registerDto.setName("박자바");
        registerDto.setNickname("자바커피");
        registerDto.setPassword("a123123!");

        Member mockMember = Member.builder().memberId(1L)
                .email(registerDto.getEmail())
                .name(registerDto.getName())
                .nickname(registerDto.getNickname())
                .password(registerDto.getPassword())
                .profileImage("")
                .postNumber(0)
                .followedNumber(0)
                .followingNumber(0)
                .role(MemberRole.USER)
                .socialLogin(null)
                .status(MemberStatus.ACTIVE)
                .build();

        when(memberRepository.findByEmail(registerDto.getEmail())).thenReturn(Optional.of(mockMember));

        // 그냥 메서드만 가정해서 findByEmail 메서드가 시작되면 이라고 가정을 주고 뒤에는 반환되어야 할 데이터를 주입해주는 것으로 마무리했습니다.
        // 그러고 나서 나타났던 문제가 의존성 관련 문제인데 memberRepository는 사용하지 않게 mock으로 주입하면서 memberService만 실제 코드를 사용하게 하려고 했으나
        // 다른 코드들에 영향을 끼치기 때문에 실행이 안되어서, 위에 보시는 것처럼 memberService는 실제 코드를 사용하게 어노테이션을 붙였고 memberRepository는 mock으로 처리했습니다.
        // 이렇게 함으로써 이 테스트를 실행할 때 다른 클래스들은 로딩하지 않고 MemberServiceUnitTest, MemberService, MemberRepository만 로딩하게 해서 테스트를 진행하게 수정했습니다.
        String email1 = "aaa@naver.com";

        MemberInformationResponseDto memberInformationResponseDto1 = memberService.getMemberInformation(email1);

        assertThat(memberInformationResponseDto1.getEmail()).isEqualTo(email1);
        assertThat(memberInformationResponseDto1.getEmail()).isNotEqualTo("bbb@naver.com");

        String email2 = "bbb@naver.com";

        Assertions.assertThatThrownBy(() -> memberService.getMemberInformation(email2)).isInstanceOf(BaseException.class);
    }

    @Test
    public void testUpdateMemberInformation() {
        RegisterDto registerDto = new RegisterDto();
        registerDto.setEmail("aaa@naver.com");
        registerDto.setName("박자바");
        registerDto.setNickname("자바커피");
        registerDto.setPassword("a123123!");

        Member mockMember = Member.builder().memberId(1L)
                .email(registerDto.getEmail())
                .name(registerDto.getName())
                .nickname(registerDto.getNickname())
                .password(registerDto.getPassword())
                .profileImage("")
                .postNumber(0)
                .followedNumber(0)
                .followingNumber(0)
                .role(MemberRole.USER)
                .socialLogin(null)
                .status(MemberStatus.ACTIVE)
                .build();

        when(memberRepository.findByEmail(registerDto.getEmail())).thenReturn(Optional.of(mockMember));

        String email1 = "aaa@naver.com";

        MemberInformationRequestDto memberInformationRequestDtoInput1 = new MemberInformationRequestDto();
        memberInformationRequestDtoInput1.setMemberId(1L);
        memberInformationRequestDtoInput1.setEmail("aaa@naver.com");
        memberInformationRequestDtoInput1.setName("박자바");
        memberInformationRequestDtoInput1.setNickname("자바커피");
        memberInformationRequestDtoInput1.setProfileImage("https://lh3.googleusercontent.com/a/ACg8ocIUDVrYxwJiLs3303WK329pqp2QXNnJE9UFEsAaPzz6=s96-c");

        MemberInformationResponseDto memberInformationResponseDtoOutput1 = memberService.updateMemberInformation(email1, memberInformationRequestDtoInput1);

        assertThat(memberInformationResponseDtoOutput1.getEmail()).isEqualTo(email1);
        assertThat(memberInformationResponseDtoOutput1.getEmail()).isEqualTo(memberInformationRequestDtoInput1.getEmail());
        assertThat(memberInformationResponseDtoOutput1.getEmail()).isNotEqualTo("bbb@naver.com");

        String email2 = "bbb@naver.com";

        MemberInformationRequestDto memberInformationRequestDtoInput2 = new MemberInformationRequestDto();
        memberInformationRequestDtoInput2.setMemberId(1L);
        memberInformationRequestDtoInput2.setEmail("aaa@naver.com");
        memberInformationRequestDtoInput2.setName("박자바");
        memberInformationRequestDtoInput2.setNickname("자바커피");
        memberInformationRequestDtoInput2.setProfileImage("https://lh3.googleusercontent.com/a/ACg8ocIUDVrYxwJiLs3303WK329pqp2QXNnJE9UFEsAaPzz6=s96-c");

        Assertions.assertThatThrownBy(() -> memberService.updateMemberInformation(email2, memberInformationRequestDtoInput2)).isInstanceOf(BaseException.class);

        String email3 = "aaa@naver.com";

        MemberInformationRequestDto memberInformationRequestDtoInput3 = new MemberInformationRequestDto();
        memberInformationRequestDtoInput3.setMemberId(3L);
        memberInformationRequestDtoInput3.setEmail("bbb@naver.com");
        memberInformationRequestDtoInput3.setName("박자바");
        memberInformationRequestDtoInput3.setNickname("자바커피");
        memberInformationRequestDtoInput3.setProfileImage("https://lh3.googleusercontent.com/a/ACg8ocIUDVrYxwJiLs3303WK329pqp2QXNnJE9UFEsAaPzz6=s96-c");

        Assertions.assertThatThrownBy(() -> memberService.updateMemberInformation(email3, memberInformationRequestDtoInput3)).isInstanceOf(BadRequestException.class);
    }

    @Test
    public void testDeleteAccount() {
        RegisterDto registerDto = new RegisterDto();
        registerDto.setEmail("aaa@naver.com");
        registerDto.setName("박자바");
        registerDto.setNickname("자바커피");
        registerDto.setPassword("a123123!");

        Member mockMember = Member.builder().memberId(1L)
                .email(registerDto.getEmail())
                .name(registerDto.getName())
                .nickname(registerDto.getNickname())
                .password(registerDto.getPassword())
                .profileImage("")
                .postNumber(0)
                .followedNumber(0)
                .followingNumber(0)
                .role(MemberRole.USER)
                .socialLogin(null)
                .status(MemberStatus.ACTIVE)
                .build();

        when(memberRepository.findByMemberId(1L)).thenReturn(Optional.of(mockMember));

        memberService.deleteAccount(1L);
        Assertions.assertThatThrownBy(() -> memberService.deleteAccount(2L)).isInstanceOf(NotFoundException.class);

        assertThat(mockMember.getStatus()).isEqualTo(MemberStatus.DELETED);
        
        Assertions.assertThatThrownBy(() -> memberService.deleteAccount(1L)).isInstanceOf(BadRequestException.class);
    }
}