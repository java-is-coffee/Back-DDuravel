package javaiscoffee.polaroad.member;

import javaiscoffee.polaroad.exception.BadRequestException;
import javaiscoffee.polaroad.exception.NotFoundException;
import javaiscoffee.polaroad.login.LoginService;
import javaiscoffee.polaroad.login.RegisterDto;
import javaiscoffee.polaroad.security.BaseException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {"JWT_SECRET_KEY=3123755132fdfds4daas4551af789d59f36977df5093be12c2314515135ddasg1f5k12hdfhjk412bh531uiadfi14b14bwebs52"})
@Transactional
@ActiveProfiles("test")
class MemberServiceTest {
    @Autowired
    private LoginService loginService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private MemberRepository memberRepository;

    @Test
    public void testGetMemberInformation() {
        RegisterDto registerDto = new RegisterDto();
        registerDto.setEmail("aaa@naver.com");
        registerDto.setName("박자바");
        registerDto.setNickname("자바커피");
        registerDto.setPassword("a123123!");

        loginService.register(registerDto);

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

        loginService.register(registerDto);

        Member member = memberRepository.findByEmail(registerDto.getEmail()).orElseThrow(() -> new NotFoundException("멤버가 없습니다."));

        MemberInformationRequestDto memberInformationRequestDtoInput1 = new MemberInformationRequestDto();
        memberInformationRequestDtoInput1.setMemberId(member.getMemberId());
        memberInformationRequestDtoInput1.setEmail("aaa@naver.com");
        memberInformationRequestDtoInput1.setName("박자바");
        memberInformationRequestDtoInput1.setNickname("자바커피");
        memberInformationRequestDtoInput1.setProfileImage("https://lh3.googleusercontent.com/a/ACg8ocIUDVrYxwJiLs3303WK329pqp2QXNnJE9UFEsAaPzz6=s96-c");

        MemberInformationResponseDto memberInformationResponseDtoOutput1 = memberService.updateMemberInformation(member.getEmail(), memberInformationRequestDtoInput1);

        assertThat(memberInformationResponseDtoOutput1.getEmail()).isEqualTo(member.getEmail());
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
    public void testResetPassword() {
        RegisterDto registerDto = new RegisterDto();
        registerDto.setEmail("aaa@naver.com");
        registerDto.setName("박자바");
        registerDto.setNickname("자바커피");
        registerDto.setPassword("a123123!");

        Member member = loginService.register(registerDto);

        Boolean boo = memberService.resetPassword(member.getEmail(), "abc123");
        assertThat(boo).isTrue();

        Assertions.assertThatThrownBy(() -> memberService.resetPassword("bbb@naver.com", "abc123")).isInstanceOf(BaseException.class);
    }

    @Test
    public void testToggleFollow() {
        RegisterDto registerDto1 = new RegisterDto();
        registerDto1.setEmail("aaa@naver.com");
        registerDto1.setName("박자바");
        registerDto1.setNickname("자바커피");
        registerDto1.setPassword("a123123!");

        Member member1 = loginService.register(registerDto1);

        RegisterDto registerDto2 = new RegisterDto();
        registerDto2.setEmail("bbb@naver.com");
        registerDto2.setName("김자바");
        registerDto2.setNickname("커피자바");
        registerDto2.setPassword("abc123");

        Member member2 = loginService.register(registerDto2);

        assertThat(member1.getFollowedNumber()).isEqualTo(0);
        assertThat(member1.getFollowingNumber()).isEqualTo(0);
        assertThat(member2.getFollowedNumber()).isEqualTo(0);
        assertThat(member2.getFollowingNumber()).isEqualTo(0);

        memberService.toggleFollow(member1.getMemberId(), member2.getMemberId());

        member1 = memberRepository.findById(member1.getMemberId()).get();
        member2 = memberRepository.findById(member2.getMemberId()).get();

        assertThat(member1.getFollowedNumber()).isEqualTo(0);
        assertThat(member1.getFollowingNumber()).isEqualTo(1);
        assertThat(member2.getFollowedNumber()).isEqualTo(1);
        assertThat(member2.getFollowingNumber()).isEqualTo(0);

        memberService.toggleFollow(member1.getMemberId(), member2.getMemberId());

        member1 = memberRepository.findById(member1.getMemberId()).get();
        member2 = memberRepository.findById(member2.getMemberId()).get();

        assertThat(member1.getFollowedNumber()).isEqualTo(0);
        assertThat(member1.getFollowingNumber()).isEqualTo(0);
        assertThat(member2.getFollowedNumber()).isEqualTo(0);
        assertThat(member2.getFollowingNumber()).isEqualTo(0);
    }

    @Test
    public void testDeleteAccount() {
        RegisterDto registerDto = new RegisterDto();
        registerDto.setEmail("aaa@naver.com");
        registerDto.setName("박자바");
        registerDto.setNickname("자바커피");
        registerDto.setPassword("a123123!");

        Member member = loginService.register(registerDto);

        memberService.deleteAccount(member.getMemberId());

        Assertions.assertThatThrownBy(() -> memberService.deleteAccount(member.getMemberId() + 1)).isInstanceOf(NotFoundException.class);
        Assertions.assertThatThrownBy(() -> memberService.deleteAccount(member.getMemberId())).isInstanceOf(BadRequestException.class);
    }
}