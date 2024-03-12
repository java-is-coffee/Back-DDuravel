package javaiscoffee.polaroad.post.hashtag;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Embeddable
@Getter @Setter
@ToString
public class PostHashtagId implements Serializable {
    private Long hashtagId;
    private Long postId;

    // equals와 hashCode 메소드는 복합 키가 정확하게 작동하기 위해 필수
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PostHashtagId)) return false;
        PostHashtagId that = (PostHashtagId) o;
        return Objects.equals(getHashtagId(), that.getHashtagId()) &&
                Objects.equals(getPostId(), that.getPostId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getHashtagId(), getPostId());
    }
}
