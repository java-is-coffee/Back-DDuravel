package javaiscoffee.polaroad.review.reviewPhoto;

import jakarta.persistence.*;
import javaiscoffee.polaroad.review.Review;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Entity
@Getter
@Table(name = "review_photo")
public class ReviewPhoto {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_photo_id")
    private Long reviewPhotoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private Review reviewId;

    private String image;   // 프론트에서 저장하고 링크 건네줌

    private String imagePath;   // 사진 경로

}
// status 추가