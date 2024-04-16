package javaiscoffee.polaroad.member;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class FollowingMemberResponseDto {
    private List<FollowingMemberInfoDto> followingMemberInfo;
    private boolean hasNext;
}
