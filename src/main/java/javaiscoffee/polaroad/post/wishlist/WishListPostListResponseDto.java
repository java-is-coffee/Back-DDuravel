package javaiscoffee.polaroad.post.wishlist;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WishListPostListResponseDto {
    private List<WishListPostDto> posts;
    private boolean hasNext;
}
