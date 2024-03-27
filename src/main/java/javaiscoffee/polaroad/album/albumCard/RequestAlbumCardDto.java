package javaiscoffee.polaroad.album.albumCard;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "앨범 사진 추가 or 삭제 요청시 사용하는 RequestDto")
public class RequestAlbumCardDto {
    private List<Long> cardId;
}
