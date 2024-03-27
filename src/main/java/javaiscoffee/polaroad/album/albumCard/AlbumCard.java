package javaiscoffee.polaroad.album.albumCard;

import jakarta.persistence.*;
import javaiscoffee.polaroad.album.Album;
import javaiscoffee.polaroad.post.card.Card;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString(exclude = {"album", "card"})
@Table(name = "albumsCards")
public class AlbumCard {

    @EmbeddedId
    private AlbumCardId id;

    @MapsId("albumId")
    @JoinColumn(name = "album_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Album album;

    @MapsId("cardId")
    @JoinColumn(name = "card_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Card card;
}
