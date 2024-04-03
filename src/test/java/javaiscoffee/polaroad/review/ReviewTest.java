package javaiscoffee.polaroad.review;

import com.navercorp.fixturemonkey.FixtureMonkey;
import javaiscoffee.polaroad.member.Member;
import javaiscoffee.polaroad.post.Post;
import javaiscoffee.polaroad.review.reviewGood.ReviewGood;
import javaiscoffee.polaroad.review.reviewPhoto.ReviewPhoto;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.BDDAssertions.then;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

public class ReviewTest {

    @Mock private Member member;
    @Mock private Post post;
    @Mock private List<ReviewPhoto> reviewPhotos;
    @Mock private List<ReviewGood> reviewGoods;

    @Test
    @DisplayName("리뷰 생성 성공 테스트")
    void writeReivew() {
        //given
        Review review = Review.builder()
                .reviewId(1L)
                .post(post)
                .member(member)
                .content("댓글 테스트입니다.")
                .goodNumber(1)
                .goodOrNot(false)
                .status(ReviewStatus.ACTIVE)
                .createdTime(LocalDateTime.now())
                .updatedTime(LocalDateTime.now())
                .reviewPhoto(reviewPhotos)
                .reviewGoods(reviewGoods)
                .build();
        //when & then
        assertThat(review.getReviewId()).isEqualTo(1L);
        assertThat(review.getPost()).isEqualTo(post);
        assertThat(review.getMember()).isEqualTo(member);
        assertThat(review.getContent()).isEqualTo("댓글 테스트입니다.");
        assertThat(review.getGoodNumber()).isEqualTo(1);
        assertThat(review.isGoodOrNot()).isEqualTo(false);
        assertThat(review.getStatus()).isEqualTo(ReviewStatus.ACTIVE);
        assertThat(review.getReviewPhoto()).isEqualTo(reviewPhotos);
        assertThat(review.getReviewGoods()).isEqualTo(reviewGoods);
    }

    @Test
    @DisplayName("리뷰 Setter 작동 성공 테스트")
    void successfullyReviewFiledSetterTest() {
        // given
        // FixtureMonkey를 사용하여 테스트 데이터 생성
        Review review = Review.builder()
                .reviewId(1L)
                .post(post)
                .member(member)
                .content("댓글 테스트입니다.")
                .goodNumber(1)
                .goodOrNot(false)
                .status(ReviewStatus.ACTIVE)
                .createdTime(LocalDateTime.now())
                .updatedTime(LocalDateTime.now())
                .reviewPhoto(reviewPhotos)
                .reviewGoods(reviewGoods)
                .build();

        //when
        review.setContent("댓글 수정");
        review.setGoodNumber(2);
        review.setGoodOrNot(true);

        //then
        then(review).extracting(Review::getReviewId).isEqualTo(1L);
        then(review).extracting(Review::getPost).isEqualTo(post);
        then(review).extracting(Review::getMember).isEqualTo(member);
        then(review).extracting(Review::getContent).isEqualTo("댓글 수정");
        then(review).extracting(Review::getGoodNumber).isEqualTo(2);
        then(review).extracting(Review::isGoodOrNot).isEqualTo(true);
        then(review).extracting(Review::getStatus).isEqualTo(ReviewStatus.ACTIVE);
        then(review).extracting(Review::getReviewPhoto).isEqualTo(reviewPhotos);
        then(review).extracting(Review::getReviewGoods).isEqualTo(reviewGoods);
    }
}
