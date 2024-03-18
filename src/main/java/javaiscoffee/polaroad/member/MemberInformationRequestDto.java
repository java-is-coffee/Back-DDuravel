package javaiscoffee.polaroad.member;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "회원정보 수정 RequestDto")
public class MemberInformationRequestDto {
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
}
