package javaiscoffee.polaroad.review;

import io.swagger.v3.oas.annotations.media.Schema;
import javaiscoffee.polaroad.review.reviewPhoto.ReviewPhoto;
import lombok.Data;

import java.util.List;

@Data
public class ReviewEditRequestDto {
    @Schema(description = "## 댓글 본문", example = "와 저도 가보고 싶어지네요.")
    private String content;
    @Schema(description = "## 사진 url 리스트", example = "\"https://lh5.googleusercontent.com/p/AF1QipM1QxKKnGOYaD3DadUkr3fJrxTquvyGP2eRhjR2=w1080-h624-n-k-no\"")
    private List<String> reviewPhotoList;
}
