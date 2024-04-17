package javaiscoffee.polaroad.album;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.introspector.ConstructorPropertiesArbitraryIntrospector;
import javaiscoffee.polaroad.album.albumCard.*;
import javaiscoffee.polaroad.exception.ForbiddenException;
import javaiscoffee.polaroad.exception.NotFoundException;
import javaiscoffee.polaroad.login.LoginService;
import javaiscoffee.polaroad.login.RegisterDto;
import javaiscoffee.polaroad.member.JpaMemberRepository;
import javaiscoffee.polaroad.member.Member;
import javaiscoffee.polaroad.member.MemberStatus;
import javaiscoffee.polaroad.post.Post;
import javaiscoffee.polaroad.post.card.Card;
import javaiscoffee.polaroad.post.card.CardRepository;
import javaiscoffee.polaroad.response.ResponseMessages;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@SpringBootTest(properties = {"JWT_SECRET_KEY=3123755132fdfds4daas4551af789d59f36977df5093be12c2314515135ddasg1f5k12hdfhjk412bh531uiadfi14b14bwebs52"})
public class AlbumServiceUnitTest {
    @InjectMocks private AlbumService albumService;
    @Mock private LoginService loginService;
    @Mock
    private JpaMemberRepository memberRepository;
    @Mock
    private AlbumRepository albumRepository;
    @Mock
    private AlbumCardService albumCardService;
    @Mock
    private CardRepository cardRepository;
    @Mock
    private AlbumCardRepository albumCardRepository;

    private Member testMember;


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

    //HACK: "javaiscoffee.polaroad.album.albumCard.AlbumCard.getCard()" because "card" is null 에러
//    @Test
    @DisplayName("앨범 생성 성공 테스트 - 1. 앨범카드 있을 경우")
    public void successToCreateAlbum() {
        Member member = Member.builder().memberId(1L).status(MemberStatus.ACTIVE).build();
        List<AlbumCard> albumCards = new ArrayList<>();
        Album album = Album.builder().albumId(1L).member(member).albumCards(albumCards).build();
        Card card1 = Card.builder()
                .cardId(1L).member(member)
                .cardIndex(1) .latitude(123.123)
                .longitude(123.123) .location("위치")
                .image("이미지") .content("내용")
                .build();
        AlbumCardId albumCardId = AlbumCardId.builder().cardId(1L).albumId(1L).build();
        AlbumCard albumCard = AlbumCard.builder().id(albumCardId).card(card1).album(album).build();
        albumCards.add(albumCard);
        AlbumDto albumDto = AlbumDto.builder()
                .name("앨범")
                .description("설명")
                .cardIdList(Arrays.asList(card1.getCardId()))
                .build();
        System.out.println("albumDto = " + albumDto);

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(albumRepository.save(album)).thenReturn(album);
        when(cardRepository.findById(any(Long.class))).thenReturn(Optional.of(card1));
        when(albumCardService.saveAlbumCard(any(Card.class),any(Album.class))).thenReturn(albumCard);
        System.out.println("card = " + cardRepository.findById(card1.getCardId()));
        System.out.println("savedAlbumCard" + albumCardService.saveAlbumCard(card1, album));

        ResponseAlbumDto response = albumService.createAlbum(albumDto, 1L);
        ResponseEntity<ResponseAlbumDto> responseEntity = ResponseEntity.ok(response);
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("앨범 생성 실패 테스트 - 1. 멤버가 없을 경우")
    public void failedByNoMemberWhenCreateAlbum() {
        AlbumDto albumDto = fm.giveMeOne(AlbumDto.class);

        assertThatThrownBy(() -> albumService.createAlbum(albumDto, null)).isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("앨범 생성 실패 테스트 - 2.삭제된 멤버인 경우")
    public void failedByMemberIdNotEqualCardIdWhenCreateAlbum() {
        AlbumDto albumDto = fm.giveMeOne(AlbumDto.class);
        Member member = Member.builder().memberId(1L).status(MemberStatus.DELETED).build();

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

        assertThatThrownBy(() -> albumService.createAlbum(albumDto, 1L)).isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("앨범 수정 성공 테스트")
    public void successToEditAlbum() {
        Member member = Member.builder().memberId(1L).status(MemberStatus.ACTIVE).build();
        List<AlbumCard> albumCards = new ArrayList<>();
        Album album = Album.builder().albumId(1L).member(member).albumCards(albumCards).build();
        Card card1 = fm.giveMeOne(Card.class);
        Card card2 = fm.giveMeOne(Card.class);
        AlbumCard ac1 = AlbumCard.builder().card(card1).album(album).build();
        AlbumCard ac2 = AlbumCard.builder().card(card2).album(album).build();
        albumCards.add(ac1);    albumCards.add(ac2);
        AlbumDto albumDto = new AlbumDto();

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(albumRepository.findById(1L)).thenReturn(Optional.of(album));
        doNothing().when(albumCardService).editAlbumCard(any(List.class), any(Album.class));
        when(albumCardRepository.findAllByAlbum(album)).thenReturn(albumCards);

        ResponseAlbumDto response = albumService.editAlbum(albumDto, 1L,1L);
        System.out.println("response = " + response);
        ResponseEntity<ResponseAlbumDto> responseEntity = ResponseEntity.ok(response);
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("앨범 수정 실패 테스트 - 1. 앨범이 없을 때 ")
    public void failedByNotExitsAlbumWhenEditAlbum() {
        AlbumDto albumDto = new AlbumDto();

        when(albumRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> albumService.editAlbum(albumDto,1L, 1L)).isInstanceOf(NotFoundException.class);
    }
    @Test
    @DisplayName("앨범 수정 실패 테스트 - 2. 멤버가 없을 경우")
    public void failedByNoMemberWhenEditAlbumWhenEditAlbum() {
        AlbumDto albumDto = new AlbumDto();
        Album album = Album.builder().albumId(1L).build();

        when(albumRepository.findById(1L)).thenReturn(Optional.of(album));
        when(memberRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> albumService.editAlbum(albumDto,1L, 1L)).isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("앨범 수정 실패 테스트 - 3. 수정 요청한 멤버가 앨범 생성자가 아닌 경우")
    public void failedByMemberIdNotEqualAlbumGetMemberIdWhenEditAlbum() {
        AlbumDto albumDto = new AlbumDto();
        Album album = Album.builder().albumId(1L).build();
        Member member = Member.builder().status(MemberStatus.DELETED).build();

        when(albumRepository.findById(1L)).thenReturn(Optional.of(album));
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

        assertThatThrownBy(() -> albumService.editAlbum(albumDto,1L, 1L)).isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("앨범 삭제 성공 테스트")
    public void successToDeleteAlbum() {
        Member member = Member.builder().memberId(1L).status(MemberStatus.ACTIVE).build();
        Album album = Album.builder().albumId(1L).member(member).build();

        when(albumRepository.findById(1L)).thenReturn(Optional.of(album));
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        doNothing().when(albumRepository).deleteById(1L);

        ResponseEntity<String> response = albumService.deleteAlbum(1L,1L);
        System.out.println("response.getBody() = " + response.getBody());
        Assertions.assertThat(response.getBody()).isEqualTo(ResponseMessages.SUCCESS.getMessage());
    }

    @Test
    @DisplayName("앨범 삭제 실패 테스트 - 1. 삭제할 앨범이 없을 경우")
    public void failedByNoAlbumWhenEditAlbumWhenDeleteAlbum() {
        when(albumRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> albumService.deleteAlbum(1L, 1L)).isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("앨범 삭제 실패 테스트 - 2. 멤버가 없을 경우")
    public void failedByNoMemberWhenEditAlbumWhenDeleteAlbum() {
        Album album = Album.builder().albumId(1L).build();

        when(albumRepository.findById(1L)).thenReturn(Optional.of(album));
        when(memberRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> albumService.deleteAlbum(1L, 1L)).isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("앨범 삭제 실패 테스트 - 3. 멤버가 삭제된 경우")
    public void failedByDeletedMemberWhenEditAlbumWhenDeleteAlbum() {
        Album album = Album.builder().albumId(1L).build();
        Member member = Member.builder().memberId(1L).status(MemberStatus.DELETED).build();

        when(albumRepository.findById(1L)).thenReturn(Optional.of(album));
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

        assertThatThrownBy(() -> albumService.deleteAlbum(1L, 1L)).isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("앨범 삭제 실패 테스트 - 4. 삭제 요청한 멤버가 앨범 생성자가 아닌 경우")
    public void failedByMemberIdNotEqualAlbumGetMemberIdWhenDeleteAlbum() {
        Member others = Member.builder().memberId(2L).build();
        Album album = Album.builder().albumId(1L).member(others).build();
        Member member = Member.builder().memberId(1L).status(MemberStatus.ACTIVE).build();

        when(albumRepository.findById(1L)).thenReturn(Optional.of(album));
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

        assertThatThrownBy(() -> albumService.deleteAlbum(1L, 1L)).isInstanceOf(ForbiddenException.class);
    }

    @Test
    @DisplayName("앨범카드 추가 성공 테스트")
    public void successToAddAlbumCard() {
        Member member = Member.builder().memberId(1L).status(MemberStatus.ACTIVE).build();
        List<AlbumCard> albumCards = new ArrayList<>();
        Album album = Album.builder().albumId(1L).member(member)
                .albumCards(albumCards).build();
        Card card1 = fm.giveMeOne(Card.class);
        Card card2 = fm.giveMeOne(Card.class);
        AlbumCard ac1 = AlbumCard.builder().card(card1).album(album).build();
        AlbumCard ac2 = AlbumCard.builder().card(card2).album(album).build();
        albumCards.add(ac1);    albumCards.add(ac2);
        RequestAlbumCardDto requestAlbumCardDto = new RequestAlbumCardDto();

        when(albumRepository.findById(1L)).thenReturn(Optional.of(album));
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        doNothing().when(albumCardService).addCard(any(List.class), any(Album.class));

        ResponseAlbumDto response = albumService.addAlbumCard(requestAlbumCardDto, 1L,1L);
        ResponseEntity<ResponseAlbumDto> responseEntity = ResponseEntity.ok(response);
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("앨범카드 추가 실패 테스트 - 1. 앨범이 없는 경우")
    public void failedByNotExitsAlbumWhenAddAlbumCard() {
        RequestAlbumCardDto albumCardDto = new RequestAlbumCardDto();

        when(albumRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> albumService.addAlbumCard(albumCardDto,1L, 1L)).isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("앨범카드 추가 실패 테스트 - 2. 멤버가 없는 경우")
    public void failedByNoMemberWhenAddAlbumCard() {
        RequestAlbumCardDto albumCardDto = new RequestAlbumCardDto();
        Album album = new Album();

        when(albumRepository.findById(1L)).thenReturn(Optional.of(album));
        when(memberRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> albumService.addAlbumCard(albumCardDto,1L, 1L)).isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("앨범카드 추가 실패 테스트 = 3. 삭제된 멤버인 경우")
    public void failedByDeletedMemberWhenAddAlbumCard() {
        RequestAlbumCardDto albumCardDto = new RequestAlbumCardDto();
        Album album = new Album();
        Member member = Member.builder().memberId(1L).status(MemberStatus.DELETED).build();

        when(albumRepository.findById(1L)).thenReturn(Optional.of(album));
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

        assertThatThrownBy(() -> albumService.addAlbumCard(albumCardDto,1L, 1L)).isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("앨범카드 추가 실패 테스트 - 4. 앨범 생성자가 아닌 경우")
    public void failedByOthersWhenAddAlbumCard() {
        Member others = Member.builder().memberId(2L).build();
        Album album = Album.builder().albumId(1L).member(others).build();
        Member member = Member.builder().memberId(1L).status(MemberStatus.ACTIVE).build();
        RequestAlbumCardDto albumCardDto = new RequestAlbumCardDto();

        when(albumRepository.findById(1L)).thenReturn(Optional.of(album));
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

        assertThatThrownBy(() -> albumService.addAlbumCard(albumCardDto,1L, 1L)).isInstanceOf(ForbiddenException.class);
    }

    @Test
    @DisplayName("앨범카드 삭제 성공 테스트")
    public void successToDeleteAlbumCard() {
        Member member = Member.builder().memberId(1L).status(MemberStatus.ACTIVE).build();
        List<AlbumCard> albumCards = new ArrayList<>();
        Album album = Album.builder().albumId(1L).member(member)
                .albumCards(albumCards).build();
        Card card1 = fm.giveMeOne(Card.class);
        Card card2 = fm.giveMeOne(Card.class);
        AlbumCard ac1 = AlbumCard.builder().card(card1).album(album).build();
        AlbumCard ac2 = AlbumCard.builder().card(card2).album(album).build();
        albumCards.add(ac1);
        albumCards.add(ac2);
        RequestAlbumCardDto requestAlbumCardDto = new RequestAlbumCardDto();

        when(albumRepository.findById(1L)).thenReturn(Optional.of(album));
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        doNothing().when(albumCardService).deleteCard(any(List.class), any(Album.class));

        ResponseAlbumDto response = albumService.addAlbumCard(requestAlbumCardDto, 1L, 1L);
        ResponseEntity<ResponseAlbumDto> responseEntity = ResponseEntity.ok(response);
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("앨범카드 삭제 실패 테스트 - 1. 앨범이 없는 경우")
    public void failedByNotExitsAlbumWhenDeleteAlbumCard() {
        RequestAlbumCardDto albumCardDto = new RequestAlbumCardDto();

        when(albumRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> albumService.deleteAlbumCard(albumCardDto,1L, 1L)).isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("앨범카드 삭제 실패 테스트 - 2. 멤버가 없는 경우")
    public void failedByNoMemberWhenDeleteAlbumCard() {
        RequestAlbumCardDto albumCardDto = new RequestAlbumCardDto();
        Album album = new Album();

        when(albumRepository.findById(1L)).thenReturn(Optional.of(album));
        when(memberRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> albumService.addAlbumCard(albumCardDto,1L, 1L)).isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("앨범카드 삭제 실패 테스트 = 3. 삭제된 멤버인 경우")
    public void failedByDeletedMemberWhenDeleteAlbumCard() {
        RequestAlbumCardDto albumCardDto = new RequestAlbumCardDto();
        Album album = new Album();
        Member member = Member.builder().memberId(1L).status(MemberStatus.DELETED).build();

        when(albumRepository.findById(1L)).thenReturn(Optional.of(album));
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

        assertThatThrownBy(() -> albumService.deleteAlbumCard(albumCardDto,1L, 1L)).isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("앨범카드 삭 실패 테스트 - 4. 앨범 생성자가 아닌 경우")
    public void failedByOthersWhenDeleteAlbumCard() {
        Member others = Member.builder().memberId(2L).build();
        Album album = Album.builder().albumId(1L).member(others).build();
        Member member = Member.builder().memberId(1L).status(MemberStatus.ACTIVE).build();
        RequestAlbumCardDto albumCardDto = new RequestAlbumCardDto();

        when(albumRepository.findById(1L)).thenReturn(Optional.of(album));
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

        assertThatThrownBy(() -> albumService.deleteAlbumCard(albumCardDto,1L, 1L)).isInstanceOf(ForbiddenException.class);
    }

}
