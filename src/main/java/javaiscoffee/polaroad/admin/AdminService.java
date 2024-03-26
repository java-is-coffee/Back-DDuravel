package javaiscoffee.polaroad.admin;

import javaiscoffee.polaroad.exception.ForbiddenException;
import javaiscoffee.polaroad.exception.NotFoundException;
import javaiscoffee.polaroad.member.*;
import javaiscoffee.polaroad.post.*;
import javaiscoffee.polaroad.post.card.Card;
import javaiscoffee.polaroad.post.card.CardInfoDto;
import javaiscoffee.polaroad.post.hashtag.PostHashtagInfoDto;
import javaiscoffee.polaroad.report.ReportRepository;
import javaiscoffee.polaroad.response.ResponseMessages;
import javaiscoffee.polaroad.review.ResponseGetReviewDto;
import javaiscoffee.polaroad.review.Review;
import javaiscoffee.polaroad.review.ReviewRepository;
import javaiscoffee.polaroad.review.ReviewStatus;
import javaiscoffee.polaroad.review.reviewPhoto.ReviewPhoto;
import javaiscoffee.polaroad.review.reviewPhoto.ReviewPhotoInfoDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Slf4j
@Service
public class AdminService {
    private final PostRepository postRepository;
    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;
    private final ReportRepository reportRepository;
    private final AdminLogRepository adminLogRepository;
    @Autowired
    public AdminService(PostRepository postRepository, ReviewRepository reviewRepository, MemberRepository memberRepository, ReportRepository reportRepository, AdminLogRepository adminLogRepository) {
        this.postRepository = postRepository;
        this.reviewRepository = reviewRepository;
        this.memberRepository = memberRepository;
        this.reportRepository = reportRepository;
        this.adminLogRepository = adminLogRepository;
    }

    //관리자가 사용자 지정한 상태로 변화시키기
    @Transactional
    public void setMemberStatus(Long adminId, Long memberId, MemberStatus status, String reason) {
        Member admin = checkAdmin(adminId);
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NotFoundException(ResponseMessages.NOT_FOUND.getMessage()));
        if(!member.getStatus().equals(MemberStatus.ACTIVE)) throw new NotFoundException(ResponseMessages.NOT_FOUND.getMessage());
        member.setStatus(status);
        //관리자 기록 저장
        saveAdminLog(admin, memberId, AdminTargetType.MEMBER, AdminActionType.UPDATE, status.toString(), reason);
    }

    /**
     * 관리자가 사용자 정보 조회
     */
    @Transactional
    public MemberInformationResponseDto getMemberInfo(Long adminId, Long memberId) {
        Member admin = checkAdmin(adminId);
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NotFoundException(ResponseMessages.NOT_FOUND.getMessage()));
        // MemberInformationDto의 내부 Data 객체를 생성하고 정보 복사
        MemberInformationResponseDto responseDto = new MemberInformationResponseDto();
        BeanUtils.copyProperties(member, responseDto);
        saveAdminLog(admin, memberId, AdminTargetType.MEMBER, AdminActionType.GET, null, null);
        return responseDto;
    }

    /**
     * 관리자가 포스트 조회
     */
    @Transactional
    public PostInfoDto getPostInfoById(Long adminId, Long postId) {
        Member admin = checkAdmin(adminId);
        Post post = postRepository.findById(postId).orElseThrow(() -> new NotFoundException(ResponseMessages.NOT_FOUND.getMessage()));
        saveAdminLog(admin, postId, AdminTargetType.POST, AdminActionType.GET, null, null);
        return toPostInfoDto(post);
    }

    /**
     * 관리자가 포스트 상태 설정
     */
    @Transactional
    public void setPostStatus(Long adminId, Long postId, PostStatus status, String reason) {
        Member admin = checkAdmin(adminId);
        Post post = postRepository.findById(postId).orElseThrow(() -> new NotFoundException(ResponseMessages.NOT_FOUND.getMessage()));
        post.setStatus(status);
        if( status.equals(PostStatus.ACTIVE)) {
            saveAdminLog(admin, postId, AdminTargetType.POST, AdminActionType.RESTORE, PostStatus.ACTIVE.toString(), reason);
        }
        else if( status.equals(PostStatus.DELETED)) {
            saveAdminLog(admin, postId, AdminTargetType.POST, AdminActionType.DELETE, PostStatus.DELETED.toString(), reason);
        }
    }

    /**
     * 관리자 리뷰 조회
     */
    @Transactional
    public ResponseGetReviewDto getReviewById (Long adminId, Long reviewId) {
        Member admin = checkAdmin(adminId);
        Review review = reviewRepository.findByReviewId(reviewId);
        List<ReviewPhotoInfoDto> reviewPhotoInfoDtoList = toReviewPhotoInfoDtoList(review.getReviewPhoto());
        saveAdminLog(admin, reviewId, AdminTargetType.REVIEW, AdminActionType.GET, null, null);
        return toResponseGetReviewDto(review, reviewPhotoInfoDtoList);
    }

    /**
     * 관리자 리뷰 상태 설정
     */
    @Transactional
    public void setReviewStatus(Long adminId, Long reviewId, ReviewStatus status, String reason) {
        Member admin = checkAdmin(adminId);
        Review review = reviewRepository.findByReviewId(reviewId);
        review.setStatus(status);
        if(status.equals(ReviewStatus.ACTIVE)) {
            saveAdminLog(admin, reviewId, AdminTargetType.REVIEW, AdminActionType.RESTORE, ReviewStatus.ACTIVE.toString(), reason);
        }
        else if(status.equals(ReviewStatus.DELETED)) {
            saveAdminLog(admin, reviewId, AdminTargetType.REVIEW, AdminActionType.DELETE, ReviewStatus.DELETED.toString(), reason);
        }
    }

    /**
     * 관리자 행동 로그 기록
     */
    private void saveAdminLog(Member admin, Long targetId, AdminTargetType targetType, AdminActionType actionType, String actionValue, String reason) {
        adminLogRepository.save(new AdminLog(admin, targetId, targetType, actionType, actionValue, reason));
    }

    /**
     * 관리자 권한 확인
     */
    private Member checkAdmin(Long memberId) {
        Member admin = memberRepository.findById(memberId).orElseThrow(() -> new NotFoundException(ResponseMessages.NOT_FOUND.getMessage()));
        if (!admin.getRole().equals(MemberRole.ADMIN)) throw new ForbiddenException(ResponseMessages.FORBIDDEN.getMessage());
        return admin;
    }

    /**
     * 포스트의 삭제된 카드 포함 모든 카드를 조회
     */
    private PostInfoDto toPostInfoDto(Post post) {
        List<CardInfoDto> cardDtos = post.getCards().stream()
                .sorted(Comparator.comparingInt(Card::getCardIndex))
                .map(this::toCardInfoDto)
                .collect(toList());

        List<PostHashtagInfoDto> hashtagDtos = post.getPostHashtags().stream()
                .map(ph -> new PostHashtagInfoDto(ph.getHashtag().getHashtagId(), ph.getHashtag().getName()))
                .collect(toList());

        PostMemberInfoDto memberDto = toPostMemberInfoDto(post.getMember());

        return new PostInfoDto(
                post.getTitle(),
                false, //항상 false로 표시
                memberDto,
                post.getRoutePoint(),
                post.getGoodNumber(),
                post.getThumbnailIndex(),
                post.getConcept(),
                post.getRegion(),
                cardDtos,
                hashtagDtos
        );
    }
    //카드 정보 Dto로 변환
    private CardInfoDto toCardInfoDto(Card card) {
        return new CardInfoDto(card.getCardId(), card.getCardIndex(), card.getLatitude(), card.getLongtitude(), card.getLocation(), card.getImage(), card.getContent());
    }

    // ReviewPhotoInfoDto로 매핑하는 메서드
    private static List<ReviewPhotoInfoDto> toReviewPhotoInfoDtoList(List<ReviewPhoto> reviewPhotos) {

        List<ReviewPhotoInfoDto> reviewPhotoInfoDtoList = new ArrayList<>();

        for (ReviewPhoto reviewPhoto : reviewPhotos) {
            ReviewPhotoInfoDto reviewPhotoInfoDto = new ReviewPhotoInfoDto();
            reviewPhotoInfoDto.setReviewPhotoId(reviewPhoto.getReviewPhotoId());
            reviewPhotoInfoDto.setReviewPhotoUrl(reviewPhoto.getImage());
            reviewPhotoInfoDtoList.add(reviewPhotoInfoDto);
        }

        log.info("InfoDto로 매핑 = {}", reviewPhotoInfoDtoList);

        return reviewPhotoInfoDtoList;
    }

    // 리뷰 조회 응답 Dto로 매핑
    private ResponseGetReviewDto toResponseGetReviewDto(Review review,List<ReviewPhotoInfoDto> reviewPhotoInfoList) {
        if (review == null) {
            return  null;
        }
        ResponseGetReviewDto responseGetReviewDto = new ResponseGetReviewDto();

        responseGetReviewDto.setPostId(review.getPost() != null ? review.getPost().getPostId() : null);
        responseGetReviewDto.setMemberId(review.getMember() != null ? review.getMember().getMemberId() : null);
        responseGetReviewDto.setProfileImage(review.getMember().getProfileImage());
        responseGetReviewDto.setNickname(review.getMember() != null ? review.getMember().getNickname() : null);
        responseGetReviewDto.setReviewId(review.getReviewId());
        responseGetReviewDto.setContent(review.getContent());
        responseGetReviewDto.setUpdatedTime(review.getUpdatedTime());
        responseGetReviewDto.setReviewPhotoInfoList(reviewPhotoInfoList);

        return responseGetReviewDto;
    }

    /**
     * 멤버 정보를 게시글 생성 멤버 정보 조회 ResponseDto로 변환
     */
    private PostMemberInfoDto toPostMemberInfoDto (Member member) {
        PostMemberInfoDto postMemberInfoDto = new PostMemberInfoDto();
        BeanUtils.copyProperties(member,postMemberInfoDto);
        return postMemberInfoDto;
    }
}
