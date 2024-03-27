package javaiscoffee.polaroad.album.albumCard;

import javaiscoffee.polaroad.album.Album;
import javaiscoffee.polaroad.post.card.Card;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlbumCardRepository extends JpaRepository<AlbumCard, Long> {
    public List<AlbumCard> findAllByAlbum(Album album);
}
