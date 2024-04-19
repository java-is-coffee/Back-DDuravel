package javaiscoffee.polaroad.member;

import javaiscoffee.polaroad.post.PostThumbnailDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
public class MemberMiniProfileDto{
    private String name;
    private String nickname;
    private String profileImage;
    private int postNumber;
    private int followedNumber;
    private int followingNumber;
    private boolean isFollowing;
    private List<PostThumbnailDto> thumbnails;
}
