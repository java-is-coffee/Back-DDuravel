package javaiscoffee.polaroad.post;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import javaiscoffee.polaroad.member.Member;
import javaiscoffee.polaroad.post.card.Card;
import javaiscoffee.polaroad.post.good.PostGood;
import javaiscoffee.polaroad.post.hashtag.PostHashtag;
import javaiscoffee.polaroad.post.wishlist.WishListPost;
import javaiscoffee.polaroad.review.Review;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(indexName = "posts")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "postId"
)
@Builder
@ToString(exclude = {"member","cards","postHashtags"})
public class ElasticPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;
    @NotNull @Setter
    private String title;
    @Setter
    private Member member;
    @Setter
    private String routePoint;  //경로 좌표 저장
    @NotNull @Setter
    private int goodNumber;     //하트 개수
    @NotNull @Setter
    private int reviewNumber;   //리뷰 개수
    @NotNull @Setter
    private int thumbnailIndex; //썸네일 카드 번호
    @NotNull @Setter
    private PostConcept concept;//여행 테마
    @NotNull @Setter
    private PostRegion region;  //여행 지역

    @NotNull @Setter
    private PostStatus status;
    private LocalDateTime createdTime;
    @Setter
    private LocalDateTime updatedTime;

    @NotNull
    private List<Card> cards;
    @NotNull
    private List<PostHashtag> postHashtags;
    @NotNull
    private List<Review> reviews;

    @PrePersist
    public void PrePersist() {
        this.goodNumber = 0;
        this.reviewNumber = 0;
        this.status = PostStatus.ACTIVE;
        this.createdTime = LocalDateTime.now();
        this.updatedTime = LocalDateTime.now();
        this.cards = new ArrayList<>();
        this.postHashtags = new ArrayList<>();
        this.reviews = new ArrayList<>();
    }

    @PreUpdate
    public void PreUpdate() {
        this.updatedTime = LocalDateTime.now();
    }
}
