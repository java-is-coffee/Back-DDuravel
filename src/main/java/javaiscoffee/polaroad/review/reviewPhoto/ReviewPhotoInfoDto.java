package javaiscoffee.polaroad.review.reviewPhoto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewPhotoInfoDto {
    private Long reviewPhotoId;
    private String reviewPhotoUrl;
}
