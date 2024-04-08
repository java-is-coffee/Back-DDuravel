package javaiscoffee.polaroad.post;

import javaiscoffee.polaroad.config.JpaConfigTest;
import javaiscoffee.polaroad.member.JpaMemberRepository;
import javaiscoffee.polaroad.member.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(JpaConfigTest.class)
class PostRepositoryUnitTest {
    @Autowired
    private JpaMemberRepository memberRepository;
    @Autowired
    private PostRepository postRepository;

    private Long savedPostId1;
    private Long savedPostId2;

    @BeforeEach
    void setUp() {
        Member member = Member.builder()
                .name("박자바")
                .nickname("자바커피")
                .email("aaa@naver.com")
                .password("a123123!")
                .build();
        member.hashPassword(new BCryptPasswordEncoder());
        memberRepository.save(member);
        member = memberRepository.findByEmail("aaa@naver.com").get();

        Post post1 = Post.builder()
                .title("제목")
                .member(member)
                .routePoint("좌표-좌표")
                .thumbnailIndex(0)
                .concept(PostConcept.FOOD)
                .region(PostRegion.BUSAN)
                .build();
        Post post2 = Post.builder()
                .title("제목2")
                .member(member)
                .routePoint("좌표2-좌표2")
                .thumbnailIndex(0)
                .concept(PostConcept.FOOD)
                .region(PostRegion.BUSAN)
                .build();
        Post savedPost1 = postRepository.save(post1);
        Post savedPost2 = postRepository.save(post2);

        this.savedPostId1 = savedPost1.getPostId();
        this.savedPostId2 = savedPost2.getPostId();
    }

    @Test
    @DisplayName("포스트 저장")
    void savePost() {
        // Given
        Post savedPost = postRepository.findById(1L).get();

        // When&Then
        assertThat(savedPost.getPostId()).isEqualTo(1l);
        assertThat(savedPost.getTitle()).isEqualTo("제목");
        assertThat(savedPost.getRoutePoint()).isEqualTo("좌표-좌표");
        assertThat(savedPost.getGoodNumber()).isEqualTo(0);
        assertThat(savedPost.getReviewNumber()).isEqualTo(0);
        assertThat(savedPost.getThumbnailIndex()).isEqualTo(0);
        assertThat(savedPost.getConcept()).isEqualTo(PostConcept.FOOD);
        assertThat(savedPost.getRegion()).isEqualTo(PostRegion.BUSAN);
        assertThat(savedPost.getStatus()).isEqualTo(PostStatus.ACTIVE);
        assertThat(savedPost.getCards().size()).isEqualTo(0);
        assertThat(savedPost.getPostHashtags().size()).isEqualTo(0);
        assertThat(savedPost.getReviews().size()).isEqualTo(0);
        assertThat(savedPost.getPostGoods().size()).isEqualTo(0);
        assertThat(savedPost.getWishListPosts().size()).isEqualTo(0);
    }

    @Test
    @DisplayName("포스트 수정")
    void updatePost() {
        // Given
        Post post = postRepository.findById(this.savedPostId1).get();

        // When
        post.setTitle("수정된제목");
        post.setRoutePoint("수정된좌표");
        post.setGoodNumber(1);
        post.setReviewNumber(1);
        post.setThumbnailIndex(0);
        post.setConcept(PostConcept.CITY);
        post.setRegion(PostRegion.CHUNGCHEONGBUKDO);
        postRepository.flush();
        Post updatedPost = postRepository.findById(this.savedPostId1).get();

        //Then
        assertThat(updatedPost.getTitle()).isEqualTo("수정된제목");
        assertThat(updatedPost.getRoutePoint()).isEqualTo("수정된좌표");
        assertThat(updatedPost.getGoodNumber()).isEqualTo(1);
        assertThat(updatedPost.getReviewNumber()).isEqualTo(1);
        assertThat(updatedPost.getThumbnailIndex()).isEqualTo(0);
        assertThat(updatedPost.getConcept()).isEqualTo(PostConcept.CITY);
        assertThat(updatedPost.getRegion()).isEqualTo(PostRegion.CHUNGCHEONGBUKDO);
    }

    @Test
    @DisplayName("포스트 목록 반환 체크")
    void getPostList() {
        // Given
        List<Post> posts = postRepository.findPostByEmail("aaa@naver.com");

        // When&Then
        assertThat(posts.size()).isEqualTo(2);
    }

}