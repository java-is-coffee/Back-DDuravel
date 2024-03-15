package javaiscoffee.polaroad.review;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import javaiscoffee.polaroad.exception.NotFoundException;
import javaiscoffee.polaroad.response.ResponseMessages;
import javaiscoffee.polaroad.response.Status;
import javaiscoffee.polaroad.security.CustomUserDetails;
import javaiscoffee.polaroad.wrapper.RequestWrapperDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/review")
@Tag(name = "댓글 관련 API", description = "댓글에 관련된 API 모음")
public class ReviewController {
    private final ReviewService reviewService;

    @Operation(summary = "댓글 작성 API", description = "댓글 작성할 때 사용하는 API")
    @Parameter(name = "postId", description = "## 포스트 Id", required = true, example = "1")
    @Parameter(name = "memberId", description = "## 맴버 Id", required = true, example = "1")
    @Parameter(name = "content", description = "## 댓글 본문", required = true, example = "저도 다녀왔는데 너무 좋았어요.")
    @Parameter(name = "reviewPhotoList", description = "## 댓글 사진 url들", required = true, example = "https://lh5.googleusercontent.com/p/AF1QipM1QxKKnGOYaD3DadUkr3fJrxTquvyGP2eRhjR2=w1080-h624-n-k-no")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "댓글 작성을 성공한 경우"),
            @ApiResponse(responseCode = "400", description = "댓글 작성을 실패한 경우")
    })
    @PostMapping("/write/{postId}")
    public ResponseEntity<?> writeReview(@RequestBody RequestWrapperDto<ReviewDto> requestDto, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long memberId = userDetails.getMemberId();
        ReviewDto reviewDto = requestDto.getData();
        log.info("입력 받은 댓글 정보 = {}", reviewDto);
        ResponseReviewDto savedReview = reviewService.createReview(reviewDto, memberId);
        // 댓글 = null 에러 반환
        if (savedReview == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Status(ResponseMessages.INPUT_ERROR));
        } else {
            return ResponseEntity.ok(savedReview);
        }
    }

    @Operation(summary = "댓글 조회 API", description = "댓글 조회할 때 사용하는 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "댓글 조회 성공한 경우"),
            @ApiResponse(responseCode = "404", description = "댓글 조회 실패한 경우")
    })
    @GetMapping("/{reviewId}")
    public ResponseEntity<?> getReviewById(@PathVariable(name = "reviewId") Long reviewId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("댓글 조회 요청");
        Long memberId = userDetails.getMemberId();
        ResponseReviewDto findedReview = reviewService.getReviewById(reviewId, memberId);
        if (findedReview == null) throw new NotFoundException(ResponseMessages.NOT_FOUND.getMessage());
        return ResponseEntity.ok(findedReview);
    }

    @Operation(summary = "댓글 수정 API", description = "댓글 수정할 때 사용하는 API")
    @Parameter(name = "content", description = "## 댓글 본문", required = true, example = "와 저도 가보고 싶어지네요.")
    @Parameter(name = "reviewPhotoList", description = "## 댓글 사진 url들", required = true, example = "\"https://lh5.googleusercontent.com/p/AF1QipM1QxKKnGOYaD3DadUkr3fJrxTquvyGP2eRhjR2=w1080-h624-n-k-no\",\n" +
            "            \"https://lh5.googleusercontent.com/p/AF1QipOAkhVKrq3broFnCMCx4sdqm45jxANDfoC2k3bi=w1080-h624-n-k-no\"")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "댓글 수정 성공한 경우"),
            @ApiResponse(responseCode = "400", description = "댓글 수정 실패한 경우")
    })
    @PatchMapping("/edit/{reviewId}")
    public ResponseEntity<?> editReview(@RequestBody RequestWrapperDto<ReviewEditRequestDto> requestDto, @PathVariable(name = "reviewId") Long reviewId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long memberId = userDetails.getMemberId();
        ReviewEditRequestDto editReviewDto = requestDto.getData();
        log.info("수정된 댓글 정보 = {}", editReviewDto);
        log.info("수정된 댓글의 댓글 ID = {}", reviewId);
        ResponseReviewDto editedReview = reviewService.editReview(editReviewDto, reviewId, memberId);
        if (editedReview == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Status(ResponseMessages.INPUT_ERROR));
        } else {
            return ResponseEntity.ok(editedReview);
        }
    }

    //권한이 없는 경우, 댓글이 없는 경우 나누기
    @Operation(summary = "댓글 삭제 API", description = "댓글 삭제할 때 사용하는 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "댓글 삭제 성공한 경우"),
            @ApiResponse(responseCode = "400", description = "댓글 삭제 실패한 경우")
    })
    @DeleteMapping("/delete/{reviewId}")
    public ResponseEntity<?> deleteReview(@PathVariable(name = "reviewId") Long reviewId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long memberId = userDetails.getMemberId();
        Boolean deletedReview = reviewService.deleteReview(reviewId, memberId);
        if (!deletedReview) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Status(ResponseMessages.DELETE_FAILED));
        }
        return ResponseEntity.ok(null);
    }

    // 신고
//    @PostMapping("/report/{reviewId}")
//    public ResponseEntity<?> reportReview(@PathVariable Long reviewId, @AuthenticationPrincipal CustomUserDetails userDetails) {
//        Long memberId = userDetails.getMemberId();
//    }

    @Operation(summary = "postId로 댓글 조회 API", description = "포스트로 댓글 조회할 때 사용하는 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "postId로 댓글 조회에 성공한 경우"),
            @ApiResponse(responseCode = "404", description = "postId로 댓글 조회에 실패한 경우")
    })
    @GetMapping("/post/{postId}")
    public ResponseEntity<?> getReviewByPostId(@PathVariable Long postId) {
        if (postId == null) throw new NotFoundException(ResponseMessages.NOT_FOUND.getMessage());
        return ResponseEntity.ok(reviewService.getReviewByPostId(postId));
    }

    @Operation(summary = "memberId로 댓글 조회 API", description = "맴버로 댓글 조회할 때 사용하는 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "memberId로 댓글 조회에 성공한 경우"),
            @ApiResponse(responseCode = "404", description = "memberId로 댓글 조회에 실패한 경우")
    })
    @GetMapping("/member/{memberId}")
    public ResponseEntity<?> getReviewByMemberId(@PathVariable Long memberId) {
        if (memberId == null) throw new NotFoundException(ResponseMessages.NOT_FOUND.getMessage());
        return ResponseEntity.ok(reviewService.getReviewByPostId(memberId));
    }
}
