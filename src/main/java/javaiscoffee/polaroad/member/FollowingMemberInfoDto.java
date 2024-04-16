package javaiscoffee.polaroad.member;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class FollowingMemberInfoDto {
    private Long memberId;
    private String nickname;
    private String profileImage;
    private LocalDateTime createdTime;
}
