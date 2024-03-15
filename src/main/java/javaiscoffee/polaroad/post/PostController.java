package javaiscoffee.polaroad.post;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import javaiscoffee.polaroad.security.CustomUserDetails;
import javaiscoffee.polaroad.wrapper.RequestWrapperDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
@Tag(name = "포스트 관련 API",description = "포스트, 카드, 해쉬태그 관련 API들 모음")
public class PostController {
    private final PostService postService;

    @Operation(summary = "포스트 생성", description = "포스트 생성하는 API")
    @Parameter(name = "title", description = "게시글 제목",
            required = true, example = "벛꽃 놀이 재밌다.")
    @Parameter(name = "routePoint", description = "전체 경로 직렬화 좌표", required = true, example = "프론트에서 알아서 정하기")
    @Parameter(name = "thumbnailIndex", description = "썸네일 인덱스", required = true, example = "0부터 사진 총 개수 -1까지")
    @Parameter(name = "concept", required = true, description = "게시글 카테고리 \n null값이면 나머지 조건으로 검색 \n- FOOD 식도락\n" +
            "- NATURE 자연\n" +
            "- CITY 도시관광\n" +
            "- PHOTO 사진&명소\n" +
            "- HOT 인기게시글\n" +
            "- WALK 도보여행\n" +
            "- CAR 자동차&대중교통 여행\n" +
            "- TRAIN 기차여행", example = "FOOD")
    @Parameter(name = "region", required = true, description = "게시글 지역 \n null값이면 나머지 조건으로 검색 \n" +
            "- SEOUL 서울, INCHEON 인천, BUSAN 부산, DAEGU 대구, \n" +
            "- GWANGJU 광주, DAEJEON 대전, ULSAN 울산, GYEONGGIDO 경기도, GANGWONDO 강원도,\n " +
            "- CHUNGCHEONGNAMDO 충남, CHUNGCHEONGBUKDO 충북,\n " +
            "- JEOLLANAMDO 전남, JEOLLABUKDO 전북,\n " +
            "- GYEONGSANGNAMDO 경남, GYEONGSANGBUKDO 경북,\n " +
            "- JEJUDO 제주도", example = "SEOUL")
    @Parameter(name = "cards", required = true,
            description = "게시글 카드 목록 \n ### cardId랑 index는 없어도 됩니다. 자동 생성되기 때문 \n- private String location 사진 세부 위치" +
                    "\n- private String latitude 위도" +
                    "\n- private String longtitude 경도" +
                    "\n- private String image 이미지 주소" +
                    "\n- private String content 카드 본문", example = "{\n" +
            "        \"location\": \"string\",\n" +
            "        \"latitude\": \"string\",\n" +
            "        \"longtitude\": \"string\",\n" +
            "        \"image\": \"string\",\n" +
            "        \"content\": \"string\"\n" +
            "      }")
    @Parameter(name = "hashtags", description = "게시글 해쉬태그 목록",required = false, example = "벚꽃, 소풍, 축제")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "포스트 생성에 성공했을 경우"),
            @ApiResponse(responseCode = "400", description = "입력값이 잘못된 경우"),
            @ApiResponse(responseCode = "404", description = "멤버가 존재하지 않는 경우")
    })
    @PostMapping("/write")
    public ResponseEntity<Post> savePost(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody RequestWrapperDto<PostSaveDto> requestWrapperDto) {
        Long memberId = userDetails.getMemberId();
        PostSaveDto postSaveDto = requestWrapperDto.getData();
        log.info("저장하려는 포스트 Dto = {}",postSaveDto);
        return postService.savePost(postSaveDto, memberId);
    }

    @Operation(summary = "포스트 수정", description = "포스트 수정하는 API")
    @Parameter(name = "title", description = "게시글 제목",
            required = true, example = "누군가 수정한 게시글 제목이다.")
    @Parameter(name = "routePoint", description = "전체 경로 직렬화 좌표", required = true, example = "프론트에서 알아서 정하기")
    @Parameter(name = "thumbnailIndex", description = "썸네일 인덱스", required = true, example = "0부터 사진 총 개수 -1까지")
    @Parameter(name = "concept", description = "게시글 카테고리 \n null값이면 나머지 조건으로 검색 \n- FOOD 식도락\n" +
            "- NATURE 자연\n" +
            "- CITY 도시관광\n" +
            "- PHOTO 사진&명소\n" +
            "- HOT 인기게시글\n" +
            "- WALK 도보여행\n" +
            "- CAR 자동차&대중교통 여행\n" +
            "- TRAIN 기차여행", example = "FOOD")
    @Parameter(name = "region", description = "게시글 지역 \n null값이면 나머지 조건으로 검색 \n" +
            "- SEOUL 서울, INCHEON 인천, BUSAN 부산, DAEGU 대구, \n" +
            "- GWANGJU 광주, DAEJEON 대전, ULSAN 울산, GYEONGGIDO 경기도, GANGWONDO 강원도,\n " +
            "- CHUNGCHEONGNAMDO 충남, CHUNGCHEONGBUKDO 충북,\n " +
            "- JEOLLANAMDO 전남, JEOLLABUKDO 전북,\n " +
            "- GYEONGSANGNAMDO 경남, GYEONGSANGBUKDO 경북,\n " +
            "- JEJUDO 제주도", example = "SEOUL")
    @Parameter(name = "cards", description = "게시글 카드 목록 \n ### 기존에 있던 카드는 cardId 있어야 합니다. \n ### 새로 추가한 카드는 cardId = null로 주시면 됩니다. \n ### index는 없어도 됩니다.", required = true,
            example = "\n- private Long cardId = 기존 카드 ID 또는 추가된 카드는 null" +
                    "\n- private String location 사진 세부 위치" +
                    "\n- private String latitude 위도" +
                    "\n- private String longtitude 경도" +
                    "\n- private String image 이미지 주소" +
                    "\n- private String content 카드 본문")
    @Parameter(name = "hashtags", description = "게시글 해쉬태그 목록",required = false, example = "벚꽃, 소풍, 축제")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "포스트 수정에 성공했을 경우"),
            @ApiResponse(responseCode = "403", description = "생성자 본인이 아닌 경우"),
            @ApiResponse(responseCode = "404", description = "멤버가 존재하지 않는 경우")
    })
    @PatchMapping("/edit/{postId}")
    public ResponseEntity<Post> editPost(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody RequestWrapperDto<PostSaveDto> requestWrapperDto, @PathVariable(name = "postId") Long postId) {
        Long memberId = userDetails.getMemberId();
        PostSaveDto postSaveDto = requestWrapperDto.getData();
        log.info("수정하려는 포스트 Dto = {}",postSaveDto);
        return postService.editPost(postSaveDto, memberId, postId);
    }

    @Operation(summary = "포스트 삭제", description = "포스트 삭제하는 API")
    @Parameter(name = "postId", description = "Path Variable로 삭제할 포스트 ID를 넘겨주면 됩니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "포스트 삭제에 성공했을 경우"),
            @ApiResponse(responseCode = "403", description = "생성자 본인이 아닌 경우"),
            @ApiResponse(responseCode = "404", description = "멤버가 존재하지 않는 경우")
    })
    @DeleteMapping("/delete/{postId}")
    public ResponseEntity<String> deletePost(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable(name = "postId") Long postId) {
        Long memberId = userDetails.getMemberId();
        return postService.deletePost(postId, memberId);
    }

    @Operation(summary = "포스트 검색", description = "키워드, 해쉬태그 검색하는 API")
    @Parameter(name = "paging", description = "0부터 시작합니다. 몇 번째 페이지를 출력할 것인지",required = true, example = "0")
    @Parameter(name = "pagingNumber", description = "한 페이지에 몇 개의 결과를 표시할 것인지 정하는 수치", required = true, example = "8")
    @Parameter(name = "searchType", description = "검색 방식을 나타냅니다. \n - KEYWORD = 키워드검색 \n - HASHTAG = 해쉬태그검색", example = "KEYWORD")
    @Parameter(name = "keyword", description = "검색할 키워드나 해쉬태그 \n null값이면 나머지 조건으로 검색", example = "자바")
    @Parameter(name = "sortBy", description = "검색 결과를 정렬할 방식 \n - GOOD = 하트순 \n - RECENT = 최신순",  required = true, example = "RECENT")
    @Parameter(name = "concept", description = "게시글 카테고리 \n null값이면 나머지 조건으로 검색 \n- FOOD 식도락\n" +
            "- NATURE 자연\n" +
            "- CITY 도시관광\n" +
            "- PHOTO 사진&명소\n" +
            "- HOT 인기게시글\n" +
            "- WALK 도보여행\n" +
            "- CAR 자동차&대중교통 여행\n" +
            "- TRAIN 기차여행", example = "FOOD")
    @Parameter(name = "region", description = "게시글 지역 \n null값이면 나머지 조건으로 검색 \n" +
            "- SEOUL 서울, INCHEON 인천, BUSAN 부산, DAEGU 대구, \n" +
            "- GWANGJU 광주, DAEJEON 대전, ULSAN 울산, GYEONGGIDO 경기도, GANGWONDO 강원도,\n " +
            "- CHUNGCHEONGNAMDO 충남, CHUNGCHEONGBUKDO 충북,\n " +
            "- JEOLLANAMDO 전남, JEOLLABUKDO 전북,\n " +
            "- GYEONGSANGNAMDO 경남, GYEONGSANGBUKDO 경북,\n " +
            "- JEJUDO 제주도", example = "SEOUL")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "리스트 조회에 성공한 경우")
    })
    @GetMapping("/list")
    public ResponseEntity<List<PostListDto>> getPostList(@RequestParam(name = "paging") int paging,
                                                         @RequestParam(name = "pagingNumber") int pagingNumber,
                                                         @RequestParam(name = "searchType") PostSearchType searchType,
                                                         @RequestParam(name = "keyword",required = false) String keyword,
                                                         @RequestParam(name = "sortBy") PostListSort sortBy,
                                                         @RequestParam(name = "concept", required = false) PostConcept concept,
                                                         @RequestParam(name = "region", required = false) PostRegion region) {
        return postService.getPostList(paging,pagingNumber,searchType,keyword,sortBy,concept,region);
    }

    @Operation(summary = "포스트 내용 조회", description = "포스트 내용 조회 페이지에서 사용하는 API")
    @Parameter(name = "postId", description = "내용 조회할 포스트 ID",required = true, example = "1")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "포스트 조회에 성공했을 경우"),
            @ApiResponse(responseCode = "404", description = "포스트가 삭제되었거나 존재하지 않는 경우")
    })
    @GetMapping("/content/{postId}")
    public ResponseEntity<PostInfoDto> getPostInfo(@PathVariable(name = "postId") Long postId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        return postService.getPostInfoById(postId, userDetails.getMemberId());
    }

    @PatchMapping("/good/{postId}")
    public ResponseEntity<?> postGoodToggle(@PathVariable(name = "postId") Long postId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long memberId = userDetails.getMemberId();
        postService.postGoodToggle(memberId, postId);
        return ResponseEntity.ok(null);
    }

}
