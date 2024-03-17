package javaiscoffee.polaroad.post.wishlist;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 사용자한테 위시리스트를 보여줄 때 현재 포스트가 어느 위시리스트에 포함되어 있는지 표시
 */
@Data
@Schema(description = "사용자한테 위시리스트를 목록을 보여줄 때 현재 포스트가 어느 위시리스트에 포함되어 있는지 함께 표시")
public class WishListAddListDto extends WishListDto{
    @Schema(description = "포스트가 위시리스트에 포함되어 있는지 여부", example = "true | false")
    private boolean postInWishList;

    public WishListAddListDto(Long wishListId, String name, boolean included) {
        super.setWishListId(wishListId);
        super.setName(name);
        this.postInWishList = included;
    }
}
