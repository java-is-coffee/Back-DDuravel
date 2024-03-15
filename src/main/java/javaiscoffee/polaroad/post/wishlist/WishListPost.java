package javaiscoffee.polaroad.post.wishlist;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import javaiscoffee.polaroad.post.Post;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
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

    @NotNull
    LocalDateTime createdTime;

    public WishListPost(WishListPostId wishListPostId, WishList wishList, Post post) {
        this.wishListPostId = wishListPostId;
        this.wishList = wishList;
        this.post = post;
    }

    @PrePersist
    public void PrePersist() {
        this.createdTime = LocalDateTime.now();
    }
}
