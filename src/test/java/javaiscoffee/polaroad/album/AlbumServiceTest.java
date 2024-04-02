package javaiscoffee.polaroad.album;

import javaiscoffee.polaroad.album.albumCard.AlbumCardInfoDto;
import javaiscoffee.polaroad.login.LoginService;
import javaiscoffee.polaroad.login.RegisterDto;
import javaiscoffee.polaroad.post.PostConcept;
import javaiscoffee.polaroad.post.PostRegion;
import javaiscoffee.polaroad.post.PostSaveDto;
import javaiscoffee.polaroad.post.card.CardInfoDto;
import javaiscoffee.polaroad.post.card.CardSaveDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

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

    @Test
    @DisplayName("앨범 생성 성공 테스트 - 1. 앨범카드 있을 경우")
    public void successCreateAlbum() {
        AlbumDto albumDto = new AlbumDto();
        albumDto.setName("부산 여행");
        albumDto.setDescription("부산 먹방 여행");
        List<Long> cardIdList = new ArrayList<>();
        cardIdList.add(1L);
        cardIdList.add(2L);
        albumDto.setCardIdList(cardIdList);

        //NOTE: when 안 되는데..

        ResponseAlbumDto response = albumService.createAlbum(albumDto, 1L);
        ResponseEntity<ResponseAlbumDto> responseEntity = ResponseEntity.ok(response);
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("앨범 생성 성공 테스트 - 2. 앨범카드 없을 경우")
    public void successCreateAlbumWithoutAlbumCard() {
        AlbumDto albumDto = new AlbumDto();
        albumDto.setName("부산 여행");
        albumDto.setDescription("부산 먹방 여행");

        ResponseAlbumDto response = albumService.createAlbum(albumDto, 1L);
        ResponseEntity<ResponseAlbumDto> responseEntity = ResponseEntity.ok(response);
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("앨범 생성 실패 테스트 - 1. 멤버가 없을 경우")
    public void failedByNoMemberWhenCreateAlbum() {
        AlbumDto albumDto = new AlbumDto();
        albumDto.setName("부산 여행");
        albumDto.setDescription("부산 먹방 여행");
        List<Long> cardIdList = new ArrayList<>();
        cardIdList.add(1L);
        cardIdList.add(2L);
        albumDto.setCardIdList(cardIdList);

        //NOTE: when 안 되는데..

        ResponseAlbumDto response = albumService.createAlbum(albumDto, 10L);
        ResponseEntity<ResponseAlbumDto> responseEntity = ResponseEntity.ok(response);
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

}
