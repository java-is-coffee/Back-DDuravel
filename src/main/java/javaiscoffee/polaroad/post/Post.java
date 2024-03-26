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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "posts")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "postId"
)
@Builder
@ToString(exclude = "member")
public class Post {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;
    @NotNull @Setter
    private String title;
    @ManyToOne @Setter
    @JoinColumn(name = "member_id", nullable = false)
    @JsonBackReference
    private Member member;
    @Setter
    @Column(length = 2000)
    private String routePoint;  //경로 좌표 저장
    @NotNull @Setter
    private int goodNumber;     //하트 개수
    @NotNull @Setter
    private int reviewNumber;   //리뷰 개수
    @NotNull @Setter
    private int thumbnailIndex; //썸네일 카드 번호
    @NotNull @Setter
    @Enumerated(EnumType.STRING)
    private PostConcept concept;//여행 테마
    @NotNull @Setter
    @Enumerated(EnumType.STRING)
    private PostRegion region;  //여행 지역

    @NotNull @Setter
    @Enumerated(EnumType.STRING)
    private PostStatus status;
    private LocalDateTime createdTime;
    @Setter
    private LocalDateTime updatedTime;

    @NotNull @OneToMany(mappedBy = "post")
    private List<Card> cards;
    @NotNull @OneToMany(mappedBy = "post")
    private List<PostHashtag> postHashtags;
    @NotNull @OneToMany(mappedBy = "post")
    private List<Review> reviews;
    @NotNull @OneToMany(mappedBy = "post")
    private List<PostGood> postGoods;
    @NotNull @OneToMany(mappedBy = "post")
    private List<WishListPost> wishListPosts;

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
        this.postGoods = new ArrayList<>();
        this.wishListPosts = new ArrayList<>();
    }
}
