package javaiscoffee.polaroad.post.wishlist;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WishListPostDto {
    private Long postId;
    private String title;
    private String thumbnailImage;
}
