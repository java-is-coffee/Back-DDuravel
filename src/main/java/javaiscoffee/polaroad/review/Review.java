package javaiscoffee.polaroad.review;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import javaiscoffee.polaroad.member.Member;
import javaiscoffee.polaroad.post.Post;
import javaiscoffee.polaroad.review.reviewGood.ReviewGood;
import javaiscoffee.polaroad.review.reviewPhoto.ReviewPhoto;
import lombok.*;

import java.beans.ConstructorProperties;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"member","post"})
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "reviewId"
)
@Entity
@Getter
@Table(name = "reviews")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long reviewId;
    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull @Setter @JoinColumn(name = "post_id")
    private Post post;
    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull @Setter @JoinColumn(name = "member_id")
    private Member member;
    @Setter @NotNull
    private String content;
    @NotNull @Setter
    private int goodNumber; // 좋아요 수
    @Setter @NotNull
    @Enumerated(EnumType.STRING)
    private ReviewStatus status;
    @Setter
    private LocalDateTime updatedTime;
    @Setter
    private LocalDateTime createdTime;
    @OneToMany(mappedBy = "review")
    private List<ReviewPhoto> reviewPhoto;
    @OneToMany(mappedBy = "review")
    private List<ReviewGood> reviewGoods;

    @PrePersist
    public void PrePersist() {
        this.createdTime = LocalDateTime.now();
        this.updatedTime = LocalDateTime.now();
        this.status = ReviewStatus.ACTIVE;
        this.reviewPhoto = new ArrayList<>();
        this.goodNumber = 0;
        this.reviewGoods = new ArrayList<>();
    }

    //NOTE: 테스트 코드에서 사용하기 위해서 작성
    @ConstructorProperties({"reviewId","post","member","content","goodNumber","status","reviewPhoto","reviewGoods"})
    public Review(Long reviewId, Post post, Member member, String content, int goodNumber,
                  ReviewStatus status, List<ReviewPhoto> reviewPhoto, List<ReviewGood> reviewGoods) {
        this.reviewId = reviewId;
        this.post = post;
        this.member = member;
        this.content = content;
        this.goodNumber = goodNumber;
        this.status = status;
        this.reviewPhoto = reviewPhoto;
        this.reviewGoods = reviewGoods;
    }
}
