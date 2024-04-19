package javaiscoffee.polaroad.post;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PostThumbnailDto {
    private Long postId;
    private String image;
}
