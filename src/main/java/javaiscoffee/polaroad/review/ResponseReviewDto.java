package javaiscoffee.polaroad.review;

import io.swagger.v3.oas.annotations.media.Schema;
import javaiscoffee.polaroad.review.reviewPhoto.ReviewPhoto;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ResponseReviewDto {
    @Schema(description = "## 댓글 Id", example = "1")
    private Long reviewId;
    @Schema(description = "## 포스트 Id", example = "1")
    private Long postId;
    @Schema(description = "## 맴버 Id", example = "1")
    private Long memberId;
    @Schema(description = "## 프로필 이미지", example = "")
    private String profileImage;
    @Schema(description = "## 맴버 닉네임", example = "폴라곰")
    private String nickname;
    @Schema(description = "## 댓글 본문", example = "저도 다녀왔는데 너무 좋았어요.")
    private String content;
    @Schema(description = "## 사진 url 리스트",
            example ="\"https://lh5.googleusercontent.com/p/AF1QipM1QxKKnGOYaD3DadUkr3fJrxTquvyGP2eRhjR2=w1080-h624-n-k-no\",\n" +
                    "\"https://lh5.googleusercontent.com/p/AF1QipOAkhVKrq3broFnCMCx4sdqm45jxANDfoC2k3bi=w1080-h624-n-k-no\"")
    private List<String> reviewPhotoList;
    @Schema(description = "## 댓글 업데이트 시간", example = "2024-03-12T15:11:30.751404")
    private LocalDateTime updatedTime;
}

