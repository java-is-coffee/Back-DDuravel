package javaiscoffee.polaroad.post.card;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MapCardListDto {
    private Long postId;
    private Long cardId;
    private String image;
    private String content;
    private String location;
    private double latitude; // 위도
    private double longitude; // 경도
}
