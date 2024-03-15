package javaiscoffee.polaroad.post.wishlist;

import javaiscoffee.polaroad.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WishListPostRepository extends JpaRepository<WishListPost, WishListPostId> {
    void deleteWishListPostByWishListPostId(WishListPostId id);

    WishListPost findWishListPostByPost(Post post);
    WishListPost findWishListPostByPostPostId(Long postId);

    //위시리스트에 포함되어 있는 모든 위시리스트포스트 조회
    List<WishListPost> findWishListPostsByWishListWishListId(Long wishListId);

    //위시리스트에 포함되어 있는 모든 위시리스트포스트 삭제
    void deleteWishListPostsByWishListWishListId(Long wishListId);
}
