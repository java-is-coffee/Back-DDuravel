package javaiscoffee.polaroad.member;

import jakarta.persistence.Column;
import lombok.Data;
import lombok.Setter;

/**
 * 유저 정보 조회할 때 응답으로 주는 클래스
 */
@Data
public class MemberInformationResponseDto {
    private Long memberId;
    private String email;
    private String name;
    private String nickname;
    private String profileImage;
    private Integer postNumber;
    private Integer followedNumber;
    private Integer followingNumber;
}
