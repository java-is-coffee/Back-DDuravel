package javaiscoffee.polaroad.post.hashtag;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import javaiscoffee.polaroad.post.Post;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name = "postHashTags")
@Getter
@NoArgsConstructor
@ToString
public class PostHashtag {

    @EmbeddedId @Setter
    private PostHashtagId postHashtagId;

    @Setter
    @MapsId("hashtagId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hashtag_id")
    private Hashtag hashtag;

    @Setter
    @MapsId("postId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @NotNull
    private LocalDateTime createdTime;

    public PostHashtag(PostHashtagId postHashtagId, Hashtag hashtag, Post post) {
        this.postHashtagId = postHashtagId;
        this.hashtag = hashtag;
        this.post = post;
    }

    @PrePersist
    public void PrePersist() {
        this.createdTime = LocalDateTime.now();
    }
}
