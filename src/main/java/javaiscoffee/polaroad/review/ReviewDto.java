package javaiscoffee.polaroad.review;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;


@Data
public class ReviewDto {
    @NotNull
    @Min(1L)
    @Max(10L)
    @Schema(description = "## 포스트 Id", example = "1")
    private Long postId;
    @NotNull
    @Schema(description = "## 맴버 Id", example = "1")
    private Long memberId;
    @Schema(description = "## 댓글 본문", example = "저도 다녀왔는데 너무 좋았어요.")
    private String content;
    @NotNull
    @Size(min = 1,max = 5)
    @Schema(description = "## 사진 url 리스트")
    private List<@NotBlank String> reviewPhotoList;
}
