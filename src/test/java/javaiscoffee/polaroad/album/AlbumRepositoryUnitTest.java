package javaiscoffee.polaroad.album;

import javaiscoffee.polaroad.config.JpaConfigTest;
import javaiscoffee.polaroad.member.JpaMemberRepository;
import javaiscoffee.polaroad.member.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
@DataJpaTest
@Import(JpaConfigTest.class)
public class AlbumRepositoryUnitTest {
    @Autowired
    private JpaMemberRepository memberRepository;
    @Autowired
    private AlbumRepository albumRepository;
    private Long savedAlbum1;
    private Long savedAlbum2;

    @BeforeEach
    void setup() {
        Member member = Member.builder()
                .name("박자바")
                .nickname("자바커피")
                .email("aaa@naver.com")
                .password("a123123!")
                .build();
        member.hashPassword(new BCryptPasswordEncoder());
        memberRepository.save(member);
        member = memberRepository.findByEmail("aaa@naver.com").get();

        Album album1 = Album.builder()
                .member(member)
                .name("제목 1")
                .description("설명 1")
                .build();
        Album album2 = Album.builder()
                .member(member)
                .name("제목 2")
                .description("설명 2")
                .build();
        Album savedAlbum1 = albumRepository.save(album1);
        Album savedAlbum2 = albumRepository.save(album2);

        this.savedAlbum1 = savedAlbum1.getAlbumId();
        this.savedAlbum2 = savedAlbum2.getAlbumId();
    }

    @Test
    @DisplayName("앨범 저장")
    public void saveAlbum() {
        // given
        Album savedAlbum = albumRepository.findById(1L).get();
        
        // when & then
        assertThat(savedAlbum.getAlbumId()).isEqualTo(1L);
        assertThat(savedAlbum.getName()).isEqualTo("제목 1");
        assertThat(savedAlbum.getDescription()).isEqualTo("설명 1");
        assertThat(savedAlbum.getAlbumCards().size()).isEqualTo(0);
    }

    @Test
    @DisplayName("앨범 수정")
    public void updateAlbum() {
        Album album = albumRepository.findById(this.savedAlbum1).get();

        album.setName("수정된 앨범");
        album.setDescription("수정된 설명");
        albumRepository.flush();
        Album updatedAlbum = albumRepository.findById(this.savedAlbum1).get();

        assertThat(updatedAlbum.getName()).isEqualTo("수정된 앨범");
        assertThat(updatedAlbum.getDescription()).isEqualTo("수정된 설명");
    }
}
