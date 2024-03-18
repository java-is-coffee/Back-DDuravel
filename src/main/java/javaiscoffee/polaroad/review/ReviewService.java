package javaiscoffee.polaroad.review;

import javaiscoffee.polaroad.exception.BadRequestException;
import javaiscoffee.polaroad.exception.ForbiddenException;
import javaiscoffee.polaroad.exception.NotFoundException;
import javaiscoffee.polaroad.member.JpaMemberRepository;
import javaiscoffee.polaroad.member.Member;
import javaiscoffee.polaroad.post.Post;
import javaiscoffee.polaroad.post.PostRepository;
import javaiscoffee.polaroad.post.PostStatus;
import javaiscoffee.polaroad.response.ResponseMessages;
import javaiscoffee.polaroad.review.reviewPhoto.JpaReviewPhotoRepository;
import javaiscoffee.polaroad.review.reviewPhoto.ReviewPhoto;
import javaiscoffee.polaroad.review.reviewPhoto.ReviewPhotoService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
@Transactional
public class ReviewService {
    private final JpaReviewRepository reviewRepository;
    private final JpaMemberRepository memberRepository;
    private final PostRepository postRepository;
    private final ReviewPhotoService reviewPhotoService;
    private final JpaReviewPhotoRepository reviewPhotoRepository;

    public ResponseReviewDto createReview(ReviewDto reviewDto, Long memberId) {
        if (!memberId.equals(reviewDto.getMemberId())) {
            throw new ForbiddenException(ResponseMessages.FORBIDDEN.getMessage());
        }
        Review newReview = new Review();
        BeanUtils.copyProperties(reviewDto, newReview);
        Member creatorMember = memberRepository.findByMemberId(memberId).get();
        Post post = postRepository.findById(reviewDto.getPostId()).get();
        //포스트 리뷰 개수 + 1
        post.setReviewNumber(post.getReviewNumber() + 1);

        if (post == null || post.getStatus() == PostStatus.DELETED) {
            throw new BadRequestException(ResponseMessages.NOT_FOUND.getMessage());
        }

        // 댓글 저장
        newReview.setMemberId(creatorMember);
        newReview.setPostId(post);
        Review savedReview = reviewRepository.save(newReview);

        List<String> savedReviewPhotos = new ArrayList<>();
        // 사진 저장
        reviewDto.getReviewPhotoList().forEach(reviewPhotoUrl ->{
            // reviewDto에 리스트로 있는 사진 url들을 하나하나 saveReviewPhoto에 보냄
            ReviewPhoto newReviewPhoto = reviewPhotoService.saveReviewPhoto(reviewPhotoUrl, savedReview);
            savedReviewPhotos.add(newReviewPhoto.getImage());
        } );


        return toResponseReviewDto(savedReview, savedReviewPhotos);
    }

    public ResponseReviewDto getReviewById(Long reviewId, Long memberId) {
        log.info("댓글 조회 시작 = {}", reviewId);
        Review findedReview = reviewRepository.findByReviewId(reviewId);
        if (findedReview == null || findedReview.getStatus() == ReviewStatus.DELETED) {
            return null;
        }
        log.info("댓글 id 조회 후 = {}", findedReview);
        Post post = postRepository.findById(findedReview.getPostId().getPostId()).get();
        if (post == null || post.getStatus() == PostStatus.DELETED) {
            return null;
        }

        // 댓글 id로 해당 댓글의 모든 사진 조회
        log.info("해당 댓글의 모든 사진 조회 시작");

        List<ReviewPhoto> reviewPhotosByReviewId = reviewRepository.findByReviewId(findedReview.getReviewId()).getReviewPhoto();
        log.info("findedReview.getReviewId() = {} ", findedReview.getReviewId());

        List<String> reviewPhotoUrls = new ArrayList<>();
        for (ReviewPhoto reviewPhotoUrl : reviewPhotosByReviewId) {
            // get 사진 url
            String image = reviewPhotoUrl.getImage();
            reviewPhotoUrls.add(image);
        }
        log.info("해당 댓글의 모든 사진 = {}", reviewPhotoUrls);
        // 찾은 댓글 + 해당 댓글에 속한 모든 사진 반환
        return toResponseReviewDto(findedReview, reviewPhotoUrls);
    }

    public ResponseReviewDto editReview(ReviewEditRequestDto editReviewDto, Long reviewId, Long memberId) {
        log.info("댓글 수정 시작");
        Review originalReview = reviewRepository.findByReviewId(reviewId);
        // 원본 댓글이 null 이거나, 삭제된 경우
        if (originalReview == null || originalReview.getStatus() == ReviewStatus.DELETED) {
            throw new BadRequestException(ResponseMessages.NOT_FOUND.getMessage());
        }
        // 포스트가 null 이거나, 삭제된 경우
        Post post = postRepository.findById(originalReview.getPostId().getPostId()).get();
        if (post == null || post.getStatus() == PostStatus.DELETED) {
            throw new BadRequestException(ResponseMessages.NOT_FOUND.getMessage());
        }

        Member member = memberRepository.findByMemberId(memberId).get();
        // 댓글을 작성한 memberId와 수정을 요청한 맴버의 memberId가 다른 경우
        if (!originalReview.getMemberId().getMemberId().equals(member.getMemberId())) {
            throw new ForbiddenException(ResponseMessages.FORBIDDEN.getMessage());
        }

        // 댓글 수정
        originalReview.setContent(editReviewDto.getContent());
        originalReview.setUpdatedTime(LocalDateTime.now());
        Review updatedReview = reviewRepository.update(originalReview);
        log.info("댓글 업데이트 = {}", updatedReview);

        // 사진 수정
        log.info("댓글 사진 수정 시작");
        reviewPhotoService.editReviewPhoto(editReviewDto.getReviewPhotoList(), updatedReview);

        return toResponseReviewDto(updatedReview, editReviewDto.getReviewPhotoList());
    }

    public Boolean deleteReview(Long reviewId, Long memberId) {
        Review review = reviewRepository.findByReviewId(reviewId);
        // 댓글이 존재하지 않거나 삭제된 댓글인 경우
        if (review == null || review.getStatus() == ReviewStatus.DELETED) {
            throw new BadRequestException(ResponseMessages.NOT_FOUND.getMessage());
        }
        Post post = postRepository.findById(review.getPostId().getPostId()).get();
        //포스트 리뷰 개수 - 1
        post.setReviewNumber(post.getReviewNumber() - 1);

        // 포스트가 존재하지 않거나 삭제된 포스트인 경우
        if (post == null || post.getStatus() == PostStatus.DELETED) {
            throw new BadRequestException(ResponseMessages.NOT_FOUND.getMessage());
        }
        Member member = memberRepository.findByMemberId(memberId).get();
        // member가 댓글 작성자가 아닌 경우
        if (!review.getMemberId().getMemberId().equals(member.getMemberId())) {
            throw new ForbiddenException(ResponseMessages.FORBIDDEN.getMessage());
        }

        // 해당 댓글에 속한 모든 사진
        List<ReviewPhoto> reviewPhotos = reviewRepository.findByReviewId(review.getReviewId()).getReviewPhoto();
        // 해당 댓글에 속한 모든 사진 삭제
        for (ReviewPhoto reviewPhoto : reviewPhotos) {
            Long reviewPhotoId = reviewPhoto.getReviewPhotoId();
            reviewPhotoRepository.delete(reviewPhotoId);
        }

        // 댓글 삭제
        reviewRepository.delete(reviewId);
        return true;
    }


    /**
     * 포스트에 딸린 ACTIVE 상태의 모든 댓글
     */
    public List<ResponseReviewDto> getReviewByPostId(Long postId) {
        log.info("해당 포스트의 모든 댓글 조회 시작 ");
        Post getPost = postRepository.findById(postId).orElse(null);
        if (getPost == null || getPost.getStatus() == PostStatus.DELETED) {
            throw new BadRequestException(ResponseMessages.NOT_FOUND.getMessage());
        }
        // 가져온 post에 속한 ACTIVE 상태인 모든 댓글을 가져옴
        List<Review> reviewList = reviewRepository.findReviewByPostId(getPost, ReviewStatus.ACTIVE);
        return toResponseReviewDtoList(reviewList);
    }


    /**
     * 포스트에 딸린 댓글 페이징
     * Slice를 사용하는 이유 => 서버에서는 클라이언트로부터 요청을 받아 해당 페이지에 해당하는 데이터를 조회하여 응답하는 역할을 한다,
     * 이를 위해 서버에서는 페이지당 데이터의 일부만을 반환하는 것이 아니라, 다음 페이지가 있는지 여부를 알려주는 방식으로 데이터를 전달해야 한다.
     */
    public SliceResponseDto<ResponseReviewDto> getReviewsPagedByPostId(Long postId, int page) {
        log.info("포스트 댓글들 페이징 시작 = {}");
        page = (page == 0) ? 0 : (page - 1);
        Post getPost = postRepository.findById(postId).orElse(null);
        if (getPost == null || getPost.getStatus() == PostStatus.DELETED) {
            throw new BadRequestException(ResponseMessages.NOT_FOUND.getMessage());
        }

        // 페이지 요청 정보 생성! 실제 데이터를 포함하고 있지 않으며, 단순히 페이지 관련 정보를 포함하고 있음
        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdTime").descending());

        Slice<Review> reviewSlice = reviewRepository.findReviewSlicedByPostId(getPost, pageable, ReviewStatus.ACTIVE);
        // 조회된 리뷰 페이지에서 실제 데이터 가져옴
        List<Review> reviewList = reviewSlice.getContent();
        // 가져온 리뷰 데이터를 ResponseReviewDto로 변환
        List<ResponseReviewDto> responseReviewDtoList = toResponseReviewDtoList(reviewList);
        // [{리뷰 리스트}, 다음 페이지가 있는지] 반환
        return new SliceResponseDto<>(responseReviewDtoList, reviewSlice.hasNext());
    }

    /**
     * 맴버가 작성한 모든 댓글
     */
    public List<ResponseReviewDto> getReviewByMemberId(Long memberId) {
        Member getMember = memberRepository.findById(memberId).orElse(null);
        if (getMember == null) {
            throw new NotFoundException(ResponseMessages.NOT_FOUND.getMessage());
        }
        // 맴버가 작성한 ACTIVE 댓글 리스트
        List<Review> reviewList = reviewRepository.findReviewByMemberId(getMember, ReviewStatus.ACTIVE);
        return toResponseReviewDtoList(reviewList);
    }

    /**
     * 맴버가 작성한 모든 댓글들 페이징
     */
    public SliceResponseDto<ResponseReviewDto> getReviewsPagedByMemberId(Long memberId, int page) {
        log.info("맴버가 작성한 모든 댓글들 페이징 시작 = {}");
        page = (page == 0) ? 0 : (page - 1);
        Member getMemberId = memberRepository.findById(memberId).orElse(null);
        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdTime").descending());
        Slice<Review> reviewSlice = reviewRepository.findReviewSlicedByMemberId(getMemberId, pageable, ReviewStatus.ACTIVE);

        List<Review> reviewList = reviewSlice.getContent();
        List<ResponseReviewDto> responseReviewDtoList = toResponseReviewDtoList(reviewList);

        return new SliceResponseDto<>(responseReviewDtoList, reviewSlice.hasNext());
    }


    // Review 객체를 ResponseReviewDto 객체로 매핑하는 메서드
    public static ResponseReviewDto toResponseReviewDto(Review review, List<String> reviewPhotos) {
        if (review == null) {
            return  null;   // 주어진 review가 null인 경우, null 반환
        }
        ResponseReviewDto responseReviewDto = new ResponseReviewDto();

        responseReviewDto.setPostId(review.getPostId() != null ? review.getPostId().getPostId() : null);
        responseReviewDto.setMemberId(review.getMemberId() != null ? review.getMemberId().getMemberId() : null);
        responseReviewDto.setProfileImage(review.getMemberId().getProfileImage());
        responseReviewDto.setNickname(review.getMemberId() != null ? review.getMemberId().getNickname() : null);
        responseReviewDto.setReviewId(review.getReviewId());
        responseReviewDto.setContent(review.getContent());
        responseReviewDto.setUpdatedTime(review.getUpdatedTime());
        responseReviewDto.setReviewPhotoList(reviewPhotos);
        //프로필 이미지

        return responseReviewDto;
    }

    // Review 객체 리스트를 ResponseReviewDto 객체 리스트로 매핑하는 메서드
    public static List<ResponseReviewDto> toResponseReviewDtoList(List<Review> reviews) {
        if (reviews == null) {
            return null;
        }

        List<ResponseReviewDto> responseReviewDtoList = new ArrayList<>();

        for (Review review : reviews) {
            List<ReviewPhoto> reviewPhoto = review.getReviewPhoto();
            List<String> urlList = new ArrayList<>();
            for (ReviewPhoto photo : reviewPhoto) {
                String image = photo.getImage();
                urlList.add(image);
            }

            ResponseReviewDto responseReviewDto = toResponseReviewDto(review, urlList);
            responseReviewDtoList.add(responseReviewDto);
        }

        return responseReviewDtoList;
    }
}
