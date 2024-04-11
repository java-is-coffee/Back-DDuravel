package javaiscoffee.polaroad.member;

import javaiscoffee.polaroad.login.RegisterDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MemberTest {
    @Test
    @DisplayName("멤버 생성 성공 테스트")
    void createMember() {
        RegisterDto registerDto = new RegisterDto();
        registerDto.setEmail("aaa@naver.com");
        registerDto.setName("박자바");
        registerDto.setNickname("자바커피");
        registerDto.setPassword("a123123!");

        Member member = Member.builder().memberId(1L)
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

        Assertions.assertThat(member.getMemberId()).isEqualTo(1L);
        Assertions.assertThat(member.getEmail()).isEqualTo(registerDto.getEmail());
        Assertions.assertThat(member.getName()).isEqualTo(registerDto.getName());
        Assertions.assertThat(member.getNickname()).isEqualTo(registerDto.getNickname());
        Assertions.assertThat(member.getPassword()).isEqualTo(registerDto.getPassword());
        Assertions.assertThat(member.getProfileImage()).isEqualTo("");
        Assertions.assertThat(member.getPostNumber()).isEqualTo(0);
        Assertions.assertThat(member.getFollowedNumber()).isEqualTo(0);
        Assertions.assertThat(member.getFollowingNumber()).isEqualTo(0);
        Assertions.assertThat(member.getRole()).isEqualTo(MemberRole.USER);
        Assertions.assertThat(member.getSocialLogin()).isEqualTo(null);
        Assertions.assertThat(member.getStatus()).isEqualTo(MemberStatus.ACTIVE);
    }

    @Test
    @DisplayName("멤버 수정 성공 테스트")
    void changeMember() {
        RegisterDto registerDto = new RegisterDto();
        registerDto.setEmail("aaa@naver.com");
        registerDto.setName("박자바");
        registerDto.setNickname("자바커피");
        registerDto.setPassword("a123123!");

        Member member = Member.builder().memberId(1L)
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

        member.setName("김말구");
        member.setNickname("채팅보이");
        member.setPassword("abc123");
        member.setProfileImage("");
        member.setPostNumber(2);
        member.setFollowedNumber(5);
        member.setFollowingNumber(9);
        member.setRole(MemberRole.ADMIN);
        member.setSocialLogin(SocialLogin.GOOGLE);
        member.setStatus(MemberStatus.SUSPENDED);

        Assertions.assertThat(member.getMemberId()).isEqualTo(1L);
        Assertions.assertThat(member.getEmail()).isEqualTo(registerDto.getEmail());
        Assertions.assertThat(member.getName()).isEqualTo("김말구");
        Assertions.assertThat(member.getNickname()).isEqualTo("채팅보이");
        Assertions.assertThat(member.getPassword()).isEqualTo("abc123");
        Assertions.assertThat(member.getProfileImage()).isEqualTo("");
        Assertions.assertThat(member.getPostNumber()).isEqualTo(2);
        Assertions.assertThat(member.getFollowedNumber()).isEqualTo(5);
        Assertions.assertThat(member.getFollowingNumber()).isEqualTo(9);
        Assertions.assertThat(member.getRole()).isEqualTo(MemberRole.ADMIN);
        Assertions.assertThat(member.getSocialLogin()).isEqualTo(SocialLogin.GOOGLE);
        Assertions.assertThat(member.getStatus()).isEqualTo(MemberStatus.SUSPENDED);
    }
}