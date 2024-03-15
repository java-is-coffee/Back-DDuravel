package javaiscoffee.polaroad.post;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostMemberInfoDto {
    private Long memberId;
    private String name; //멤버 이름
    private String nickname; //멤버 닉네임
    private String profileImage; //프로필 이미지
}
