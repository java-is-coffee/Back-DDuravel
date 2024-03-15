package javaiscoffee.polaroad.post;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "포스트 작성자 정보가 담긴 Dto")
public class PostMemberInfoDto {
    @Schema(description = "포스트 작성자 ID", example = "1")
    private Long memberId;
    @Schema(description = "포스트 작성자 이름", example = "박자바")
    private String name; //멤버 이름
    @Schema(description = "포스트 작성자 닉네임", example = "자바칩스무디")
    private String nickname; //멤버 닉네임
    @Schema(description = "포스트 작성자 프로필 이미지 url", example = "http://")
    private String profileImage; //프로필 이미지
}
