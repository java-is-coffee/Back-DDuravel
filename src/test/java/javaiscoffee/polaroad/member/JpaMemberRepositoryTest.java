package javaiscoffee.polaroad.member;

import javaiscoffee.polaroad.config.JpaConfigTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(JpaConfigTest.class)
class JpaMemberRepositoryTest {
    @Autowired
    private MemberRepository memberRepository;

    private Long savedMemberId1;
    private Long savedMemberId2;

    @BeforeEach
    void setUp() {
        Member member1 = Member.builder()
                .name("박자바")
                .nickname("자바커피")
                .email("aaa@naver.com")
                .password("a123123!")
                .build();
        member1.hashPassword(new BCryptPasswordEncoder());
        memberRepository.save(member1);
        member1 = memberRepository.findByEmail("aaa@naver.com").get();

        Member member2 = Member.builder()
                .name("김자바")
                .nickname("커피자바")
                .email("bbb@naver.com")
                .password("abc123")
                .build();
        member2.hashPassword(new BCryptPasswordEncoder());
        memberRepository.save(member2);
        member2 = memberRepository.findByEmail("bbb@naver.com").get();

        this.savedMemberId1 = member1.getMemberId();
        this.savedMemberId2 = member2.getMemberId();
    }

    @Test
    @DisplayName("멤버 조회")
    void findMember() {
        // Given
        Member member = memberRepository.findById(savedMemberId1).get();

        // When&Then
        Assertions.assertThat(member.getMemberId()).isEqualTo(savedMemberId1);
        Assertions.assertThat(member.getEmail()).isEqualTo("aaa@naver.com");
        Assertions.assertThat(member.getName()).isEqualTo("박자바");
        Assertions.assertThat(member.getNickname()).isEqualTo("자바커피");
        Assertions.assertThat(member.getProfileImage()).isEqualTo("");
        Assertions.assertThat(member.getPostNumber()).isEqualTo(0);
        Assertions.assertThat(member.getFollowedNumber()).isEqualTo(0);
        Assertions.assertThat(member.getFollowingNumber()).isEqualTo(0);
        Assertions.assertThat(member.getRole()).isEqualTo(MemberRole.USER);
        Assertions.assertThat(member.getSocialLogin()).isEqualTo(null);
        Assertions.assertThat(member.getStatus()).isEqualTo(MemberStatus.ACTIVE);
    }

    @Test
    @DisplayName("멤버 수정")
    void updateMember() {
        // Given
        Member member = memberRepository.findById(savedMemberId1).get();

        member.setName("김말구");
        member.setNickname("채팅보이");
        member.setProfileImage("https://lh3.googleusercontent.com/a/ACg8ocJupG7o_Y0DlY4Fg8uUmlElHoG2o7l4PF3UhBSSelYnAZ9YAw=s96-c");
        member.setPostNumber(2);
        member.setFollowedNumber(5);
        member.setFollowingNumber(9);
        member.setRole(MemberRole.ADMIN);
        member.setSocialLogin(SocialLogin.GOOGLE);
        member.setStatus(MemberStatus.SUSPENDED);

        // When&Then
        Assertions.assertThat(member.getMemberId()).isEqualTo(savedMemberId1);
        Assertions.assertThat(member.getEmail()).isEqualTo("aaa@naver.com");
        Assertions.assertThat(member.getName()).isEqualTo("김말구");
        Assertions.assertThat(member.getNickname()).isEqualTo("채팅보이");
        Assertions.assertThat(member.getProfileImage()).isEqualTo("https://lh3.googleusercontent.com/a/ACg8ocJupG7o_Y0DlY4Fg8uUmlElHoG2o7l4PF3UhBSSelYnAZ9YAw=s96-c");
        Assertions.assertThat(member.getPostNumber()).isEqualTo(2);
        Assertions.assertThat(member.getFollowedNumber()).isEqualTo(5);
        Assertions.assertThat(member.getFollowingNumber()).isEqualTo(9);
        Assertions.assertThat(member.getRole()).isEqualTo(MemberRole.ADMIN);
        Assertions.assertThat(member.getSocialLogin()).isEqualTo(SocialLogin.GOOGLE);
        Assertions.assertThat(member.getStatus()).isEqualTo(MemberStatus.SUSPENDED);
    }

    @Test
    @DisplayName("이메일로 멤버 체크")
    void getMember() {
        // Given
        Member member = memberRepository.findByEmail("aaa@naver.com").get();

        // When&Then
        assertThat(member).isNotNull();
    }
}