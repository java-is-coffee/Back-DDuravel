package javaiscoffee.polaroad.review;

import com.navercorp.fixturemonkey.FixtureMonkey;
import javaiscoffee.polaroad.login.LoginService;
import javaiscoffee.polaroad.login.RegisterDto;
import javaiscoffee.polaroad.member.JpaMemberRepository;
import javaiscoffee.polaroad.member.Member;
import javaiscoffee.polaroad.post.*;
import javaiscoffee.polaroad.review.reviewGood.ReviewGoodRepository;
import javaiscoffee.polaroad.review.reviewPhoto.JpaReviewPhotoRepository;
import javaiscoffee.polaroad.review.reviewPhoto.ReviewPhoto;
import javaiscoffee.polaroad.review.reviewPhoto.ReviewPhotoService;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

//NOTE: 레포지토리 사용하는 부분, 예외 처리 부분을 검증하는 것
// - 레포지토리의 값을 담는 변수에 내가 만든 가짜 객체를 넣어주는 것
@SpringBootTest(properties = {"JWT_SECRET_KEY=3123758a0d7ef02a46cba8bdd3f898dec8afc9f8470341af789d59f3695093be"})
public class ReviewServiceUnitTest {
     @InjectMocks
     private ReviewService reviewService;
     @Mock private LoginService loginService;
     @Mock
     private JpaReviewRepository reviewRepository;
     @Mock private JpaMemberRepository memberRepository;
     @Mock private PostRepository postRepository;
     @Mock private ReviewPhotoService reviewPhotoService;
     @Mock private JpaReviewPhotoRepository reviewPhotoRepository;
     @Mock private ReviewGoodRepository reviewGoodRepository;

     private FixtureMonkey fm = FixtureMonkey.create();

     @BeforeEach
     void setup() {
          RegisterDto registerDto = new RegisterDto();
          registerDto.setEmail("aaa@naver.com");
          registerDto.setName("박자바");
          registerDto.setNickname("자바커피");
          registerDto.setPassword("a123123!");
          loginService.register(registerDto);

//          Member member = Member.builder()
//                  .memberId(1L)
//                  .email("aaa@naver.com")
//                  .name("박자바")
//                  .nickname("자바커피")
//                  .password("a123123!")
//                  .profileImage("http://")
//                  .build();
//          memberRepository.save(member);
//          when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
//
//          Post post = new Post();
//          post.setTitle("꽃놀이 명당 추천");
//          post.setRoutePoint("좌표-좌표;좌표-좌표");
//          post.setThumbnailIndex(0);
//          post.setConcept(PostConcept.CITY);
//          post.setRegion(PostRegion.SEOUL);
//          postRepository.save(post);
//          when(postRepository.findById(1L)).thenReturn(Optional.of(post));
//
//          Review review = new Review();
//          review.setMember(member);
//          review.setPost(post);
//          review.setContent("리뷰입니다.");
//          when(reviewRepository.save(review)).thenReturn(review);
//          System.out.println("review = " + review);
//
//          ReviewPhoto reviewPhoto = ReviewPhoto.builder()
//                  .reviewPhotoId(1L)
//                  .image("http://1111")
//                  .review(review)
//                  .build();
//          when(reviewPhotoService.saveReviewPhoto(reviewPhoto.getImage(), review)).thenReturn(new ReviewPhoto());
     }

     @Test
     @DisplayName("리뷰 생성 성공 테스트")
     public void successToCreateReview() {
          Member member = Member.builder()
                  .memberId(1L)
                  .email("aaa@naver.com")
                  .name("박자바")
                  .nickname("자바커피")
                  .password("a123123!")
                  .profileImage("http://")
                  .build();
          memberRepository.save(member);
          when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

          Post post = new Post();
          post.setTitle("꽃놀이 명당 추천");
          post.setRoutePoint("좌표-좌표;좌표-좌표");
          post.setThumbnailIndex(0);
          post.setConcept(PostConcept.CITY);
          post.setRegion(PostRegion.SEOUL);
          postRepository.save(post);
          when(postRepository.findById(1L)).thenReturn(Optional.of(post));

          Review review = new Review();
          review.setMember(member);
          review.setPost(post);
          review.setContent("리뷰입니다.");
          when(reviewRepository.save(review)).thenReturn(review);
          System.out.println("review = " + review);

          ReviewPhoto reviewPhoto = ReviewPhoto.builder()
                  .reviewPhotoId(1L)
                  .image("http://1111")
                  .review(review)
                  .build();
          when(reviewPhotoService.saveReviewPhoto(reviewPhoto.getImage(), review)).thenReturn(reviewPhoto);
          System.out.println("reviewPhoto = " + reviewPhoto);

          ReviewDto reviewDto = fm.giveMeBuilder(ReviewDto.class)
                  .set("memberId", 1L)
                  .set("postId", 1L)
                  .sample();

          ResponseReviewDto responseReviewDto = reviewService.createReview(reviewDto, 1L, 1L);
          //NOTE: responseReviewDto null로 넘어오니까 값 넘어오게 할 것
          System.out.println("responseReviewDto = " + responseReviewDto);
          ResponseEntity<ResponseReviewDto> response = ResponseEntity.ok(responseReviewDto);
          Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
     }

     @Test
     @DisplayName("리뷰 생성 성공 테스트 - 리뷰 사진 없는 경우")
     public void successToCreateReviewWithoutReviewPhotos() {
          ReviewDto reviewDto = fm.giveMeBuilder(ReviewDto.class)
                  .set("memberId", 1L)
                  .set("postId", 1L)
                  .sample();
          ReviewPhoto reviewPhoto = ReviewPhoto.builder()
                  .image(null).build();

          ResponseReviewDto responseReviewDto = reviewService.createReview(reviewDto, 1L, 1L);
          ResponseEntity<ResponseReviewDto> response = ResponseEntity.ok(responseReviewDto);
          Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
     }
}

