package javaiscoffee.polaroad.post.card;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "마이페이지에서 카드 리스트 조회할 때 사용하는 ResponseDto")
public class CardListDto {
    @Schema(description = "카드 ID", example = "1")
    private Long cardId;
    @Schema(description = "상세 위치", example = "인천광역시 연수구")
    private String location;
    @Schema(description = "카드 이미지 url", example = "http:")
    private String image;
}
