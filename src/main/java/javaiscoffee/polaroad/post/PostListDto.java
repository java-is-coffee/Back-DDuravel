package javaiscoffee.polaroad.post;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 검색이나 탐색페이지에서 필요한 정보를 응답으로 넘겨주는 Dto
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "검색이나 탐색페이지에서 필요한 정보를 넘겨주는 ResponseDto")
public class PostListDto {
    @Schema(description = "포스트 제목", example = "물놀이 명당 추천")
    private String title;
    @Schema(description = "포스트 ID", example = "1")
    private Long postId;
    @Schema(description = "포스트 작성자 닉네임", example = "당일치기달인")
    private String nickname;
    @Schema(description = "포스트 추천 개수", example = "77")
    private int goodNumber;
    @Schema(description = "포스트 카테고리", example = "PHOTO")
    private PostConcept concept;
    @Schema(description = "포스트 지역", example = "BUSAN")
    private PostRegion region;
    @Schema(description = "포스트 썸네일 포함 이미지 3장")
    private List<String> images;
}
