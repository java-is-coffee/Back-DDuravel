package javaiscoffee.polaroad.post.wishlist;

import javaiscoffee.polaroad.post.PostListDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WishListPostListResponseDto {
    private List<WishListPostDto> posts;
    private int maxPage;
}
