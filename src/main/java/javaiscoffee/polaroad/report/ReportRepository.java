package javaiscoffee.polaroad.report;

import javaiscoffee.polaroad.member.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {
    // 신고 상태로 조회
    public Page<Report> getReportsByStatusOrderByCreatedTimeDesc(ReportStatus status, Pageable pageable);
    // 신고 전체 조회
    public Page<Report> findByOrderByCreatedTimeDesc(Pageable pageable);

    //같은 신고가 존재하는지 확인
    public boolean existsReportByMemberAndTargetIdAndTargetType(Member member, Long targetId, ReportTargetType type);

    public Long countByStatus(ReportStatus status);
}
