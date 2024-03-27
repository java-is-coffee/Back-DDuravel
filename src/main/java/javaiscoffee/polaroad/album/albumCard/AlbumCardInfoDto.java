package javaiscoffee.polaroad.album.albumCard;

import io.swagger.v3.oas.annotations.media.Schema;
import javaiscoffee.polaroad.post.card.CardInfoDto;
import lombok.Data;


@Data
public class AlbumCardInfoDto {
    @Schema(description = "카드 정보")
    private CardInfoDto cardInfo;
}
