package javaiscoffee.polaroad.admin;

import javaiscoffee.polaroad.exception.ForbiddenException;
import javaiscoffee.polaroad.exception.NotFoundException;
import javaiscoffee.polaroad.member.Member;
import javaiscoffee.polaroad.member.MemberRepository;
import javaiscoffee.polaroad.member.MemberRole;
import javaiscoffee.polaroad.member.MemberStatus;
import javaiscoffee.polaroad.post.PostRepository;
import javaiscoffee.polaroad.response.ResponseMessages;
import javaiscoffee.polaroad.review.ReviewRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
public class AdminService {
    private final PostRepository postRepository;
    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;
    private final ReportRepository reportRepository;
    @Autowired
    public AdminService(PostRepository postRepository, ReviewRepository reviewRepository, MemberRepository memberRepository, ReportRepository reportRepository) {
        this.postRepository = postRepository;
        this.reviewRepository = reviewRepository;
        this.memberRepository = memberRepository;
        this.reportRepository = reportRepository;
    }

    //관리자가 사용자 정지시키기
    @Transactional
    public void suspendMember(Long memberId, Long targetId) {
        Member admin = memberRepository.findById(memberId).orElseThrow(() -> new NotFoundException(ResponseMessages.NOT_FOUND.getMessage()));
        if (!admin.getRole().equals(MemberRole.ADMIN)) throw new ForbiddenException(ResponseMessages.FORBIDDEN.getMessage());
        Member member = memberRepository.findById(targetId).orElseThrow(() -> new NotFoundException(ResponseMessages.NOT_FOUND.getMessage()));
        if(!member.getStatus().equals(MemberStatus.ACTIVE)) throw new NotFoundException(ResponseMessages.NOT_FOUND.getMessage());
        member.setStatus(MemberStatus.SUSPENDED);
    }


}
