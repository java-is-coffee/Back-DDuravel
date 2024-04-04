package javaiscoffee.polaroad.post;

import io.swagger.v3.oas.annotations.media.Schema;
import javaiscoffee.polaroad.post.card.Card;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class PostListRepositoryDto {
    @Schema(description = "포스트 제목", example = "꽃 놀이 맛집 탐방기")
    private String title;
    @Schema(description = "포스트 ID", example = "1")
    private Long postId;
    @Schema(description = "포스트 작성자 닉네임", example = "당일치기달인")
    private String nickname;
    @Schema(description = "포스트 썸네일 번호", example = "0")
    private int thumbnailIndex;
    @Schema(description = "포스트 추천 개수", example = "77")
    private int goodNumber;
    @Schema(description = "포스트 카테고리", example = "PHOTO")
    private PostConcept concept;
    @Schema(description = "포스트 지역", example = "BUSAN")
    private PostRegion region;
    @Schema(description = "포스트 썸네일 포함 이미지 3장")
    private List<Card> cards;
    @Schema(description = "포스트 업데이트 날짜", example = "2024-04-04T12:12:12")
    private LocalDateTime updatedTime;
}
