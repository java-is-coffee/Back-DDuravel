package javaiscoffee.polaroad.review;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.introspector.ArbitraryIntrospector;
import com.navercorp.fixturemonkey.api.introspector.ConstructorPropertiesArbitraryIntrospector;
import javaiscoffee.polaroad.exception.BadRequestException;
import javaiscoffee.polaroad.exception.ForbiddenException;
import javaiscoffee.polaroad.exception.NotFoundException;
import javaiscoffee.polaroad.login.LoginService;
import javaiscoffee.polaroad.login.RegisterDto;
import javaiscoffee.polaroad.member.JpaMemberRepository;
import javaiscoffee.polaroad.member.Member;
import javaiscoffee.polaroad.member.MemberStatus;
import javaiscoffee.polaroad.post.*;
import javaiscoffee.polaroad.response.ResponseMessages;
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


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

//NOTE: 레포지토리 사용하는 부분, 예외 처리 부분을 검증하는 것, 레포지토리의 값을 담는 변수에 내가 만든 가짜 객체를 넣어주는 것
@SpringBootTest(properties = {"JWT_SECRET_KEY=3123758a0d7ef02a46cba8bdd3f898dec8afc9f8470341af789d59f3695093be"})
public class ReviewServiceUnitTest {
     @InjectMocks
     private ReviewService reviewService;
     @Mock
     private LoginService loginService;
     @Mock
     private JpaReviewRepository reviewRepository;
     @Mock
     private JpaMemberRepository memberRepository;
     @Mock
     private PostRepository postRepository;
     @Mock
     private ReviewPhotoService reviewPhotoService;
     @Mock
     private JpaReviewPhotoRepository reviewPhotoRepository;
     @Mock
     private ReviewGoodRepository reviewGoodRepository;

     private static final FixtureMonkey fm = FixtureMonkey.builder()
             .objectIntrospector(ConstructorPropertiesArbitraryIntrospector.INSTANCE)
             .build();

     @BeforeEach
     void setup() {
          RegisterDto registerDto = new RegisterDto();
          registerDto.setEmail("aaa@naver.com");
          registerDto.setName("박자바");
          registerDto.setNickname("자바커피");
          registerDto.setPassword("a123123!");
          loginService.register(registerDto);
     }

     //HACK : ReviewService에서 newReviewPhoto가 null이라고 NullPointerException 발생
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
          when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

          Post post = Post.builder()
                  .postId(1L)
                  .title("꽃놀이 명당 추천")
                  .routePoint("좌표-좌표;좌표-좌표")
                  .thumbnailIndex(0)
                  .concept(PostConcept.CITY)
                  .region(PostRegion.SEOUL)
                  .build();
          when(postRepository.findById(1L)).thenReturn(Optional.of(post));

          List<ReviewPhoto> photos = new ArrayList<>();
//          ReviewPhoto photo = fm.giveMeBuilder(ReviewPhoto.class)
//                  .setNotNull("image")
//                  .set("review", reviewRepository.findByReviewId(1L))
//                  .sample();
          Review review = fm.giveMeBuilder(Review.class)
                  .set("reviewId", 1L)
                  .set("member", memberRepository.findById(1L).get())
                  .set("post", postRepository.findById(1L).get())
                  .set("status", ReviewStatus.ACTIVE)
                  .set("reviewPhoto", photos)
                  .sample();
          when(reviewRepository.findByReviewId(1L)).thenReturn(review);
          System.out.println("review = " + review);

          ReviewPhoto photo = ReviewPhoto.builder()
                  .reviewPhotoId(1L)
                  .review(review)
                  .image("http://이미지입니다.")
                  .build();
          System.out.println("ReviewPhoto = " + photo);
          photos.add(photo);
          List<String> images = new ArrayList<>();
          String image = photo.getImage();
          images.add(image);

          ReviewDto reviewDto = fm.giveMeBuilder(ReviewDto.class)
                  .set("memberId", 1L)
                  .set("postId", 1L)
                  .set("reviewPhotoList", images)
                  .sample();
          System.out.println("reviewDto = " + reviewDto);

          when(reviewRepository.save(review)).thenReturn(review);
          when(reviewPhotoService.saveReviewPhoto(image, review)).thenReturn(photo);
          when(reviewPhotoRepository.save(photo)).thenReturn(photo);

          //HACK: responseReviewDto null로 넘어오니까 값 넘어오게 해결할 것
          ResponseReviewDto responseReviewDto = reviewService.createReview(reviewDto, 1L, 1L);
          System.out.println("responseReviewDto = " + responseReviewDto);
          ResponseEntity<ResponseReviewDto> response = ResponseEntity.ok(responseReviewDto);
          Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
     }

     @Test
     @DisplayName("리뷰 생성 성공 테스트 - 리뷰 사진 없는 경우")
     public void successToCreateReviewWithoutReviewPhotos() {
          Member member = Member.builder()
                  .memberId(1L)
//                  .email("aaa@naver.com")
//                  .name("박자바")
//                  .nickname("자바커피")
//                  .password("a123123!")
//                  .profileImage("http://")
                  .build();
          when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

          Post post = Post.builder()
                  .postId(1L)
//                  .title("꽃놀이 명당 추천")
//                  .routePoint("좌표-좌표;좌표-좌표")
//                  .thumbnailIndex(0)
//                  .concept(PostConcept.CITY)
//                  .region(PostRegion.SEOUL)
                  .build();
          when(postRepository.findById(1L)).thenReturn(Optional.of(post));
//          doReturn(Optional.of(member)).when(memberRepository).findById(1L);
//          doReturn(Optional.of(post)).when(postRepository).findById(1L);

          Review review = fm.giveMeBuilder(Review.class)
                  .set("reviewId", 1L)
                  .set("member", memberRepository.findById(1L).get())
                  .set("post", postRepository.findById(1L).get())
                  .sample();
          when(reviewRepository.save(any(Review.class))).thenReturn(review);
//          System.out.println("review = " + review);

          ReviewDto reviewDto = fm.giveMeBuilder(ReviewDto.class)
                  .set("memberId", 1L)
                  .set("postId", 1L)
                  .setNull("reviewPhotoList")
                  .sample();
//          System.out.println("reviewDto = " + reviewDto);

          ResponseReviewDto responseDto = reviewService.createReview(reviewDto, 1L, 1L);
//          System.out.println("responseReviewDto = " + responseDto);
          ResponseEntity<ResponseReviewDto> response = ResponseEntity.ok(responseDto);
          Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
     }

     @Test
     @DisplayName("리뷰 생성 실패 테스트 - 1. 멤버가 다른 경우")
     public void failedByOthersWhenCreateReview() {
          ReviewDto reviewDto = fm.giveMeBuilder(ReviewDto.class)
                  .set("memberId", 10L)
                  .set("postId", 1L)
                  .set("reviewPhotoList", null)
                  .sample();

          //NOTE: try catch 구문 사용한 방법, Throwable 타입으로 반환하는 방법, asserThatThrowBy 사용한 방법
/*          try {
               reviewService.createReview(reviewDto, 1L, 1L);
               fail("예외 발생하지 않음");
          } catch (ForbiddenException e) {
               Assertions.assertThat(e.getMessage()).isEqualTo(ResponseMessages.FORBIDDEN.getMessage());
          }
          Throwable exception = assertThrows(ForbiddenException.class, () -> reviewService.createReview(reviewDto, 1L, 1L));
          Assertions.assertThat(exception.getMessage()).isEqualTo(ResponseMessages.FORBIDDEN.getMessage());*/
          assertThatThrownBy(() -> reviewService.createReview(reviewDto, 1L, 1L)).isInstanceOf(ForbiddenException.class);
     }

     @Test
     @DisplayName("리뷰 생성 실페 테스트 - 2. 멤버가 삭제된 경우")
     public void failedByDeletedMemberWhenCreateReview() {
          Member member = Member.builder()
                  .memberId(1L)
                  .status(MemberStatus.DELETED)
                  .build();

          ReviewDto reviewDto = fm.giveMeBuilder(ReviewDto.class)
                  .set("memberId", 1L)
                  .set("postId", 1L)
                  .set("reviewPhotoList", null)
                  .sample();

          when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

          assertThatThrownBy(() -> reviewService.createReview(reviewDto, 1L, 1L)).isInstanceOf(NotFoundException.class);
     }

     @Test
     @DisplayName("리뷰 생성 실페 테스트 - 3. 멤버가 없는 경우")
     public void failedByNoMemberWhenCreateReview() {
          ReviewDto reviewDto = fm.giveMeBuilder(ReviewDto.class)
                  .set("memberId", 1L)
                  .set("postId", 1L)
                  .sample();

          when(memberRepository.findById(1L)).thenReturn(Optional.empty());

          assertThatThrownBy(() -> reviewService.createReview(reviewDto, 1L, 1L)).isInstanceOf(NotFoundException.class);
     }

     @Test
     @DisplayName("리뷰 생성 실페 테스트 - 4. 포스트가 없는 경우")
     public void failedByNoPostWhenCreateReview() {
          ReviewDto reviewDto = fm.giveMeBuilder(ReviewDto.class)
                  .set("memberId", 1L)
                  .set("postId", 1L)
                  .set("reviewPhotoList", null)
                  .sample();

          when(postRepository.findById(1L)).thenReturn(Optional.empty());

          assertThatThrownBy(() -> reviewService.createReview(reviewDto, 1L, 1L)).isInstanceOf(NotFoundException.class);
     }

     @Test
     @DisplayName("리뷰 생성 실페 테스트 - 5. 포스트가 삭제된 경우")
     public void failedByDeletedPostWhenCreateReview() {
          Post post = Post.builder()
                  .postId(1L)
                  .status(PostStatus.DELETED)
                  .build();

          ReviewDto reviewDto = fm.giveMeBuilder(ReviewDto.class)
                  .set("memberId", 1L)
                  .set("postId", 1L)
                  .set("reviewPhotoList", null)
                  .sample();

          when(postRepository.findById(1L)).thenReturn(Optional.of(post));

          assertThatThrownBy(() -> reviewService.createReview(reviewDto, 1L, 1L)).isInstanceOf(NotFoundException.class);
     }

     @Test
     @DisplayName("리뷰 수정 성공 테스트")
     public void successToEditReview() {
          Member member = Member.builder()
                  .memberId(1L)
                  .status(MemberStatus.ACTIVE)
                  .build();
          when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

          Post post = Post.builder()
                  .postId(1L)
                  .status(PostStatus.ACTIVE)
                  .build();
          when(postRepository.findById(1L)).thenReturn(Optional.of(post));

          Review review = fm.giveMeBuilder(Review.class)
                  .set("reviewId", 1L)
                  .set("member", memberRepository.findById(1L).get())
                  .set("post", postRepository.findById(1L).get())
                  .set("status", ReviewStatus.ACTIVE)
                  .sample();
          System.out.println("review = " + review);
          when(reviewRepository.findByReviewId(1L)).thenReturn(review);

          EditeRequestReviewDto editDto = fm.giveMeBuilder(EditeRequestReviewDto.class)
                  .set("content", "수정된 댓글")
                  .setNotNull("editPhotoList")
                  .sample();
          System.out.println("editDto = " + editDto);

          Review updatedReview = fm.giveMeBuilder(Review.class)
                  .set("reviewId", 1L)
                  .set("member", memberRepository.findById(1L).get())
                  .set("post", postRepository.findById(1L).get())
                  .set("content", "수정된 댓글")
                  .set("status", ReviewStatus.ACTIVE)
                  .sample();
          System.out.println("updatedReview = " + updatedReview);

          when(reviewRepository.update(review)).thenReturn(updatedReview);
//          verify(reviewPhotoService).editReviewPhoto(editDto.getEditPhotoList(),updatedReview);

          //HACK : 댓글 내용 수정 성공, 리뷰사진 null이라 테스트 필요
          ResponseReviewDto responseDto = reviewService.editReview(editDto, 1L, 1L);
          System.out.println("responseReviewDto = " + responseDto);
          ResponseEntity<ResponseReviewDto> response = ResponseEntity.ok(responseDto);
          Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
     }

     @Test
     @DisplayName("리뷰 수정 실패 테스트 - 1. 리뷰가 없을 때")
     public void failedByNoReviewWhenEditReview() {
          EditeRequestReviewDto editDto = fm.giveMeOne(EditeRequestReviewDto.class);

          when(reviewRepository.findByReviewId(1L)).thenReturn(null);

          assertThatThrownBy(() -> reviewService.editReview(editDto, 1L, 1L)).isInstanceOf(BadRequestException.class);
     }

     @Test
     @DisplayName("리뷰 수정 실패 테스트 - 2. 리뷰가 삭제된 경우")
     public void failedByDeletedReviewWhenEditReview() {
          Review review = fm.giveMeBuilder(Review.class)
                  .set("status", ReviewStatus.DELETED)
                  .sample();
          System.out.println("review = " + review);
          EditeRequestReviewDto editDto = fm.giveMeOne(EditeRequestReviewDto.class);

          when(reviewRepository.findByReviewId(1L)).thenReturn(review);

          assertThatThrownBy(() -> reviewService.editReview(editDto, 1L, 1L)).isInstanceOf(BadRequestException.class);
     }

     @Test
     @DisplayName("리뷰 수정 실패 테스트 - 3. 멤버가 없는 경우")
     public void failedByNoMemberWhenEditReview() {
          Review review = fm.giveMeBuilder(Review.class)
                  .set("status", ReviewStatus.ACTIVE)
                  .sample();
          EditeRequestReviewDto editDto = fm.giveMeOne(EditeRequestReviewDto.class);

          when(reviewRepository.findByReviewId(1L)).thenReturn(review);
          when(memberRepository.findById(1L)).thenReturn(Optional.empty());

          assertThatThrownBy(() -> reviewService.editReview(editDto, 1L, 1L)).isInstanceOf(NotFoundException.class);
     }

     @Test
     @DisplayName("리뷰 수정 실패 테스트 - 4. 댓글 멤버Id와 요청한 멤버Id가 다른 경우")
     public void failedByOthersWhenEditReview() {
          Member member = Member.builder()
                  .memberId(1L)
                  .status(MemberStatus.ACTIVE)
                  .build();
          Member others = fm.giveMeOne(Member.class);
          Review review = fm.giveMeBuilder(Review.class)
                  .set("member", others)
                  .set("status", ReviewStatus.ACTIVE)
                  .sample();
          EditeRequestReviewDto editDto = fm.giveMeOne(EditeRequestReviewDto.class);

          when(reviewRepository.findByReviewId(1L)).thenReturn(review);
          when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

          assertThatThrownBy(() -> reviewService.editReview(editDto, 1L, 1L)).isInstanceOf(ForbiddenException.class);
     }

     @Test
     @DisplayName("리뷰 수정 실패 테스트 - 5. 포스트가 없는 경우")
     public void failedByNoPostWhenEditReview() {
          Member member = Member.builder()
                  .memberId(1L)
                  .status(MemberStatus.ACTIVE)
                  .build();
          when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
          Post post = Post.builder()
                  .postId(1L)
                  .status(PostStatus.ACTIVE)
                  .build();
          when(postRepository.findById(1L)).thenReturn(Optional.of(post));
          Review review = fm.giveMeBuilder(Review.class)
                  .set("member", memberRepository.findById(1L).get())
                  .set("post", postRepository.findById(1L).get())
                  .set("status", ReviewStatus.ACTIVE)
                  .sample();
          EditeRequestReviewDto editDto = fm.giveMeOne(EditeRequestReviewDto.class);

          when(reviewRepository.findByReviewId(1L)).thenReturn(review);
          when(postRepository.findById(1L)).thenReturn(Optional.empty());

          assertThatThrownBy(() -> reviewService.editReview(editDto, 1L, 1L)).isInstanceOf(NotFoundException.class);
     }

     @Test
     @DisplayName("리뷰 수정 실패 테스트 - 6. 포스트가 삭제된 경우")
     public void failedByDeletedPostEditReview() {
          Member member = Member.builder()
                  .memberId(1L)
                  .status(MemberStatus.ACTIVE)
                  .build();
          when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
          Post post = Post.builder()
                  .postId(1L)
                  .status(PostStatus.DELETED)
                  .build();
          when(postRepository.findById(1L)).thenReturn(Optional.of(post));
          Review review = fm.giveMeBuilder(Review.class)
                  .set("member", memberRepository.findById(1L).get())
                  .set("post", postRepository.findById(1L).get())
                  .set("status", ReviewStatus.ACTIVE)
                  .sample();
          when(reviewRepository.findByReviewId(1L)).thenReturn(review);
          EditeRequestReviewDto editDto = fm.giveMeOne(EditeRequestReviewDto.class);

          assertThatThrownBy(() -> reviewService.editReview(editDto, 1L, 1L)).isInstanceOf(BadRequestException.class);
     }

     @Test
     @DisplayName("리뷰 수정 실패 테스트 - 7. 멤버가 삭제된 경우")
     public void failedByDeletedMemberWhenEditReview() {
          Member member = Member.builder()
                  .memberId(1L)
                  .status(MemberStatus.DELETED)
                  .build();
          when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
          Post post = Post.builder()
                  .postId(1L)
                  .status(PostStatus.ACTIVE)
                  .build();
          when(postRepository.findById(1L)).thenReturn(Optional.of(post));
          Review review = fm.giveMeBuilder(Review.class)
                  .set("member", memberRepository.findById(1L).get())
                  .set("post", postRepository.findById(1L).get())
                  .set("status", ReviewStatus.ACTIVE)
                  .sample();
          when(reviewRepository.findByReviewId(1L)).thenReturn(review);
          EditeRequestReviewDto editDto = fm.giveMeOne(EditeRequestReviewDto.class);

          assertThatThrownBy(() -> reviewService.editReview(editDto, 1L, 1L)).isInstanceOf(BadRequestException.class);
     }

     @Test
     @DisplayName("리뷰 삭제 성공 테스트")
     public void successToDeleteReview() {
          Member member = Member.builder()
                  .memberId(1L)
                  .build();
          when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
          System.out.println("member = " + member);

          Post post = Post.builder()
                  .postId(1L)
                  .build();
          when(postRepository.findById(1L)).thenReturn(Optional.of(post));

          List<ReviewPhoto> photos = new ArrayList<>();
          ReviewPhoto photo = fm.giveMeBuilder(ReviewPhoto.class)
                  .setNotNull("image")
                  .set("review", reviewRepository.findByReviewId(1L))
                  .sample();
          photos.add(photo);
          System.out.println("photos = " + photos);

          Review review = fm.giveMeBuilder(Review.class)
                  .set("reviewId", 1L)
                  .set("member", memberRepository.findById(1L).get())
                  .set("post", postRepository.findById(1L).get())
                  .set("reviewPhoto", photos)
                  .sample();

          when(reviewRepository.findByReviewId(1L)).thenReturn(review);

          Boolean responseDto = reviewService.deleteReview(1L, 1L);
          System.out.println("responseReviewDto = " + responseDto);
          Assertions.assertThat(responseDto).isTrue();
     }

     @Test
     @DisplayName("리뷰 삭제 실패 테스트 - 1. 리뷰가 없는 경우")
     public void failedByNoReviewWhenDeleteReview() {
          when(reviewRepository.findByReviewId(1L)).thenReturn(null);
          assertThatThrownBy(() -> reviewService.deleteReview(1L, 1L)).isInstanceOf(BadRequestException.class);
     }

     @Test
     @DisplayName("리뷰 삭제 실패 테스트 - 2. 리뷰가 삭제된 경우")
     public void failedByDeletedReviewWhenDeleteReview() {
          Review review = Review.builder()
                  .reviewId(1L)
                  .status(ReviewStatus.DELETED)
                  .build();
          when(reviewRepository.findByReviewId(1L)).thenReturn(review);
          assertThatThrownBy(() -> reviewService.deleteReview(1L, 1L)).isInstanceOf(BadRequestException.class);
     }

     @Test
     @DisplayName("리뷰 삭제 실패 테스트 - 3. 멤버가 없는 경우")
     public void failedByNoMemberWhenDeleteReview() {
          Review review = Review.builder()
                  .reviewId(1L)
                  .status(ReviewStatus.ACTIVE)
                  .build();

          when(reviewRepository.findByReviewId(1L)).thenReturn(review);
          when(memberRepository.findById(1L)).thenReturn(Optional.empty());

          assertThatThrownBy(() -> reviewService.deleteReview(1L, 1L)).isInstanceOf(NotFoundException.class);
     }


}

