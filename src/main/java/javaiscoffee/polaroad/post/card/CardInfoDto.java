package javaiscoffee.polaroad.post.card;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardInfoDto {
    private Long cardId;
    private int cardIndex;
    private String latitude;
    private String longtitude;
    private String location;
    private String image;
    private String content;
}
