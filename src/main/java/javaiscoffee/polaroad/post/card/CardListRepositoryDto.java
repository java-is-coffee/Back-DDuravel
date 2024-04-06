package javaiscoffee.polaroad.post.card;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CardListRepositoryDto {
    private Long postId;
    private int cardIndex;
    private String image;
}
