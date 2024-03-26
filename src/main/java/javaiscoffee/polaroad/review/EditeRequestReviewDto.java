package javaiscoffee.polaroad.review;

import io.swagger.v3.oas.annotations.media.Schema;
import javaiscoffee.polaroad.review.reviewPhoto.ReviewPhotoInfoDto;
import lombok.Data;

import java.util.List;

@Data
public class EditeRequestReviewDto {
    @Schema(description = "## 댓글 본문", example = "와 저도 가보고 싶어지네요.")
    private String content;
    @Schema(description = "## 댓글 사진 정보", example = "")
    private List<ReviewPhotoInfoDto> editPhotoList;
}
