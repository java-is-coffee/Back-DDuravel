package javaiscoffee.polaroad.review.reviewGood;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Embeddable
@Builder
public class ReviewGoodId implements Serializable {
    private Long memberId;
    private Long reviewId;

}
