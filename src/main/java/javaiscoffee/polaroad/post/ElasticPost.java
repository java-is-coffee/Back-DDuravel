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
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

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
@ToString(exclude = {"member", "cards", "postHashtags"})
public class ElasticPost {
    @Id
    @Field(type = FieldType.Long)
    private Long postId;

    @Setter
    @Field(type = FieldType.Text, analyzer = "nori")
    private String title;

    @Setter
    @Field(type = FieldType.Long)
    private Long memberId;  // Consider flattening to store only relevant member details

    @Setter
    @Field(type = FieldType.Keyword)
    private PostConcept concept;

    @Setter
    @Field(type = FieldType.Keyword)
    private PostRegion region;

    @Setter @NotNull
    @Field(type = FieldType.Keyword)
    private PostStatus status;

    @Setter
    @Field(type = FieldType.Text, index = false) // Store but do not index routePoint
    private String routePoint;

    @NotNull @Setter
    @Field(type = FieldType.Integer, index = false) // Not used for searching or sorting
    private int goodNumber;

    @NotNull @Setter
    @Field(type = FieldType.Integer, index = false) // Not used for searching or sorting
    private int reviewNumber;

    @NotNull @Setter
    @Field(type = FieldType.Integer, index = false) // Not used for searching or sorting
    private int thumbnailIndex;

    private LocalDateTime createdTime;
    @Setter
    private LocalDateTime updatedTime;

    @NotNull
    @Field(type = FieldType.Nested)  // Use nested for complex objects if needed
    private List<Card> cards;

    @NotNull
    @Field(type = FieldType.Nested)
    private List<PostHashtag> postHashtags;

    @NotNull
    @Field(type = FieldType.Nested)
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