package javaiscoffee.polaroad.post.good;

import jakarta.persistence.*;
import javaiscoffee.polaroad.member.Member;
import javaiscoffee.polaroad.post.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "goods")
@ToString
public class PostGood {
    @EmbeddedId
    private PostGoodId postGoodId;

    @MapsId("memberId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @MapsId("postId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    private LocalDateTime createdTime;

    public PostGood(PostGoodId id, Member member, Post post) {
        this.postGoodId = id;
        this.member = member;
        this.post = post;
    }

    @PrePersist
    public void PrePersist() {
        this.createdTime = LocalDateTime.now();
    }
}
