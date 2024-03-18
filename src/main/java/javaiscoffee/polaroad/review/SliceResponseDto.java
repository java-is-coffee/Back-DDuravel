package javaiscoffee.polaroad.review;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 페이징 처리 후 응답으로 보낼 때 사용하는 Dto
 */
@Data
public class SliceResponseDto<T> {
    @Schema(description = "## 댓글 리스트",
            example =
                    "[\n" +
                    "    {\n" +
                    "        \"reviewId\": 2,\n" +
                    "        \"postId\": 1,\n" +
                    "        \"memberId\": 1,\n" +
                    "        \"profileImage\": \"\",\n" +
                    "        \"nickname\": \"폴라곰곰\",\n" +
                    "        \"content\": \"저도 다녀왔는데 너무 좋았어요.\",\n" +
                    "        \"reviewPhotoList\": [\n" +
                    "            \"https://lh5.googleusercontent.com/p/AF1QipM1QxKKnGOYaD3DadUkr3fJrxTquvyGP2eRhjR2=w1080-h624-n-k-no\"\n" +
                    "        ],\n" +
                    "        \"updatedTime\": \"2024-03-18T11:26:13.117278\"\n" +
                    "    },\n" +
                    "    {\n" +
                    "        \"reviewId\": 1,\n" +
                    "        \"postId\": 1,\n" +
                    "        \"memberId\": 1,\n" +
                    "        \"profileImage\": \"\",\n" +
                    "        \"nickname\": \"폴라곰곰\",\n" +
                    "        \"content\": \"저도 다녀왔는데 너무 좋았어요.\",\n" +
                    "        \"reviewPhotoList\": [\n" +
                    "            \"https://lh5.googleusercontent.com/p/AF1QipM1QxKKnGOYaD3DadUkr3fJrxTquvyGP2eRhjR2=w1080-h624-n-k-no\"\n" +
                    "        ],\n" +
                    "        \"updatedTime\": \"2024-03-18T11:26:10.492205\"\n" +
                    "    }\n" +
                    "]")
    private List<ResponseReviewDto> content;
    @Schema(description = "## 댓글 리스트", example ="true")
    private boolean hasNext;    // 다음 페이지가 있는지

    public SliceResponseDto(List<ResponseReviewDto> content, boolean hasNext) {
        this.content = content;
        this.hasNext = hasNext;
    }
}
