package javaiscoffee.polaroad.post.wishlist;

import lombok.Data;

/**
 * 사용자한테 위시리스트를 보여줄 때 현재 포스트가 어느 위시리스트에 포함되어 있는지 표시
 */
@Data
public class WishListAddListDto extends WishListDto{
    private boolean postInWishList;

    public WishListAddListDto(Long wishListId, String name, boolean included) {
        super.setWishListId(wishListId);
        super.setName(name);
        this.postInWishList = included;
    }
}
