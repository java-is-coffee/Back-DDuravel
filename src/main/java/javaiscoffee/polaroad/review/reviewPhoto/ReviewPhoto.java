package javaiscoffee.polaroad.review.reviewPhoto;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import javaiscoffee.polaroad.review.Review;
import lombok.*;


@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "reviewId")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "reviewPhotoId"
)
@Entity
@Getter
//@Setter //NOTE: 테스트시 사용 주석 처리 할 것
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
}