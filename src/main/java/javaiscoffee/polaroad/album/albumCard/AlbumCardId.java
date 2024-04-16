package javaiscoffee.polaroad.album.albumCard;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Embeddable
@Setter
@Getter
@Builder
public class AlbumCardId implements Serializable {
    private Long albumId;
    private Long cardId;
}
