package javaiscoffee.polaroad.post.card;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Schema(description = "마이페이지에서 카드 리스트 조회할 때 사용하는 ResponseDto")
public class CardListResponseDto {
    @Schema(description = "카드 목록")
    private List<CardListDto> cards;
    @Schema(description = "총 페이지", example = "1")
    private int maxPage;
}
