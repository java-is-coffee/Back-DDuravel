package javaiscoffee.polaroad.report;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "신고할 때 사용하는 RequestDto")
public class ReportSaveDto {
    @Schema(description = "신고 대상 ID",example = "1")
    private Long targetId;
    @Schema(description = "신고 대상 유형이 포스트인지 리뷰인지",example = "POST")
    private ReportTargetType targetType;
    @Schema(description = "신고 사유", example = "혐오스러운 내용")
    private String reason;
}
