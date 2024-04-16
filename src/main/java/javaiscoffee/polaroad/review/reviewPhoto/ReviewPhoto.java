package javaiscoffee.polaroad.review.reviewPhoto;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import javaiscoffee.polaroad.review.Review;
import lombok.*;

import java.beans.ConstructorProperties;


@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "review")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "reviewPhotoId"
)
@Entity
@Getter
@Table(name = "review_photo")
public class ReviewPhoto {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_photo_id")
    private Long reviewPhotoId;
    @ManyToOne(fetch = FetchType.LAZY)
    @Setter @JoinColumn(name = "review_id")
    private Review review;
    @Setter
    private String image;   // 사진 url

    @ConstructorProperties({"reviewPhotoId","image","review"})
    public ReviewPhoto(Long reviewPhotoId, String image, Review review) {
        this.reviewPhotoId = reviewPhotoId;
        this.review = review;
        this.image = image;
    }

}