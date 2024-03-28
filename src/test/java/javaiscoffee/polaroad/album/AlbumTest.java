package javaiscoffee.polaroad.album;

import javaiscoffee.polaroad.album.albumCard.AlbumCard;
import javaiscoffee.polaroad.member.Member;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.time.LocalDateTime;
import java.util.List;

public class AlbumTest {
    @Mock
    private Member member;
    @Mock
    private List<AlbumCard> albumCards;

    @Test
    @DisplayName("앨범 생성 성공 테스트")
    void createAlbum() {
        // given
        Album album = Album.builder()
                .albumId(1L)
                .member(member)
                .name("앨범 이름")
                .description("앨범 간단한 설명")
                .updatedTime(LocalDateTime.now())
                .createdTime(LocalDateTime.now())
                .albumCards(albumCards)
                .build();

        // when then
        Assertions.assertThat(album.getAlbumId()).isEqualTo(1L);
        Assertions.assertThat(album.getMember()).isEqualTo(member);
        Assertions.assertThat(album.getName()).isEqualTo("앨범 이름");
        Assertions.assertThat(album.getDescription()).isEqualTo("앨범 간단한 설명");
        Assertions.assertThat(album.getAlbumCards()).isEqualTo(albumCards);
    }

    @Test
    @DisplayName("앨범 수정 성공 테스트")
    void changeAlbum() {
        // given
        Album album = Album.builder()
                .albumId(1L)
                .member(member)
                .name("앨범 이름")
                .description("앨범 간단한 설명")
                .albumCards(albumCards)
                .createdTime(LocalDateTime.now())
                .updatedTime(LocalDateTime.now())
                .build();

        // when
        album.setName("수정된 앨범 이름");
        album.setDescription("수정된 앨범 설명");

        // then
        Assertions.assertThat(album.getAlbumId()).isEqualTo(1L);
        Assertions.assertThat(album.getMember()).isEqualTo(member);
        Assertions.assertThat(album.getName()).isEqualTo("수정된 앨범 이름");
        Assertions.assertThat(album.getDescription()).isEqualTo("수정된 앨범 설명");
    }
}
