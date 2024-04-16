package javaiscoffee.polaroad.post.wishlist;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import ext.javaiscoffee.polaroad.member.QMember;
import ext.javaiscoffee.polaroad.post.QPost;
import ext.javaiscoffee.polaroad.post.card.QCard;
import ext.javaiscoffee.polaroad.post.wishlist.QWishList;
import ext.javaiscoffee.polaroad.post.wishlist.QWishListPost;
import jakarta.persistence.EntityManager;
import javaiscoffee.polaroad.post.Post;
import javaiscoffee.polaroad.post.PostListRepositoryDto;
import javaiscoffee.polaroad.post.card.Card;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Repository
public class QueryWishListRepositoryImpl implements QueryWishListRepository{
    private final JPAQueryFactory queryFactory;
    public QueryWishListRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    /**
     * 위시리스트에 포함되어 있는 포스트들을 반환
     */
    @Override
    public WishListPostListResponseDto getWishListPostDtos(Long wishListId,int page, int pageSize) {
        QPost post = QPost.post;
        QCard card = QCard.card;
        QWishList wishList = QWishList.wishList;
        QWishListPost wishListPost = QWishListPost.wishListPost;

        // 주요 변경: fetchJoin 제거, DTO 직접 조회
        JPAQuery<WishListPostDto> query = queryFactory
                .select(Projections.constructor(
                                WishListPostDto.class,
                                post.postId,
                                post.title,
                                JPAExpressions
                                        .select(card.image)
                                        .from(card)
                                        .where(card.post.eq(post), card.cardIndex.eq(post.thumbnailIndex))
                                        .orderBy(card.cardIndex.asc())
                                        .limit(1)
                ))
                .from(post)
                .leftJoin(post.wishListPosts, wishListPost)
                .leftJoin(wishListPost.wishList, wishList)
                .where(wishListPost.wishList.wishListId.eq(wishListId))
                .orderBy(wishListPost.createdTime.desc())
                .offset(getOffset(page, pageSize))
                .limit(pageSize + 1);
        List<WishListPostDto> wishListPostDtos = query.fetch();
        return new WishListPostListResponseDto(wishListPostDtos, hasNextPage(wishListPostDtos, pageSize));
    }

    private boolean hasNextPage(List<WishListPostDto> list, int pageSize) {
        if (list.size() > pageSize) {
            list.remove(pageSize);
            return true;
        }
        return false;
    }

    private int getOffset(int page, int pageSize) {
        return (page - 1) * pageSize;
    }
}
