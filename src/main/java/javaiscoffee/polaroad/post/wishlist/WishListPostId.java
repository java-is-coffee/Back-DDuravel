package javaiscoffee.polaroad.post.wishlist;

import jakarta.persistence.Embeddable;
import javaiscoffee.polaroad.post.hashtag.PostHashtagId;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@ToString
public class WishListPostId implements Serializable {
    private Long wishListId;
    private Long postId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WishListPostId)) return false;
        WishListPostId that = (WishListPostId) o;
        return Objects.equals(getWishListId(), that.getWishListId()) && Objects.equals(getPostId(), that.getPostId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getWishListId(), getPostId());
    }
}
