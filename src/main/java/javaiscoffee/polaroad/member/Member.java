package javaiscoffee.polaroad.member;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "member")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "memberId"
)
@Builder
public class Member implements UserDetails {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long memberId;

    @Column(nullable = false, unique = true, length = 255)
    private String email;
    @Setter
    @Column(nullable = false, length = 255)
    private String password;
    @Setter
    @Column(nullable = false, length = 255)
    private String name;
    @Setter
    @Column(nullable = false, length = 255)
    private String nickname;
    @Setter
    @Column(nullable = false)
    private String profileImage;
    @Setter
    @Column(nullable = false)
    private Integer postNumber;
    @Setter
    @Column(nullable = false)
    private Integer followedNumber;
    @Setter
    @Column(nullable = false)
    private Integer followingNumber;
    @Setter
    @Column(nullable = false, length = 255)
    @Enumerated(EnumType.STRING)
    private MemberRole role;
    @Column(name = "created_time")
    private LocalDate createdTime;
    @Setter
    @Column(name = "updated_date")
    private LocalDate updatedTime;
    @Setter @NotNull
    @Enumerated(EnumType.STRING)
    private MemberStatus status;

    @PrePersist
    public void PrePersist() {
        this.profileImage = "";
        this.postNumber = 0;
        this.followedNumber = 0;
        this.followingNumber = 0;
        this.role = MemberRole.USER;
        this.createdTime = LocalDate.now();
        this.updatedTime = LocalDate.now();
        this.status = MemberStatus.ACTIVE;
    }


    //UserDetails를 위한 추가
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(role.name()));
        return authorities;
    }
    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    /**
     * 저장되어 있는 현재 비밀번호를 암호화
     * @param passwordEncoder 암호화 할 인코더 클래스
     * @return 변경된 멤버 Entity
     */
    public Member hashPassword(PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(this.password);
        return this;
    }

    /**
     * 비밀번호 확인
     * @param plainPassword
     * @param passwordEncoder
     * @return 입력받은 비밀번호를 암호화해서 db에 있는 암호화되어 있는 값을
     */
    public boolean checkPassword(String plainPassword, PasswordEncoder passwordEncoder) {
        return passwordEncoder.matches(plainPassword,this.password);
    }

    public void deleteMember() {
        this.status = MemberStatus.DELETED;
//        this.setName("삭제된 멤버");
//        this.setNickname("삭제된 멤버");
        this.setUpdatedTime(LocalDate.now());
        this.email = "deleted" + this.email;
    }


    public Member(String email, String name, String nickname, String password) {
        this.email = email;
        this.name = name;
        this.nickname = nickname;
        this.password = password;
    }

    public Member(String email, String name, String nickname, String password, String profileImage, Integer postNumber, Integer followedNumber, Integer followingNumber, MemberRole role) {
        this.email = email;
        this.name = name;
        this.nickname = nickname;
        this.password = password;
        this.profileImage = profileImage;
        this.postNumber = postNumber;
        this.followedNumber = followedNumber;
        this.followingNumber = followingNumber;
        this.role = role;
    }
}
