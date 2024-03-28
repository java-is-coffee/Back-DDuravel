package javaiscoffee.polaroad.review.reviewGood;

import jakarta.persistence.*;
import javaiscoffee.polaroad.member.Member;

import javaiscoffee.polaroad.review.Review;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "goods_review")
public class ReviewGood {
    @EmbeddedId
    private ReviewGoodId reviewGoodId;

    @MapsId("memberId")
    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @MapsId("reviewId")
    @ManyToOne
    @JoinColumn(name = "review_id")
    private Review review;

    private LocalDateTime createdTime;

    public ReviewGood(ReviewGoodId reviewGoodId, Member member, Review review) {
        this.reviewGoodId = reviewGoodId;
        this.member = member;
        this.review = review;
    }

    @PrePersist
    public void PrePersist() {
        this.createdTime = LocalDateTime.now();
    }
}
