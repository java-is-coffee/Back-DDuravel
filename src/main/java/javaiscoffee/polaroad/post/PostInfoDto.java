package javaiscoffee.polaroad.post;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "포스트 내용 조회할 때 사용되는 응답Dto")
public class PostInfoDto {
    @Schema(description = "포스트 제목", example = "한강 물 수제비 맛집")
    private String title;
    @Schema(description = "멤버가 해당 포스트를 추천했는지 유무", example = "false")
    private boolean isMemberGood;
    @Schema(description = "포스트를 적은 멤버의 정보가 담긴 Dto")
    private PostMemberInfoDto memberInfo;
    @Schema(description = "포스트 경로 좌표 직렬화", example = "프론트에서 알아서")
    private String routePoint;
    @Schema(description = "포스트 추천 수", example = "7")
    private int goodNumber;
    @Schema(description = "썸네일 번호", example = "1")
    private int thumbnailIndex;
    @Schema(description = "포스트 카테고리", example = "FOOD")
    private PostConcept concept;
    @Schema(description = "포스트 지역", example = "SEOUL")
    private PostRegion region;
    @Schema(description = "포스트 카드 정보")
    private List<CardInfoDto> cards;
    @Schema(description = "포스트 해쉬태그")
    private List<PostHashtagInfoDto> postHashtags;
}
