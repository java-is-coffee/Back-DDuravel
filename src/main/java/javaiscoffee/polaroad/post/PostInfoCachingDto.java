package javaiscoffee.polaroad.post;

import io.swagger.v3.oas.annotations.media.Schema;
import javaiscoffee.polaroad.post.card.CardInfoDto;
import javaiscoffee.polaroad.post.hashtag.PostHashtagInfoDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "포스트 내용 캐싱할 때 사용되는 Dto")
public class PostInfoCachingDto {
    @Schema(description = "포스트 제목", example = "한강 물 수제비 맛집")
    private String title;
    @Schema(description = "글 작성자 고유 아이디", example = "1")
    private Long memberId;
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
    @Schema(description = "포스트 업데이트 시간", example = "2024-04-09T10:10:10")
    private LocalDateTime updatedTime;
    @Schema(description = "포스트 카드 정보")
    private List<CardInfoDto> cards;
    @Schema(description = "포스트 해쉬태그")
    private List<PostHashtagInfoDto> postHashtags;

}
