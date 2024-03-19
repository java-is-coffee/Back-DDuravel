package javaiscoffee.polaroad.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Schema(description = "신고 목록 조회할 때 사용하는 ResponseDto")
public class ReportInfoDto {
    @Schema(description = "신고 ID", example = "1")
    private Long reportId;
    @Schema(description = "신고 대상 ID",example = "1")
    private Long targetId;
    @Schema(description = "신고 대상 유형 > 포스트인지 리뷰인지",example = "POST")
    private ReportTargetType targetType;
    @Schema(description = "신고 사유", example = "혐오스러운 내용")
    private String reason;
    @Schema(description = "신고한 멤버 id", example = "1")
    private Long memberId;
    @Schema(description = "신고 처리 상태",example = "INCOMPLETE")
    private ReportStatus status;
    @Schema(description = "신고 처리한 관리자 ID",example = "1")
    private Long adminId;
    @Schema(description = "신고가 생성된 시간", example = "2024-03-19T15:50:31")
    private LocalDateTime createdTime;
    @Schema(description = "신고가 처리된 시간", example = "2024-03-19T18:42:18")
    private LocalDateTime updatedTime;
}
