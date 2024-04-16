package javaiscoffee.polaroad.member;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberBasicInfoDto {
    private String name;
    private String nickname;
    private String profileImage;
    private int postNumber;
    private int followedNumber;
    private int followingNumber;
    private MemberStatus status;
}
