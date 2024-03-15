package javaiscoffee.polaroad.post.wishlist;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 위시리스트 목록 조회할 때 사용하는 Dto
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "위시리스트 목록 조회할 때 사용하는 Dto")
public class WishListDto {
    @Schema(description = "위시리스트ID", example = "1")
    private Long wishListId;
    @Schema(description = "위시리스트 이름", example = "꽃놀이 모음집")
    private String name;
}
