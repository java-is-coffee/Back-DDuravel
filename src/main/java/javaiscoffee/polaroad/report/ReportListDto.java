package javaiscoffee.polaroad.report;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Schema(description = "신고 목록 조회할 때 최대 페이지도 함께 주는 ResponseDto")
public class ReportListDto {
    private List<ReportInfoDto> reports;
    @Schema(description = "신고 목록 최대 페이지",example = "5")
    private int maxPage;
}
