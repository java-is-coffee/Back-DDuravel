package javaiscoffee.polaroad.album.albumCard;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Embeddable
@Setter
@Getter
public class AlbumCardId implements Serializable {
    private Long albumId;
    private Long cardId;
}
