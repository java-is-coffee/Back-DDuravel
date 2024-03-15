package javaiscoffee.polaroad.post.wishlist;

import javaiscoffee.polaroad.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WishListRepository extends JpaRepository<WishList, Long> {
    int countWishListByMember(Member member);
    List<WishList> findWishListsByMember(Member member);
}
