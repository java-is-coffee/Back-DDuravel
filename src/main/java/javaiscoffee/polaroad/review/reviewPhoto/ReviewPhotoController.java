package javaiscoffee.polaroad.review.reviewPhoto;

import io.swagger.v3.oas.annotations.tags.Tag;
import javaiscoffee.polaroad.response.ResponseMessages;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/review-photos")
@Tag(name = "댓글 사진 관련 API", description = "댓글에 첨부하는 사진과 관련된 API")
public class ReviewPhotoController {

    private ReviewPhotoService photoService;

    // 사진 링크를 List로, 쿼리 파라미터로 받아오면 @RequestParam => 얘기해 봐야 할 듯?
    @PostMapping("/save")
    public ResponseEntity<?> reviewPhotos(@RequestParam("imageUrl") List<String> imageUrls, @RequestParam("reviewId") Long reviewId) {
        for (String imageUrl : imageUrls) {
            photoService.saveReviewPhoto(imageUrl, reviewId);
        }
        return ResponseEntity.ok(ResponseMessages.SUCCESS);
    }
}
