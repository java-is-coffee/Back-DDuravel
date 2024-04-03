package javaiscoffee.polaroad.album;

import jakarta.persistence.PrePersist;
import javaiscoffee.polaroad.album.albumCard.AlbumCardInfoDto;
import javaiscoffee.polaroad.album.albumCard.RequestAlbumCardDto;
import javaiscoffee.polaroad.album.albumCard.SliceAlbumCardInfoDto;
import javaiscoffee.polaroad.exception.NotFoundException;
import javaiscoffee.polaroad.login.LoginService;
import javaiscoffee.polaroad.login.RegisterDto;
import javaiscoffee.polaroad.member.Member;
import javaiscoffee.polaroad.post.card.Card;
import javaiscoffee.polaroad.post.card.CardInfoDto;
import javaiscoffee.polaroad.response.ResponseMessages;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(properties = {"JWT_SECRET_KEY=3123755132fdfds4daas4551af789d59f36977df5093be12c2314515135ddasg1f5k12hdfhjk412bh531uiadfi14b14bwebs52"})
public class AlbumServiceTest {
    @Mock private LoginService loginService;
    @Mock private AlbumService albumService;

    @BeforeEach
    void setup() {
        RegisterDto registerDto = new RegisterDto();
        registerDto.setEmail("aaa@naver.com");
        registerDto.setName("박자바");
        registerDto.setNickname("자바커피");
        registerDto.setPassword("a123123!");
        loginService.register(registerDto);
    }

//    @Test
    @DisplayName("앨범 생성 성공 테스트 - 1. 앨범카드 있을 경우")
    public void successToCreateAlbum() {
        AlbumDto albumDto = new AlbumDto();
        albumDto.setName("부산 여행");
        albumDto.setDescription("부산 먹방 여행");
        albumDto.setCardIdList(Arrays.asList(1L,2L,3L));

        when(albumService.createAlbum(any(AlbumDto.class), any(Long.class)))
                .thenReturn(new ResponseAlbumDto());

        ResponseAlbumDto response = albumService.createAlbum(albumDto, 1L);
        ResponseEntity<ResponseAlbumDto> responseEntity = ResponseEntity.ok(response);
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

//    @Test
    @DisplayName("앨범 생성 성공 테스트 - 2. 앨범카드 없을 경우")
    public void successToCreateAlbumWithoutAlbumCard() {
        AlbumDto albumDto = new AlbumDto();
        albumDto.setName("부산 여행");
        albumDto.setDescription("부산 먹방 여행");

        when(albumService.createAlbum(any(AlbumDto.class), any(Long.class)))
                .thenReturn(new ResponseAlbumDto());

        ResponseAlbumDto response = albumService.createAlbum(albumDto, 1L);
        ResponseEntity<ResponseAlbumDto> responseEntity = ResponseEntity.ok(response);
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

//    @Test
    @DisplayName("앨범 생성 실패 테스트 - 1. 멤버가 없을 경우")
    public void failedByNoMemberWhenCreateAlbum() {
        AlbumDto albumDto = new AlbumDto();
        albumDto.setName("부산 여행");
        albumDto.setDescription("부산 먹방 여행");
        albumDto.setCardIdList(Arrays.asList(1L,2L,3L));

        when(albumService.createAlbum(any(AlbumDto.class), any(Long.class)))
                .thenThrow(new NotFoundException(ResponseMessages.NOT_FOUND.getMessage()));

        ResponseAlbumDto response = albumService.createAlbum(albumDto, 10L);
        ResponseEntity<ResponseAlbumDto> responseEntity = new ResponseEntity<>(response,HttpStatus.NOT_FOUND);
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

//    @Test
    @DisplayName("앨범 생성 실패 테스트 - 2.앨범 생성 요청한 멤버와 카드의 멤버가 다른 경우")
    public void failedByMemberIdNotEqualCardIdWhenCreateAlbum() {
        AlbumDto albumDto = new AlbumDto();
        albumDto.setName("부산 여행");
        albumDto.setDescription("부산 먹방 여행");
        albumDto.setCardIdList(Arrays.asList(1L,2L,3L));

        when(albumService.createAlbum(any(AlbumDto.class), any(Long.class)))
                .thenThrow(new NotFoundException(ResponseMessages.FORBIDDEN.getMessage()));

        ResponseAlbumDto response = albumService.createAlbum(albumDto, 10L);
        ResponseEntity<ResponseAlbumDto> responseEntity = new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

//    @Test
    @DisplayName("앨범 수정 성공 테스트")
    public void successToEditAlbum() {
        AlbumDto albumDto = new AlbumDto();
        albumDto.setName("부산 여행");
        albumDto.setDescription("부산 먹방 여행");
        albumDto.setCardIdList(Arrays.asList(1L,4L,5L));

        when(albumService.editAlbum(any(AlbumDto.class),any(Long.class), any(Long.class)))
                .thenReturn(new ResponseAlbumDto());

        ResponseAlbumDto response = albumService.editAlbum(albumDto, 1L,1L);
        ResponseEntity<ResponseAlbumDto> responseEntity = ResponseEntity.ok(response);
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

//    @Test
    @DisplayName("앨범 수정 실패 테스트 - 1. 앨범이 없을 때 ")
    public void failedByNotExitsAlbumWhenEditAlbum() {
        AlbumDto albumDto = new AlbumDto();
        albumDto.setName("부산 여행");
        albumDto.setDescription("부산 먹방 여행");
        albumDto.setCardIdList(Arrays.asList(1L,4L,5L));

        when(albumService.editAlbum(any(AlbumDto.class), any(Long.class), any(Long.class)))
                .thenThrow(new NotFoundException(ResponseMessages.NOT_FOUND.getMessage()));

        ResponseAlbumDto response = albumService.editAlbum(albumDto, 55L,1L);
        ResponseEntity<ResponseAlbumDto> responseEntity = new ResponseEntity<>(response,HttpStatus.NOT_FOUND);
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
//    @Test
    @DisplayName("앨범 수정 실패 테스트 - 2. 멤버가 없을 경우")
    public void failedByNoMemberWhenEditAlbumWhenEditAlbum() {
        AlbumDto albumDto = new AlbumDto();
        albumDto.setName("부산 여행");
        albumDto.setDescription("부산 먹방 여행");
        albumDto.setCardIdList(Arrays.asList(1L,2L,3L));

        when(albumService.editAlbum(any(AlbumDto.class), any(Long.class), any(Long.class)))
                .thenThrow(new NotFoundException(ResponseMessages.NOT_FOUND.getMessage()));

        ResponseAlbumDto response = albumService.editAlbum(albumDto, 1L,10L);
        ResponseEntity<ResponseAlbumDto> responseEntity = new ResponseEntity<>(response,HttpStatus.NOT_FOUND);
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

//    @Test
    @DisplayName("앨범 수정 실패 테스트 - 3. 수정 요청한 멤버가 앨범 생성자가 아닌 경우")
    public void failedByMemberIdNotEqualAlbumGetMemberIdWhenEditAlbum() {
        AlbumDto albumDto = new AlbumDto();
        albumDto.setName("부산 여행");
        albumDto.setDescription("부산 먹방 여행");
        albumDto.setCardIdList(Arrays.asList(1L,2L,3L));

        when(albumService.editAlbum(any(AlbumDto.class), any(Long.class), any(Long.class)))
                .thenThrow(new NotFoundException(ResponseMessages.FORBIDDEN.getMessage()));

        ResponseAlbumDto response = albumService.editAlbum(albumDto, 1L,10L);
        ResponseEntity<ResponseAlbumDto> responseEntity = new ResponseEntity<>(response,HttpStatus.FORBIDDEN);
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

//    @Test
    @DisplayName("앨범 삭제 성공 테스트")
    public void successToDeleteAlbum() {
//        AlbumDto albumDto = new AlbumDto();
//        albumDto.setName("부산 여행");
//        albumDto.setDescription("부산 먹방 여행");
//        albumDto.setCardIdList(Arrays.asList(1L,2L,3L));

        when(albumService.deleteAlbum(any(Long.class), any(Long.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        ResponseEntity<String> response = albumService.deleteAlbum(1L,1L);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

//    @Test
    @DisplayName("앨범 삭제 실패 테스트 - 1. 앨범이 없을 경우")
    public void failedByNotExitsAlbumWhenDeleteAlbum() {
//        AlbumDto albumDto = new AlbumDto();
//        albumDto.setName("부산 여행");
//        albumDto.setDescription("부산 먹방 여행");
//        albumDto.setCardIdList(Arrays.asList(1L,4L,5L));

        when(albumService.deleteAlbum(any(Long.class), any(Long.class)))
                .thenThrow(new NotFoundException(ResponseMessages.NOT_FOUND.getMessage()));

        ResponseEntity<String> response = albumService.deleteAlbum(10L,1L);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

//    @Test
    @DisplayName("앨범 삭제 실패 테스트 - 2. 멤버가 없을 경우")
    public void failedByNoMemberWhenEditAlbumWhenDeleteAlbum() {
//        AlbumDto albumDto = new AlbumDto();
//        albumDto.setName("부산 여행");
//        albumDto.setDescription("부산 먹방 여행");
//        albumDto.setCardIdList(Arrays.asList(1L,2L,3L));

        when(albumService.deleteAlbum(any(Long.class), any(Long.class)))
                .thenThrow(new NotFoundException(ResponseMessages.NOT_FOUND.getMessage()));

        ResponseEntity<String> response = albumService.deleteAlbum(1L,10L);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

//    @Test
    @DisplayName("앨범 삭제 실패 테스트 - 3. 삭제 요청한 멤버가 앨범 생성자가 아닌 경우")
    public void failedByMemberIdNotEqualAlbumGetMemberIdWhenDeleteAlbum() {
//        AlbumDto albumDto = new AlbumDto();
//        albumDto.setName("부산 여행");
//        albumDto.setDescription("부산 먹방 여행");
//        albumDto.setCardIdList(Arrays.asList(1L,2L,3L));

        when(albumService.deleteAlbum(any(Long.class), any(Long.class)))
                .thenThrow(new NotFoundException(ResponseMessages.FORBIDDEN.getMessage()));

        ResponseEntity<String> response = albumService.deleteAlbum(1L,10L);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

//    @Test
    @DisplayName("앨범카드 추가 성공 테스트")
    public void successToAddAlbumCard() {
        RequestAlbumCardDto requestAlbumCardDto = new RequestAlbumCardDto();
        requestAlbumCardDto.setCardId(Arrays.asList(4L,5L));

        when(albumService.addAlbumCard(any(RequestAlbumCardDto.class),any(Long.class), any(Long.class)))
                .thenReturn(new ResponseAlbumDto());

        ResponseAlbumDto response = albumService.addAlbumCard(requestAlbumCardDto, 1L,1L);
        ResponseEntity<ResponseAlbumDto> responseEntity = ResponseEntity.ok(response);
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

//    @Test
    @DisplayName("앨범카드 추가 실패 테스트 - 1. 앨범이 없는 경우")
    public void failedByNotExitsAlbumWhenAddAlbumCard() {
        RequestAlbumCardDto requestAlbumCardDto = new RequestAlbumCardDto();
        requestAlbumCardDto.setCardId(Arrays.asList(4L,5L));

        when(albumService.addAlbumCard(any(RequestAlbumCardDto.class),any(Long.class), any(Long.class)))
                .thenThrow(new NotFoundException(ResponseMessages.NOT_FOUND.getMessage()));

        ResponseAlbumDto response = albumService.addAlbumCard(requestAlbumCardDto, 10L,1L);
        ResponseEntity<ResponseAlbumDto> responseEntity = new ResponseEntity<>(response,HttpStatus.NOT_FOUND);
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

//    @Test
    @DisplayName("앨범카드 추가 실패 테스트 - 2. 멤버가 없는 경우")
    public void failedByNoMemberWhenAddAlbumCard() {
        RequestAlbumCardDto requestAlbumCardDto = new RequestAlbumCardDto();
        requestAlbumCardDto.setCardId(Arrays.asList(4L,5L));

        when(albumService.addAlbumCard(any(RequestAlbumCardDto.class),any(Long.class), any(Long.class)))
                .thenThrow(new NotFoundException(ResponseMessages.FORBIDDEN.getMessage()));

        ResponseAlbumDto response = albumService.addAlbumCard(requestAlbumCardDto, 1L,10L);
        ResponseEntity<ResponseAlbumDto> responseEntity = new ResponseEntity<>(response,HttpStatus.FORBIDDEN);
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

//    @Test
    @DisplayName("앨범카드 삭제 성공 테스트")
    public void successToDeleteAlbumCard() {
        RequestAlbumCardDto requestAlbumCardDto = new RequestAlbumCardDto();
        requestAlbumCardDto.setCardId(Arrays.asList(1L,2L));

        when(albumService.deleteAlbumCard(any(RequestAlbumCardDto.class),any(Long.class), any(Long.class)))
                .thenReturn(new ResponseAlbumDto());

        ResponseAlbumDto response = albumService.deleteAlbumCard(requestAlbumCardDto, 1L,1L);
        ResponseEntity<ResponseAlbumDto> responseEntity = ResponseEntity.ok(response);
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

//    @Test
    @DisplayName("앨범카드 삭제 실패 테스트 - 1. 앨범이 없는 경우")
    public void failedByNotExitsAlbumWhenDeleteAlbumCard() {
        RequestAlbumCardDto requestAlbumCardDto = new RequestAlbumCardDto();
        requestAlbumCardDto.setCardId(Arrays.asList(1L,2L));

        when(albumService.deleteAlbumCard(any(RequestAlbumCardDto.class),any(Long.class), any(Long.class)))
                .thenThrow(new NotFoundException(ResponseMessages.NOT_FOUND.getMessage()));

        ResponseAlbumDto response = albumService.deleteAlbumCard(requestAlbumCardDto, 1L,1L);
        ResponseEntity<ResponseAlbumDto> responseEntity = new ResponseEntity<>(response,HttpStatus.NOT_FOUND);
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

//    @Test
    @DisplayName("앨범카드 삭제 실패 테스트 - 2. 멤버가 없는 경우")
    public void failedByNoMemberWhenDeleteAlbumCard() {
        RequestAlbumCardDto requestAlbumCardDto = new RequestAlbumCardDto();
        requestAlbumCardDto.setCardId(Arrays.asList(1L,2L));

        when(albumService.deleteAlbumCard(any(RequestAlbumCardDto.class),any(Long.class), any(Long.class)))
                .thenThrow(new NotFoundException(ResponseMessages.FORBIDDEN.getMessage()));

        ResponseAlbumDto response = albumService.deleteAlbumCard(requestAlbumCardDto, 1L,10L);
        ResponseEntity<ResponseAlbumDto> responseEntity = new ResponseEntity<>(response,HttpStatus.FORBIDDEN);
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

//    @Test
//    @DisplayName("앨범 목록 페이징 성공 테스트")
//    public void successToPagedAlbumList() {
//        AlbumInfoDto albumInfoDto = new AlbumInfoDto();
//        albumInfoDto.setMemberId(1L);
//        albumInfoDto.setAlbumId(1L);
//        albumInfoDto.setName("앨범 이름");
//        albumInfoDto.setDescription("앨범 설명");
//        albumInfoDto.setUpdatedTime(LocalDateTime.now());
//        List<AlbumInfoDto> albumInfoList = new ArrayList<>();
//        albumInfoList.add(albumInfoDto);
//        albumInfoList.add(albumInfoDto);
//        albumInfoList.add(albumInfoDto);
//
//        when(albumService.getPagedAlbumList(any(Integer.class),any(Long.class)))
//                .thenReturn(new SliceAlbumListDto(albumInfoList,false));
//
//        SliceAlbumListDto<AlbumInfoDto> response = albumService.getPagedAlbumList(1, 1L);
//        ResponseEntity<SliceAlbumListDto<AlbumInfoDto>> responseEntity = ResponseEntity.ok(response);
//        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
//    }

//    @Test
    @DisplayName("앨범 목록 페이징 실패 테스트 - 1. 멤버가 없는 경우")
    public void failedByNoMemberWhenPagedAlbumList() {
//        AlbumInfoDto albumInfoDto = new AlbumInfoDto();
//        albumInfoDto.setMemberId(1L);
//        albumInfoDto.setAlbumId(1L);
//        albumInfoDto.setName("앨범 이름");
//        albumInfoDto.setDescription("앨범 설명");
//        albumInfoDto.setUpdatedTime(LocalDateTime.now());
//        List<AlbumInfoDto> albumInfoList = new ArrayList<>();
//        albumInfoList.addAll(Arrays.asList(albumInfoDto,albumInfoDto,albumInfoDto));

        when(albumService.getPagedAlbumList(any(Integer.class), any(Long.class)))
                .thenThrow(new NotFoundException(ResponseMessages.NOT_FOUND.getMessage()));

        SliceAlbumListDto<AlbumInfoDto> response = albumService.getPagedAlbumList(1,10L);
        ResponseEntity<SliceAlbumListDto<AlbumInfoDto>> responseEntity = new ResponseEntity<>(response,HttpStatus.NOT_FOUND);
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

//    @Test
//    @DisplayName("앨범 내용 페이징 성공 테스트")
//    public void successToPagedAlbumCardList() {
//        CardInfoDto cardInfoDto = new CardInfoDto();
//        cardInfoDto.setCardId(1L);
//        cardInfoDto.setCardIndex(1);
//        cardInfoDto.setContent("카드카드카드");
//        cardInfoDto.setImage("http:abcd");
//        cardInfoDto.setLocation("서울특별시");
//        cardInfoDto.setLatitude(123.12345);
//        cardInfoDto.setLongitude(123.12345);
//        AlbumCardInfoDto albumCardInfoDto = new AlbumCardInfoDto();
//        albumCardInfoDto.setCardInfo(cardInfoDto);
//        List<AlbumCardInfoDto> albumCardInfoList = new ArrayList<>();
//        albumCardInfoList.add(albumCardInfoDto);
//
//        when(albumService.getPagedAlbumCardList(any(Long.class),any(Long.class),any(Integer.class)))
//                .thenReturn(new SliceAlbumCardInfoDto(1L, albumCardInfoList,false));
//
//        SliceAlbumCardInfoDto<AlbumCardInfoDto> response = albumService.getPagedAlbumCardList(1L, 1L,1);
//        ResponseEntity<SliceAlbumCardInfoDto<AlbumCardInfoDto>> responseEntity = ResponseEntity.ok(response);
//        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
//    }

//    @Test
    @DisplayName("앨범 내용 페이징 실패 테스트 - 1. 멤버가 없는 경우")
    public void failedByNoMemberWhenPagedAlbumCardList() {
//        CardInfoDto cardInfoDto = new CardInfoDto();
//        cardInfoDto.setCardId(1L);
//        cardInfoDto.setCardIndex(1);
//        cardInfoDto.setContent("카드카드카드");
//        cardInfoDto.setImage("http:abcd");
//        cardInfoDto.setLocation("서울특별시");
//        cardInfoDto.setLatitude(123.12345);
//        cardInfoDto.setLongitude(123.12345);
//        AlbumCardInfoDto albumCardInfoDto = new AlbumCardInfoDto();
//        albumCardInfoDto.setCardInfo(cardInfoDto);
//        List<AlbumCardInfoDto> albumCardInfoList = new ArrayList<>();
//        albumCardInfoList.add(albumCardInfoDto);

        when(albumService.getPagedAlbumCardList(any(Long.class),any(Long.class),any(Integer.class)))
                .thenThrow(new NotFoundException(ResponseMessages.NOT_FOUND.getMessage()));

        SliceAlbumCardInfoDto<AlbumCardInfoDto> response = albumService.getPagedAlbumCardList(10L, 1L,1);
        ResponseEntity<SliceAlbumCardInfoDto<AlbumCardInfoDto>> responseEntity =  new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

//    @Test
    @DisplayName("앨범 내용 페이징 실패 테스트 - 2. 앨범이 없는 경우")
    public void failedByNotExitsAlbumWhenPagedAlbumCardList() {
//        AlbumInfoDto albumInfoDto = new AlbumInfoDto();
//        albumInfoDto.setMemberId(1L);
//        albumInfoDto.setAlbumId(1L);
//        albumInfoDto.setName("앨범 이름");
//        albumInfoDto.setDescription("앨범 설명");
//        albumInfoDto.setUpdatedTime(LocalDateTime.now());
//        List<AlbumInfoDto> albumInfoList = new ArrayList<>();
//        albumInfoList.addAll(Arrays.asList(albumInfoDto,albumInfoDto,albumInfoDto));

        when(albumService.getPagedAlbumCardList(any(Long.class),any(Long.class),any(Integer.class)))
                .thenThrow(new NotFoundException(ResponseMessages.NOT_FOUND.getMessage()));

        SliceAlbumCardInfoDto<AlbumCardInfoDto> response = albumService.getPagedAlbumCardList(1L, 1L,1);
        ResponseEntity<SliceAlbumCardInfoDto<AlbumCardInfoDto>> responseEntity =  new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
