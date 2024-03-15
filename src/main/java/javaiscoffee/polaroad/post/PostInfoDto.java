package javaiscoffee.polaroad.post;

import javaiscoffee.polaroad.post.card.CardInfoDto;
import javaiscoffee.polaroad.post.hashtag.PostHashtagInfoDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * 포스트 내용 조회 ResponseDto
 */

@Data
@AllArgsConstructor
public class PostInfoDto {
    private String title;
    private boolean isMemberGood;
    private PostMemberInfoDto memberInfo;
    private String routePoint;
    private int goodNumber;
    private int thumbnailIndex;
    private PostConcept concept;
    private PostRegion region;
    private List<CardInfoDto> cards;
    private List<PostHashtagInfoDto> postHashtags;
}
