package javaiscoffee.polaroad.album;

import javaiscoffee.polaroad.album.albumCard.AlbumCard;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface QueryAlbumRepository {
    public Slice<Album> findAlbumSlicedByMemberId(Long memberId, Pageable pageable);

    public Slice<AlbumCard> findAlbumCardSlicedByAlbum(Album album, Pageable pageable);
}
