package javaiscoffee.polaroad.post.wishlist;

import java.util.List;

public interface QueryWishListRepository {
    List<WishListPostDto> getWishListPostDtos(Long wishListId,int paging, int pagingNumber);
}
