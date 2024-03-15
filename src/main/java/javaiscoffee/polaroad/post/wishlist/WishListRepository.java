package javaiscoffee.polaroad.post.wishlist;

import javaiscoffee.polaroad.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WishListRepository extends JpaRepository<WishList, Long>, QueryWishListRepository {
    int countWishListByMember(Member member);
    List<WishList> findWishListsByMember(Member member);
}
