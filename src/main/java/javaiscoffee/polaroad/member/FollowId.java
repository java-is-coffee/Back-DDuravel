package javaiscoffee.polaroad.member;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class FollowId implements Serializable {
    private Long followingMemberId;
    private Long followedMemberId;
}
