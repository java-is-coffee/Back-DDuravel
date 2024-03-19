package javaiscoffee.polaroad.review;

import io.swagger.v3.oas.annotations.media.Schema;
import javaiscoffee.polaroad.review.reviewPhoto.ReviewPhoto;
import lombok.Data;

import java.util.List;

@Data
public class ReviewEditRequestDto {
    @Schema(description = "## 댓글 사진 ID", example = "null")
    private Long reviewPhotoId; // 새로운 사진 url 추가 할 때 null이면 새로운 ReviewPhoto 객체 생성 후 저장하기 위해서 사용
    @Schema(description = "## 댓글 본문", example = "와 저도 가보고 싶어지네요.")
    private String content;
    @Schema(description = "## 사진 url 리스트", example = "\"https://lh5.googleusercontent.com/p/AF1QipM1QxKKnGOYaD3DadUkr3fJrxTquvyGP2eRhjR2=w1080-h624-n-k-no\"")
    private List<String> reviewPhotoList;
}
