package javaiscoffee.polaroad.review;

import javaiscoffee.polaroad.member.JpaMemberRepository;
import javaiscoffee.polaroad.member.Member;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
@Transactional
public class ReviewService {
    private final JpaReviewRepository reviewRepository;
    private final JpaMemberRepository memberRepository;
    private final JpaPostRepository postRepository;

    public ResponseReviewDto createReview(ReviewDto reviewDto, Long memberId) {
        if (!memberId.equals(reviewDto.getMemberId())) {
            return null;
        }
        Review newReview = new Review();
        BeanUtils.copyProperties(reviewDto, newReview);
        Member creatorMember = memberRepository.findByMemberId(memberId).get();
        Post post = postRepository.findByPostId(reviewDto.getPostId()).get();

        if (post.getPostStatus() == PostStatus.DELETE) {
            return null;
        }

        // 댓글 저장
        newReview.setMemberId(creatorMember);
        newReview.setPostId(post);
        Review savedReview = reviewRepository.save(newReview);
        return toResponseReviewDto(savedReview);
    }

    public ResponseReviewDto getReviewById(Long reviewId, Long memberId) {
        Review findedReview = reviewRepository.findByReviewId(reviewId);
        Post post = postRepository.findByPostId(findedReview.getPostId().getPostId()).get();
        // 삭제된 댓글이거나 삭제된 포스트인 경우 null 반환
        if (findedReview == null || findedReview.getStatus() == ReviewStatus.DELETED || post == null || post.getPostStatus() == PostStatus.DELETED) {
            return null;
        }
        return toResponseReviewDto(findedReview);
    }

    public ResponseReviewDto editReview(ReviewEditRequestDto editReviewDto, Long reviewId, Long memberId) {
        Review originalReview = reviewRepository.findByReviewId(reviewId);
        Post post = postRepository.findByPostId(originalReview.getPostId().getPostId()).get();

        // 원본 댓글 & 포스트가 null 이거나, 삭제된 경우 null 반환
        if (originalReview == null || originalReview.getStatus() == ReviewStatus.DELETED || post == null || post.getPostStatus() == PostStatus.DELETED) {
            return null;
        }

        Member member = memberRepository.findByMemberId(memberId).get();
        // 댓글을 작성한 memberId와 수정을 요청한 맴버의 memberId가 다른 경우 null 반환
        if (!originalReview.getMemberId().getMemberId().equals(member.getMemberId())) {
            return null;
        }

        // 댓글 수정
        originalReview.setContent(editReviewDto.getContent());
        originalReview.setUpdatedTime(LocalDateTime.now());
        Review updatedReview = reviewRepository.update(originalReview);
        return toResponseReviewDto(updatedReview);
    }

    public Boolean deleteReview(Long reviewId, Long memberId) {
        Review review = reviewRepository.findByReviewId(reviewId);
        // 댓글이 존재하지 않거나 삭제된 댓글인 경우 false 반환
        if (review == null || review.getStatus() == ReviewStatus.DELETED) {
            return false;
        }

        Post post = postRepository.findByPostId(review.getPostId().getPostId()).get();
        // 포스트가 존재하지 않거나 삭제된 포스트인 경우 false 반환
        if (post == null || post.getStatus() == PostStatus.DELETED) {
            return false;
        }

        Member member = memberRepository.findByMemberId(memberId).get();
        // member가 댓글 작성자가 아닌 경우 false 반환
        if (!review.getMemberId().getMemberId().equals(member.getMemberId())) {
            return false;
        }
        reviewRepository.delete(reviewId);
        return true;
    }

    public List<ResponseReviewDto> getReviewByPostId(Long postId) {
        Post getPost = postRepository.findByPostId(postId).get();
        // 가져온 post에 속한 ACTIVE 상태인 모든 댓글을 가져옴
        List<Review> reviewList = reviewRepository.findReviewByPostId(getPost, ReviewStatus.ACTIVE);
        return toResponseReviewDtoList(reviewList);
    }

    public List<ResponseReviewDto> getReviewByMemberId(Long memberId) {
        Member getMember = memberRepository.findByMemberId(memberId).get();
        List<Review> reviewList = reviewRepository.findReviewByMemberId(getMember, ReviewStatus.ACTIVE);
        return toResponseReviewDtoList(reviewList);
    }

    // Review 객체를 ResponseReviewDto 객체로 매핑하는 메서드
    public static ResponseReviewDto toResponseReviewDto(Review review) {
        if (review == null) {
            return  null;   // 주어진 review가 null인 경우, null 반환
        }
        ResponseReviewDto responseReviewDto = new ResponseReviewDto();

        responseReviewDto.setPostId(review.getPostId() != null ? review.getPostId().getPostId() : null);
        responseReviewDto.setMemberId(review.getMemberId() != null ? review.getMemberId().getMemberId() : null);
        responseReviewDto.setReviewId(review.getReviewId());
        responseReviewDto.setContent(review.getContent());
        responseReviewDto.setStatus(review.getStatus());
        responseReviewDto.setCreatedTime(review.getCreatedTime());

        return responseReviewDto;
    }

    // Review 객체 리스트를 ResponseReviewDto 객체 리스트로 매핑하는 메서드
    public static List<ResponseReviewDto> toResponseReviewDtoList(List<Review> reviews) {
        if (reviews == null) {
            return null;
        }

        List<ResponseReviewDto> responseReviewDtoList = new ArrayList<>();

        for (Review review : reviews) {
            ResponseReviewDto responseReviewDto = toResponseReviewDto(review);
            responseReviewDtoList.add(responseReviewDto);
        }

        return responseReviewDtoList;
    }
}
