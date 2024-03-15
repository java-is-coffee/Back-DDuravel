package javaiscoffee.polaroad.post.wishlist;

import jakarta.persistence.*;
import javaiscoffee.polaroad.post.Post;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "wish_list_posts")
public class WishListPost {

    @EmbeddedId @Setter
    private WishListPostId wishListPostId;

    @Setter
    @MapsId("wishListId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wish_list_id")
    private WishList wishList;

    @Setter
    @MapsId("postId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;
}
