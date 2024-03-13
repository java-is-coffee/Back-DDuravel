package javaiscoffee.polaroad.post;

import javaiscoffee.polaroad.post.card.CardSaveDto;
import lombok.Data;

import java.util.List;

@Data
public class PostSaveDto {
    private String title;
    private String routePoint;
    private int thumbnailIndex;
    private PostConcept concept;
    private PostRegion region;
    private List<CardSaveDto> cards;
    private List<String> hashtags;
}
