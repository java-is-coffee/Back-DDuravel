package javaiscoffee.polaroad.review.reviewPhoto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewPhotoInfoDto {
    private Long reviewPhotoId;
    private String reviewPhotoUrl;
}
