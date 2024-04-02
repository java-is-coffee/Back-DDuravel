package javaiscoffee.polaroad.report;

import javaiscoffee.polaroad.exception.BadRequestException;
import javaiscoffee.polaroad.exception.ForbiddenException;
import javaiscoffee.polaroad.exception.NotFoundException;
import javaiscoffee.polaroad.member.Member;
import javaiscoffee.polaroad.member.MemberRepository;
import javaiscoffee.polaroad.member.MemberRole;
import javaiscoffee.polaroad.post.Post;
import javaiscoffee.polaroad.post.PostRepository;
import javaiscoffee.polaroad.post.PostStatus;
import javaiscoffee.polaroad.response.ResponseMessages;
import javaiscoffee.polaroad.review.Review;
import javaiscoffee.polaroad.review.ReviewRepository;
import javaiscoffee.polaroad.review.ReviewStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
public class ReportService {
    private final PostRepository postRepository;
    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;
    private final ReportRepository reportRepository;
    @Autowired
    public ReportService(PostRepository postRepository, ReviewRepository reviewRepository, MemberRepository memberRepository, ReportRepository reportRepository) {
        this.postRepository = postRepository;
        this.reviewRepository = reviewRepository;
        this.memberRepository = memberRepository;
        this.reportRepository = reportRepository;
    }

    //일반 사용자가 포스트나 리뷰 신고
    @Transactional
    public void saveReport(ReportSaveDto saveDto, Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NotFoundException(ResponseMessages.NOT_FOUND.getMessage()));
        //같은 대상에 대해 같은 멤버가 중복 신고를 할 경우 에러
        if(reportRepository.existsReportByMemberAndTargetIdAndTargetType(member, saveDto.getTargetId(), saveDto.getTargetType())) throw new BadRequestException(ResponseMessages.BAD_REQUEST.getMessage());
        //신고 유형이 포스트일 경우
        if(saveDto.getTargetType().equals(ReportTargetType.POST)) {
            postRepository.findById(saveDto.getTargetId()).orElseThrow(() -> new NotFoundException(ResponseMessages.NOT_FOUND.getMessage()));
            Report report = new Report();
            BeanUtils.copyProperties(saveDto, report);
            report.setMember(member);
            reportRepository.save(report);
        }
        //신고 유형이 리뷰일 경우
        else {
            Review review = reviewRepository.findByReviewId(saveDto.getTargetId());
            if(review == null) throw new NotFoundException(ResponseMessages.NOT_FOUND.getMessage());
            Report report = new Report();
            BeanUtils.copyProperties(saveDto, report);
            report.setMember(member);
            reportRepository.save(report);
        }
    }

    /**
     * 관리자가 신고 리스트 조회
     */
    public ReportListDto getReportList(Long memberId, int page, int pageSize, ReportStatus status) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NotFoundException(ResponseMessages.NOT_FOUND.getMessage()));
        if (!member.getRole().equals(MemberRole.ADMIN)) throw new ForbiddenException(ResponseMessages.FORBIDDEN.getMessage());
        Pageable pageable = PageRequest.of(page, pageSize, Sort.Direction.DESC, "createdTime");
        Page<Report> reports;
        int maxPage = 0;
        //전체 조회일 경우
        if(status == null) {
            reports = reportRepository.findByOrderByCreatedTimeDesc(pageable);
            Long reportCounts = reportRepository.count();
            maxPage = (int)Math.ceil((double) reportCounts/pageSize);
        }
        //신고 상태별 조회일 경우
        else {
            reports = reportRepository.getReportsByStatusOrderByCreatedTimeDesc(status, pageable);
            Long reportCounts = reportRepository.countByStatus(status);
            maxPage = (int)Math.ceil((double) reportCounts/pageSize);
        }
        List<ReportInfoDto> reportInfoDtos = reports.stream()
                .map(report -> new ReportInfoDto(report.getReportId(),
                        report.getTargetId(),
                        report.getTargetType(),
                        report.getReason(),
                        report.getMember().getMemberId(),
                        report.getStatus(),
                        report.getAdmin().getMemberId(),
                        report.getCreatedTime(),
                        report.getUpdatedTime()))
                .toList();
        return new ReportListDto(reportInfoDtos, maxPage);
    }

    // 관리자가 신고 대상 포스트나 리뷰 삭제 처리
    @Transactional
    public void deleteReportTarget(Long memberId, Long reportId) {
        Member admin = memberRepository.findById(memberId).orElseThrow(() -> new NotFoundException(ResponseMessages.NOT_FOUND.getMessage()));
        if (!admin.getRole().equals(MemberRole.ADMIN)) throw new ForbiddenException(ResponseMessages.FORBIDDEN.getMessage());
        Report report = reportRepository.findById(reportId).orElseThrow(() -> new NotFoundException(ResponseMessages.NOT_FOUND.getMessage()));

        if(report.getTargetType().equals(ReportTargetType.POST)) {
            Post post = postRepository.findById(report.getTargetId()).orElseThrow(() -> new NotFoundException(ResponseMessages.NOT_FOUND.getMessage()));
            if(post.getStatus().equals(PostStatus.DELETED)) throw new NotFoundException(ResponseMessages.NOT_FOUND.getMessage());
            //포스트 삭제 처리
            post.setStatus(PostStatus.DELETED);
            post.setUpdatedTime(LocalDateTime.now());
            //멤버 포스트 개수 1개 감소
            post.getMember().setPostNumber(post.getMember().getPostNumber() - 1);
        }
        else if(report.getTargetType().equals(ReportTargetType.REVIEW)) {
            Review review = reviewRepository.findByReviewId(report.getTargetId());
            if(review==null || review.getStatus().equals(ReviewStatus.DELETED)) throw new NotFoundException(ResponseMessages.NOT_FOUND.getMessage());
            //리뷰 삭제 처리
            review.setStatus(ReviewStatus.DELETED);
            review.setUpdatedTime(LocalDateTime.now());
            //멤버 리뷰 개수 1개 감소
            review.getMember().setPostNumber(review.getMember().getPostNumber() - 1);
        }
        //신고 완료 처리
        report.setStatus(ReportStatus.COMPLETED);
        report.setAdmin(admin);
        report.setUpdatedTime(LocalDateTime.now());
    }
}
