package javaiscoffee.polaroad.post.good;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class PostGoodId implements Serializable {
    private Long memberId;
    private Long postId;
}
