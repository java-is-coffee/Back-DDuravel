package javaiscoffee.polaroad.album.albumCard;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "앨범 사진 추가 요청시 사용하는 RequestDto")
public class RequestToAddAlbumCardDto {
    private Long cardId;
}
