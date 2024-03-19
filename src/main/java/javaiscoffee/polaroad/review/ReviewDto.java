package javaiscoffee.polaroad.review;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;


@Data
public class ReviewDto {
    @Schema(description = "## 포스트 Id", example = "1")
    private Long postId;
    @Schema(description = "## 맴버 Id", example = "1")
    private Long memberId;
    @Schema(description = "## 댓글 본문", example = "저도 다녀왔는데 너무 좋았어요.")
    private String content;
    @Schema(description = "## 사진 url 리스트")
    private List<String> reviewPhotoList;
}
