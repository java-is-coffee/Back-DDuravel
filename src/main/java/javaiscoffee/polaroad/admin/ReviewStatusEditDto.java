package javaiscoffee.polaroad.admin;

import javaiscoffee.polaroad.post.PostStatus;
import javaiscoffee.polaroad.review.ReviewStatus;
import lombok.Data;

@Data
public class ReviewStatusEditDto {
    private ReviewStatus status;
    private String reason;
}
