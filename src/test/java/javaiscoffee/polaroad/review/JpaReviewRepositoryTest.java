package javaiscoffee.polaroad.review;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.introspector.ConstructorPropertiesArbitraryIntrospector;
import javaiscoffee.polaroad.config.JpaConfigTest;
import javaiscoffee.polaroad.member.JpaMemberRepository;
import javaiscoffee.polaroad.member.Member;
import javaiscoffee.polaroad.post.Post;
import javaiscoffee.polaroad.post.PostConcept;
import javaiscoffee.polaroad.post.PostRegion;
import javaiscoffee.polaroad.post.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(JpaConfigTest.class)

public class JpaReviewRepositoryTest {
    @Autowired
    private JpaMemberRepository memberRepository;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private PostRepository postRepository;

    private Long savedReviewId1;
    private Long savedReviewId2;

    @BeforeEach
    void setup() {
        Member member = Member.builder()
                .name("박자바")
                .nickname("자바커피")
                .email("aaa@naver.com")
                .password("a123123!")
                .build();
        member.hashPassword(new BCryptPasswordEncoder());
        memberRepository.save(member);
        member = memberRepository.findByEmail("aaa@naver.com").get();

        Post post = Post.builder()
                .title("제목")
                .member(member)
                .routePoint("좌표-좌표")
                .thumbnailIndex(0)
                .concept(PostConcept.FOOD)
                .region(PostRegion.BUSAN)
                .build();
        postRepository.save(post);

        Review review1 = Review.builder()
                .post(post)
                .member(member)
                .content("리뷰1")
                .build();

        Review review2 = Review.builder()
                .post(post)
                .member(member)
                .content("리뷰2")
                .build();
        Review savedReview1 = reviewRepository.save(review1);
        Review savedReview2 = reviewRepository.save(review2);

        this.savedReviewId1 = savedReview1.getReviewId();
        this.savedReviewId2 = savedReview2.getReviewId();
    }

    @Test
    @DisplayName("리뷰 저장")
    void saveReview() {
        Review savedReview = reviewRepository.findByReviewId(1L);

        assertThat(savedReview.getReviewId()).isEqualTo(1L);
        assertThat(savedReview.getContent()).isEqualTo("리뷰1");
        assertThat(savedReview.getGoodNumber()).isEqualTo(0);
        assertThat(savedReview.getStatus()).isEqualTo(ReviewStatus.ACTIVE);
        assertThat(savedReview.getReviewPhoto().size()).isEqualTo(0);
        assertThat(savedReview.getReviewGoods().size()).isEqualTo(0);
    }

    @Test
    @DisplayName("리뷰 수정")
    void updateReview() {
        Review review = reviewRepository.findByReviewId(this.savedReviewId1);

        review.setContent("수정한 리뷰");
        review.setGoodNumber(1);

        assertThat(review.getReviewId()).isEqualTo(1L);
        assertThat(review.getContent()).isEqualTo("수정한 리뷰");
        assertThat(review.getGoodNumber()).isEqualTo(1);
        assertThat(review.getStatus()).isEqualTo(ReviewStatus.ACTIVE);
        assertThat(review.getReviewPhoto().size()).isEqualTo(0);
        assertThat(review.getReviewGoods().size()).isEqualTo(0);
    }

}
