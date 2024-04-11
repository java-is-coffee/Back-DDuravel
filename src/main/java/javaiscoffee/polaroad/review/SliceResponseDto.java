package javaiscoffee.polaroad.review;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 페이징 처리 후 응답으로 보낼 때 사용하는 Dto
 */
@Data
public class SliceResponseDto<T> {
    @Schema(description = "## 댓글 리스트")
    private List<ResponseReviewDto> content;
    @Schema(description = "## 다음 페이지가 있는지 여부", example ="true")
    private boolean hasNext;

    public SliceResponseDto(List<ResponseReviewDto> content, boolean hasNext) {
        this.content = content;
        this.hasNext = hasNext;
    }
}
