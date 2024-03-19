package javaiscoffee.polaroad.post.wishlist;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import javaiscoffee.polaroad.response.ResponseMessages;
import javaiscoffee.polaroad.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/wishlist")
@Tag(name = "위시리스트 관련 API", description = "위시리스트 관련 API 모음 - 담당자 박상현")
@RequiredArgsConstructor
public class WishListController {
    private final WishListService wishListService;

    @Operation(summary = "위시리스트 생성", description = "위시리스트 생성하는 API")
    @Parameter(name = "wishListName", description = "생성하려는 위시리스트 이름", required = true, example = "한강공원 모음집")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "위시리스트 생성에 성공했을 경우"),
            @ApiResponse(responseCode = "400", description = "입력값이 잘못된 경우"),
            @ApiResponse(responseCode = "404", description = "멤버가 존재하지 않는 경우")
    })
    @PostMapping("/create")
    public ResponseEntity<String> createWishList(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestParam(name = "wishListName") String wishListName) {
        wishListService.createWishList(userDetails.getMemberId(), wishListName);
        return ResponseEntity.ok(ResponseMessages.SUCCESS.getMessage());
    }

    @Operation(summary = "위시리스트 이름 변경", description = "위시리스트 수정하는 API")
    @Parameter(name = "wishListId", description = "수정하려는 위시리스트 ID", required = true, example = "1")
    @Parameter(name = "wishListName", description = "새로 바꾸려는 위시리스트 이름", required = true, example = "한강공원 모음집")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "위시리스트 이름 변경에 성공했을 경우"),
            @ApiResponse(responseCode = "400", description = "입력값이 잘못된 경우"),
            @ApiResponse(responseCode = "403", description = "자기 위시리스트가 아닌 경우"),
            @ApiResponse(responseCode = "404", description = "멤버나 위시리스트가 존재하지 않는 경우")
    })
    @PatchMapping("/edit/{wishListId}")
    public ResponseEntity<String> editWishList(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable(name = "wishListId") Long wishListId ,@RequestParam(name = "wishListName") String wishListName) {
        wishListService.renameWishList(userDetails.getMemberId(), wishListId, wishListName);
        return ResponseEntity.ok(ResponseMessages.SUCCESS.getMessage());
    }

    @Operation(summary = "위시리스트 삭제", description = "위시리스트 삭제하는 API \n 위시리스트에 있는 포스트들도 함께 삭제")
    @Parameter(name = "wishListId", description = "삭제하려는 위시리스트 ID", required = true, example = "1")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "위시리스트 삭제에 성공했을 경우"),
            @ApiResponse(responseCode = "400", description = "입력값이 잘못된 경우"),
            @ApiResponse(responseCode = "403", description = "자기 위시리스트가 아닌 경우"),
            @ApiResponse(responseCode = "404", description = "멤버나 위시리스트가 존재하지 않는 경우")
    })
    @DeleteMapping("/delete/{wishListId}")
    public ResponseEntity<String> deleteWishList( @AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable(name = "wishListId") Long wishListId) {
        wishListService.deleteWishListWithPost(userDetails.getMemberId(), wishListId);
        return ResponseEntity.ok(ResponseMessages.SUCCESS.getMessage());
    }

    @Operation(summary = "위시리스트에 포스트 추가", description = "위시리스트에 해당 포스트를 추가하는 API \n ## 포스트가 다른 위시리스트에 들어있으면 삭제 후 목표 위시리스트에 추가")
    @Parameter(name = "wishListId", description = "포스트를 추가하려는 위시리스트 ID", required = true, example = "1")
    @Parameter(name = "postId", description = "위시리스트에 추가하려는 포스트 ID", required = true, example = "1")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "위시리스트에 포스트 추가를 성공했을 경우"),
            @ApiResponse(responseCode = "400", description = "입력값이 잘못된 경우"),
            @ApiResponse(responseCode = "403", description = "자기 위시리스트가 아닌 경우"),
            @ApiResponse(responseCode = "404", description = "멤버나 위시리스트, 포스트가 존재하지 않는 경우")
    })
    @PostMapping("/add/{wishListId}/{postId}")
    public ResponseEntity<String> addPostToWishList(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable(name = "wishListId") Long wishListId, @PathVariable(name = "postId") Long postId) {
        wishListService.addPostToWishList(userDetails.getMemberId(), wishListId, postId);
        return ResponseEntity.ok(ResponseMessages.SUCCESS.getMessage());
    }

    @Operation(summary = "위시리스트에서 포스트 삭제", description = "위시리스트에 해당 포스트를 삭제하는 API")
    @Parameter(name = "wishListId", description = "포스트를 삭제하려는 위시리스트 ID", required = true, example = "1")
    @Parameter(name = "postId", description = "위시리스트에 삭제하려는 포스트 ID", required = true, example = "1")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "위시리스트에 포스트 삭제를 성공했을 경우"),
            @ApiResponse(responseCode = "400", description = "입력값이 잘못된 경우"),
            @ApiResponse(responseCode = "403", description = "자기 위시리스트가 아닌 경우"),
            @ApiResponse(responseCode = "404", description = "멤버나 위시리스트, 포스트가 존재하지 않는 경우")
    })
    @DeleteMapping("/delete/{wishListId}/{postId}")
    public ResponseEntity<String> deletePostFromWishList(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable(name = "wishListId") Long wishListId, @PathVariable(name = "postId") Long postId) {
        wishListService.deletePostFromWishList(userDetails.getMemberId(), wishListId, postId);
        return ResponseEntity.ok(ResponseMessages.SUCCESS.getMessage());
    }

    @Operation(summary = "사용자의 위시리스트 목록 조회", description = "사용자의 모든 위시리스트를 리스트로 조회하는 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "위시리스트에 포스트 삭제를 성공했을 경우"),
            @ApiResponse(responseCode = "404", description = "멤버가 존재하지 않는 경우")
    })
    @GetMapping("/list")
    public ResponseEntity<List<WishListDto>> getWishLists(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(wishListService.getWishLists(userDetails.getMemberId()));
    }

    @Operation(summary = "포스트 추가할 때 보여줄 위시리스트 목록 및 포함여부 조회", description = "사용자가 포스트를 위시리스트에 추가하기 위해 요청했을 때 사용자한테 보여줄 위시리스트 전체 목록 \n + 현재 포스트가 각각의 위시리스트에 포함되었는지 여부를 목록으로 조회하는 API")
    @Parameter(name = "postId", description = "위시리스트에 포함되어 있는지 확인하려는 포스트 ID", required = true, example = "1")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "위시리스트에 포스트 삭제를 성공했을 경우"),
            @ApiResponse(responseCode = "404", description = "멤버나 포스트가 존재하지 않는 경우")
    })
    @GetMapping("/add-list/{postId}")
    public ResponseEntity<List<WishListAddListDto>> getWishListsWithIncluded(@AuthenticationPrincipal CustomUserDetails userDetails,@PathVariable(name = "postId") Long postId) {
        return ResponseEntity.ok(wishListService.getWishListsWithIncluded(userDetails.getMemberId(), postId));
    }

    @Operation(summary = "위시리스트에 들어있는 포스트 목록 조회", description = "위시리스트에 있는 포스트 전체 목록 조회하는 API \n ## 현재 본인의 위시리스트 내용만 볼 수 있게 설정")
    @Parameter(name = "wishListId", description = "포스트 목록 조회할 위시리스트 ID", required = true, example = "1")
    @Parameter(name = "paging", description = "조회할 페이지 번호 \n ### 0부터 시작합니다.", required = true, example = "0")
    @Parameter(name = "pagingNumber", description = "한 번에 조회할 포스트 개수", required = true, example = "8")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "위시리스트에 포스트 삭제를 성공했을 경우"),
            @ApiResponse(responseCode = "403", description = "남의 위시리스트 조회하려고 할 경우"),
            @ApiResponse(responseCode = "404", description = "멤버나 위시리스트가 존재하지 않는 경우")
    })
    @GetMapping("/content/{wishListId}")
    public ResponseEntity<WishListPostListResponseDto> getWishListPostsInWishList(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                                            @PathVariable(name = "wishListId") Long wishListId,
                                                                            @RequestParam(name = "paging") int paging,
                                                                            @RequestParam(name = "pagingNumber") int pagingNumber) {
        return ResponseEntity.ok(wishListService.getWishListPostsInWishList(userDetails.getMemberId(), wishListId, paging, pagingNumber));
    }


}
