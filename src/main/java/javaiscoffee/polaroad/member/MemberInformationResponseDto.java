package javaiscoffee.polaroad.member;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import lombok.Data;
import lombok.Setter;

/**
 * 유저 정보 조회할 때 응답으로 주는 클래스
 */
@Data
@Schema(description = "회원정보 수정 ResponseDto")
public class MemberInformationResponseDto {
    @Schema(description = "정보 수정하려는 멤버 ID", example = "1")
    private Long memberId;
    @Schema(description = "정보 수정하려는 멤버 이메일", example = "aaa@naver.com")
    private String email;
    @Schema(description = "정보 수정하려는 멤버 이름", example = "박박구름")
    private String name;
    @Schema(description = "정보 수정하려는 멤버 닉네임", example = "구름자바커피")
    private String nickname;
    @Schema(description = "정보 수정하려는 멤버 프로필 이미지 url", example = "http")
    private String profileImage;
    @Schema(description = "정보 수정하려는 멤버 포스트 개수", example = "3")
    private Integer postNumber;
    @Schema(description = "정보 수정하려는 멤버 팔로워 개수", example = "3")
    private Integer followedNumber;
    @Schema(description = "정보 수정하려는 멤버 팔로잉 개수", example = "3")
    private Integer followingNumber;
}
