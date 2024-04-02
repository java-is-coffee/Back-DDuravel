package javaiscoffee.polaroad.album;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import javaiscoffee.polaroad.album.albumCard.AlbumCardInfoDto;
import javaiscoffee.polaroad.album.albumCard.RequestAlbumCardDto;
import javaiscoffee.polaroad.album.albumCard.SliceAlbumCardInfoDto;
import javaiscoffee.polaroad.exception.BadRequestException;
import javaiscoffee.polaroad.response.ResponseMessages;
import javaiscoffee.polaroad.security.CustomUserDetails;
import javaiscoffee.polaroad.wrapper.RequestWrapperDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/album")
@Tag(name = "앨범 관련 API", description = "앨범에 관련된 API 모음 - 담당자 문경미")
public class AlbumController {
    private final AlbumService albumService;


    @Operation(summary = "앨범 생성 API", description = "앨범 생성할 때 사용하는 API \n 앨범 카드 리스트는 nullable")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "입력값이 잘못된 경우"),
            @ApiResponse(responseCode = "403", description = "권한이 없는 경우"),
            @ApiResponse(responseCode = "404", description = "멤버가 존재하지 않거나 삭제된 경우 경우")
    })
    @PostMapping("/create")
    public ResponseEntity<ResponseAlbumDto> createAlbum(@RequestBody RequestWrapperDto<AlbumDto> requestWrapperDto, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long memberId = userDetails.getMemberId();
        AlbumDto albumDto = requestWrapperDto.getData();
        log.info("앨범 생성 요청 = {}", albumDto);
        ResponseAlbumDto responseAlbumDto = albumService.createAlbum(albumDto, memberId);
        if (responseAlbumDto == null) {
            throw new BadRequestException(ResponseMessages.INPUT_ERROR.getMessage());
        }
        return ResponseEntity.ok(responseAlbumDto);
    }

    @Operation(summary = "앨범 1개 조회 API", description = "앨범 1개 조회할 때 사용하는 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "403", description = "권한이 없는 경우"),
            @ApiResponse(responseCode = "404", description = "앨범이나 멤버가 존재하지 않거나 삭제된 경우 경우")
    })
    @GetMapping("/{albumId}")
    public ResponseEntity<ResponseAlbumDto> getAlbum(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable(name = "albumId") Long albumId) {
        log.info("댓글 조회 요청");
        Long memberId = userDetails.getMemberId();
        ResponseAlbumDto responseAlbumDto = albumService.getAlbum(memberId, albumId);
        return ResponseEntity.ok(responseAlbumDto);
    }


    @Operation(summary = "앨범 수정 API", description = "앨범 수정할 때 사용하는 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "입력값이 잘못된 경우"),
            @ApiResponse(responseCode = "403", description = "권한이 없는 경우"),
            @ApiResponse(responseCode = "404", description = "앨범이 없거나 멤버가 존재하지 않거나 삭제된 경우 경우")
    })
    @PatchMapping("/edit/{albumId}")
    public ResponseEntity<ResponseAlbumDto> editAlbum(@RequestBody RequestWrapperDto<AlbumDto> requestWrapperDto, @AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable(name = "albumId") Long albumId) {
        Long memberId = userDetails.getMemberId();
        AlbumDto editAlbumDto = requestWrapperDto.getData();
        log.info("앨범 수정 요청 = {}", editAlbumDto);
        ResponseAlbumDto responseAlbumDto = albumService.editAlbum(editAlbumDto, albumId, memberId);
        if (responseAlbumDto == null) {
            throw new BadRequestException(ResponseMessages.INPUT_ERROR.getMessage());
        }
        return ResponseEntity.ok(responseAlbumDto);
    }

    @Operation(summary = "앨범 삭제 API", description = "앨범 삭제할 때 사용하는 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "403", description = "권한이 없는 경우"),
            @ApiResponse(responseCode = "404", description = "앨범이 없거나 멤버가 존재하지 않거나 삭제된 경우 경우")
    })
    @DeleteMapping("/delete/{albumId}")
    public ResponseEntity<String> deleteAlbum(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable(name = "albumId") Long albumId) {
        log.info("앨범 삭제 요청");
        Long memberId = userDetails.getMemberId();
        return albumService.deleteAlbum(memberId, albumId);
    }


    @Operation(summary = "앨범 카드 추가 API", description = "앨범에 앨범 카드 추가할 때 사용하는 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "입력값이 잘못된 경우"),
            @ApiResponse(responseCode = "403", description = "권한이 없는 경우"),
            @ApiResponse(responseCode = "404", description = "앨범이 없거나 멤버가 존재하지 않거나 삭제된 경우 경우")
    })
    @PostMapping("/add-card/{albumId}")
    public ResponseEntity<ResponseAlbumDto> addCardToAlbum(@RequestBody RequestWrapperDto<RequestAlbumCardDto> requestWrapperDto, @AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable(name = "albumId") Long albumId) {
        Long memberId = userDetails.getMemberId();
        RequestAlbumCardDto addAlbumCardDto = requestWrapperDto.getData();
        log.info("앨범 사진 추가 요청 = {}", addAlbumCardDto);
        ResponseAlbumDto responseAlbumDto = albumService.addAlbumCard(addAlbumCardDto, albumId, memberId);
        if (responseAlbumDto == null) {
            throw new BadRequestException(ResponseMessages.INPUT_ERROR.getMessage());
        }
        return ResponseEntity.ok(responseAlbumDto);
    }

    @Operation(summary = "앨범 카드 삭제 API", description = "앨범에서 앨범 카드를 삭제할 때 사용하는 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "403", description = "권한이 없는 경우"),
            @ApiResponse(responseCode = "404", description = "앨범이 없거나 멤버가 존재하지 않거나 삭제된 경우 경우")
    })
    @DeleteMapping("/delete-card/{albumId}")
    public ResponseEntity<ResponseAlbumDto> deleteCardToAlbum(@RequestBody RequestWrapperDto<RequestAlbumCardDto> requestWrapperDto, @AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable(name = "albumId") Long albumId) {
        Long memberId = userDetails.getMemberId();
        RequestAlbumCardDto deleteAlbumCardDto = requestWrapperDto.getData();
        log.info("앨범 사진 삭제 요청 = {}", deleteAlbumCardDto);
        ResponseAlbumDto responseAlbumDto = albumService.deleteAlbumCard(deleteAlbumCardDto, albumId, memberId);
        return ResponseEntity.ok(responseAlbumDto);
    }

    //HACK: 앨범 목록 조회시 각 앨범의 앨범카드 개수도 보낼지
    @Operation(summary = "앨범 목록 조회 API", description = "페이징 처리 된 앨범 목록을 조회할 때 사용하는 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
//            @ApiResponse(responseCode = "403", description = "권한이 없는 경우"),
            @ApiResponse(responseCode = "404", description = "멤버가 존재하지 않거나 삭제된 경우 경우")
    })
    @GetMapping("/list/paging")
    public ResponseEntity<SliceAlbumListDto<AlbumInfoDto>> getAlbumList(
            @Parameter(name = "page", description = "## 앨범 페이지 번호", required = true, example = "1") @RequestParam int page,
            @AuthenticationPrincipal CustomUserDetails userDetails)
    {
        log.info("앨범 목록 조회 요청");
        Long memberId = userDetails.getMemberId();
        SliceAlbumListDto<AlbumInfoDto> albumPage = albumService.getPagedAlbumList(page, memberId);
        return ResponseEntity.ok(albumPage);
    }

    @Operation(summary = "앨범 내용 조회 API", description = "페이징 처리 된 앨범 내용을 조회할 때 사용하는 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "403", description = "권한이 없는 경우"),
            @ApiResponse(responseCode = "404", description = "앨범이 없거나 멤버가 존재하지 않거나 삭제된 경우 경우")
    })
    @GetMapping("/{albumId}/content/paging")
    public ResponseEntity<SliceAlbumCardInfoDto<AlbumCardInfoDto>> getAlbumCardList(
            @Parameter(name = "page", description = "## 앨범 페이지 번호", required = true, example = "1") @RequestParam int page,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable(name = "albumId") Long albumId)
    {
        log.info("앨범 내용 조회 요청");
        Long memberId = userDetails.getMemberId();
        SliceAlbumCardInfoDto<AlbumCardInfoDto> albumCardPage = albumService.getPagedAlbumCardList(memberId, albumId, page);
        return ResponseEntity.ok(albumCardPage);
    }
}
