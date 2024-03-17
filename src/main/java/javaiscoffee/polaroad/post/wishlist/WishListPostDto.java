package javaiscoffee.polaroad.post.wishlist;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "위시리스트의 포스트 목록을 보여줄 때 사용하는 Dto")
public class WishListPostDto {
    @Schema(description = "포스트 ID", example = "1")
    private Long postId;
    @Schema(description = "포스트 제목", example = "지나가던 개도 웃는 여행")
    private String title;
    @Schema(description = "포스트 썸네일 이미지 url", example = "http://")
    private String thumbnailImage;
}
