package javaiscoffee.polaroad.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import javaiscoffee.polaroad.member.MemberInformationResponseDto;
import javaiscoffee.polaroad.post.PostInfoDto;
import javaiscoffee.polaroad.response.ResponseMessages;
import javaiscoffee.polaroad.review.ResponseGetReviewDto;
import javaiscoffee.polaroad.security.CustomUserDetails;
import javaiscoffee.polaroad.wrapper.RequestWrapperDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "관리자 API 모음", description = "관리자 멤버,포스트,리뷰 조회 및 수정 API 모음 - 담당자 박상현")
public class AdminController {
    private AdminService adminService;

    @Operation(summary = "사용자 조회", description = "관리자가 사용자 정보 조회 API")
    @Parameter(name = "memberId", description = "조회할 멤버 ID", required = true, example = "1")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "사용자 조회에 성공했을 경우"),
            @ApiResponse(responseCode = "403", description = "관리자가 아닌 경우"),
            @ApiResponse(responseCode = "404", description = "멤버나 관리자가 존재하지 않는 경우")
    })
    @GetMapping("/member/info/{memberId}")
    public ResponseEntity<MemberInformationResponseDto> getMemberInfo (@AuthenticationPrincipal CustomUserDetails userDetails,
                                                                       @RequestParam(name = "memberId") Long memberId) {
        return ResponseEntity.ok(adminService.getMemberInfo(userDetails.getMemberId(), memberId));
    }

    @Operation(summary = "사용자 상태 변경", description = "관리자가 사용자 상태 설정 API")
    @Parameter(name = "memberId", description = "상태 수정할 멤버 ID", required = true, example = "1")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "사용자 상태 변경에 성공했을 경우"),
            @ApiResponse(responseCode = "403", description = "관리자가 아닌 경우"),
            @ApiResponse(responseCode = "404", description = "멤버나 관리자가 존재하지 않는 경우")
    })
    @PatchMapping("/member/set/{memberId}")
    public ResponseEntity<String> setMemberStatus(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                  @RequestBody RequestWrapperDto<MemberStatusEditDto> wrapperDto,
                                                  @RequestParam(name = "memberId") Long memberId) {
        MemberStatusEditDto editDto = wrapperDto.getData();
        adminService.setMemberStatus(userDetails.getMemberId(), memberId, editDto.getStatus(), editDto.getReason());
        return ResponseEntity.ok(ResponseMessages.SUCCESS.getMessage());
    }

    @Operation(summary = "포스트 조회", description = "관리자가 포스트 정보 조회 API")
    @Parameter(name = "postId", description = "조회할 포스트 ID", required = true, example = "1")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "포스트 조회에 성공했을 경우"),
            @ApiResponse(responseCode = "403", description = "관리자가 아닌 경우"),
            @ApiResponse(responseCode = "404", description = "포스트나 관리자가 존재하지 않는 경우")
    })
    @GetMapping("/post/info/{postId}")
    public ResponseEntity<PostInfoDto> getPostInfo (@AuthenticationPrincipal CustomUserDetails userDetails,
                                                    @RequestParam(name = "postId") Long postId) {
        return ResponseEntity.ok(adminService.getPostInfoById(userDetails.getMemberId(), postId));
    }

    @Operation(summary = "포스트 상태 변경", description = "관리자가 포스트 상태 설정 API")
    @Parameter(name = "postId", description = "상태 수정할 포스트 ID", required = true, example = "1")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "포스트 상태 변경에 성공했을 경우"),
            @ApiResponse(responseCode = "403", description = "관리자가 아닌 경우"),
            @ApiResponse(responseCode = "404", description = "포스트나 관리자가 존재하지 않는 경우")
    })
    @PatchMapping("/post/set/{postId}")
    public ResponseEntity<String> setPostStatus(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                  @RequestBody RequestWrapperDto<PostStatusEditDto> wrapperDto,
                                                  @RequestParam(name = "postId") Long postId) {
        PostStatusEditDto editDto = wrapperDto.getData();
        adminService.setPostStatus(userDetails.getMemberId(), postId, editDto.getStatus(), editDto.getReason());
        return ResponseEntity.ok(ResponseMessages.SUCCESS.getMessage());
    }

    @Operation(summary = "리뷰 조회", description = "관리자가 리뷰 정보 조회 API")
    @Parameter(name = "reviewId", description = "조회할 리뷰 ID", required = true, example = "1")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "리뷰 조회에 성공했을 경우"),
            @ApiResponse(responseCode = "403", description = "관리자가 아닌 경우"),
            @ApiResponse(responseCode = "404", description = "리뷰나 관리자가 존재하지 않는 경우")
    })
    @GetMapping("/post/info/{reviewId}")
    public ResponseEntity<ResponseGetReviewDto> getReviewInfo (@AuthenticationPrincipal CustomUserDetails userDetails,
                                                               @RequestParam(name = "reviewId") Long reviewId) {
        return ResponseEntity.ok(adminService.getReviewById(userDetails.getMemberId(), reviewId));
    }

    @Operation(summary = "리뷰 상태 변경", description = "관리자가 리뷰 상태 설정 API")
    @Parameter(name = "reviewId", description = "상태 수정할 리뷰 ID", required = true, example = "1")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "리뷰 상태 변경에 성공했을 경우"),
            @ApiResponse(responseCode = "403", description = "관리자가 아닌 경우"),
            @ApiResponse(responseCode = "404", description = "리뷰나 관리자가 존재하지 않는 경우")
    })
    @PatchMapping("/review/set/{reviewId}")
    public ResponseEntity<String> setReviewStatus(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                @RequestBody RequestWrapperDto<ReviewStatusEditDto> wrapperDto,
                                                @RequestParam(name = "reviewId") Long reviewId) {
        ReviewStatusEditDto editDto = wrapperDto.getData();
        adminService.setReviewStatus(userDetails.getMemberId(), reviewId, editDto.getStatus(), editDto.getReason());
        return ResponseEntity.ok(ResponseMessages.SUCCESS.getMessage());
    }
}
