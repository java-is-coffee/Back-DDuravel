package javaiscoffee.polaroad.post.wishlist;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 위시리스트 목록 조회할 때 사용하는 Dto
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WishListDto {
    private Long wishListId;
    private String name;
}
