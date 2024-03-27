package javaiscoffee.polaroad.album;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "앨범 수정 정보 받는 requestDto")
/**
 * 필요 없어짐. 삭제 예정
 */
public class EditAlbumDto {
    @Schema(description = "앨범 이름", example = "제주도")
    private String name;
    @Schema(description = "앨범 간단한 설명", example = "제주도 맛집 여행 앨범")
    private String description;
    @Schema(description = "카드 id 리스트 \n 카드 id 입력해주세요.")
    private List<Long> cardIdList;
}
