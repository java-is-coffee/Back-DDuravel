package javaiscoffee.polaroad.post;

import javaiscoffee.polaroad.member.Member;
import javaiscoffee.polaroad.post.card.Card;
import javaiscoffee.polaroad.post.good.PostGood;
import javaiscoffee.polaroad.post.hashtag.PostHashtag;
import javaiscoffee.polaroad.post.wishlist.WishListPost;
import javaiscoffee.polaroad.review.Review;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class PostTest {
    @Mock private Member member;
    @Mock private List<Card> cards;
    @Mock private List<PostHashtag> postHashtags;
    @Mock private List<Review> reviews;
    @Mock private List<PostGood> postGoods;
    @Mock private List<WishListPost> wishListPosts;

    @Test
    @DisplayName("포스트 생성 성공 테스트")
    void createpost() {
        // Given
        Post post = Post.builder()
                .member(member)
                .postId(1l)
                .title("제목")
                .concept(PostConcept.CITY)
                .region(PostRegion.SEOUL)
                .postGoods(postGoods)
                .postHashtags(postHashtags)
                .reviews(reviews)
                .cards(cards)
                .createdTime(LocalDateTime.now())
                .updatedTime(LocalDateTime.now())
                .routePoint("좌표")
                .status(PostStatus.ACTIVE)
                .thumbnailIndex(0)
                .wishListPosts(wishListPosts)
                .goodNumber(0)
                .reviewNumber(0)
                .build();
        // When Then
        Assertions.assertThat(post.getPostId()).isEqualTo(1L);
        Assertions.assertThat(post.getMember()).isEqualTo(member);
        Assertions.assertThat(post.getTitle()).isEqualTo("제목");
        Assertions.assertThat(post.getRoutePoint()).isEqualTo("좌표");
        Assertions.assertThat(post.getGoodNumber()).isEqualTo(0);
        Assertions.assertThat(post.getReviewNumber()).isEqualTo(0);
        Assertions.assertThat(post.getThumbnailIndex()).isEqualTo(0);
        Assertions.assertThat(post.getConcept()).isEqualTo(PostConcept.CITY);
        Assertions.assertThat(post.getRegion()).isEqualTo(PostRegion.SEOUL);
        Assertions.assertThat(post.getStatus()).isEqualTo(PostStatus.ACTIVE);
        Assertions.assertThat(post.getCards()).isEqualTo(cards);
        Assertions.assertThat(post.getPostHashtags()).isEqualTo(postHashtags);
        Assertions.assertThat(post.getReviews()).isEqualTo(reviews);
        Assertions.assertThat(post.getPostGoods()).isEqualTo(postGoods);
        Assertions.assertThat(post.getWishListPosts()).isEqualTo(wishListPosts);
    }

    @Test
    @DisplayName("포스트 수정 성공 테스트")
    void changePost() {
        // Given
        Post post = Post.builder()
                .member(member)
                .postId(1l)
                .title("제목")
                .concept(PostConcept.CITY)
                .region(PostRegion.SEOUL)
                .postGoods(postGoods)
                .postHashtags(postHashtags)
                .reviews(reviews)
                .cards(cards)
                .createdTime(LocalDateTime.now())
                .updatedTime(LocalDateTime.now())
                .routePoint("좌표")
                .status(PostStatus.ACTIVE)
                .thumbnailIndex(0)
                .wishListPosts(wishListPosts)
                .goodNumber(0)
                .reviewNumber(0)
                .build();

        // When
        post.setTitle("수정된제목");
        post.setRoutePoint("수정된좌표");
        post.setGoodNumber(1);
        post.setReviewNumber(1);
        post.setThumbnailIndex(1);
        post.setConcept(PostConcept.FOOD);
        post.setRegion(PostRegion.BUSAN);
        post.setStatus(PostStatus.DELETED);

        // Then
        Assertions.assertThat(post.getPostId()).isEqualTo(1L);
        Assertions.assertThat(post.getMember()).isEqualTo(member);
        Assertions.assertThat(post.getTitle()).isEqualTo("수정된제목");
        Assertions.assertThat(post.getRoutePoint()).isEqualTo("수정된좌표");
        Assertions.assertThat(post.getGoodNumber()).isEqualTo(1);
        Assertions.assertThat(post.getReviewNumber()).isEqualTo(1);
        Assertions.assertThat(post.getThumbnailIndex()).isEqualTo(1);
        Assertions.assertThat(post.getConcept()).isEqualTo(PostConcept.FOOD);
        Assertions.assertThat(post.getRegion()).isEqualTo(PostRegion.BUSAN);
        Assertions.assertThat(post.getStatus()).isEqualTo(PostStatus.DELETED);
    }
}