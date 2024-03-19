package javaiscoffee.polaroad.member;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "follow")
public class Follow {
    @EmbeddedId
    private FollowId id;

    @MapsId("followingMemberId")
    @ManyToOne
    @JoinColumn(name = "following_member_id")
    private Member followingMember;

    @MapsId("followedMemberId")
    @ManyToOne
    @JoinColumn(name = "followed_member_id")
    private Member followedMember;

    private LocalDateTime createdTime;

    public Follow(FollowId id, Member followingMember, Member followedMember) {
        this.id = id;
        this.followingMember = followingMember;
        this.followedMember = followedMember;
    }

    @PrePersist
    public void PrePersist() {
        this.createdTime = LocalDateTime.now();
    }
}
