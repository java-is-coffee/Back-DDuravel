package javaiscoffee.polaroad.post;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostListResponseDto {
    @Schema(description = "포스트 목록")
    private List<PostListDto> posts;
    @Schema(description = "총 페이지", example = "1")
    private int maxPage;
}
