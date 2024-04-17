package javaiscoffee.polaroad.review;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import javaiscoffee.polaroad.review.reviewPhoto.ReviewPhotoInfoDto;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.beans.ConstructorProperties;
import java.util.List;

@Data
@NoArgsConstructor
@Builder
public class EditeRequestReviewDto {
    @Schema(description = "## 댓글 본문", example = "와 저도 가보고 싶어지네요.")
    private String content;
    @Schema(description = "## 댓글 사진 정보")
    private List<ReviewPhotoInfoDto> editPhotoList;

    @ConstructorProperties({"content", "editPhotoList"})
    public EditeRequestReviewDto(String content, List<ReviewPhotoInfoDto> editPhotoList) {
        this.content = content;
        this.editPhotoList = editPhotoList;
    }
}
